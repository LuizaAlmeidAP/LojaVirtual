package pt.loualmeida.lojavirtual.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.charts.model.Navigation;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pt.loualmeida.lojavirtual.service.SegurancaService;

@Route("")
@AnonymousAllowed
public class HomeView extends AppLayout implements BeforeEnterObserver {
    private final SegurancaService segurancaService;

    public HomeView( SegurancaService segurancaService ) {
        this.segurancaService = segurancaService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if(segurancaService.isLoggedIn()) {
            event.forwardTo(PrincipalView.class);
        }
        else{
            event.forwardTo(PublicaView.class);
        }
    }
}
