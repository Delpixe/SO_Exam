package gestoremagazzino;

import java.util.Random;

public class GestoreMagazzino {

    public static void main(String[] args) {
        //Avvio la simulazione
        //richiedo all'utente se vole scrivere un log
        if (Log.wannaWriteLog())
            Log.insertPath();

        Random rnd = new Random();
        int nAddetti = rnd.nextInt(100);
        int nAcquirenti = rnd.nextInt(100);
        int fTime = Math.abs(rnd.nextInt(3000)+ 2000); //messi almeno 2 secondi per semplicità di testing

        Magazzino magazzino = new Magazzino();
        Fornitore_di_risorse fornitore = new Fornitore_di_risorse("Adesivo_World",fTime,magazzino);
        Acquirente[] acquirenti = new Acquirente[nAcquirenti];
        Addetto_spedizioni[] addetti = new Addetto_spedizioni[nAddetti];

        welcome(nAddetti,nAcquirenti);

        //Lancio tutti i thread
        fornitore.start();
        //Instance the acquirenti
        for (int i = 0; i < acquirenti.length ; i++){
            acquirenti[i] = new Acquirente(i,magazzino);
            acquirenti[i].start();
        }
        //Instance the addetti
        for (int i = 0; i < addetti.length ; i++){
            addetti[i] = new Addetto_spedizioni(i,magazzino);
            addetti[i].start();
        }

        try{
            //mi metto in attesa della terminazione dei thread acquirenti
           for (int i = 0; i < acquirenti.length ; i++)
                acquirenti[i].join();

            for (int i = 0; i < addetti.length ; i++) {
                addetti[i].interrupt();
                addetti[i].join();
            }
            //genero l'interrupt per far terminare il fornitore
            fornitore.interrupt();
            fornitore.join();
        }catch (InterruptedException e){
            Log.writeLog(e.toString());
        }

        magazzino.stampaListe();
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
        System.out.println("Simulazione terminata.");
        System.out.println("________________________________________________________");

        //log
        Log.writeLog("________________________________________________________");
        Log.writeLog("Simulazione terminata.");
        Log.writeLog("________________________________________________________");
       // waitForEnter();

    } //end-printFinalPageList

    public static void waitForEnter() {
        System.out.println("La simulazione è termiata,premi Invio per uscire...");
        Utility.ReadLine();
    }//end-waitForEnter
}
