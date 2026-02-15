package pt.loualmeida.lojavirtual.view.admin;

/*
@Route("cadastro-produto")
@PageTitle("Cadastro de Produto")
public class CadastroProdutoView extends VerticalLayout {
    private final Logger logger = LoggerFactory.getLogger(CadastroProdutoView.class);
    private final TextField nome = new TextField("Nome");
    private final TextField descricao = new TextField("Descrição");
    private final NumberField valor = new NumberField("Valor");
    private final ComboBox<CategoriaOpcoes> categoria = new ComboBox<>("Categória do produto");
    private final NumberField quantidade = new NumberField("Quantidade");
    private final Image foto = new Image();
    private final Button cadastrar = new Button("Cadastrar");
    private final Button cancelar = new Button("Cancelar");
    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    public CadastroProdutoView(ProdutoService produtoService, CategoriaService categoriaService) {
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;

        inicializaView();





    }
    //para cadastrar o produto tem q ter autorização de acesso, nao é todo mundo que pode fazer isso
    //garantir que o que seja adicionado não seja repetido
    //lembrar de fazer algo para carregar o estoque

    private void inicializaView(){

        organizacaoEspacial();


        H2 titulo = new H2("Cadastro de Produto");


        List<CategoriaOpcoes> opcoes = gerarListaHierarquica(categoriasRaiz);
        categoria.setItemLabelGenerator(CategoriaOpcoes::toString);
        categoria.setItems(opcoes);
        botaoCadastrar();

        FormLayout cadastro = new FormLayout(nome, descricao, valor, categoria, quantidade);
        HorizontalLayout botoes = new HorizontalLayout(cadastrar, cancelar);
        add(titulo, cadastro, botoes);

    }

    private void organizacaoEspacial(){
        setSizeFull();
        setSpacing(false);
        setPadding(false);
        setJustifyContentMode(JustifyContentMode.START);
        setAlignItems(Alignment.CENTER);
    }

    private void botaoCadastrar() {
        cadastrar.addClickListener(e -> salvarProduto());
    }

    private void salvarProduto() {
        Produto produto = new Produto();
        produto.setNome(nome.getValue());
        produto.setDescricao(descricao.getValue());
        produto.setPreco(BigDecimal.valueOf(valor.getValue()));
        produto.setCategoria(categoria.getValue().getCategoria());
        produto.setEstoque(quantidade.getValue().longValue());
        produto.setFoto(foto);
        try {
            produtoService.cadastrarProduto(produto);
            DialogoView.sucesso("Produto cadastrado com sucesso!");
        }
        catch(IllegalArgumentException e){
            DialogoView.aviso(e.getMessage());
        }
        catch(Exception erro){
            DialogoView.erro("Erro ao cadastrar produto. Tente novamente mais taarde!");
            logger.error(erro.getMessage(), erro);
        }


        //Instanciar o produto passando as informações dos camp


    }

    private List<CategoriaOpcoes> gerarListaHierarquica(List<Categoria> categoriasRaiz) {
        List<CategoriaOpcoes> resultado = new ArrayList<>();
        for (Categoria categoria : categoriasRaiz) {
            adicionarRecursivo(categoria, 0, resultado);
        }
        return resultado;
    }

    private String gerarPrefixo(int nivel) {
        if (nivel == 0) return "";
        return "│  ".repeat(nivel - 1) + "├─ ";
    }

    private void adicionarRecursivo(Categoria categoria, int nivel, List<CategoriaOpcoes> lista) {


        String prefixo = gerarPrefixo(nivel);
        lista.add(new CategoriaOpcoes(prefixo + categoria.getNome(), categoria));

        if (categoria.getFilhas() != null) {
            for (Categoria filho : categoria.getFilhas()) {
                adicionarRecursivo(filho, nivel + 1, lista);
            }
        }
    }



}
*/