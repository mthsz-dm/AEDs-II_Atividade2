import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public class Lista<E> {
    private Celula<E> inicio;
    private Celula<E> fim;
    private int tamanho;

    public Lista() {
        Celula<E> sentinela = new Celula<E>();
        inicio = sentinela;
        fim = sentinela;
        tamanho = 0;
    }

    public boolean vazia() {
        return inicio == fim;
    }

    public int tamanho() {
        return tamanho;
    }

    /** Retorna a célula anterior à posição informada. A posição deve ser validada por quem chama. */
    private Celula<E> celulaAnterior(int posicao) {
        Celula<E> anterior = inicio;

        for (int i = 0; i < posicao; i++) {
            anterior = anterior.getProximo();
        }
        return anterior;
    }

    private void validarPosicao(int posicao, int limite) {
        if (posicao < 0 || posicao > limite) {
            throw new IndexOutOfBoundsException("Posição inválida: " + posicao);
        }
    }

    /**
     * Insere um item na posição informada, deslocando os itens seguintes.
     * @param item Item a ser inserido.
     * @param posicao Posição do novo item, de 0 até tamanho().
     * @throws IndexOutOfBoundsException se a posição for inválida.
     */
    public void inserir(E item, int posicao) {
        validarPosicao(posicao, tamanho);

        Celula<E> anterior = celulaAnterior(posicao);
        Celula<E> nova = new Celula<E>(item, anterior.getProximo());

        anterior.setProximo(nova);
        if (anterior == fim) {
            fim = nova;
        }
        tamanho++;
    }

    public void inserirInicio(E item) {
        inserir(item, 0);
    }

    public void inserirFinal(E item) {
        Celula<E> nova = new Celula<E>(item);

        fim.setProximo(nova);
        fim = nova;
        tamanho++;
    }

    /**
     * Remove e retorna o item da posição informada.
     * @param posicao Posição do item, de 0 até tamanho() - 1.
     * @return O item removido.
     * @throws NoSuchElementException se a lista estiver vazia.
     * @throws IndexOutOfBoundsException se a posição for inválida.
     */
    public E remover(int posicao) {
        if (vazia()) {
            throw new NoSuchElementException("Não há nenhum item na lista!");
        }
        validarPosicao(posicao, tamanho - 1);

        Celula<E> anterior = celulaAnterior(posicao);
        Celula<E> removida = anterior.getProximo();

        anterior.setProximo(removida.getProximo());
        if (removida == fim) {
            fim = anterior;
        }
        tamanho--;
        return removida.getItem();
    }

    public E removerInicio() {
        return remover(0);
    }

    public E removerFinal() {
        return remover(tamanho - 1);
    }

    /**
     * Retorna, sem remover, o item da posição informada.
     * Em uma lista encadeada, o custo é proporcional à posição: evite chamar este método dentro de laços longos.
     * @throws IndexOutOfBoundsException se a posição for inválida.
     */
    public E obter(int posicao) {
        validarPosicao(posicao, tamanho - 1);
        return celulaAnterior(posicao).getProximo().getItem();
    }

    /** Retorna o primeiro item que satisfaz a condição, ou null se nenhum satisfizer. */
    public E localizar(Predicate<E> condicional) {
        Celula<E> atual = inicio.getProximo();

        while (atual != null) {
            if (condicional.test(atual.getItem())) {
                return atual.getItem();
            }
            atual = atual.getProximo();
        }
        return null;
    }

    /** Calcula o valor médio de um atributo dos primeiros elementos da lista, ou 0.0 se não houver elementos a considerar. */
    public double calcularValorMedio(Function<E, Double> extrator, int quantidade) {
        if (vazia() || quantidade <= 0) {
            return 0.0;
        }
        double soma = 0.0;
        int cont = 0;

        Celula<E> atual = inicio.getProximo();

        while (atual != null && cont < quantidade) {
            soma += extrator.apply(atual.getItem());
            cont++;
            atual = atual.getProximo();
        }

        return soma / cont;
    }

    /** Retorna uma nova lista com os elementos que satisfazem a condição, dentre os primeiros elementos desta lista. */
    public Lista<E> filtrar(Predicate<E> condicional, int quantidade) {
        Lista<E> nova = new Lista<E>();
        Celula<E> atual = inicio.getProximo();
        int cont = 0;

        while (atual != null && cont < quantidade) {
            if (condicional.test(atual.getItem())) {
                nova.inserirFinal(atual.getItem());
            }
            atual = atual.getProximo();
            cont++;
        }

        return nova;
    }

    /**
     * Retorna uma nova lista com os itens a partir de uma posição, na mesma ordem. Esta lista não é alterada.
     * @param posicaoInicial Posição do primeiro item copiado.
     * @param quantidade Quantidade de itens copiados.
     * @throws IllegalArgumentException se a lista não contém esse trecho.
     */
    public Lista<E> subLista(int posicaoInicial, int quantidade) {
        if (posicaoInicial < 0 || quantidade < 0 || posicaoInicial + quantidade > tamanho) {
            throw new IllegalArgumentException("A lista não contém o trecho especificado.");
        }
        Lista<E> nova = new Lista<E>();
        Celula<E> atual = celulaAnterior(posicaoInicial).getProximo();

        for (int i = 0; i < quantidade; i++) {
            nova.inserirFinal(atual.getItem());
            atual = atual.getProximo();
        }

        return nova;
    }

    /**
     * Copia os itens para um vetor, permitindo usar a lista com Buscador e Ordenacao.
     * @param destino Vetor com pelo menos tamanho() posições.
     * @return A quantidade de itens copiados.
     * @throws IllegalArgumentException se o vetor não tiver espaço suficiente.
     */
    public int copiarPara(E[] destino) {
        if (destino.length < tamanho) {
            throw new IllegalArgumentException("O vetor não tem espaço para todos os itens.");
        }
        Celula<E> atual = inicio.getProximo();

        for (int i = 0; i < tamanho; i++) {
            destino[i] = atual.getItem();
            atual = atual.getProximo();
        }

        return tamanho;
    }

    /** Retorna os itens da lista, um por linha, na ordem em que estão armazenados. */
    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder();
        Celula<E> atual = inicio.getProximo();

        while (atual != null) {
            texto.append(atual.getItem()).append("\n");
            atual = atual.getProximo();
        }

        return texto.toString();
    }
}
