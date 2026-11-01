package pt.loualmeida.lojavirtual.view.admin;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import pt.loualmeida.lojavirtual.view.PrincipalView;

@Route(value = "cadastro-admin", layout = PrincipalView.class)
@RolesAllowed("ADMINISTRADOR")
public class CadastroAdminView extends VerticalLayout {
}
