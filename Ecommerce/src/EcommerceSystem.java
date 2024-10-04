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


        executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(10000);
                    reabastecerEstoque();
                    reprocessarPedidosPendentes();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });


        executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(30000);
                    gerarRelatorio();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Iniciando o shutdown do sistema...");
        executor.shutdown();

        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        System.out.println("Sistema encerrado.");


}
