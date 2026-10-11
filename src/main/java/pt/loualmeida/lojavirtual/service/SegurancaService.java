package pt.loualmeida.lojavirtual.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import pt.loualmeida.lojavirtual.enums.Perfil;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Usuario;

@Service
public class SegurancaService {
    private static final String LOGOUT_URL = "/";

    private final UsuarioService usuarioService;

    public SegurancaService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public Usuario getUsuarioLogado() throws ObjetoNaoEncontrado {
        UserDetails authenticatedUser = getUsuarioAutenticado();
        if (authenticatedUser != null) {
            return usuarioService.buscarUsuarioPorEmail(authenticatedUser.getUsername());
        }
        return null;
    }

    public Perfil getPerfilUsuarioLogado() throws ObjetoNaoEncontrado {
        Usuario usuarioLogado = getUsuarioLogado();
        if (usuarioLogado == null) {
            return null;
        }
        return usuarioLogado.getPerfil();
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(LOGOUT_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                null);
    }

    private UserDetails getUsuarioAutenticado() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) context.getAuthentication().getPrincipal();
        }
        // Anonymous or no authentication.
        return null;
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
    }
}
