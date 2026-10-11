package pt.loualmeida.lojavirtual.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

//Esquecer a senha
//Criar opção de cadastro

@Route("login")
@PageTitle("Login/Loja Virtual")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    private final LoginForm loginForm = new LoginForm();
    private final String nomeLoja = "Loja Virtual";

    public LoginView() {

        estilicacao();

        configurarAcoesDeLogin();

        H1 titulo = new H1(nomeLoja);
        titulo.getStyle().set("margin", "0");
        Paragraph texto = new Paragraph("Faça login com seu email e senha");
        texto.getStyle().set("margin-top", "0").set("color", "var(--lumo-secondary-text-color)");

        loginForm.addForgotPasswordListener(e -> {acaoEsqueciMinhaSenha();});
        loginForm.setAction("login");
        add(titulo, texto, loginForm);
    }


    private LoginI18n criarPortuguesI18n(){
        LoginI18n i18n =  LoginI18n.createDefault();
        i18n.getForm().setTitle("Login");
        i18n.getForm().setUsername("Email");
        i18n.getForm().setPassword("Senha");
        i18n.getForm().setSubmit("Entrar");
        i18n.getForm().setForgotPassword("Esqueci minha senha");
        String errorCampoObrigatorio = "Campo obrigatório";
        i18n.getErrorMessage().setUsername(errorCampoObrigatorio);
        i18n.getErrorMessage().setPassword(errorCampoObrigatorio);
        i18n.getErrorMessage().setTitle("Erro ao tentar fazer login");
        i18n.getErrorMessage().setMessage("Nome de usuario ou senha invalidos");
        return i18n;

    }

    private void estilicacao(){
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    private void configurarAcoesDeLogin(){
        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(true);
        loginForm.setI18n(criarPortuguesI18n());
    }


    //Fazer
    private void acaoEsqueciMinhaSenha(){
        loginForm.setAction("forgotPassword");

    }



    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginForm.setError(true);
        }


    }
}
