package pt.loualmeida.lojavirtual.view.cliente;


import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.RolesAllowed;
import pt.loualmeida.lojavirtual.service.*;
import pt.loualmeida.lojavirtual.view.PrincipalView;
import pt.loualmeida.lojavirtual.view.PublicaView;

@Route(value = "catalogo-publico", layout = PublicaView.class)
@AnonymousAllowed
@PageTitle("Catalogo de Produtos")
public class CatalogoPublicoView extends CatalogoView {

    public CatalogoPublicoView(ProdutoService produtoService, AutorService autorService, CarrinhoService carrinhoService, SegurancaService segurancaService, ClienteService clienteService) {
        super(produtoService, autorService, carrinhoService, segurancaService, clienteService);
    }
}
