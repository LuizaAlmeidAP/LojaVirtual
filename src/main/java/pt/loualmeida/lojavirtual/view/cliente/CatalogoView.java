package pt.loualmeida.lojavirtual.view.cliente;

/*
@Route("Catalogo")
@PageTitle("Catalogo de Produtos")
public class CatalogoView extends VerticalLayout {
    private final ProdutoService produtoService;



    public CatalogoView(ProdutoService produtoService) {
        this.produtoService = produtoService;


        inicializaView();


    }

    private void inicializaView() {
        organizacaoEspacial();
        H2 titulo = new H2("Catalogo de Produtos");
        Div containerProdutos = new Div();
        containerProdutos.getStyle().set("display", "grid");
        containerProdutos.getStyle().set("grid-template-columns", "repeat(auto-fill, minmax(250px, 1fr))");
        containerProdutos.getStyle().set("gap", "20px");
        containerProdutos.setWidthFull();

        List<Produto> produtos = produtoService.listarAtivos();
        produtos.forEach(produto -> {containerProdutos.add(criarCardProduto(produto));});

        add(titulo, containerProdutos);
    }

    private void organizacaoEspacial() {
        setSizeFull();
        setSpacing(false);
        setPadding(false);
        setJustifyContentMode(JustifyContentMode.START);
        setAlignItems(Alignment.CENTER);
    }

    private Component criarCardProduto(Produto produto) {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(false);
        card.setSpacing(false);
        card.setWidth("100%");
        card.getStyle()
                .set("border", "1px solid #ddd")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 6px rgba(0,0,0,0.05)")
                .set("padding", "1rem")
                .set("background-color", "white");
        Image imagem = new Image("https://via.placeholder.com/250x180?text=" + produto.getNome(), produto.getNome());
        imagem.setWidthFull();
        imagem.setHeight("180px");
        imagem.getStyle().set("object-fit", "cover");

        Span nome = new Span(produto.getNome());
        nome.getStyle().set("font-weight", "bold");

        Span preco = new Span("R$ " + produto.getPreco().setScale(2));
        preco.getStyle().set("color", "green");

        Button detalhes = new Button("Ver detalhes", event -> {
            verDetalhes(produto);
        });

        card.add(imagem, nome, preco, detalhes);
        return card;
    }

    private void verDetalhes(Produto produto) {
        UI.getCurrent().navigate("produto/" + produto.getId());
    }

}*/