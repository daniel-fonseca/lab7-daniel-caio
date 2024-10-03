import java.util.List;

public class Pedido {
    private final List<Item> itens;
    private final int clienteId;

    public Pedido(int clienteId, List<Item> itens) {
        this.clienteId = clienteId;
        this.itens = itens;
    }
}