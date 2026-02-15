package pt.loualmeida.lojavirtual.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "configuracoes", layout = PrincipalView.class)
@RolesAllowed({"CLIENTE", "ADMINISTRADOR"})
public class ConfiguracoesView extends VerticalLayout {
}
