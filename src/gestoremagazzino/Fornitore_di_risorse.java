/*
Quando il fornitore deposita nuove risorse ne notifica la presenza a tutti gli addetti
alle spedizioni che si dovessero trovare in attesa di risorse.
*/
package gestoremagazzino;

import java.util.Random;

public class Fornitore_di_risorse extends Thread{

    private final int fTime;
    private final Magazzino myMagazzino;

    public Fornitore_di_risorse(String name,int time_ms,Magazzino mag)
    {
        super(name);
        this.myMagazzino = mag;
        this.fTime = time_ms;

        Log.writeLog("Fornitore " + name + " creato con tempo di ricarica di " + time_ms + " millisecondi.");
    }//end costruttore

    @Override
    public void run() {
        Random rnd = new Random();
        int nscatole_rifornimento;
        int cm_nastro_rifornimento;
        boolean isAlive = true;

        while(isAlive){
            try{
                nscatole_rifornimento = rnd.nextInt(10);
                cm_nastro_rifornimento = nscatole_rifornimento * 50;

                if ((cm_nastro_rifornimento != 0) || (nscatole_rifornimento != 0))
                    this.myMagazzino.depositaRisorse(this,cm_nastro_rifornimento,nscatole_rifornimento);

                if (this.fTime != 0)
                    sleep(fTime);
            }catch(InterruptedException e){
                //Log.writeLog(e.toString());
                isAlive = false;
            }
        }
        Log.writeLog(super.getName() + " termina...");
    }//end metodo run()

}//end classe Fornitore_di_risorse
