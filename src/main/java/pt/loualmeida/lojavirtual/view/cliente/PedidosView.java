package pt.loualmeida.lojavirtual.view.cliente;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import pt.loualmeida.lojavirtual.view.PrincipalView;

@Route(value = "pedidos", layout = PrincipalView.class)
@RolesAllowed("CLIENTE")
public class PedidosView extends VerticalLayout {
}
