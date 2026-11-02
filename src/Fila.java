import java.util.function.Function;
import java.util.function.Predicate;

public class Fila<E> {
    private Celula<E> inicio;
    private Celula<E> fim;

    public Fila() {
        Celula<E> sentinela = new Celula<E>();
        inicio = sentinela;
        fim = sentinela;
    }

    public boolean vazia() {
        return inicio == fim;
    }

    public void emfileirar(E item) {
        Celula<E> nova = new Celula<E>(item);
        fim.setProximo(nova);
        fim = nova;
    }

    public E desemfileirar() {
        if (vazia()) {
            throw new IllegalArgumentException("Vazio");
        }
        inicio = inicio.getProximo();
        E item = inicio.getItem();
        return item;
    }

    public double calcularValorMedio(Function<E, Double> extrator, int quantidade) {
        if (vazia() || quantidade <= 0) {
            return 0.0;
        }
        double soma = 0.0;
        int cont = 0;

        Celula<E> atual = inicio.getProximo();

        while (atual != null && cont < quantidade) {
            E elem = atual.getItem();
            soma += extrator.apply(elem);
            cont++;
            atual = atual.getProximo();
        }

        return soma / cont;
    }

    public Fila<E> filtrar(Predicate<E> condicional, int quantidade){
        Fila<E> nova = new Fila<E>();
        if (vazia() || quantidade <= 0) {
            return nova;
        }
        Celula<E> atual = inicio.getProximo();
        int cont = 0;

        while(atual != null && cont < quantidade){
            E elem = atual.getItem();
            if(condicional.test(elem)){
                nova.emfileirar(elem);
            }
            atual = atual.getProximo();
            cont++;
        }
        
        return nova;
    }

}