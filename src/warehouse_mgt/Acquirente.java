package warehouse_mgt;

import java.util.Random;

public class Acquirente extends Thread{
//singolo thread
    //tipologia acquirente = false -> standard = true -> prime
    public boolean tipo_acquirente;

    public void Acquirente(){
        tipo_acquirente = RandomlyAssign_TipoAcquirente();
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

        Magazzino.accedi_magazzino();
        Magazzino.effettuaOrdine(num_pacchi);
        Magazzino.rilascia_magazzino();
    }

    private void RandomlyWait() {
        Random rnd = new Random();
        int random_waitingSeconds = rnd.nextInt(50);

        try {
            sleep(random_waitingSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}