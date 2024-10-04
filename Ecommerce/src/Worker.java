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

    public Worker(int workerId, BlockingQueue<Pedido> filaDePedidos, BlockingQueue<Pedido> filaDeEspera, ConcurrentHashMap<String, Produto> estoque, AtomicInteger pedidosProcessados, AtomicInteger pedidosRejeitados, AtomicInteger valorTotalVendas) {
        this.workerId = workerId;
        this.filaDePedidos = filaDePedidos;
        this.filaDeEspera = filaDeEspera;
        this.estoque = estoque;
        this.pedidosProcessados = pedidosProcessados;
        this.pedidosRejeitados = pedidosRejeitados;
        this.valorTotalVendas = valorTotalVendas;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Pedido pedido = filaDePedidos.take();
                processarPedido(pedido);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processarPedido(Pedido pedido) {
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
            System.out.println("Worker " + workerId + " processou o pedido do Cliente " + pedido.getClienteId() + " Valor: R$" + valorPedido);
        } else {
            pedidosRejeitados.incrementAndGet();
            filaDeEspera.offer(pedido);
            System.out.println("Worker " + workerId + " colocou o pedido do Cliente " + pedido.getClienteId() + " na fila de espera (falta de estoque).");
        }
    }
}
