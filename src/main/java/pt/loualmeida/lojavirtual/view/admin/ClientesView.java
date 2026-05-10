package pt.loualmeida.lojavirtual.view.admin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import jakarta.annotation.security.RolesAllowed;
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Autor;
import pt.loualmeida.lojavirtual.model.Cliente;
import pt.loualmeida.lojavirtual.repository.ClienteRepository;
import pt.loualmeida.lojavirtual.service.AutorService;
import pt.loualmeida.lojavirtual.service.ClienteService;
import pt.loualmeida.lojavirtual.view.Notificacoes;
import pt.loualmeida.lojavirtual.view.PrincipalView;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Route(value = "clientes", layout = PrincipalView.class)
@RolesAllowed("ADMINISTRADOR")
public class ClientesView extends VerticalLayout {



    private final ClienteService clienteService;
    private final Grid<Cliente> grid = new Grid<>(Cliente.class, false);
    private final TextField filterText = new TextField();
    private final ClienteRepository clienteRepository;

    public ClientesView(ClienteService clienteService, ClienteRepository clienteRepository) {
        this.clienteService = clienteService;

        setSizeFull();
        configureGrid();

        // Cria a barra de topo (Filtro + Botão)
        HorizontalLayout toolbar = getToolbar();

        add(toolbar, grid);

        // Carrega dados iniciais
        atualizarLista();
        this.clienteRepository = clienteRepository;
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filtrar por nome...");
        filterText.setClearButtonVisible(true);
        // PERFORMANCE: Espera o usuário parar de digitar um pouco antes de ir ao banco
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> atualizarLista());



        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        // 1. Coluna de FOTO (Renderizador Personalizado)
        grid.addColumn(new ComponentRenderer<>(cliente -> {
            Image image;

            if (cliente.getUsuario().getFoto() != null && cliente.getUsuario().getFoto().length > 0) {

                // --- NOVA ABORDAGEM (Sem StreamResource) ---
                // Cria um handler que fornece o InputStream sob demanda
                DownloadHandler handler = DownloadHandler.fromInputStream(context ->
                        new DownloadResponse(
                                new ByteArrayInputStream(cliente.getUsuario().getFoto()), // O Stream de dados
                                "foto_" + cliente.getId() + ".png",          // Nome do arquivo (importante para cache/tipo)
                                "image/png",                               // Mime Type (opcional, mas recomendado)
                                cliente.getUsuario().getFoto().length                     // Tamanho (opcional)
                        )
                );

                // Passa o handler diretamente para a imagem
                image = new Image(handler, "Foto de " + cliente.getUsuario().getNome());

            } else {
                // Placeholder continua igual
                image = new Image("imagens/user-placeholder.png", "Sem foto");
            }

            // Estilização (igual ao anterior)
            image.setWidth("50px");
            image.setHeight("50px");
            image.getStyle().set("border-radius", "50%");
            image.getStyle().set("object-fit", "cover");

            return image;

        })).setHeader("Foto").setFlexGrow(0).setWidth("80px");

        // 2. Colunas de Texto Simples
        grid.addColumn(cliente -> {
            return cliente.getUsuario().getNome();
        }).setHeader("Nome").setSortable(true);

        // 3. Colunas de Data Formatada
        grid.addColumn(new LocalDateRenderer<>(
                Cliente::getNascimento, "dd/MM/yyyy")
        ).setHeader("Nascimento");
        grid.addColumn(Cliente::getDocumento).setHeader("Documento");
        grid.addComponentColumn(cliente -> {
            Button bntExcluirCliente = new Button(VaadinIcon.TRASH.create());
            bntExcluirCliente.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            bntExcluirCliente.setTooltipText("Excluir cliente");
            bntExcluirCliente.addClickListener(e -> {
                abrirDialogoExcluirCliente(cliente);
            });

            return bntExcluirCliente;
        }).setHeader("Excluir cliente");


    }

    private void abrirDialogoExcluirCliente(Cliente cliente) {

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Excluir Cliente");
        TextField nomeCliente = new TextField("Nome");
        nomeCliente.setReadOnly(true);
        nomeCliente.setValue(cliente.getUsuario().getNome());
        EmailField emailCliente = new EmailField("E-mail");
        emailCliente.setReadOnly(true);
        emailCliente.setValue(cliente.getUsuario().getEmail());
        TextArea motivoCliente = new TextArea("Motivo");
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(false);
        dialogLayout.getStyle().set("width", "400px"); // Largura fixa para ficar bonito

        dialog.add(dialogLayout);
        Button btnSalvar = new Button("Salvar", e -> {
            try{
                clienteService.excluirCliente(cliente.getId(), motivoCliente.getValue());

            }
            catch (ObjetoNaoEncontrado e){

            }
        });
        btnSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnCancelar = new Button("Cancelar", e -> dialog.close());

        dialog.getFooter().add(btnCancelar, btnSalvar);

        dialog.open();




    }

    private void atualizarLista() {
        if (filterText.isEmpty()) {
            grid.setItems(clienteService.buscarTodosCliente("nome"));
        } else {
            grid.setItems(clienteService.buscarClientePorNome(filterText.getValue()));
        }
    }


}
