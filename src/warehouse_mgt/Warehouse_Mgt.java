package warehouse_mgt;

import java.util.Random;

public class Warehouse_Mgt {

    public static void main(String[] args) {
        if (Log.wannaWriteLog())
            Log.insertPath();

        /*
        Random rnd = new Random();
        */
        int nAddetti = 100;//rnd.nextInt(1000);
        int nAcquirenti = 10;//rnd.nextInt(1000);
        int fTime = 1000;//Math.abs(rnd.nextInt(5000)); //messi 5 secondi per semplicità di testing

        welcome(nAddetti,nAcquirenti);

        Fornitore_di_risorse fornitore = new Fornitore_di_risorse("Adesivo_World",fTime);
        fornitore.start();

        Acquirente[] acquirenti = new Acquirente[nAcquirenti];
        Addetto_spedizioni[] addetti = new Addetto_spedizioni[nAddetti];

        //Instance the acquirenti

        for (int i = 0; i < acquirenti.length ; i++)
            acquirenti[i] = new Acquirente(i);
        for (int i = 0; i < acquirenti.length ; i++)
            acquirenti[i].start();

        //Instance the addetti
        for (int i = 0; i < addetti.length ; i++)
            addetti[i] = new Addetto_spedizioni(i);
        for (int i = 0; i < addetti.length ; i++)
            addetti[i].start();

        try{
            //non ci deve stare altrimenti non finisce mai --> fornitore.join altrimenti non finirebbe mai il loop

           for (int i = 0; i < acquirenti.length ; i++)
                acquirenti[i].join();

            for (int i = 0; i < addetti.length ; i++)
                addetti[i].join();

        }catch (InterruptedException e){
            Log.writeLog(e.toString());
        }

        fornitore.stopRefilling();
        fornitore.interrupt();
        printFinalPageList();
    }

    private static void welcome(int nAddetti, int nAcquirenti) {
        System.out.println("----------------------------"); //log
        System.out.println("Inizio del programma"); //log
        System.out.println("con " + nAddetti + " Addetti");
        System.out.println("con " + nAcquirenti + " Acquirenti");
        System.out.println("----------------------------"); //log

        //log
        Log.writeLog("");
        Log.writeLog("----------------------------");
        Log.writeLog("Inizio del programma");
        Log.writeLog("con " + nAddetti + " Addetti");
        Log.writeLog("con " + nAcquirenti + " Acquirenti");
        Log.writeLog("----------------------------");
        Log.writeLog("");
    } //end-welcome

    private static void printFinalPageList() {

        System.out.println("________________________________________________________");
        System.out.println("Fine");
        System.out.println("________________________________________________________");

        //log
        Log.writeLog("________________________________________________________");
        Log.writeLog("Fine");
        Log.writeLog("________________________________________________________");
        waitForEnter();

    } //end-printFinalPageList

    public static void waitForEnter() {
        System.out.println("Premi Invio per uscire...");
        Utility.ReadLine();
    }//end-waitForEnter
}
