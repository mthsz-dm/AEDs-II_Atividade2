import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListaDuplamenteEncadeada<E> {

    /** Célula com referências para a célula anterior e para a próxima. */
    private static class CelulaDupla<E> {
        private final E item;
        private CelulaDupla<E> anterior;
        private CelulaDupla<E> proximo;

        private CelulaDupla(E item) {
            this.item = item;
        }
    }

    private CelulaDupla<E> inicio;
    private CelulaDupla<E> fim;
    private int tamanho;

    public ListaDuplamenteEncadeada() {
        CelulaDupla<E> sentinela = new CelulaDupla<E>(null);
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

    /** Retorna a célula da posição informada, percorrendo a partir da extremidade mais próxima. A posição deve ser validada por quem chama. */
    private CelulaDupla<E> celulaNaPosicao(int posicao) {
        CelulaDupla<E> atual;

        if (posicao < tamanho / 2) {
            atual = inicio.proximo;
            for (int i = 0; i < posicao; i++) {
                atual = atual.proximo;
            }
        } else {
            atual = fim;
            for (int i = tamanho - 1; i > posicao; i--) {
                atual = atual.anterior;
            }
        }
        return atual;
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

        if (posicao == tamanho) {
            inserirFinal(item);
            return;
        }

        CelulaDupla<E> seguinte = celulaNaPosicao(posicao);
        CelulaDupla<E> nova = new CelulaDupla<E>(item);

        nova.anterior = seguinte.anterior;
        nova.proximo = seguinte;
        seguinte.anterior.proximo = nova;
        seguinte.anterior = nova;
        tamanho++;
    }

    public void inserirInicio(E item) {
        inserir(item, 0);
    }

    public void inserirFinal(E item) {
        CelulaDupla<E> nova = new CelulaDupla<E>(item);

        nova.anterior = fim;
        fim.proximo = nova;
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

        CelulaDupla<E> removida = celulaNaPosicao(posicao);

        removida.anterior.proximo = removida.proximo;
        if (removida == fim) {
            fim = removida.anterior;
        } else {
            removida.proximo.anterior = removida.anterior;
        }
        tamanho--;
        return removida.item;
    }

    public E removerInicio() {
        return remover(0);
    }

    /** Remove o último item em tempo constante, pois a célula conhece a anterior. */
    public E removerFinal() {
        return remover(tamanho - 1);
    }

    /**
     * Retorna, sem remover, o item da posição informada.
     * @throws IndexOutOfBoundsException se a posição for inválida.
     */
    public E obter(int posicao) {
        validarPosicao(posicao, tamanho - 1);
        return celulaNaPosicao(posicao).item;
    }

    /** Retorna o primeiro item que satisfaz a condição, ou null se nenhum satisfizer. */
    public E localizar(Predicate<E> condicional) {
        CelulaDupla<E> atual = inicio.proximo;

        while (atual != null) {
            if (condicional.test(atual.item)) {
                return atual.item;
            }
            atual = atual.proximo;
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

        CelulaDupla<E> atual = inicio.proximo;

        while (atual != null && cont < quantidade) {
            soma += extrator.apply(atual.item);
            cont++;
            atual = atual.proximo;
        }

        return soma / cont;
    }

    /** Retorna uma nova lista com os elementos que satisfazem a condição, dentre os primeiros elementos desta lista. */
    public ListaDuplamenteEncadeada<E> filtrar(Predicate<E> condicional, int quantidade) {
        ListaDuplamenteEncadeada<E> nova = new ListaDuplamenteEncadeada<E>();
        CelulaDupla<E> atual = inicio.proximo;
        int cont = 0;

        while (atual != null && cont < quantidade) {
            if (condicional.test(atual.item)) {
                nova.inserirFinal(atual.item);
            }
            atual = atual.proximo;
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
    public ListaDuplamenteEncadeada<E> subLista(int posicaoInicial, int quantidade) {
        if (posicaoInicial < 0 || quantidade < 0 || posicaoInicial + quantidade > tamanho) {
            throw new IllegalArgumentException("A lista não contém o trecho especificado.");
        }
        ListaDuplamenteEncadeada<E> nova = new ListaDuplamenteEncadeada<E>();

        if (quantidade > 0) {
            CelulaDupla<E> atual = celulaNaPosicao(posicaoInicial);

            for (int i = 0; i < quantidade; i++) {
                nova.inserirFinal(atual.item);
                atual = atual.proximo;
            }
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
        CelulaDupla<E> atual = inicio.proximo;

        for (int i = 0; i < tamanho; i++) {
            destino[i] = atual.item;
            atual = atual.proximo;
        }

        return tamanho;
    }

    /** Retorna os itens da lista, um por linha, na ordem em que estão armazenados. */
    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder();
        CelulaDupla<E> atual = inicio.proximo;

        while (atual != null) {
            texto.append(atual.item).append("\n");
            atual = atual.proximo;
        }

        return texto.toString();
    }

    /** Retorna os itens da lista, um por linha, do último para o primeiro. */
    public String toStringInvertido() {
        StringBuilder texto = new StringBuilder();
        CelulaDupla<E> atual = fim;

        while (atual != inicio) {
            texto.append(atual.item).append("\n");
            atual = atual.anterior;
        }

        return texto.toString();
    }
}
