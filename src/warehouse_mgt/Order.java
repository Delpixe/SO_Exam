package warehouse_mgt;

public class Order {

    private int num_pacchi_richiesti;
    private Acquirente acquirente_ordine;
    private static Integer orderNos = 0;
    private Integer numero_ordine;

    public Order(Acquirente acquirente_ordine, int num_pacchi){
        this.acquirente_ordine = acquirente_ordine;
        this.num_pacchi_richiesti = num_pacchi;
        this.numero_ordine = (orderNos += 1);
    }

    public int getNum_pacchi_richiesti(){
        return num_pacchi_richiesti;
    }

    public String getNomeAcquirente() {
        return(this.acquirente_ordine.getName());
    }

    public Integer getNumero_ordine() { return this.numero_ordine; }

    public Integer getOrderNos() { return orderNos; }

    public Acquirente getAcquirente_ordine() {
        return acquirente_ordine;
    }
}
