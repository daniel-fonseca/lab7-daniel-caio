import java.util.*;
import java.util.concurrent.BlockingQueue;

public class Cliente implements Runnable {
    private final int clienteId;
    private final BlockingQueue<Pedido> filaDePedidos;

    public Cliente(int clienteId, BlockingQueue<Pedido> filaDePedidos) {
        this.clienteId = clienteId;
        this.filaDePedidos = filaDePedidos;
    }
}