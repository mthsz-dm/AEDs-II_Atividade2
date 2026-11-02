import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;

    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Índice: referências de produtosCadastrados, em ordem crescente de descrição (sem diferenciar maiúsculas de minúsculas) */
    static Produto[] produtosPorDescricao;

    /** Tempo gasto, em nanossegundos, na criação do índice por descrição */
    static long tempoCriacaoIndice;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    /** Quantidade de chaves inexistentes usadas na comparação de desempenho das buscas */
    static final int CHAVES_INEXISTENTES = 1000;

    /** Buscador de produtos por código */
    static Buscador<Produto, Integer> buscadorPorCodigo = new Buscador<>(p -> p.hashCode());

    /** Buscador de produtos por descrição, sem diferenciar maiúsculas de minúsculas */
    static Buscador<Produto, String> buscadorPorDescricao = new Buscador<>(p -> p.descricao.toLowerCase());

    /** Pilha de pedidos */
    static Pilha<Pedido> pilhaPedidos = new Pilha<>();

    /** Fila de pedidos, por ordem de chegada */
    static Fila<Pedido> filaPedidos = new Fila<>();

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }

    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {

    	T valor;

    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }

    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar por um produto, por código");
        System.out.println("3 - Procurar por um produto, por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Fechar pedido");
        System.out.println("6 - Listar produtos dos pedidos mais recentes");
        System.out.println("7 - Exibir o valor total médio dos N primeiros pedidos");
        System.out.println("8 - Exibir os primeiros pedidos com valor total acima de um determinado valor");
        System.out.println("9 - Exibir os primeiros pedidos que apresentam um determinado produto");
        System.out.println("10 - Procurar produtos pelo início do nome");
        System.out.println("11 - Comparar desempenho das buscas");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }

    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {

    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;

    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));

    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];

    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;

    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}

    	return produtosCadastrados;
    }

    /** Cria o índice produtosPorDescricao, com cópias das referências de produtosCadastrados, e o ordena por descrição. */
    static void criarIndicePorDescricao() {

    	produtosPorDescricao = new Produto[quantosProdutos];
    	for (int i = 0; i < quantosProdutos; i++) {
    		produtosPorDescricao[i] = produtosCadastrados[i];
    	}
    	Ordenacao.ordenar(produtosPorDescricao, quantosProdutos, (Produto p) -> p.descricao.toLowerCase());
    }

    /**
     * Procura a chave por busca binária no vetor informado, e exibe quantas comparações a busca binária fez
     * e quantas a busca sequencial em produtosCadastrados faria para a mesma chave.
     * @param buscador Buscador do critério desejado.
     * @param vetorOrdenado Vetor em ordem crescente da chave do buscador.
     * @param chave Chave procurada.
     * @return O produto encontrado ou null, caso não tenha sido localizado.
     */
    private static <K extends Comparable<K>> Produto buscarExibindoComparacoes(Buscador<Produto, K> buscador, Produto[] vetorOrdenado, K chave) {

    	long comparacoesSequencial;
    	int posicao;

    	buscador.zerarComparacoes();
    	buscador.buscaSequencial(produtosCadastrados, quantosProdutos, chave);
    	comparacoesSequencial = buscador.getComparacoes();

    	buscador.zerarComparacoes();
    	posicao = buscador.buscaBinaria(vetorOrdenado, quantosProdutos, chave);

    	System.out.println(String.format("Comparações - busca sequencial: %d | busca binária: %d", comparacoesSequencial, buscador.getComparacoes()));

    	return (posicao >= 0) ? vetorOrdenado[posicao] : null;
    }

    /** Localiza um produto no vetor de produtos cadastrados, a partir do código de produto informado pelo usuário, e o retorna.
     *  Usa busca binária, pois o vetor já está em ordem crescente de código. Em caso de não encontrar o produto, retorna null
     */
    static Produto localizarProduto() {

    	cabecalho();
    	System.out.println("Localizando um produto...");
        int idProduto = lerOpcao("Digite o código identificador do produto desejado: ", Integer.class);

        return buscarExibindoComparacoes(buscadorPorCodigo, produtosCadastrados, idProduto);
    }

    /** Localiza um produto no índice por descrição, a partir do nome de produto informado pelo usuário, e o retorna.
     *  Usa busca binária. A busca não é sensível ao caso. Em caso de não encontrar o produto, retorna null
     *  @return O produto encontrado ou null, caso o produto não tenha sido localizado no vetor de produtos cadastrados.
     */
    static Produto localizarProdutoDescricao() {

    	String descricao;

    	cabecalho();
    	System.out.println("Localizando um produto...");
    	System.out.println("Digite o nome ou a descrição do produto desejado:");
        descricao = teclado.nextLine();

        return buscarExibindoComparacoes(buscadorPorDescricao, produtosPorDescricao, descricao.toLowerCase());
    }

    private static void mostrarProduto(Produto produto) {

        cabecalho();
        String mensagem = "Dados inválidos para o produto!";

        if (produto != null){
            mensagem = String.format("Dados do produto:\n%s", produto);
        }

        System.out.println(mensagem);
    }

    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {

        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < quantosProdutos; i++) {
        	System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
        }
    }

    /**
     * Inicia um novo pedido.
     * Permite ao usuário escolher e incluir produtos no pedido.
     * @return O novo pedido
     */
    public static Pedido iniciarPedido() {

    	int formaPagamento = lerOpcao("Digite a forma de pagamento do pedido, sendo 1 para pagamento à vista e 2 para pagamento a prazo", Integer.class);
    	Pedido pedido = new Pedido(LocalDate.now(), formaPagamento);
    	Produto produto;
    	int numProdutos;
    	int quantidade;

    	listarTodosOsProdutos();
    	System.out.println("Incluindo produtos no pedido...");
    	numProdutos = lerOpcao("Quantos produtos serão incluídos no pedido?", Integer.class);
        for (int i = 0; i < numProdutos; i++) {
        	produto = localizarProdutoDescricao();
        	if (produto == null) {
        		System.out.println("Produto não encontrado");
        		i--;
        	} else {
        		quantidade = lerOpcao("Quantos itens desse produto serão incluídos no pedido?", Integer.class);
        		pedido.incluirProduto(produto, quantidade);
        	}
        }

        return pedido;
    }

    /**
     * Finaliza um pedido, momento no qual ele deve ser armazenado na pilha de pedidos e na fila de pedidos.
     * @param pedido O pedido que deve ser finalizado.
     */
    public static void finalizarPedido(Pedido pedido) {

    	if (pedido == null) {
    		System.out.println("Não há pedido em aberto para ser fechado.");
    		return;
    	}
    	pilhaPedidos.empilhar(pedido);
    	filaPedidos.emfileirar(pedido);
    }

    /** Lista os produtos dos N pedidos mais recentes, do mais recente para o mais antigo. */
    public static void listarProdutosPedidosRecentes() {

    	Pilha<Pedido> pedidosRecentes;
    	Pedido pedido;
    	ItemDePedido[] itens;

    	cabecalho();
    	Integer numPedidos = lerOpcao("Quantos pedidos recentes devem ser considerados?", Integer.class);
    	if (numPedidos == null) {
    		System.out.println("Quantidade inválida.");
    		return;
    	}

    	try {
    		pedidosRecentes = pilhaPedidos.subPilha(numPedidos);
    	} catch (IllegalArgumentException excecao) {
    		System.out.println("Quantidade inválida: " + excecao.getMessage());
    		return;
    	}

    	while (!pedidosRecentes.vazia()) {
    		pedido = pedidosRecentes.desempilhar();
    		System.out.println("Produtos do pedido " + pedido.hashCode() + ":");
    		itens = pedido.getItensDoPedido();
    		for (int i = 0; (i < itens.length && itens[i] != null); i++) {
    			System.out.println(itens[i].getProduto());
    		}
    	}
    }

    /** Exibe o valor total médio dos N primeiros pedidos da fila. */
    public static void exibirValorMedioPedidos() {

    	cabecalho();
    	Integer numPedidos = lerOpcao("Quantos primeiros pedidos devem ser considerados?", Integer.class);
    	if (numPedidos == null) {
    		System.out.println("Quantidade inválida.");
    		return;
    	}

    	double valorMedio = filaPedidos.calcularValorMedio(pedido -> pedido.valorFinal(), numPedidos);
    	System.out.println(String.format("Valor total médio dos pedidos: R$ %.2f", valorMedio));
    }

    /** Exibe, dentre os N primeiros pedidos da fila, aqueles com valor total acima de um valor informado pelo usuário. */
    public static void exibirPedidosAcimaDeValor() {

    	cabecalho();
    	Integer numPedidos = lerOpcao("Quantos primeiros pedidos devem ser considerados?", Integer.class);
    	Double valorMinimo = lerOpcao("Exibir pedidos com valor total acima de (use ponto como separador decimal):", Double.class);
    	if (numPedidos == null || valorMinimo == null) {
    		System.out.println("Valor inválido.");
    		return;
    	}

    	exibirPedidos(filaPedidos.filtrar(pedido -> pedido.valorFinal() > valorMinimo, numPedidos));
    }

    /** Exibe, dentre os N primeiros pedidos da fila, aqueles que apresentam um produto informado pelo usuário. */
    public static void exibirPedidosComProduto() {

    	cabecalho();
    	Integer numPedidos = lerOpcao("Quantos primeiros pedidos devem ser considerados?", Integer.class);
    	if (numPedidos == null) {
    		System.out.println("Quantidade inválida.");
    		return;
    	}

    	Produto produto = localizarProdutoDescricao();
    	if (produto == null) {
    		System.out.println("Produto não encontrado");
    		return;
    	}

    	exibirPedidos(filaPedidos.filtrar(pedido -> pedido.existeNoPedido(produto) != null, numPedidos));
    }

    /** Exibe os pedidos de uma fila, esvaziando-a. Deve receber uma fila auxiliar, como a devolvida por filtrar. */
    private static void exibirPedidos(Fila<Pedido> pedidos) {

    	int encontrados = 0;

    	while (!pedidos.vazia()) {
    		System.out.println(pedidos.desemfileirar());
    		encontrados++;
    	}
    	System.out.println("Pedidos encontrados: " + encontrados);
    }

    /**
     * Lista os produtos cuja descrição começa com o texto informado pelo usuário, sem diferenciar maiúsculas de minúsculas.
     * Localiza o primeiro candidato por busca binária no índice e, a partir dele, varre o índice sequencialmente
     * até encontrar uma descrição que não começa com o texto.
     */
    static void listarProdutosPorInicioDoNome() {

    	String inicioDoNome;
    	int encontrados = 0;
    	int posicao;

    	cabecalho();
    	System.out.println("Digite o início do nome do produto:");
    	inicioDoNome = teclado.nextLine().toLowerCase();
    	if (inicioDoNome.isEmpty()) {
    		System.out.println("Texto inválido.");
    		return;
    	}

    	posicao = buscadorPorDescricao.primeiraPosicaoMaiorOuIgual(produtosPorDescricao, quantosProdutos, inicioDoNome);
    	while (posicao < quantosProdutos && produtosPorDescricao[posicao].descricao.toLowerCase().startsWith(inicioDoNome)) {
    		System.out.println(produtosPorDescricao[posicao]);
    		encontrados++;
    		posicao++;
    	}

    	System.out.println("Produtos encontrados: " + encontrados);
    }

    /**
     * Executa uma bateria de buscas e exibe seus resultados: encontrados, comparações e tempo.
     * @param criterio Nome do critério de busca, para exibição.
     * @param buscador Buscador do critério desejado.
     * @param dados Vetor em que as buscas serão feitas.
     * @param chaves Chaves procuradas: primeiro as chaves existentes e, depois, as inexistentes.
     * @param binaria true para usar busca binária; false para usar busca sequencial.
     */
    private static <K extends Comparable<K>> void executarBateria(String criterio, Buscador<Produto, K> buscador, Produto[] dados, K[] chaves, boolean binaria) {

    	int encontrados = 0;
    	int posicao;
    	long comparacoesExistentes = 0;
    	long inicio, tempo;
    	long comparacoes;

    	buscador.zerarComparacoes();
    	inicio = System.nanoTime();
    	for (int i = 0; i < chaves.length; i++) {
    		if (i == quantosProdutos) {
    			comparacoesExistentes = buscador.getComparacoes();
    		}
    		posicao = binaria ? buscador.buscaBinaria(dados, quantosProdutos, chaves[i])
    				: buscador.buscaSequencial(dados, quantosProdutos, chaves[i]);
    		if (posicao >= 0) {
    			encontrados++;
    		}
    	}
    	tempo = System.nanoTime() - inicio;
    	comparacoes = buscador.getComparacoes();

    	System.out.println(String.format("%-10s %-11s %12d %15d %17.2f %12.3f", criterio, binaria ? "Binária" : "Sequencial", encontrados,
    			comparacoes, (double) comparacoes / chaves.length, tempo / 1_000_000.0));
    	System.out.println(String.format("  Média de comparações - chaves existentes: %.2f | chaves inexistentes: %.2f",
    			(double) comparacoesExistentes / quantosProdutos, (double) (comparacoes - comparacoesExistentes) / CHAVES_INEXISTENTES));
    }

    /** Para cada critério (código e descrição) e método (sequencial e binária), executa a mesma bateria de buscas e exibe os resultados. */
    static void compararDesempenhoBuscas() {

    	Integer[] codigos = new Integer[quantosProdutos + CHAVES_INEXISTENTES];
    	String[] descricoes = new String[quantosProdutos + CHAVES_INEXISTENTES];

    	for (int i = 0; i < quantosProdutos; i++) {
    		codigos[i] = produtosCadastrados[i].hashCode();
    		descricoes[i] = produtosCadastrados[i].descricao.toLowerCase();
    	}
    	for (int i = 1; i <= CHAVES_INEXISTENTES; i++) {
    		codigos[quantosProdutos + i - 1] = i;
    		descricoes[quantosProdutos + i - 1] = "inexistente " + i;
    	}

    	cabecalho();
    	System.out.println(String.format("%-10s %-11s %12s %15s %17s %12s", "Critério", "Método", "Encontrados", "Comparações", "Comparações/busca", "Tempo (ms)"));
    	executarBateria("Código", buscadorPorCodigo, produtosCadastrados, codigos, false);
    	executarBateria("Código", buscadorPorCodigo, produtosCadastrados, codigos, true);
    	executarBateria("Descrição", buscadorPorDescricao, produtosCadastrados, descricoes, false);
    	executarBateria("Descrição", buscadorPorDescricao, produtosPorDescricao, descricoes, true);
    	System.out.println(String.format("Tempo de criação do índice por descrição: %.3f ms", tempoCriacaoIndice / 1_000_000.0));
    }

	public static void main(String[] args) {

		teclado = new Scanner(System.in, Charset.forName("UTF-8"));

		nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);

        long inicioCriacaoIndice = System.nanoTime();
        criarIndicePorDescricao();
        tempoCriacaoIndice = System.nanoTime() - inicioCriacaoIndice;

        Pedido pedido = null;

        int opcao = -1;

        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> mostrarProduto(localizarProduto());
                case 3 -> mostrarProduto(localizarProdutoDescricao());
                case 4 -> pedido = iniciarPedido();
                case 5 -> {
                    finalizarPedido(pedido);
                    pedido = null;
                }
                case 6 -> listarProdutosPedidosRecentes();
                case 7 -> exibirValorMedioPedidos();
                case 8 -> exibirPedidosAcimaDeValor();
                case 9 -> exibirPedidosComProduto();
                case 10 -> listarProdutosPorInicioDoNome();
                case 11 -> compararDesempenhoBuscas();
            }
            pausa();
        }while(opcao != 0);

        teclado.close();
    }
}
