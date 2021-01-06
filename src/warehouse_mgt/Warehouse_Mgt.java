package warehouse_mgt;

import java.util.Random;

public class Warehouse_Mgt {

    public static void main(String[] args) {
        if (Log.wannaWriteLog())
            Log.insertPath();

        /*
        Random rnd = new Random();
        int nAddetti = rnd.nextInt(1000);
        int nAcquirenti = rnd.nextInt(1000);
        int fTime = Math.abs(rnd.nextInt(5000)); //messi 5 secondi per semplicità di testing
        */
        welcome(1,1);

        Fornitore_di_risorse fornitore = new Fornitore_di_risorse("Adesivo_World",1000);
        fornitore.start();

        Acquirente[] acquirenti = new Acquirente[1];
        Addetto_spedizioni[] addetti = new Addetto_spedizioni[10];

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
//            fornitore.join(); //non ci deve stare altrimenti non finisce mai

           for (int i = 0; i < acquirenti.length ; i++)
                acquirenti[i].join();

            for (int i = 0; i < addetti.length ; i++)
                addetti[i].join();

        }catch (InterruptedException e){
            Log.writeLog(e.toString());
        }

        fornitore.stopRefilling();
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
        System.out.println("Premi Invio per continuare...");
        Utility.ReadLine();
    }//end-waitForEnter
}
