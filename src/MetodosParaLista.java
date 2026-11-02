import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Rascunho para a atividade de listas: métodos atuais do sistema reescritos com Lista.
 * Esta classe não é usada pelo App. Copie para App ou Pedido apenas o que a atividade pedir.
 * Os parâmetros fazem o papel dos atributos que os métodos usariam nas classes de destino.
 */
public class MetodosParaLista {

    private static final double DESCONTO_PG_A_VISTA = 0.15;

    // ---------- Pedidos: substituem a pilha e a fila do App ----------

    /** Substitui App.finalizarPedido. A lista guarda os pedidos por ordem de chegada. */
    public static void finalizarPedido(Lista<Pedido> pedidos, Pedido pedido) {
        if (pedido == null) {
            System.out.println("Não há pedido em aberto para ser fechado.");
            return;
        }
        pedidos.inserirFinal(pedido);
    }

    /** Substitui App.listarProdutosPedidosRecentes. Os pedidos mais recentes estão no final da lista. */
    public static void listarProdutosPedidosRecentes(Lista<Pedido> pedidos, int numPedidos) {
        Lista<Pedido> recentes;
        ItemDePedido[] itens;

        try {
            recentes = pedidos.subLista(pedidos.tamanho() - numPedidos, numPedidos);
        } catch (IllegalArgumentException excecao) {
            System.out.println("Quantidade inválida: " + excecao.getMessage());
            return;
        }

        while (!recentes.vazia()) {
            Pedido pedido = recentes.removerFinal();
            System.out.println("Produtos do pedido " + pedido.hashCode() + ":");
            itens = pedido.getItensDoPedido();
            for (int i = 0; (i < itens.length && itens[i] != null); i++) {
                System.out.println(itens[i].getProduto());
            }
        }
    }

    /** Substitui App.exibirValorMedioPedidos. */
    public static double valorMedioPedidos(Lista<Pedido> pedidos, int numPedidos) {
        return pedidos.calcularValorMedio(pedido -> pedido.valorFinal(), numPedidos);
    }

    /** Substitui App.exibirPedidosAcimaDeValor. */
    public static Lista<Pedido> pedidosAcimaDeValor(Lista<Pedido> pedidos, int numPedidos, double valorMinimo) {
        return pedidos.filtrar(pedido -> pedido.valorFinal() > valorMinimo, numPedidos);
    }

    /** Substitui App.exibirPedidosComProduto. */
    public static Lista<Pedido> pedidosComProduto(Lista<Pedido> pedidos, int numPedidos, Produto produto) {
        return pedidos.filtrar(pedido -> pedido.existeNoPedido(produto) != null, numPedidos);
    }

    // ---------- Produtos: substituem o vetor produtosCadastrados do App ----------

    /** Substitui App.lerProdutos. Retorna uma lista vazia em caso de problemas com o arquivo. */
    public static Lista<Produto> lerProdutos(String nomeArquivoDados) {
        Lista<Produto> produtos = new Lista<>();

        try (Scanner arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"))) {
            int numProdutos = Integer.parseInt(arquivo.nextLine());

            for (int i = 0; i < numProdutos; i++) {
                produtos.inserirFinal(Produto.criarDoTexto(arquivo.nextLine()));
            }
        } catch (IOException excecaoArquivo) {
            return new Lista<>();
        }

        return produtos;
    }

    /** Substitui App.localizarProduto. Na lista encadeada, a busca é sequencial. */
    public static Produto localizarProduto(Lista<Produto> produtos, int idProduto) {
        return produtos.localizar(produto -> produto.hashCode() == idProduto);
    }

    /** Substitui App.localizarProdutoDescricao. Na lista encadeada, a busca é sequencial. */
    public static Produto localizarProdutoDescricao(Lista<Produto> produtos, String descricao) {
        return produtos.localizar(produto -> produto.descricao.equalsIgnoreCase(descricao));
    }

    /** Copia a lista para um vetor, mantendo a busca binária com Buscador, que trabalha com vetores. */
    public static Produto[] paraVetor(Lista<Produto> produtos) {
        Produto[] vetor = new Produto[produtos.tamanho()];

        produtos.copiarPara(vetor);
        return vetor;
    }

    // ---------- Itens do pedido: substituem o vetor itensDePedido de Pedido ----------

    /** Substitui Pedido.existeNoPedido. */
    public static ItemDePedido existeNoPedido(Lista<ItemDePedido> itens, Produto produto) {
        return itens.localizar(item -> item.getProduto().equals(produto));
    }

    /** Substitui Pedido.incluirProduto. A lista dispensa o limite de 10 itens; mantenha-o se a atividade exigir. */
    public static void incluirProduto(Lista<ItemDePedido> itens, Produto novo, int quantidade) {
        ItemDePedido itemDePedido = existeNoPedido(itens, novo);

        if (itemDePedido != null) {
            itemDePedido.setQuantidade(quantidade + itemDePedido.getQuantidade());
        } else {
            itens.inserirFinal(new ItemDePedido(novo, quantidade, novo.valorDeVenda()));
        }
    }

    /** Substitui Pedido.valorFinal. */
    public static double valorFinal(Lista<ItemDePedido> itens, int formaDePagamento) {
        double valorPedido = itens.calcularValorMedio(item -> item.getQuantidade() * item.getPrecoVenda(), itens.tamanho()) * itens.tamanho();

        if (formaDePagamento == 1) {
            valorPedido = valorPedido * (1.0 - DESCONTO_PG_A_VISTA);
        }

        return new BigDecimal(Double.toString(valorPedido)).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
