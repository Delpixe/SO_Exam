package gestoremagazzino;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Magazzino {

    // costanti per codificare il servizio richiesto
    public static final String PRIME    = "PRIME";
    public static final String STANDARD = "STANDARD";

    //strutture per emulare le liste degli ordini
    //prime e standard
    private final ArrayList<Order> standard_list;
    private final ArrayList<Order> prime_list;
    private final ArrayList<Order> prime_orderTime_list;
    private final ArrayList<Order> standard_orderTime_list;

    // attributi di sincronizzazione lock per la mutua esclusione
    // a guardia di standard_list, prime_list
    private final ReentrantLock lckAccesso;
    private final ReentrantLock lckRisorse;
    private final Condition attesaRisorse;

    // semaforo contatore per sospendere l'addetto
    // in attesa di nuove richieste
    private final Semaphore nuoveRichieste;

    //Risorse
    private int cm_nastro_disponibili;
    private int scatole_disponibili;

    //costruttore della classe
    public Magazzino(){
        this.standard_list = new ArrayList<>();
        this.prime_list = new ArrayList<>();
        this.prime_orderTime_list = new ArrayList<>();
        this.standard_orderTime_list = new ArrayList<>();

        this.lckRisorse = new ReentrantLock(true);
        this.lckAccesso = new ReentrantLock(true);
        this.nuoveRichieste = new Semaphore(0,true);
        this.attesaRisorse = this.lckRisorse.newCondition();

        this.cm_nastro_disponibili = 0;
        this.scatole_disponibili = 0;
    }//end costruttore

    /********** Metodi **********/
    //metodi pubblici dell'oggetto Magazzino
    //1)effettuaOrdine: invocato dall'Acquirente per inserire il proprio ordine in lista
    //2)gestisciOrdine: invocato dall'Addetto_spedizioni per gestire e rimuovere l'ordine dalla lista
    //3)depositaRisorse: omvpcatp dal Fornitore_di_risorse per aggiungere merce al magazzino

    //invocato dagli acquirenti
    public void effettuaOrdine(Acquirente acquirente_ordine,int num_pacchi){
        this.lckAccesso.lock();
        try{
            Order ordine = new Order(acquirente_ordine,num_pacchi);
            if (acquirente_ordine.getTipoAcquirente().equals(PRIME))
                prime_list.add(ordine);
            else
                standard_list.add(ordine);

            Log.writeLog(acquirente_ordine.getName() + " ha messo in coda l'ordine <"
                                + ordine.getNumero_ordine() +"> contenente "
                                + num_pacchi + " pacchi");

            // devo svegliare l'addetto se sta dormendo
            this.nuoveRichieste.release();
            // sospendo l'acquirente con il metodo interno
            acquirente_ordine.setCondition(this.lckAccesso.newCondition());
            acquirente_ordine.sospendi();
        }catch(InterruptedException e){
            Log.writeLog(e.toString());
        }finally{
            this.lckAccesso.unlock();
            //FINE SEZIONE CRITICA
        }
    }//end effettuaOrdine

    //invocato dagli addetti_alle_spedizioni
    public void gestisciOrdine(Addetto_spedizioni addetto_spedizioni) throws InterruptedException {
        // acquisisco un permesso sul semaforo delle richieste
        this.nuoveRichieste.acquire();
        // ora so che c'è almeno una richiesta in attesa di essere servita
        // posso finalmente gesitere una richiesta devo cercare l'Ordine migliore nelle mie code

        this.lckAccesso.lock();
        try{
            long tempoInizio = System.currentTimeMillis(),
                    tempoFine;

            Log.writeLog(addetto_spedizioni.getName() + " scruta la lista:");
            this.stampaCode();

            Order firstOrder = selectFIFOOrder();

            this.lckRisorse.lock();
            while (!CanHandlePacchi(firstOrder)){
                this.attesaRisorse.await();
            }

            int num_pacchi_usati = firstOrder.getNum_pacchi_richiesti();
            int sleeptime = num_pacchi_usati * 5;
            Thread.sleep(sleeptime);
            // sveglio l'Ordine selezionato
            this.scatole_disponibili -= num_pacchi_usati;
            this.cm_nastro_disponibili -= num_pacchi_usati * 50;

            Log.writeLog(addetto_spedizioni.getName() + " ha gestito l'ordine: " +
                    firstOrder.getNumero_ordine() + " di tipo " +
                    firstOrder.getAcquirente_ordine().getTipoAcquirente() + " contente " +
                    num_pacchi_usati);

            Log.writeLog("nuova giacenza: "
                            + this.scatole_disponibili + " scatole e "
                            + this.cm_nastro_disponibili + " cm nastro disponibili");

            firstOrder.getAcquirente_ordine().risveglia();

            tempoFine = System.currentTimeMillis();
            firstOrder.setTempo_impiegato(tempoFine - tempoInizio);
            if (firstOrder.getAcquirente_ordine().getTipoAcquirente().equals(PRIME))
                this.prime_orderTime_list.add(firstOrder);
            else
                this.standard_orderTime_list.add(firstOrder);

        }finally{
            this.lckRisorse.unlock();
            this.lckAccesso.unlock();
            //FINE SEZIONE CRITICA
        }


    }//end gestisciOrdine

    // metodo privato funzionale per trovare l'ordine per fifo e prio
    private Order selectFIFOOrder(){
        Order best = null;
        // la precedenza deve essere sempre data all'atterraggio
        if(!this.prime_list.isEmpty())
            best = getAndRemoveFirst(this.prime_list);
        else if(!this.standard_list.isEmpty())
            best = getAndRemoveFirst(this.standard_list);
        return best;
    }//end selectFIFOOrder

    // metodo privato per trovare il miglior ordine
    // all'interno di una coda e rimuoverlo
    private Order getAndRemoveFirst(List<Order> orderList){
        Order best;
        best = orderList.get(0);
        // rimuovo il primo dalla coda
        orderList.remove(best);
        return best;
    }//end getAndRemoveFirst

    //invocato dai fornitori
    public void depositaRisorse(Fornitore_di_risorse fornitore_di_risorse,int cm_nastro,int nscatole) throws InterruptedException {
        //Inizio sezione critica
        this.lckRisorse.lock();
        try{
            this.cm_nastro_disponibili += cm_nastro;
            this.scatole_disponibili += nscatole;
            // devo svegliare gli addetti se stanno in wait
            this.attesaRisorse.signalAll();

            Log.writeLog(fornitore_di_risorse.getName() + " ha depositato: "
                        + nscatole + " scatole e "
                        + cm_nastro + " cm di nastro. La giacenza ora -> Scatole "
                        + scatole_disponibili + " nastro " + cm_nastro_disponibili);
        } finally {
            this.lckRisorse.unlock();
        }
        //FINE sezione critica
    }//end depositaRisorse
    //fine - lck and sem mgt

    //funzioni extra
    private boolean CanHandlePacchi(Order ordine) {
        int num_pacchi_richiesti = ordine.getNum_pacchi_richiesti();
        return (cm_nastro_disponibili >= (num_pacchi_richiesti * 50)) & (scatole_disponibili >= num_pacchi_richiesti);
    }//end CanHandlePacchi

    // metodo per la stampa del contenuto delle code
    public void stampaCode(){
        // stampo il conteunto delle due code
        StampaLista(this.prime_list,this.PRIME);
        StampaLista(this.standard_list,this.STANDARD);
    }//end-stampaCode

    private void StampaLista( ArrayList<Order> list,String tipoLista){
        Log.writeLog("___________________________");
        Log.writeLog(tipoLista +" : ");

        System.out.println("___________________________");
        System.out.println(tipoLista +" : ");

        for(int i = 0; i < list.size(); i++){
            Order o = list.get(i);
            Log.writeLog("Ordine_" + o.getNumero_ordine().toString());
            System.out.println("Ordine_" + o.getNumero_ordine().toString());
        }

        Log.writeLog("___________________________");
        System.out.println("___________________________");

    }//end-StampaLista

    public void stampaListeTempi() {
        StampaListaTempi(this.prime_orderTime_list,this.PRIME);
        StampaListaTempi(this.standard_orderTime_list,this.STANDARD);
    }//end-StampaListeTempi

    private void StampaListaTempi( ArrayList<Order> list,String tipoLista) {
        Log.writeLog("___________"+tipoLista+"___________");
        System.out.println("___________"+tipoLista+"___________");

        List<Double> arrayList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Order o = list.get(i);
            stampaTempoPacco(arrayList, o);
        }

        double deviazioneStandard = 0,
                media = 0;

        double[] array = new double[arrayList.size()];
        if (list.size() != 0) {
            for(int i = 0; i < arrayList.size(); i++)
                array[i] = arrayList.get(i);

            deviazioneStandard = Utility.calcolaDeviazioneStandard(array);
            media = Utility.calcolaMedia(array);
        }

        System.out.println("__________________________");
        String final_message = "I "+tipoLista+" sono " + array.length
                + " la deviazione standard dei "+tipoLista+" prime è "+ deviazioneStandard
                + " la media dei "+tipoLista+" è "+ media;

        System.out.println(final_message);
        Log.writeLog(final_message);
        Log.writeLog("__________________________");

        System.out.println("_________end-"+tipoLista+"_________");
        Log.writeLog("_________end-"+tipoLista+"_________");
    }//end-StampaListaTempi

    private void stampaTempoPacco(List<Double> array, Order o) {
        for (int j = 0;j<o.getNum_pacchi_richiesti();j++){
            String message = "Ordine_"+o.getNumero_ordine() + " pacco_" + j + " " +o.getTempo_impiegato()/o.getNum_pacchi_richiesti()+" millisecondi";
            System.out.println(message);
            Log.writeLog(message);
            array.add((double) (o.getTempo_impiegato()/o.getNum_pacchi_richiesti()));
        }
    }//end-stampaTempoPacco
}
