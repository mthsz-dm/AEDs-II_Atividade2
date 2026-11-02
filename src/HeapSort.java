import java.util.function.Function;

public class HeapSort {

	/**
	 * Ordena dados[0..quantidade-1] em ordem crescente de chave, usando heapsort.
	 * @param dados Vetor a ser ordenado
	 * @param quantidade Quantidade de elementos válidos no vetor
	 * @param extratorDeChave Função que extrai a chave de cada elemento
	 */
	public static <T, K extends Comparable<K>> void ordenar(T[] dados, int quantidade, Function<T, K> extratorDeChave) {

		for (int i = quantidade / 2 - 1; i >= 0; i--) {
			descer(dados, i, quantidade, extratorDeChave);
		}

		for (int fim = quantidade - 1; fim > 0; fim--) {
			trocar(dados, 0, fim);
			descer(dados, 0, fim, extratorDeChave);
		}
	}

	/** Desce o elemento da posição informada até restaurar o heap máximo em dados[0..tamanhoHeap-1]. */
	private static <T, K extends Comparable<K>> void descer(T[] dados, int posicao, int tamanhoHeap, Function<T, K> extratorDeChave) {

		int atual = posicao;
		int filho = 2 * atual + 1;

		while (filho < tamanhoHeap) {
			if ((filho + 1 < tamanhoHeap) && (extratorDeChave.apply(dados[filho + 1]).compareTo(extratorDeChave.apply(dados[filho])) > 0)) {
				filho++;
			}
			if (extratorDeChave.apply(dados[atual]).compareTo(extratorDeChave.apply(dados[filho])) >= 0) {
				return;
			}
			trocar(dados, atual, filho);
			atual = filho;
			filho = 2 * atual + 1;
		}
	}

	private static <T> void trocar(T[] dados, int i, int j) {

		T temporario = dados[i];
		dados[i] = dados[j];
		dados[j] = temporario;
	}
}
