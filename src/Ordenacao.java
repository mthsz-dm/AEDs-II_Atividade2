import java.util.function.Function;

public class Ordenacao {

	/**
	 * Ordena dados[0..quantidade-1] em ordem crescente de chave, usando mergesort.
	 * @param dados Vetor a ser ordenado
	 * @param quantidade Quantidade de elementos válidos no vetor
	 * @param extratorDeChave Função que extrai a chave de cada elemento
	 */
	public static <T, K extends Comparable<K>> void ordenar(T[] dados, int quantidade, Function<T, K> extratorDeChave) {

		@SuppressWarnings("unchecked")
		T[] auxiliar = (T[]) new Object[quantidade];

		mergesort(dados, auxiliar, 0, quantidade - 1, extratorDeChave);
	}

	private static <T, K extends Comparable<K>> void mergesort(T[] dados, T[] auxiliar, int inicio, int fim, Function<T, K> extratorDeChave) {

		if (inicio < fim) {
			int meio = inicio + (fim - inicio) / 2;
			mergesort(dados, auxiliar, inicio, meio, extratorDeChave);
			mergesort(dados, auxiliar, meio + 1, fim, extratorDeChave);
			intercalar(dados, auxiliar, inicio, meio, fim, extratorDeChave);
		}
	}

	/** Intercala os trechos ordenados dados[inicio..meio] e dados[meio+1..fim]. */
	private static <T, K extends Comparable<K>> void intercalar(T[] dados, T[] auxiliar, int inicio, int meio, int fim, Function<T, K> extratorDeChave) {

		for (int i = inicio; i <= fim; i++) {
			auxiliar[i] = dados[i];
		}

		int esquerda = inicio;
		int direita = meio + 1;

		for (int i = inicio; i <= fim; i++) {
			if (esquerda > meio) {
				dados[i] = auxiliar[direita++];
			} else if (direita > fim) {
				dados[i] = auxiliar[esquerda++];
			} else if (extratorDeChave.apply(auxiliar[esquerda]).compareTo(extratorDeChave.apply(auxiliar[direita])) <= 0) {
				dados[i] = auxiliar[esquerda++];
			} else {
				dados[i] = auxiliar[direita++];
			}
		}
	}
}
