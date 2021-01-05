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
import java.util.concurrent.locks.Condition;
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

    public boolean Orders_present = true;

    /* Lista ordini */
    private static final List<Order> order_List = new ArrayList<>();

    //invocato dagli acquirenti
    public static void effettuaOrdine(Acquirente acquirente_ordine,int num_pacchi){
        Magazzino.accedi_magazzino();
        Order ordine = new Order(acquirente_ordine,num_pacchi);
        order_List.add(ordine);
        Log.writeLog(acquirente_ordine.getName() + " ha aggiunto un ordine di " + num_pacchi + " pacchi");
        /*
        try{
            orderToHandle.signal();
        }finally{
            Magazzino.rilascia_magazzino();
        }
         */
        Magazzino.rilascia_magazzino();
    }

    //invocato dagli addetti_alle_spedizioni
    public static void gestisciOrdine(Addetto_spedizioni addetto_spedizioni){
        /*
        Seleziona l’ordine da gestire secondo la politica FIFO
        Cerca di accaparrarsi un quantitativo di risorse proporzionale al numero di pacchi
        contenuti nell’ordine secondo la relazione:
        1 scatola e 50cm di nastro adesivo per ogni pacco.
        · Se trova risorse sufficienti, si sospende per un tempo proporzionale al numero
        di pacchi contenuti nell’ordine secondo la relazione 5 millisecondi per ogni
        pacco.
        · Se non trova risorse si sospende in attesa di queste.
         */

        boolean wait = true;
        Order ordine_da_gestire = null;

        try{
            //finche la lista degli ordini è vuota o la qta dell'ordine è maggiore del magazzino l'addetto aspetta
            while (wait){
                Magazzino.accedi_magazzino();

                wait = (order_List.isEmpty());
                ordine_da_gestire = null;
                if (! wait){
                    ordine_da_gestire = order_List.get(0);
                    wait = (!CanHandlePacchi(ordine_da_gestire));
                }
                if (wait){
                    try{
                        Log.writeLog("Addetto " + addetto_spedizioni.getName() + " viene messo in wait");
                        addetto_spedizioni.sleep(500);
                    } catch ( IllegalMonitorStateException ex) {
                        System.out.println("[Errore]" + addetto_spedizioni.getName() + ex.toString());
                    }finally {
                        Magazzino.rilascia_magazzino();
                    }
                }
            }

            if (ordine_da_gestire != null){
                Log.writeLog("Addetto " + addetto_spedizioni.getName() + " sta gestendo i pacchi, ci vorrà " + ordine_da_gestire.getNum_pacchi_richiesti()*5 + " millisecondi");
                addetto_spedizioni.sleep(5*ordine_da_gestire.getNum_pacchi_richiesti());
                int num_pacchi = ordine_da_gestire.getNum_pacchi_richiesti();
                cm_nastro_disponibili -= (num_pacchi * 50);
                scatole_disponibili -= num_pacchi;
                order_List.remove(ordine_da_gestire);
                Log.writeLog("Addetto " + addetto_spedizioni.getName() + " ha gestito l'ordine da " + ordine_da_gestire.getNum_pacchi_richiesti() + " pacchi fatto da " + ordine_da_gestire.getNomeAcquirente());
                Log.writeLog("Giacenza ora -> Scatole " + scatole_disponibili + " nastro " + cm_nastro_disponibili);
            }
        }catch (InterruptedException e) {
            Log.writeLog("non ci sono ordini in lista");
        }finally {
            Magazzino.rilascia_magazzino();
        }
    }

    private static boolean CanHandlePacchi(Order ordine_da_gestire) {
        int num_pacchi_richiesti = ordine_da_gestire.getNum_pacchi_richiesti();
        if ((cm_nastro_disponibili > (num_pacchi_richiesti * 50)) & (scatole_disponibili > num_pacchi_richiesti))
            return true;
        return false;
    }

    //invocato dai fornitori
    public static void depositaRisorse(Fornitore_di_risorse fornitore_di_risorse,int cm_nastro,int nscatole){
        Magazzino.accedi_magazzino();
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
        Magazzino.rilascia_magazzino();
        Log.writeLog(fornitore_di_risorse.getName() + " ha depositato " + cm_nastro + " cm di nastro e " + nscatole + " scatole");
    }

    //lck and sem mgt
    public static void accedi_magazzino(){
        try{
            sem.acquire();
        }catch(InterruptedException e){
            System.out.println(e.toString());
            Log.writeLog(e.toString());
        }
        lck.lock();
    }

    public static void rilascia_magazzino(){
        lck.unlock();
        sem.release();
    }
    //fine - lck and sem mgt
}
