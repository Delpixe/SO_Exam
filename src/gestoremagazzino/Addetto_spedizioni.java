package gestoremagazzino;

//implementa l'addetto alle spedizioni
public class Addetto_spedizioni extends Thread{
    private final Magazzino myMagazzino;

    public Addetto_spedizioni(int num_addetto,Magazzino mag)
    {
        super("Addetto_" + num_addetto);
        this.myMagazzino = mag;
    }//end costruttore

    @Override
    public void run() {
        boolean isAlive = true;

        Log.writeLog( super.getName() + " Incomincia a lavorare");
        while(isAlive){
            try{
                this.myMagazzino.gestisciOrdine(this);
            }catch(InterruptedException e){
                //System.out.println("Interrupt: "+e);
                isAlive = false;
            }
        }
        Log.writeLog( super.getName() + " termina...");
    }// end metodo run()

}// end classe Addetto_spedizioni
