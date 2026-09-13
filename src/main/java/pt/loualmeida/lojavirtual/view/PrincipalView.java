package pt.loualmeida.lojavirtual.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.loualmeida.lojavirtual.enums.Perfil;
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Carrinho;
import pt.loualmeida.lojavirtual.model.Cliente;
import pt.loualmeida.lojavirtual.model.Produto;
import pt.loualmeida.lojavirtual.model.Usuario;
import pt.loualmeida.lojavirtual.service.CarrinhoService;
import pt.loualmeida.lojavirtual.service.ClienteService;
import pt.loualmeida.lojavirtual.service.SegurancaService;
import pt.loualmeida.lojavirtual.view.admin.*;
import pt.loualmeida.lojavirtual.view.cliente.CarrinhoView;
import pt.loualmeida.lojavirtual.view.cliente.CatalogoView;
import pt.loualmeida.lojavirtual.view.cliente.PedidosView;

import java.util.List;

@Route("")
@RolesAllowed({"CLIENTE", "ADMINISTRADOR"})
public class PrincipalView extends AppLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrincipalView.class);

    private static final String TITULO_APP = "LouAlmeida";

    private final SegurancaService segurancaService;

    private final CarrinhoService carrinhoService;

    private final ClienteService clienteService;

    private final Span quantidadeCarrinhoItens = new Span();

    private int totalItens = 0;

    public PrincipalView(SegurancaService segurancaService, CarrinhoService carrinhoService, ClienteService clienteService) {
        this.segurancaService = segurancaService;
        this.carrinhoService = carrinhoService;
        this.clienteService = clienteService;
        try {
            // Top navbar
            Usuario usuarioLogado = segurancaService.getUsuarioLogado();
            HorizontalLayout header = createHeader(usuarioLogado);
            addToNavbar(header);
            createDrawer();
            if(usuarioLogado.getPerfil() == Perfil.CLIENTE) exibirCarrinho(true);
        } catch (Exception e) {
            LOGGER.error("Erro ao criar o menu de navegação: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private HorizontalLayout createHeader(Usuario usuarioLogado) throws ObjetoNaoEncontrado {
        DrawerToggle toggle = new DrawerToggle();

        Image logo = new Image("imagens/logo_for_loualmeida.jpg", TITULO_APP);
        logo.setHeight("50px"); // <- small toolbar size
        RouterLink logoLink = new RouterLink();
        logoLink.setRoute(ProdutosView.class);
        logoLink.add(logo);

        H1 title = new H1(TITULO_APP);
        title.getStyle().setMargin("0");
        RouterLink brand = new RouterLink();
        brand.setRoute(ProdutosView.class);
        brand.add(title);



        Span loggedUser = new Span(usuarioLogado.getNome());
        Avatar avatar = new Avatar(usuarioLogado.getNome());

        RouterLink cartLink = new RouterLink();
        Icon cartIcon = VaadinIcon.CART.create();
        cartLink.setRoute(CarrinhoView.class);
        cartLink.add(cartIcon);




        Button logoutButton = new Button("Sair", VaadinIcon.SIGN_OUT.create());
        logoutButton.addClickListener(s -> segurancaService.logout());

        if (hasAdminRole()) {
            cartLink.setVisible(false);
        }
        HorizontalLayout userSection = new HorizontalLayout(loggedUser, avatar, cartLink, quantidadeCarrinhoItens, logoutButton);
        userSection.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout header = new HorizontalLayout(toggle, logoLink, brand, userSection);

        header.setWidthFull();


        header.expand(brand);

        // Allow wrapping on small screens
        header.getStyle().set("flex-wrap", "wrap");

        userSection.getStyle().set("margin-left", "auto");

        return header;
    }

    public void incrementarCarrinho(int quantidadeItens) {
        if(totalItens == 0) {
            this.totalItens = quantidadeItens;
        }
        else{
            this.totalItens += 1;
        }

        quantidadeCarrinhoItens.setText(String.valueOf(totalItens));

    }

    public void exibirCarrinho(boolean incrementar) throws ObjetoNaoEncontrado, DadosInvalidosException {
        Usuario usuario = segurancaService.getUsuarioLogado();
        Cliente cliente = clienteService.buscarClientePorUsuario(usuario);
        Carrinho carrinho = carrinhoService.buscarCarrinhoPorCliente(cliente.getId());
        if(carrinho == null) {return;}
        int quantidadeItens = carrinho.getQuatidadeItens();
        if (incrementar) {
            incrementarCarrinho(quantidadeItens);
        } else {
            decrementarCarrinho(quantidadeItens);
        }
    }

    public void decrementarCarrinho(int quantidadeItens) {
        if(totalItens == 0) {
            this.totalItens = quantidadeItens;
        }
        else{
            this.totalItens -= 1;
        }

        quantidadeCarrinhoItens.setText(String.valueOf(totalItens));
    }

    public void clearCarrinho(){

        totalItens = 0;
        quantidadeCarrinhoItens.setText("");
    }

    private void createDrawer() throws ObjetoNaoEncontrado {

        boolean hasAdminRole = hasAdminRole();

        SideNav nav = new SideNav();
        nav.setSizeFull();

        if (hasAdminRole) {
            buildAdminMenu(nav);
        } else {
            buildClientMenu(nav);
        }

        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);
        addToDrawer(scroller);
    }

    private void buildAdminMenu(SideNav nav) {
        SideNavItem vendasLink = new SideNavItem("Vendas", VendasFeitasView.class, VaadinIcon.MONEY.create());
        SideNavItem clientesLink = new SideNavItem("Clientes", ClientesView.class, VaadinIcon.USERS.create());
        SideNavItem autoresLink = new SideNavItem("Autores", AutoresView.class, VaadinIcon.PENCIL.create());
        SideNavItem livrosLink = new SideNavItem("Livros", LivrosView.class, VaadinIcon.BOOK.create());
        SideNavItem produtosLink = new SideNavItem("Produtos", ProdutosView.class, VaadinIcon.PACKAGE.create());
        SideNavItem configuracoesLink = new SideNavItem("Configurações", ConfiguracoesView.class, VaadinIcon.COG.create());

        SideNavItem cadastrosMenu = new SideNavItem("Cadastros", "", VaadinIcon.DATABASE.create());
        cadastrosMenu.addItem(autoresLink, livrosLink, produtosLink);
        cadastrosMenu.setExpanded(true);

        nav.addItem(vendasLink,
                clientesLink,
                cadastrosMenu,
                configuracoesLink);
    }

    private void buildClientMenu(SideNav nav) {
        SideNavItem produtosLink = new SideNavItem("Produtos", CatalogoView.class, VaadinIcon.PACKAGE.create());
        SideNavItem pedidosLink = new SideNavItem("Meus Pedidos", PedidosView.class, VaadinIcon.MONEY.create());
        SideNavItem configuracoesLink = new SideNavItem("Configurações", ConfiguracoesView.class, VaadinIcon.COG.create());

        nav.addItem(produtosLink,
                pedidosLink,
                configuracoesLink);
    }

    private boolean hasAdminRole() throws ObjetoNaoEncontrado {
        Perfil perfil = segurancaService.getPerfilUsuarioLogado();
        return Perfil.ADMINISTRADOR == perfil;
    }
}
