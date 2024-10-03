import java.util.*;
import java.util.concurrent.BlockingQueue;

public class Cliente implements Runnable {
    private final int clienteId;
    private final BlockingQueue<Pedido> filaDePedidos;

    public Cliente(int clienteId, BlockingQueue<Pedido> filaDePedidos) {
        this.clienteId = clienteId;
        this.filaDePedidos = filaDePedidos;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<Item> itens = new ArrayList<>();
                int quantidadeA = random.nextInt(5) + 1;
                int quantidadeB = random.nextInt(5) + 1;

                boolean adicionouProdutoA = random.nextBoolean();
                boolean adicionouProdutoB = random.nextBoolean();

                if (adicionouProdutoA) {
                    itens.add(new Item("ProdutoA", quantidadeA));
                }
                if (adicionouProdutoB) {
                    itens.add(new Item("ProdutoB", quantidadeB));
                }
                if (!adicionouProdutoA && !adicionouProdutoB) {
                    itens.add(new Item("ProdutoA", quantidadeA));
                }

                Pedido pedido = new Pedido(clienteId, itens);
                filaDePedidos.put(pedido);

                System.out.println("Cliente " + clienteId + " enviou um pedido de " +
                        (itens.stream().map(item -> item.getNomeProduto() + ": " + item.getQuantidade())
                                .reduce((a, b) -> a + ", " + b).orElse("Nenhum produto")));

                Thread.sleep(random.nextInt(2000) + 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}