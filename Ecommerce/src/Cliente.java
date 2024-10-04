import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Cliente implements Runnable {
    private final int clienteId;
    private final BlockingQueue<Pedido> filaDePedidos;
    private final ConcurrentHashMap<String, Produto> estoque;

    public Cliente(int clienteId, BlockingQueue<Pedido> filaDePedidos, ConcurrentHashMap<String, Produto> estoque) {
        this.clienteId = clienteId;
        this.filaDePedidos = filaDePedidos;
        this.estoque = estoque;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<Item> itens = new ArrayList<>();
                if (random.nextBoolean()) {
                    itens.add(new Item("ProdutoA", random.nextInt(5) + 1));
                }
                if (random.nextBoolean()) {
                    itens.add(new Item("ProdutoB", random.nextInt(5) + 1));
                }

                if (itens.isEmpty()) {
                    itens.add(new Item("ProdutoA", random.nextInt(5) + 1));
                }

                System.out.println("Cliente " + clienteId + " enviou um pedido de " + itens.stream().map(item -> item.getNomeProduto() + ": " + item.getQuantidade()).reduce((a, b) -> a + ", " + b).orElse("Nenhum produto"));
                
                Pedido pedido = new Pedido(clienteId, itens);
                filaDePedidos.put(pedido);
                

                Thread.sleep(random.nextInt(2000) + 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
