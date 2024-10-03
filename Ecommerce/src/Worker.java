import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable {
    private final int workerId;
    private final BlockingQueue<Pedido> filaDePedidos;
    private final BlockingQueue<Pedido> filaDeEspera;
    private final ConcurrentHashMap<String, Produto> estoque;
    private final AtomicInteger pedidosProcessados;
    private final AtomicInteger pedidosRejeitados;
    private final AtomicInteger valorTotalVendas;

    public Worker(int workerId, BlockingQueue<Pedido> filaDePedidos, BlockingQueue<Pedido> filaDeEspera,
                  ConcurrentHashMap<String, Produto> estoque, AtomicInteger pedidosProcessados,
                  AtomicInteger pedidosRejeitados, AtomicInteger valorTotalVendas) {
        this.workerId = workerId;
        this.filaDePedidos = filaDePedidos;
        this.filaDeEspera = filaDeEspera;
        this.estoque = estoque;
        this.pedidosProcessados = pedidosProcessados;
        this.pedidosRejeitados = pedidosRejeitados;
        this.valorTotalVendas = valorTotalVendas;
    }
}