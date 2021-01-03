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
    private static Condition notEmpty = lck.newCondition();
    /* end-lock_mgt */


    /* Lista ordini */
    private static final List<Integer> order_List = new ArrayList<>();

    //invocato dagli acquirenti
    public static void effettuaOrdine(int num_pacchi){
        Magazzino.accedi_magazzino();
        order_List.add(num_pacchi);
        notEmpty.signal();
        Magazzino.rilascia_magazzino();
        Log.writeLog("Aggiunto ordine per " + num_pacchi + " pacchi");
    }

    //invocato dagli addetti_alle_spedizioni
    public static void gestisciOrdine(){
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

        try{
            while (order_List.isEmpty())
                notEmpty.await();
            Magazzino.accedi_magazzino();

            Integer num_pacchi = order_List.get(0);
            if (!HandlePacchi(num_pacchi)) {
                System.out.println("non ci sono abbastanza pacchi");
                Log.writeLog("non ci sono abbastanza pacchi");
            }
            Magazzino.rilascia_magazzino();
        }catch (InterruptedException e){
            System.out.println("non ci sono ordini in lista");
            Log.writeLog("non ci sono ordini in lista");
        }
    }

    private static boolean HandlePacchi(Integer num_pacchi) {
        if (((num_pacchi * 50) > (cm_nastro_disponibili)) || ((num_pacchi > scatole_disponibili)))
                return false;

        order_List.remove(0);
        return true;
    }

    //invocato dai fornitori
    public static void depositaRisorse(int cm_nastro,int nscatole){
        cm_nastro_disponibili += cm_nastro;
        scatole_disponibili += nscatole;
        System.out.println("depositati " + cm_nastro + " cm di nastro e " + nscatole + " scatole");
        Log.writeLog("depositati " + cm_nastro + " cm di nastro e " + nscatole + " scatole");
    }

    //lck and sem mgt
    public static void accedi_magazzino(){
        try{
            sem.acquire();
        }catch(InterruptedException e){
            System.out.println(e.toString());
        }
        lck.lock();
    }

    public static void rilascia_magazzino(){
        lck.unlock();
        sem.release();
    }
    //fine - lck and sem mgt

}
