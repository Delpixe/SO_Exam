package warehouse_mgt;

import java.util.Random;

public class Acquirente extends Thread{
//singolo thread
    //tipologia acquirente = false -> standard = true -> prime
    public boolean Prime;

    public Acquirente(int num_Acquirente){
        super("Acquirente_" + num_Acquirente);
        Prime = RandomlyAssign_TipoAcquirente();

        if (Prime)
            Log.writeLog(this.getName() + " creato acquirente di tipo Prime");
        else
            Log.writeLog(this.getName() + " creato acquirente di tipo Standard");
    }

    private boolean RandomlyAssign_TipoAcquirente() {
        Random rnd = new Random();
        int random_Number = rnd.nextInt(10) + 1;
        boolean isPrime = false;

        switch (random_Number){
            case 1,2,3:
                isPrime = true;
            case 4,5,6,7,8,9,10:
                isPrime = false;
        }
        return isPrime;
    }

    public void Place_Orders(){
        for (int num_Ordine = 0;num_Ordine < 10;num_Ordine++)
        {
            RandomlyWait();
            Place_Single_Order();
        }
    }

    private void Place_Single_Order() {
        Random rnd = new Random();
        int num_pacchi = rnd.nextInt(4)+1;

        Magazzino.effettuaOrdine(this,num_pacchi);
    }

    private void RandomlyWait() {
        Random rnd = new Random();
        int random_waitingSeconds = rnd.nextInt(5000);

        try {
            Log.writeLog("Acquirente " + this.getName() + " sta piazzando l'ordine, ci vorranno " + random_waitingSeconds + " millisecondi");
            sleep(random_waitingSeconds);
        } catch (InterruptedException e) {
            Log.writeLog("[ERRORE] " + e.toString());
        }
    }

    @Override
    public void run() {
        Place_Orders();
    }
}
