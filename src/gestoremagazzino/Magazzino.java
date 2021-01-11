package gestoremagazzino;

import java.lang.module.ModuleReader;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Magazzino {

    // costanti per codificare il servizio richiesto
    public static final String PRIME    = "PRIME";
    public static final String STANDARD = "STANDARD";

    //strutture per emulare le liste degli ordini
    //prime e standard
    private final ArrayList<Order> standard_list;
    private final ArrayList<Order> prime_list;

    // attributi di sincronizzazione lock per la mutua esclusione
    // a guardia di standard_list, prime_list
    private final ReentrantLock lck;

    // semaforo contatore per sospendere il Controllore
    // in attesa di nuove richieste
    private final Semaphore nuoveRichieste;

    //Risorse
    private int cm_nastro_disponibili;
    private int scatole_disponibili;

    //costruttore della classe
    public Magazzino(){
        this.standard_list = new ArrayList();
        this.prime_list = new ArrayList();

        this.lck = new ReentrantLock(true);
        this.nuoveRichieste = new Semaphore(0,true);

        this.cm_nastro_disponibili = 0;
        this.scatole_disponibili = 0;
    }//end costruttore

    /********** Metodi **********/
    //metodi pubblici dell'oggetto Magazzino
    //1)effettuaOrdine: invocato dall'Acquirente per inserire il proprio ordine in lista
    //2)gestisciOrdine: invocato dall'Addetto_spedizioni per gestire e rimuovere l'ordine dalla lista
    //3)depositaRisorse: omvpcatp dal Fornitore_di_risorse per aggiungere merce al magazzino


    //invocato dagli acquirenti
    //!todo funzionante
    public void effettuaOrdine(Acquirente acquirente_ordine,int num_pacchi){
        this.lck.lock();
        try{
            Order ordine = new Order(acquirente_ordine,num_pacchi);
            Log.writeLog(acquirente_ordine.getName() + " mette in coda l'ordine <"+ ordine.getNumero_ordine() +"> contenente " + num_pacchi + " pacchi");
            if (acquirente_ordine.getTipoAcquirente().equals(PRIME))
                prime_list.add(ordine);
            else
                standard_list.add(ordine);

            // devo svegliare l'addetto se sta dormendo
            this.nuoveRichieste.release();
            // sospendo l'acquirente con il metodo interno    
            acquirente_ordine.setCondition(this.lck.newCondition());
            acquirente_ordine.sospendi();
        }catch(InterruptedException e){
            Log.writeLog(e.toString());
        }finally{
            this.lck.unlock();
            //FINE SEZIONE CRITICA
        }
    }//end effettuaOrdine

    //invocato dagli addetti_alle_spedizioni
    //!todo funzionante , però al primo giro entra da vuoto...
    public void gestisciOrdine(Addetto_spedizioni addetto_spedizioni) throws InterruptedException {
        // acquisisco un permesso sul semaforo delle richieste
        this.nuoveRichieste.acquire();
        // ora so che c'è almeno una richiesta in attesa di essere servita
        // posso finalmente gesitere una richiesta devo cercare l'Ordine migliore nelle mie code

        this.lck.lock();
        try{
            Order firstOrder = null;
            while (firstOrder == null) {
                firstOrder = selectFIFOOrder(false);

                if (firstOrder == null)
                    this.magazzinoRifornito.acquire();
            }

            Log.writeLog(addetto_spedizioni.getName() + " scruta la lista:");
            this.stampaListe();
            Order firstOrder = selectFIFOOrder(false);
            if (firstOrder != null){
                int num_pacchi_usati = firstOrder.getNum_pacchi_richiesti();
                Log.writeLog(addetto_spedizioni.getName() + " sta gestendo l'ordine: " +
                        firstOrder.getNumero_ordine() + " di tipo " +
                        firstOrder.getAcquirente_ordine().getTipoAcquirente() + " contente " +
                        num_pacchi_usati);

                // sveglio l'Ordine selezionato
                this.scatole_disponibili -= num_pacchi_usati;
                this.cm_nastro_disponibili -= num_pacchi_usati * 50;

                Log.writeLog("nuova giacenza: "
                                + this.scatole_disponibili + " scatole e "
                                + this.cm_nastro_disponibili + " cm nastro disponibili");

                firstOrder.getAcquirente_ordine().risveglia();
            }
        }finally{
            this.lck.unlock();
            //FINE SEZIONE CRITICA
        }


    }//end gestisciOrdine

    // metodo privato funzionale per trovare l'ordine per fifo e prio
    private Order selectFIFOOrder(boolean onlyGet){
        Order best = null;
        // la precedenza deve essere sempre data all'atterraggio
        if(!this.prime_list.isEmpty())
            best = getAndRemoveFirst(this.prime_list,onlyGet);
        else if(!this.standard_list.isEmpty())
            best = getAndRemoveFirst(this.standard_list,onlyGet);
        return best;
    }//end selectFIFOOrder

    // metodo privato per trovare il miglior ordine
    // all'interno di una coda e rimuoverlo
    private Order getAndRemoveFirst(List<Order> orderList,boolean onlyGet){
        Order best;
        best = orderList.get(0);
        // rimuovo il primo dalla coda
        if (!CanHandlePacchi(best))
            return null;

        if (!onlyGet)
            orderList.remove(best);
        return best;
    }//end getAndRemoveFirst

    //invocato dai fornitori
    public void depositaRisorse(Fornitore_di_risorse fornitore_di_risorse,int cm_nastro,int nscatole) throws InterruptedException {
        this.lck.lock();

        cm_nastro_disponibili += cm_nastro;
        scatole_disponibili += nscatole;

        Log.writeLog(fornitore_di_risorse.getName() + " ha depositato: "
                        + nscatole + " scatole e "
                        + cm_nastro + " cm di nastro. La giacenza ora -> Scatole "
                        + scatole_disponibili + " nastro " + cm_nastro_disponibili);


        // devo svegliare l'addetto se sta dormendo
        this.nuoveRichieste.release();

        this.lck.unlock();
    }//end depositaRisorse

    //fine - lck and sem mgt

    //funzioni extra
    private boolean CanHandlePacchi(Order ordine) {
        int num_pacchi_richiesti = ordine.getNum_pacchi_richiesti();
        return (cm_nastro_disponibili >= (num_pacchi_richiesti * 50)) & (scatole_disponibili >= num_pacchi_richiesti);
    }//end CanHandlePacchi

    // metodo per la stampa del contenuto delle code
    private void stampaListe(){
        // stampo il conteunto delle due code
        Log.writeLog("___________________________");
        Log.writeLog("PRIME: ");
        for(int i = 0; i < this.prime_list.size(); i++){
            Order o = this.prime_list.get(i);
            Log.writeLog("Ordine_" + o.getNumero_ordine().toString());
        }
        Log.writeLog("___________________________");
        Log.writeLog("STANDARD: ");
        for(int i = 0; i < this.standard_list.size(); i++){
            Order o = this.standard_list.get(i);
            Log.writeLog("Ordine_" + o.getNumero_ordine().toString());
        }
        Log.writeLog("___________________________");
    }

}
