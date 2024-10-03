import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

public class EcommerceSystem {

    private static BlockingQueue<Pedido> filaDePedidos = new ArrayBlockingQueue<>(100);
    private static BlockingQueue<Pedido> filaDeEspera = new LinkedBlockingQueue<>();
    private static ConcurrentHashMap<String, Produto> estoque = new ConcurrentHashMap<>();
    private static AtomicInteger pedidosProcessados = new AtomicInteger(0);
    private static AtomicInteger pedidosRejeitados = new AtomicInteger(0);
    private static AtomicInteger valorTotalVendas = new AtomicInteger(0);
    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
}