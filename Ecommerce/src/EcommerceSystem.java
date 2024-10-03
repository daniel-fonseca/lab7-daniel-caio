import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

public class ECommerceSystem {

    private static BlockingQueue<Pedido> filaDePedidos = new ArrayBlockingQueue<>(100);
    private static BlockingQueue<Pedido> filaDeEspera = new LinkedBlockingQueue<>();
    private static ConcurrentHashMap<String, Produto> estoque = new ConcurrentHashMap<>();
    private static AtomicInteger pedidosProcessados = new AtomicInteger(0);
    private static AtomicInteger pedidosRejeitados = new AtomicInteger(0);
    private static AtomicInteger valorTotalVendas = new AtomicInteger(0);
    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        inicializarEstoque();


        for (int i = 0; i < 5; i++) {
            executor.submit(new Cliente(i + 1, filaDePedidos, estoque));
        }
        for (int i = 0; i < 3; i++) {
            executor.submit(new Worker(i + 1, filaDePedidos, filaDeEspera, estoque, pedidosProcessados, pedidosRejeitados, valorTotalVendas));
        }

    }