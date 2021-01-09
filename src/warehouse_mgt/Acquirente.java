package warehouse_mgt;

import java.util.Random;

public class Acquirente extends Thread{
//singolo thread
    //tipologia acquirente = false -> standard = true -> prime
    public boolean isPrime;
    private boolean inAttesa;
    private int numOrdinidaEvadere = 10;

    public Acquirente(int num_Acquirente){
        super("Acquirente_" + num_Acquirente);
        this.isPrime = Randomly_IsPrime();

        if (this.isPrime)
            Log.writeLog(this.getName() + " creato acquirente di tipo Prime");
        else
            Log.writeLog(this.getName() + " creato acquirente di tipo Standard");
    }

    private boolean Randomly_IsPrime() {
        Random rnd = new Random();
        int random_Number = rnd.nextInt(10) + 1;
        boolean prime = false;

        switch (random_Number){
            case 1,2,3:
                prime = true;
                break;
            case 4,5,6,7,8,9,10:
                prime = false;
                break;
        }
        return prime;
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

    public void resta_in_attesa() {
        this.inAttesa = true;
    }

    public void riprendi() {
        this.inAttesa = false;
    }

    @Override
    public void run() {
        //for (int num_Ordine = 0;num_Ordine < 10;num_Ordine++)
        int num_ordine = 0;

        while(num_ordine < this.numOrdinidaEvadere)
        {
            while(!this.inAttesa){
                num_ordine++;
                RandomlyWait();
                Place_Single_Order();
            }
        }
        Log.writeLog("Acquirente " + this.getName() + " ha finito di emettere ordini.");
    }
}
