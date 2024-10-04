import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Produto {
    private final String nome;
    private final int preco;
    private int quantidade;
    private int vendas;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Produto(String nome, int quantidade, int preco) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.preco = preco;
        this.vendas = 0;
    }

    public boolean retirar(int quantidadeDesejada) {
        lock.writeLock().lock();
        try {
            if (quantidade >= quantidadeDesejada) {
                quantidade -= quantidadeDesejada;
                vendas += quantidadeDesejada;
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void reabastecer(int quantidadeAdicional) {
        lock.writeLock().lock();
        try {
            quantidade += quantidadeAdicional;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getPreco() {
        return preco;
    }

    public int getVendas() {
        return vendas;
    }

    public void resetarVendas() {
        this.vendas = 0;
    }
}