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
                    reabastecerEstoqueInteligente();
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

    private static void inicializarEstoque() {
        Random random = new Random();
        int quantidadeA = random.nextInt(51) + 100;
        int quantidadeB = random.nextInt(51) + 50;

        estoque.put("ProdutoA", new Produto("ProdutoA", quantidadeA, 10));
        estoque.put("ProdutoB", new Produto("ProdutoB", quantidadeB, 20));

        System.out.println("Estoque inicializado com " + quantidadeA + " unidades de ProdutoA e " + quantidadeB + " unidades de ProdutoB.");
    }

    private static void reabastecerEstoqueInteligente() {
        int quantidadeBase = 20;

        Produto produtoA = estoque.get("ProdutoA");
        Produto produtoB = estoque.get("ProdutoB");

        int vendasA = produtoA.getVendas();
        int vendasB = produtoB.getVendas();

        int quantidadeReabastecidaA = quantidadeBase + (vendasA / 2);
        int quantidadeReabastecidaB = quantidadeBase + (vendasB / 2);

        produtoA.reabastecer(quantidadeReabastecidaA);
        produtoB.reabastecer(quantidadeReabastecidaB);

        System.out.println("Reabastecendo estoque: Adicionados " + quantidadeReabastecidaA + " unidades de ProdutoA e " + quantidadeReabastecidaB + " de ProdutoB.");

        produtoA.resetarVendas();
        produtoB.resetarVendas();
    }

    private static void reprocessarPedidosPendentes() {
        while (!filaDeEspera.isEmpty()) {
            try {
                Pedido pedido = filaDeEspera.poll();
                if (pedido != null) {
                    processarPedidoReprocessado(pedido);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void processarPedidoReprocessado(Pedido pedido) {
        int valorPedido = 0;
        boolean pedidoProcessado = true;

        for (Item item : pedido.getItens()) {
            Produto produto = estoque.get(item.getNomeProduto());
            if (produto != null && produto.retirar(item.getQuantidade())) {
                valorPedido += produto.getPreco() * item.getQuantidade();
            } else {
                pedidoProcessado = false;
                break;
            }
        }

        if (pedidoProcessado) {
            pedidosProcessados.incrementAndGet();
            valorTotalVendas.addAndGet(valorPedido);
            System.out.println("Pedido pendente do Cliente " + pedido.getClienteId() + " foi processado.");
        } else {
            pedidosRejeitados.incrementAndGet();
            filaDeEspera.offer(pedido);
            System.out.println("Pedido pendente do Cliente " + pedido.getClienteId() + " foi colocado de volta na fila de espera (falta de estoque).");
        }
    }

    private static void gerarRelatorio() {
        System.out.println("Relatório de Vendas:");
        System.out.println("Pedidos processados: " + pedidosProcessados.get());
        System.out.println("Pedidos rejeitados: " + pedidosRejeitados.get());
        System.out.println("Valor total das vendas: R$" + valorTotalVendas.get());
    }
}