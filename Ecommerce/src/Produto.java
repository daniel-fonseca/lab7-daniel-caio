import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Produto {
    private final String nome;
    private final int preco;
    private int quantidade;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Produto(String nome, int quantidade, int preco) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.preco = preco;
    }

    public boolean retirar(int quantidadeDesejada) {
        lock.writeLock().lock();
        try {
            if (quantidade >= quantidadeDesejada) {
                quantidade -= quantidadeDesejada;
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
}
