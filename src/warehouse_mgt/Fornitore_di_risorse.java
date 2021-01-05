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
        super(name);
        fTime = time;
        keepRefilling = true;

        System.out.println("Fornitore " + name + " creato con tempo di ricarica di " + time + " millisecondi.");
        Log.writeLog("Fornitore " + name + " creato con tempo di ricarica di " + time + " millisecondi.");
    }

    @Override
    public void run() {
        Random rnd = new Random();
        int cm_nastro = 750;//rnd.nextInt(5000);
        int nscatole = 5;//rnd.nextInt(100);

        try{
            if (fTime != 0)
                sleep(fTime);

            if ((cm_nastro != 0) || (nscatole != 0))
            {
                Magazzino.depositaRisorse(this,cm_nastro,nscatole);
            }
        }catch(InterruptedException e){
            System.out.println(e.toString());
            Log.writeLog(e.toString());
        }finally {
            //commentato per test, riempie solo una volta il magazzino così
            //if (keepRefilling)
                //run();
        }
    }

    public void stopRefilling()
    {
        keepRefilling = false;
    }
}
