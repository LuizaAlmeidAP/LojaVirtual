package pt.loualmeida.lojavirtual.view.cliente;

/*
@Route(value = "produto/")
@PageTitle("Produto")
public class ProdutoView extends VerticalLayout implements HasUrlParameter<Long> {

    private final ProdutoService produtoService;

    public ProdutoView( ProdutoService produtoService) {
        this.produtoService = produtoService;
        inicializaView();


    }

    public void inicializaView(){
        organizacaoEspacial();
    }

    public void organizacaoEspacial(){

    }


    @Override
    public void setParameter(BeforeEvent beforeEvent, Long id) {
        if (id != null) {
            carregarProduto(id);
        } else {
            add(new H3("Produto não encontrado"));
        }
    }

    private void carregarProduto(Long id) {
        produtoService.buscarPorId(id).ifPresentOrElse(produto -> {
            Image imagem = new Image("https://via.placeholder.com/700x400?text=" + produto.getNome(), "Imagem");
            imagem.setWidth("100%");
            imagem.getStyle().set("border-radius", "12px");

            H2 nome = new H2(produto.getNome());
            Span preco = new Span("R$ " + produto.getPreco().setScale(2));
            preco.getStyle().set("color", "green").set("font-size", "1.5rem");
            Paragraph descricao = new Paragraph(produto.getDescricao());
            Button voltar = new Button("← Voltar", e -> UI.getCurrent().navigate("catalogo"));

            add(imagem, nome, preco, descricao, voltar);

        }, () -> add(new H3("Produto não encontrado")));
    }
}*/
