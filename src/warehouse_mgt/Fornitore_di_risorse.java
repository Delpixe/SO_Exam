/*
Quando il fornitore deposita nuove risorse ne notifica la presenza a tutti gli addetti
alle spedizioni che si dovessero trovare in attesa di risorse.
*/
package warehouse_mgt;

import java.util.Random;

public class Fornitore_di_risorse extends Thread{

    private final int fTime;
    private boolean keepRefilling;

    public Fornitore_di_risorse(String name,int time)
    {
        fTime = time;
        keepRefilling = true;

        System.out.println("Fornitore " + name + " creato con tempo di ricarica di " + time + " minuti.");
        Log.writeLog("Fornitore " + name + " creato con tempo di ricarica di " + time + " minuti.");
    }

    @Override
    public void run() {
        Random rnd = new Random();
        int cm_nastro = rnd.nextInt(5000);
        int nscatole = rnd.nextInt(100);

        try{
            if (fTime != 0)
                sleep(fTime);

            if ((cm_nastro != 0) || (nscatole != 0))
            {
                Magazzino.accedi_magazzino();
                Magazzino.depositaRisorse(cm_nastro,nscatole);
                Magazzino.rilascia_magazzino();
            }
        }catch(InterruptedException e){
            System.out.println(e.toString());
            Log.writeLog(e.toString());
        }finally {
            if (keepRefilling)
                run();
        }
    }

    public void stop_Refilling()
    {
        keepRefilling = false;
    }
}
