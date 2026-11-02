import java.util.function.Function;

/**
 * Procura elementos do tipo T em um vetor pela chave do tipo K, que a função extratorDeChave extrai de cada elemento.
 * Conta uma comparação a cada chamada de compareTo entre a chave procurada e a chave de um elemento do vetor.
 */
public class Buscador<T, K extends Comparable<K>> {

	private final Function<T, K> extratorDeChave;
	private long comparacoes;

	public Buscador(Function<T, K> extratorDeChave) {
		this.extratorDeChave = extratorDeChave;
		this.comparacoes = 0;
	}

	/** Compara a chave procurada com a chave de um elemento, contabilizando a comparação. */
	private int comparar(K chave, T elemento) {
		comparacoes++;
		return chave.compareTo(extratorDeChave.apply(elemento));
	}

	/**
	 * Retorna a posição do primeiro elemento com chave igual a chave, ou -1.
	 * Não exige vetor ordenado.
	 */
	public int buscaSequencial(T[] dados, int quantidade, K chave) {

		for (int i = 0; i < quantidade; i++) {
			if (comparar(chave, dados[i]) == 0) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Retorna a posição de um elemento com chave igual a chave, ou -1.
	 * Pré-condição: dados[0..quantidade-1] em ordem crescente de chave.
	 */
	public int buscaBinaria(T[] dados, int quantidade, K chave) {

		int inicio = 0;
		int fim = quantidade - 1;

		while (inicio <= fim) {
			int meio = inicio + (fim - inicio) / 2;
			int resultado = comparar(chave, dados[meio]);

			if (resultado == 0) {
				return meio;
			} else if (resultado < 0) {
				fim = meio - 1;
			} else {
				inicio = meio + 1;
			}
		}
		return -1;
	}

	/**
	 * Retorna a primeira posição cuja chave é maior ou igual à chave informada, ou quantidade se todas forem menores.
	 * Pré-condição: dados[0..quantidade-1] em ordem crescente de chave.
	 */
	public int primeiraPosicaoMaiorOuIgual(T[] dados, int quantidade, K chave) {

		int inicio = 0;
		int fim = quantidade;

		while (inicio < fim) {
			int meio = inicio + (fim - inicio) / 2;
			int resultado = comparar(chave, dados[meio]);

			if (resultado <= 0) {
				fim = meio;
			} else {
				inicio = meio + 1;
			}
		}
		return inicio;
	}

	public long getComparacoes() {
		return comparacoes;
	}

	public void zerarComparacoes() {
		comparacoes = 0;
	}
}
