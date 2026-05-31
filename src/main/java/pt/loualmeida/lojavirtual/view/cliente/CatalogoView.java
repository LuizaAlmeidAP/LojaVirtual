package pt.loualmeida.lojavirtual.view.cliente;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import jakarta.annotation.security.RolesAllowed;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Autor;
import pt.loualmeida.lojavirtual.model.Produto;
import pt.loualmeida.lojavirtual.service.ProdutoService;
import pt.loualmeida.lojavirtual.view.PrincipalView;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Route(value = "catalogo", layout = PrincipalView.class)
@RolesAllowed("CLIENTE")
@PageTitle("Catalogo de Produtos")
public class CatalogoView extends VerticalLayout {
    private final ProdutoService produtoService;
    private final TextField filterText = new TextField();
    private final Div containerProdutos = new Div();


    public CatalogoView(ProdutoService produtoService) {
        this.produtoService = produtoService;


        inicializaView();


    }



    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filtrar por titulo...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> {
            try {
                atualizarLista();
            } catch (ObjetoNaoEncontrado ex) {
                throw new RuntimeException(ex);
            }
        });

        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void atualizarLista() throws ObjetoNaoEncontrado {
        if (filterText.isEmpty()) {
            List<Produto> produtos = produtoService.buscarTodosProdutos("livro.titulo");
            produtos.forEach(produto -> {
                containerProdutos.add(criarCardProduto(produto));
            });

        } else {
            Set<Produto> produtos = produtoService.buscarProdutoPorTitulo(filterText.getValue());
            produtos.forEach(produto -> {
                containerProdutos.add(criarCardProduto(produto));
            });

        }
    }

    private void inicializaView() {
        organizacaoEspacial();

        containerProdutos.getStyle().set("display", "grid");
        containerProdutos.getStyle().set("grid-template-columns", "repeat(auto-fill, minmax(250px, 1fr))");
        containerProdutos.getStyle().set("gap", "20px");
        containerProdutos.setWidthFull();



        HorizontalLayout toolbar = getToolbar();

        add(toolbar, containerProdutos);
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

        Image image;


        if (produto.getLivro() != null && produto.getLivro().getCapa() != null && produto.getLivro().getCapa().length > 0) {


            DownloadHandler handler = DownloadHandler.fromInputStream(context ->
                    new DownloadResponse(
                            new ByteArrayInputStream(produto.getLivro().getCapa()), // O Stream de dados
                            "foto_" + produto.getId() + ".png",          // Nome do arquivo (importante para cache/tipo)
                            "image/png",                               // Mime Type (opcional, mas recomendado)
                            produto.getLivro().getCapa().length                     // Tamanho (opcional)
                    )
            );

            // Passa o handler diretamente para a imagem
            image = new Image(handler, "Foto de " + produto.getLivro().getTitulo());

        } else {
            // Placeholder continua igual
            image = new Image("imagens/user-placeholder.png", "Sem foto");
        }

        Div imageContainer = new Div();
        imageContainer.getStyle()
                .set("width", "100%")
                .set("height", "180px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("background-color", "#f9f9f9")
                .set("border-radius", "4px")
                .set("overflow", "hidden");

        image.getStyle()
                .set("max-width", "100%")
                .set("max-height", "100%")
                .set("object-fit", "contain");

        imageContainer.add(image);


        Span nome = new Span(produto.getLivro().getTitulo());
        nome.getStyle().set("font-weight", "bold");
        Span autores = new Span(produto.getLivro().getAutores().stream()
            .map(Autor::getNome)
                .collect(Collectors.joining(", ")));
        autores.getStyle().set("font-weight", "bold");

        NumberFormat formatador = new DecimalFormat("#0.00");
        Span preco = new Span("R$ " + formatador.format(produto.getValor()));
        preco.getStyle().set("color", "green");


        Button detalhes = new Button("Ver detalhes", event -> {
            verDetalhes(produto);
        });

        card.add(imageContainer, nome, preco, detalhes);
        return card;
    }

    private void verDetalhes(Produto produto) {
        UI.getCurrent().navigate("produto/" + produto.getId());
    }

}