# AEDs II - Oficinas e Atividade 2 - Sistemas de software com Estruturas de dados lineares 
Oficinas e atividade pontuada realizada em AEDs II, tendo em vista estruturas de dados lineares em sistemas de software.

## Aluno 

* Nome completo do aluno 1

## Oficina: Busca sequencial e busca binária

### Tarefa 0

**1. Qual método de busca `localizarProduto` e `localizarProdutoDescricao` utilizam?**

Na versão original, os dois usam **busca sequencial**: percorrem `produtosCadastrados` desde o início até encontrar o produto ou chegar ao fim do vetor.

**2. Quantas comparações cada um faz, com os 7.750 produtos cadastrados?**

| Método | Melhor caso | Pior caso |
|---|---|---|
| `localizarProduto` | 1 comparação | 7.750 comparações |
| `localizarProdutoDescricao` | 1 comparação | 7.750 comparações |

* **Melhor caso:** o produto procurado é o primeiro do vetor.
* **Pior caso:** o produto procurado é o último do vetor, ou não está cadastrado.

### Tarefa 2

Os códigos são atribuídos por um contador (`ultimoID`), que começa em 10000 e é incrementado a cada produto criado. `lerProdutos` cria os produtos na ordem do arquivo e guarda cada um na posição `i` do vetor, com `i` crescente. Assim, o produto da posição `i` tem o código `10000 + i`, e cada código é maior que o anterior. Portanto, `produtosCadastrados` já está em ordem crescente de código, que é a pré-condição da busca binária, sem precisar ser ordenado.

### Tarefa 5

Resultados de uma execução da opção *Comparar desempenho das buscas*, com 8.750 buscas por combinação. As comparações são exatas e se repetem em toda execução; os tempos variam conforme a máquina e a execução.

| Critério | Método | Encontrados | Comparações (total) | Comparações por busca (média) | Tempo (ms) |
|---|---|---:|---:|---:|---:|
| Código | Sequencial | 7.750 | 37.785.125 | 4.318,30 | 136,482 |
| Código | Binária | 7.750 | 104.572 | 11,95 | 1,696 |
| Descrição | Sequencial | 7.750 | 37.785.125 | 4.318,30 | 2.001,025 |
| Descrição | Binária | 7.750 | 105.572 | 12,07 | 6,899 |

Tempo de criação do índice por descrição: **30,001 ms**.

**1. Médias obtidas e previstas pela análise de complexidade (n = 7.750)**

| Método | Chaves | Previsto | Obtido (código) | Obtido (descrição) |
|---|---|---:|---:|---:|
| Sequencial | Existentes | (n + 1) / 2 = 3.875,50 | 3.875,50 | 3.875,50 |
| Sequencial | Inexistentes | n = 7.750 | 7.750,00 | 7.750,00 |
| Binária | Existentes | ≈ 11,94 | 11,94 | 11,94 |
| Binária | Inexistentes | 12 a 13 (log₂(n + 1) ≈ 12,92) | 12,00 | 13,00 |

* **Sequencial:** os valores obtidos são iguais aos previstos. Cada chave existente é encontrada uma única vez em cada posição, e não há descrições repetidas, então a média é exatamente (n + 1) / 2. Uma chave inexistente percorre o vetor inteiro.
* **Binária, chaves existentes:** a busca equivale a uma árvore binária balanceada, em que um elemento no nível `i` custa `i` comparações. Os níveis de 1 a 12 estão completos (4.095 elementos, somando 45.057 comparações) e os outros 3.655 elementos ficam no nível 13 (47.515 comparações). A média é (45.057 + 47.515) / 7.750 ≈ 11,94, igual à obtida. O máximo é ⌊log₂ n⌋ + 1 = 13.
* **Binária, chaves inexistentes:** uma busca sem sucesso termina depois de 12 ou 13 comparações, dependendo de onde a chave cairia no vetor. Os códigos de 1 a 1000 são todos menores que o menor código, e a descida sempre pela esquerda custa 12. As descrições "inexistente N" caem todas no mesmo trecho do índice, que custa 13. Por isso os valores são 12,00 e 13,00, e não a média geral de 12,92.

**2. A partir de quantas buscas por nome compensa criar o índice?**

Custo médio por busca, usando a bateria de 8.750 buscas por descrição:

* Sequencial: 2.001,025 ms / 8.750 ≈ 0,22869 ms
* Binária: 6,899 ms / 8.750 ≈ 0,00079 ms

O índice compensa quando `30,001 + k × 0,00079 < k × 0,22869`, ou seja:

`k > 30,001 / (0,22869 − 0,00079) ≈ 131,6`

Portanto, compensa a partir de **132 buscas por nome**. A estimativa é aproximada, pois o tempo do índice é medido no início da execução, antes de a JVM otimizar o código, e variou de 30 a 47 ms entre as execuções. Mesmo no pior valor medido, o ponto de equilíbrio fica por volta de 205 buscas, muito pouco diante de um índice que é criado uma só vez.

**3. Vale a pena trocar a busca sequencial de `existeNoPedido` por busca binária?**

Não. Um pedido tem no máximo 10 itens, então a busca sequencial faz no máximo 10 comparações e a binária faria no máximo 4, um ganho insignificante. Além disso, os itens estão em ordem de inclusão, e a busca binária exigiria mantê-los ordenados, deslocando elementos a cada inclusão. Isso deixaria o código mais complexo sem benefício perceptível.
