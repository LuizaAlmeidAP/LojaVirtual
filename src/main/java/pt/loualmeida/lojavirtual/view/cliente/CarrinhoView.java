package pt.loualmeida.lojavirtual.view.cliente;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.*;
import pt.loualmeida.lojavirtual.service.CarrinhoService;
import pt.loualmeida.lojavirtual.service.ClienteService;
import pt.loualmeida.lojavirtual.service.ProdutoService;
import pt.loualmeida.lojavirtual.service.SegurancaService;
import pt.loualmeida.lojavirtual.view.PrincipalView;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

@Route(value = "carrinho", layout = PrincipalView.class)
@RolesAllowed("CLIENTE")
public class CarrinhoView extends VerticalLayout {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(CarrinhoView.class);

    private final ClienteService clienteService;
    private final ProdutoService produtoService;
    private final CarrinhoService carrinhoService;
    private final SegurancaService segurancaService;
    private final Carrinho carrinho;
    private final Grid<Map.Entry<Produto, Long>> grid = new Grid<>();

    public CarrinhoView(ClienteService clienteService, ProdutoService produtoService, CarrinhoService carrinhoService, SegurancaService segurancaService) throws ObjetoNaoEncontrado {
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        this.carrinhoService = carrinhoService;
        this.segurancaService = segurancaService;

        Usuario usuario = segurancaService.getUsuarioLogado();
        Cliente cliente = clienteService.buscarClientePorUsuario(usuario);
        carrinho = carrinhoService.buscarCarrinhoPorCliente(cliente.getId());

        setSizeFull();
        configureGrid();
        HorizontalLayout toolbar = getToolbar();

        add(toolbar, grid);
        atualizarLista();



    }

    private HorizontalLayout getToolbar() throws ObjetoNaoEncontrado {

        NumberFormat formatador = new DecimalFormat("#0.00");
        Span nItens = new Span("Número de itens: " + carrinho.getQuatidadeItens());
        Span valorTotal = new Span("Valor total: R$" + formatador.format(carrinho.getTotal()));
        HorizontalLayout toolbar = new HorizontalLayout(nItens, valorTotal);
        toolbar.addClassName("toolbar");
        return toolbar;
    }


    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);


        grid.addColumn(new ComponentRenderer<>(produto -> {
            Image image;


            if (produto.getKey().getLivro() != null && produto.getKey().getLivro().getCapa() != null && produto.getKey().getLivro().getCapa().length > 0) {


                DownloadHandler handler = DownloadHandler.fromInputStream(context ->
                        new DownloadResponse(
                                new ByteArrayInputStream(produto.getKey().getLivro().getCapa()),
                                "foto_" + produto.getKey().getId() + ".png",
                                "image/png",
                                produto.getKey().getLivro().getCapa().length
                        )
                );

                // Passa o handler diretamente para a imagem
                image = new Image(handler, "Foto de " + produto.getKey().getLivro().getTitulo());

            } else {
                // Placeholder continua igual
                image = new Image("imagens/user-placeholder.png", "Sem foto");
            }

            // Estilização (igual ao anterior)
            image.setWidth("50px");
            image.setHeight("50px");
            image.getStyle().set("border-radius", "50%");
            image.getStyle().set("object-fit", "cover");

            return image;

        })).setHeader("Capa").setFlexGrow(0).setWidth("80px");


        grid.addColumn(produto -> {
            return produto.getKey().getLivro().getTitulo();
        }).setHeader("Título").setSortable(true);
        grid.addColumn(produto -> {
            List<String> nomesAutores = produto.getKey().getLivro().getAutores().stream().map(Autor::getNome).toList();
            return String.join(", ", nomesAutores);
        }).setHeader("Autores");
        grid.addColumn(produto -> {
            return produto.getKey().getValor();
        }).setHeader("Valor").setSortable(true);



        grid.addColumn(Map.Entry::getValue).setHeader("Quantidade").setSortable(true);

        grid.addComponentColumn(produto -> {
            Button btnProduto = new Button(VaadinIcon.BOOK.create());
            btnProduto.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            btnProduto.setTooltipText("Detalhes");

            btnProduto.addClickListener(e -> {
                Notification.show("Visualizando produto: " + produto.getKey().getLivro().getTitulo());
            });
            return btnProduto;
        }).setHeader("Detalhes");

        grid.addComponentColumn(produto-> {
            Button btnAdicionarProduto = new Button(VaadinIcon.PLUS.create());
            btnAdicionarProduto.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            btnAdicionarProduto.setTooltipText("Adicionar uma unidade do produto");
            btnAdicionarProduto.addClickListener(e -> {
                produto.setValue(produto.getValue() + 1);
                carrinho.adicionarProduto(produto.getKey());
                try {
                    carrinhoService.adicionarItens(carrinho.getId(), List.of(produto.getKey()));
                    ((PrincipalView) getParent().get()).exibirCarrinho();
                    atualizarLista();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            });
            return btnAdicionarProduto;
        }).setHeader("Adicionar");

        grid.addComponentColumn(produto-> {
            Button btnRemoverProduto = new Button(VaadinIcon.MINUS.create());
            btnRemoverProduto.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            btnRemoverProduto.setTooltipText("Remover uma unidade do produto");
            btnRemoverProduto.addClickListener(e -> {
                produto.setValue(produto.getValue() - 1);
                carrinho.removerProduto(produto.getKey());
                try {
                    carrinhoService.removerItem(carrinho.getId(), produto.getKey());
                    ((PrincipalView) getParent().get()).exibirCarrinho(); //mudar para remover
                    atualizarLista();

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            });
            return btnRemoverProduto;
        }).setHeader("Remover");




    }

    private void atualizarLista() throws ObjetoNaoEncontrado {
        grid.setItems(carrinhoService.mapaDeCompras(carrinho).entrySet());


    }






}
