/*
Quando il fornitore deposita nuove risorse ne notifica la presenza a tutti gli addetti
alle spedizioni che si dovessero trovare in attesa di risorse.
*/
package warehouse_mgt;

import java.util.Random;

public class Fornitore_di_risorse extends Thread{

    private final int fTime;
    private boolean keepRefilling;

    public Fornitore_di_risorse(String name,int time_ms)
    {
        super(name);
        fTime = time_ms;
        keepRefilling = true;

        Log.writeLog("Fornitore " + name + " creato con tempo di ricarica di " + time_ms + " millisecondi.");
    }

    @Override
    public void run() {
        Random rnd = new Random();
        int nscatole;
        int cm_nastro;

        try{
            nscatole = rnd.nextInt(10);
            cm_nastro = nscatole * 50;

            if ((cm_nastro != 0) || (nscatole != 0))
            {
                Magazzino.depositaRisorse(this,cm_nastro,nscatole);
            }
            if (fTime != 0)
                this.sleep(fTime);
        }catch(InterruptedException e){
            System.out.println(e.toString());
            Log.writeLog(e.toString());
        }finally {
            //commentato per test, riempie solo una volta il magazzino così --> trovare il modo di farlo smettere
            if (keepRefilling)
                run();
        }
    }

    public void stopRefilling()
    {
        keepRefilling = false;
    }
}
