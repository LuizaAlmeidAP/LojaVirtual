package pt.loualmeida.lojavirtual.view;

import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pt.loualmeida.lojavirtual.view.admin.ProdutosView;
import pt.loualmeida.lojavirtual.view.cliente.CatalogoView;


@Route("publica")
@AnonymousAllowed
public class PublicaView extends AppLayout {


    private static final String TITULO_APP = "Livraria";




    public PublicaView() {
            HorizontalLayout header = createHeader();
            addToNavbar(header);
    }

    private HorizontalLayout createHeader() {
        DrawerToggle toggle = new DrawerToggle();

        Image logo = new Image("imagens/logo_for_loualmeida.jpg", TITULO_APP);
        logo.setHeight("50px");
        RouterLink logoLink = new RouterLink();
        logoLink.setRoute(CatalogoView.class);
        logoLink.add(logo);

        H1 title = new H1(TITULO_APP);
        title.getStyle().setMargin("0");
        RouterLink brand = new RouterLink();
        brand.setRoute(CatalogoView.class);
        brand.add(title);





        Button cartLink = new Button();
        Icon cartIcon = VaadinIcon.CART.create();
        cartLink.setIcon(cartIcon);
        cartLink.addClickListener(event -> {
            cartLink.getUI().ifPresent(ui -> {ui.navigate(LoginView.class);});
        });



        Button loginButton = new Button("Login", VaadinIcon.SIGN_IN.create());
        loginButton.addClickListener(event -> {
            loginButton.getUI().ifPresent(ui -> {ui.navigate(LoginView.class);});

        });


        HorizontalLayout userSection = new HorizontalLayout(cartLink,  loginButton);
        userSection.setAlignItems(FlexComponent.Alignment.CENTER);
        HorizontalLayout header = new HorizontalLayout(toggle, logoLink, brand, userSection);
        header.setWidthFull();
        header.expand(brand);
        header.getStyle().set("flex-wrap", "wrap");
        userSection.getStyle().set("margin-left", "auto");

        return header;
    }



}
