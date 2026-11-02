import java.util.function.Function;

public class QuickSort {

	/**
	 * Ordena dados[0..quantidade-1] em ordem crescente de chave, usando quicksort.
	 * @param dados Vetor a ser ordenado
	 * @param quantidade Quantidade de elementos válidos no vetor
	 * @param extratorDeChave Função que extrai a chave de cada elemento
	 */
	public static <T, K extends Comparable<K>> void ordenar(T[] dados, int quantidade, Function<T, K> extratorDeChave) {
		quicksort(dados, 0, quantidade - 1, extratorDeChave);
	}

	private static <T, K extends Comparable<K>> void quicksort(T[] dados, int inicio, int fim, Function<T, K> extratorDeChave) {

		if (inicio < fim) {
			K pivo = medianaDeTres(extratorDeChave.apply(dados[inicio]), extratorDeChave.apply(dados[inicio + (fim - inicio) / 2]), extratorDeChave.apply(dados[fim]));
			int esquerda = inicio;
			int direita = fim;

			while (esquerda <= direita) {
				while (extratorDeChave.apply(dados[esquerda]).compareTo(pivo) < 0) {
					esquerda++;
				}
				while (extratorDeChave.apply(dados[direita]).compareTo(pivo) > 0) {
					direita--;
				}
				if (esquerda <= direita) {
					trocar(dados, esquerda, direita);
					esquerda++;
					direita--;
				}
			}

			quicksort(dados, inicio, direita, extratorDeChave);
			quicksort(dados, esquerda, fim, extratorDeChave);
		}
	}

	/** Retorna a chave intermediária, evitando o pior caso do quicksort em vetores já ordenados. */
	private static <K extends Comparable<K>> K medianaDeTres(K a, K b, K c) {

		if (a.compareTo(b) > 0) {
			K temporario = a;
			a = b;
			b = temporario;
		}
		if (b.compareTo(c) <= 0) {
			return b;
		}
		return (a.compareTo(c) > 0) ? a : c;
	}

	private static <T> void trocar(T[] dados, int i, int j) {

		T temporario = dados[i];
		dados[i] = dados[j];
		dados[j] = temporario;
	}
}
