package warehouse_mgt;

public class Order {

    private int num_pacchi_richiesti;
    private Acquirente acquirente;

    public Order(Acquirente acquirente_ordine, int num_pacchi){
        this.acquirente = acquirente_ordine;
        this.num_pacchi_richiesti = num_pacchi;
    }

    public int getNum_pacchi_richiesti(){
        return num_pacchi_richiesti;
    }

    public String getNomeAcquirente() {
        return(acquirente.getName());
    }
}
