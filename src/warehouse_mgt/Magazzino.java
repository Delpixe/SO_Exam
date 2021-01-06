/*
nel magazzino si trovano un numero nAddetti di addetti alle spedizioni che
gestiscono gli ordini di acquisto effettuati da un numero nAcquirenti di acquirenti.

Gli acquirenti potranno essere di due tipi:
- standard
- prime
effettueranno ordini per un numero variabile di pacchi (scelto casualmente).

Nel magazzino è presente un fornitore di risorse che periodicamente fornisce agli addetti alle spedizioni
una quantità variabile (scelta casualmente) di scatole e di nastro adesivo, utilizzati per gestire gli
ordini.

La simulazione terminerà quando tutti gli acquirenti avranno sottomesso 10 richieste ciascuno.
*/

package warehouse_mgt;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Magazzino {

    /* Risorse */
    private static int cm_nastro_disponibili = 0;
    private static int scatole_disponibili = 0;
    /* end-Risorse */

    /* lock_mgt */
    private static final ReentrantLock lck = new ReentrantLock(true);
    private static final Semaphore sem = new Semaphore(1,true);//a guardia di lck
    //private static Condition orderToHandle = lck.newCondition();
    /* end-lock_mgt */

    /* Lista ordini */
    private static final List<Order> std_order_List = new ArrayList<>();
    private static final List<Order> prime_order_List = new ArrayList<>();


    /********** Funzioni **********/
    //invocato dagli acquirenti
    public static void effettuaOrdine(Acquirente acquirente_ordine,int num_pacchi){
        Magazzino.accediMagazzino();
        Order ordine = new Order(acquirente_ordine,num_pacchi);
        if (acquirente_ordine.Prime)
            prime_order_List.add(ordine);
        else
            std_order_List.add(ordine);
        Log.writeLog(acquirente_ordine.getName() + " ha aggiunto un ordine di " + num_pacchi + " pacchi");
        /*
        try{
            orderToHandle.signal();
        }finally{
            Magazzino.rilascia_magazzino();
        }
         */
        Magazzino.rilasciaMagazzino();
    }

    //invocato dagli addetti_alle_spedizioni
    public static void gestisciOrdine(Addetto_spedizioni addetto_spedizioni){
        Order ordine_da_gestire = null;
        boolean stdIsEmpty = true;
        boolean primeIsEmpty = true;

        try{
            //finche la lista degli ordini è vuota o la qta dell'ordine è maggiore del magazzino l'addetto aspetta
            ordine_da_gestire = getOrder(addetto_spedizioni, ordine_da_gestire);

            if (ordine_da_gestire != null){
                Log.writeLog("Addetto " + addetto_spedizioni.getName() + " sta gestendo i pacchi, ci vorrà " + ordine_da_gestire.getNum_pacchi_richiesti()*5 + " millisecondi");
                addetto_spedizioni.sleep(5*ordine_da_gestire.getNum_pacchi_richiesti());
                int num_pacchi = ordine_da_gestire.getNum_pacchi_richiesti();
                cm_nastro_disponibili -= (num_pacchi * 50);
                scatole_disponibili -= num_pacchi;
                std_order_List.remove(ordine_da_gestire);
                Log.writeLog("Addetto " + addetto_spedizioni.getName() + " ha gestito l'ordine da " + ordine_da_gestire.getNum_pacchi_richiesti() + " pacchi fatto da " + ordine_da_gestire.getNomeAcquirente());
                Log.writeLog("Giacenza ora -> Scatole " + scatole_disponibili + " nastro " + cm_nastro_disponibili);
            }
        }catch (InterruptedException e) {
            Log.writeLog("non ci sono ordini in lista");
        }finally {
            Magazzino.rilasciaMagazzino();
        }
    }

    private static Order getOrder(Addetto_spedizioni addetto_spedizioni, Order ordine_da_gestire) throws InterruptedException {
        boolean primeIsEmpty;
        boolean stdIsEmpty;
        boolean gestisci_ordine = true;

        while (gestisci_ordine){
            Magazzino.accediMagazzino();
            //controllo se siano vuote le liste
            stdIsEmpty = std_order_List.isEmpty();
            primeIsEmpty = prime_order_List.isEmpty();
            gestisci_ordine = (stdIsEmpty & primeIsEmpty);

            ordine_da_gestire = null;
            //se una delle due non è vuota
            if (!gestisci_ordine){
                if (!primeIsEmpty)
                    ordine_da_gestire = prime_order_List.get(0);
                else
                    ordine_da_gestire = std_order_List.get(0);
                gestisci_ordine = (!CanHandlePacchi(ordine_da_gestire));
            }
            if (gestisci_ordine){
                try{
                    Log.writeLog("Addetto " + addetto_spedizioni.getName() + " è in attesa");
                    Magazzino.rilasciaMagazzino();
                    addetto_spedizioni.sleep(1000);
                } catch ( IllegalMonitorStateException ex) {
                    System.out.println("[Errore]" + addetto_spedizioni.getName() + ex.toString());
                }
            }
        }
        return ordine_da_gestire;
    }

    //invocato dai fornitori
    public static void depositaRisorse(Fornitore_di_risorse fornitore_di_risorse,int cm_nastro,int nscatole){
        Magazzino.accediMagazzino();
        cm_nastro_disponibili += cm_nastro;
        scatole_disponibili += nscatole;
        /*
        try{
            if (checkBeforeLockCondition())
                orderToHandle.signal();
        }finally{
            Magazzino.rilascia_magazzino();
        }
        */
        Magazzino.rilasciaMagazzino();
        Log.writeLog(fornitore_di_risorse.getName() + " ha depositato " + cm_nastro + " cm di nastro e " + nscatole + " scatole");
    }

    //lck and sem mgt
    public static void accediMagazzino(){
        try{
            sem.acquire();
        }catch(InterruptedException e){
            System.out.println(e.toString());
            Log.writeLog(e.toString());
        }
        lck.lock();
    }

    public static void rilasciaMagazzino(){
        lck.unlock();
        sem.release();
    }
    //fine - lck and sem mgt

    //funzioni extra
    private static boolean CanHandlePacchi(Order ordine) {
        int num_pacchi_richiesti = ordine.getNum_pacchi_richiesti();
        return (cm_nastro_disponibili > (num_pacchi_richiesti * 50)) & (scatole_disponibili > num_pacchi_richiesti);
    }

}
