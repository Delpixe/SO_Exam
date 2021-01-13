package gestoremagazzino;

public class GestoreMagazzino {

    public static void main(String[] args) {
        //Avvio la simulazione
        //richiedo all'utente se vole scrivere un log
        if (Log.wannaWriteLog())
            Log.insertPath();

        System.out.println("inserire il numero di addetti: ");
        int nAddetti = Utility.catchToLaunch();

        System.out.println("inserire il numero di acquirenti: ");
        int nAcquirenti = Utility.catchToLaunch();

        System.out.println("inserire il tempo di ricarica del fornitore (in millisecondi): ");
        int fTime = Utility.catchToLaunch();

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

        Log.writeLog("Stampa della lista alla fine del programma");
        System.out.println("Stampa della lista alla fine del programma");
        magazzino.stampaCode();

        Log.writeLog("Stampa degli ordini con i tempi impiegati");
        System.out.println("Stampa degli ordini con i tempi impiegati");
        magazzino.stampaListeTempi();

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
        waitForEnter();

    } //end-printFinalPageList

    public static void waitForEnter() {
        System.out.println("La simulazione è termiata,premi Invio per uscire...");
        Utility.ReadLine();
    }//end-waitForEnter
}
