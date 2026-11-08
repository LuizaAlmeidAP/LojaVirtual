package pt.loualmeida.lojavirtual.view.cliente;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pt.loualmeida.lojavirtual.model.Usuario;
import pt.loualmeida.lojavirtual.service.ClienteService;
import pt.loualmeida.lojavirtual.service.UsuarioService;

import java.util.Locale;

@Route("cadastro-cliente")
@AnonymousAllowed
public class CadastroClienteView extends VerticalLayout {
    private FormLayout form = new FormLayout();
    private UsuarioService usuarioService;
    private ClienteService clienteService;
    public CadastroClienteView(UsuarioService usuarioService, ClienteService clienteService) {
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;

        VerticalLayout layout = new VerticalLayout();
        formularioCadastro();
        layout.add(form);




    }

    public void formularioCadastro() {

        form.setSizeFull();
        TextField nomeUsuario = new TextField("Nome");
        nomeUsuario.setId("nomeUsuario");
        EmailField emailField = new EmailField("E-mail");
        emailField.setId("email");
        TextField documentoField = new TextField("Documento");
        documentoField.setId("documento");
        PasswordField pass = new PasswordField("Senha");
        pass.setId("pass");
        PasswordField confirmacao = new PasswordField("Confirmação de senha");
        confirmacao.setId("confirmacao");
        DatePicker nascimento = new DatePicker("Data de nascimento");
        nascimento.setId("nascimento");
        nascimento.setLocale(new Locale("pt", "BR"));
        Button salvar = new Button("Cadastrar");
        salvar.addClickListener(e -> {
            Usuario usuario = criarUsuario();
            criarCliente(usuario);
        });
        form.add(nomeUsuario, emailField, documentoField, nascimento, pass, confirmacao, salvar);



    }

    public Usuario criarUsuario(){

        Usuario usuario = new Usuario();
       usuario.setNome(form.getElement().getAttribute("nome"));
        return usuario;
    }


    public void criarCliente(Usuario usuario){

    }

}
