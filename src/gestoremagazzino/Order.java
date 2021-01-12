package gestoremagazzino;

public class Order {

    private final int num_pacchi_richiesti;
    private final Acquirente acquirente_ordine;
    private static Integer orderNos = 0;
    private final Integer numero_ordine;
    private long tempo_impiegato = 0;

    public Order(Acquirente acquirente_ordine, int num_pacchi){
        this.acquirente_ordine = acquirente_ordine;
        this.num_pacchi_richiesti = num_pacchi;
        this.numero_ordine = (orderNos += 1);
    }

    public int getNum_pacchi_richiesti(){
        return num_pacchi_richiesti;
    }

    public Integer getNumero_ordine() { return this.numero_ordine; }

    public Acquirente getAcquirente_ordine() {
        return acquirente_ordine;
    }

    public long getTempo_impiegato(){
        return this.tempo_impiegato;
    }
    public void setTempo_impiegato(long tempo){
        this.tempo_impiegato = tempo;
    }
}
