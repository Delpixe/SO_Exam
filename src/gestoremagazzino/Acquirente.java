package gestoremagazzino;

import java.util.Random;
import java.util.concurrent.locks.Condition;

public class Acquirente extends Thread{
    private final String tipoAcquirente;
    private final int NUMORDINIDAEVADERE = 10;
    private Condition myCondition;
    private final Magazzino myMagazzino;

    public Acquirente(int num_Acquirente,Magazzino mag){
        super("Acquirente_" + num_Acquirente);
        this.myMagazzino = mag;
        this.tipoAcquirente = Randomly_IsPrime();

        Log.writeLog(super.getName() + " creato di tipo "+ tipoAcquirente);
    }

    //override della run
    @Override
    public void run() {
        int num_ordine = 0;
        Random rnd = new Random();

        while (num_ordine < this.NUMORDINIDAEVADERE) {
            int num_pacchi = rnd.nextInt(4)+1;
            num_ordine++;
            RandomlySleep();
            this.myMagazzino.effettuaOrdine(this,num_pacchi);
        }
        Log.writeLog( super.getName() + " termina...");
    }

    //Assegna il tipo acquirente tra prime e standard
    private String Randomly_IsPrime() {
        Random rnd = new Random();
        int random_Number = rnd.nextInt(10) + 1;
        String tipo = switch (random_Number) {
            case 1, 2, 3 -> "PRIME";
            default -> "STANDARD";
        };

        return tipo;
    }

    //Ritorna il tipoAcquirente
    public String getTipoAcquirente()
    {
        return(this.tipoAcquirente);
    }

    //funzione per fare una sleep random
    private void RandomlySleep() {
        Random rnd = new Random();
        int random_waitingSeconds = rnd.nextInt(5000);

        try {
            Log.writeLog(super.getName() + " sta piazzando l'ordine, ci vorranno " + random_waitingSeconds + " millisecondi");
            sleep(random_waitingSeconds);
        } catch (InterruptedException e) {
            Log.writeLog("[ERRORE] " + e.toString());
        }
    }

    //gestisco la condition di risveglio dell'acquirente
    public void setCondition(Condition c){
        this.myCondition = c;
    }
    public void sospendi() throws InterruptedException{
        this.myCondition.await();
    }
    public void risveglia() {
        this.myCondition.signal();
    }

}