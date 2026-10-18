package pt.loualmeida.lojavirtual.view.cliente;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import pt.loualmeida.lojavirtual.service.*;
import pt.loualmeida.lojavirtual.view.PrincipalView;

@Route(value = "catalogo", layout = PrincipalView.class)
@RolesAllowed("CLIENTE")
@PageTitle("Catalogo de Produtos")
public class CatalogoAutenticadoView extends CatalogoView {
    public CatalogoAutenticadoView(ProdutoService produtoService, AutorService autorService, CarrinhoService carrinhoService, SegurancaService segurancaService, ClienteService clienteService) {
        super(produtoService, autorService, carrinhoService, segurancaService, clienteService);
    }
}
