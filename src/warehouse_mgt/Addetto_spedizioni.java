/*
Ogni addetto alle spedizioni agisce indipendentemente e contemporaneamente agli altri gestendo
un singolo ordine di acquisto alla volta, secondo la politica di gestione descritta in seguito.

Quando non ci sono richieste da servire, ogni addetto alle spedizioni rimane in attesa
sospendendo la propria esecuzione.

Ogni addetto, per processare un ordine di acquisto, avrà bisogno di accaparrarsi un quantitativo
di risorse così determinato: 1 scatola e 50cm di nastro adesivo per ogni pacco contenuto
nell’ordine. Una volta ottenute le risorse, l’addetto alle spedizioni inizia ad inscatolare i pacchi
impiegando un tempo proporzionale al numero di pacchi richiesti secondo la relazione:
tempo=pacchi*5 espresso in millisecondi. Appena gestito un ordine, l’addetto alle spedizioni sarà
in grado di gestirne un altro oppure, in mancanza di ordini, tornerà a riposo. Nel caso in cui,
l’addetto alle spedizioni non trovi sufficienti risorse per gestire l’intero ordine sottomesso da un
acquirente, dovrà attendere che il fornitore di risorse deposti nuove scatole e nuovo nastro adesivo
nel magazzino.
*/
package warehouse_mgt;

public class Addetto_spedizioni extends Thread{

    @Override
    public void run() {
        Magazzino.accedi_magazzino();
        Magazzino.gestisciOrdine();
        Magazzino.rilascia_magazzino();
    }
}
