package pt.loualmeida.lojavirtual.view.cliente;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.model.Label;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import pt.loualmeida.lojavirtual.enums.Categoria;
import pt.loualmeida.lojavirtual.enums.FaixaEtaria;
import pt.loualmeida.lojavirtual.enums.Genero;
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.*;
import pt.loualmeida.lojavirtual.service.*;
import pt.loualmeida.lojavirtual.view.PrincipalView;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;


@Route(value = "catalogo", layout = PrincipalView.class)
@RolesAllowed("CLIENTE")
@PageTitle("Catalogo de Produtos")
public class CatalogoView extends VerticalLayout {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(CatalogoView.class);

    private final ProdutoService produtoService;
    private final TextField filterText = new TextField();
    private final Div containerProdutos = new Div();
    private final AutorService autorService;
    private final CarrinhoService carrinhoService;
    private final SegurancaService segurancaService;
    private final ClienteService clienteService;


    public CatalogoView(ProdutoService produtoService, AutorService autorService,
                        CarrinhoService carrinhoService, SegurancaService segurancaService,
                        ClienteService clienteService) {
        this.produtoService = produtoService;
        this.autorService = autorService;
        this.carrinhoService = carrinhoService;
        this.segurancaService = segurancaService;
        this.clienteService = clienteService;


        inicializaView();


    }



    private HorizontalLayout getToolbar() {
        //SINGULAR, PLURAL E ACENTUAÇÃO
        filterText.setPlaceholder("Filtrar por titulo...");
        filterText.setWidth("100%");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> {

                atualizarLista(null, false);

        });


        ComboBox<Autor> filtroAutor = new ComboBox<>("Autor");
        List<Autor> listaAutores = autorService.buscarTodosAutores("nome");
        filtroAutor.setItems(listaAutores);
        filtroAutor.setItemLabelGenerator(Autor::getNome);


        ComboBox<Categoria> filtroCategoria = new ComboBox<>("Categoria");
        filtroCategoria.setItems(Categoria.values());
        filtroCategoria.setItemLabelGenerator(Categoria::getTitulo);
        ComboBox<Genero> filtroGenero = new ComboBox<>("Gênero");
        filtroGenero.setEnabled(false);
        filtroCategoria.addValueChangeListener(e -> {
            Categoria categoriaEscolhida = e.getValue();
            List<Genero> listaDeGenero = Genero.getGenerosDaCategoria(categoriaEscolhida);
            filtroGenero.setItems(listaDeGenero);
            filtroGenero.setItemLabelGenerator(Genero::getTitulo);
            filtroGenero.setEnabled(true);
        });
        ComboBox<FaixaEtaria> filtroFaixaEtaria = new ComboBox<>("Faixa Etaria");
        filtroFaixaEtaria.setItems(FaixaEtaria.values());
        filtroFaixaEtaria.setItemLabelGenerator(FaixaEtaria::getTitulo);
        NumberField filtroMinimo = new NumberField("Preço mínimo");
        NumberField filtroMaximo = new NumberField("Preço máximo");


        Button btnAplicarFiltros = new Button("Aplicar Filtros");

        // Ação do botão de filtros (Exemplo)
        btnAplicarFiltros.addClickListener(e -> {

            try {
                List<Produto> produtosFiltrados = produtoService.filtraProdutos(filterText.getValue(), filtroAutor.getValue().getNome(),
                        filtroCategoria.getValue(), filtroGenero.getValue(), filtroFaixaEtaria.getValue(),
                        (filtroMinimo.getValue() == null) ? null : filtroMinimo.getValue(),
                        (filtroMaximo.getValue() == null) ? null : filtroMaximo.getValue());
                atualizarLista(produtosFiltrados, true);
            } catch (Exception ex) {
                logger.error("Erro ao aplicar filtros: ", ex);
            }
        }

        );

        // 3. Agrupando os filtros adicionais em um layout
        HorizontalLayout layoutFiltrosExtras = new HorizontalLayout(filtroAutor ,filtroCategoria, filtroGenero, filtroFaixaEtaria, filtroMinimo, filtroMaximo, btnAplicarFiltros);
        layoutFiltrosExtras.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        layoutFiltrosExtras.getStyle().set("flex-wrap", "wrap"); // Para responsividade

        // 4. Área Expansível (Details)
        // O Vaadin Details cria a aba que o usuário clica para abrir/fechar
        Details areaFiltrosAvancados = new Details("Filtros Avançados", layoutFiltrosExtras);
        areaFiltrosAvancados.setOpened(false); // Garante que a área comece FECHADA




        setSizeFull();


        HorizontalLayout toolbar = new HorizontalLayout(filterText, areaFiltrosAvancados);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void atualizarLista(List<Produto> produtosFiltrados, boolean temFiltro) {
        containerProdutos.removeAll();
        if(produtosFiltrados != null && !produtosFiltrados.isEmpty()) {
            produtosFiltrados.forEach(produto -> {
                containerProdutos.add(criarCardProduto(produto));
            });

        }
        else if (filterText.isEmpty()&&!temFiltro) {
            List<Produto> produtos = produtoService.buscarTodosProdutos("livro.titulo");
            produtos.forEach(produto -> {
                containerProdutos.add(criarCardProduto(produto));
            });

        } else if(!temFiltro) {
            Set<Produto> produtos = produtoService.buscarProdutoPorTitulo(filterText.getValue());
            produtos.forEach(produto -> {
                containerProdutos.add(criarCardProduto(produto));
            });

        }
        else{
            add(new Span("Nenhum produto encontrado para o filtro informado."));
        }
    }

    private void inicializaView() {
        organizacaoEspacial();

        containerProdutos.getStyle().set("display", "grid");
        containerProdutos.getStyle().set("grid-template-columns", "repeat(auto-fill, minmax(250px, 1fr))");
        containerProdutos.getStyle().set("gap", "20px");
        containerProdutos.setWidthFull();


        atualizarLista(null, false);
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


        Button detalhes = new Button("Ver detalhes", VaadinIcon.SEARCH.create(), event -> {
            verDetalhes(produto);
        });
        Button adicionarCarrinho = new Button("Adicionar carrinho", VaadinIcon.CART.create(), event -> {
            try {
                adicionarAoCarrinho(produto);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        card.add(imageContainer, nome, preco, detalhes, adicionarCarrinho);
        return card;
    }

    private void verDetalhes(Produto produto) {
        UI.getCurrent().navigate("produto/" + produto.getId());
    }
    private void adicionarAoCarrinho(Produto produto) throws ObjetoNaoEncontrado, DadosInvalidosException {
        Usuario usuario = segurancaService.getUsuarioLogado();
        Cliente cliente = clienteService.buscarClientePorUsuario(usuario);
        Carrinho carrinho = carrinhoService.buscarCarrinhoPorCliente(cliente.getId());
        if(carrinho == null) {
            carrinho = new Carrinho();
            carrinho.setCliente(cliente);
            carrinho.adicionarProduto(produto);
            carrinho = carrinhoService.cadastrarCarrinho(carrinho);
        }
        else {
            carrinhoService.adicionarItens( carrinho.getId(), List.of(produto));
        }

        //Adicionar a informação dos produtos no carrinho


    }
}