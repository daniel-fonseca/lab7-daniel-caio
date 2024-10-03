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
}