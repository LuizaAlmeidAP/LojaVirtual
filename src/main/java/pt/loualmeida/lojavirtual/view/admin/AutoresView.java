package pt.loualmeida.lojavirtual.view.admin;

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
import pt.loualmeida.lojavirtual.service.AutorService;
import pt.loualmeida.lojavirtual.view.Notificacoes;
import pt.loualmeida.lojavirtual.view.PrincipalView;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Route(value = "autores", layout = PrincipalView.class)
@RolesAllowed("ADMINISTRADOR")
public class AutoresView extends VerticalLayout {

    private final AutorService autorService;
    private final Grid<Autor> grid = new Grid<>(Autor.class, false);
    private final TextField filterText = new TextField();

    public AutoresView(AutorService autorService) {
        this.autorService = autorService;

        setSizeFull();
        configureGrid();

        // Cria a barra de topo (Filtro + Botão)
        HorizontalLayout toolbar = getToolbar();

        add(toolbar, grid);

        // Carrega dados iniciais
        atualizarLista();
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filtrar por nome...");
        filterText.setClearButtonVisible(true);
        // PERFORMANCE: Espera o usuário parar de digitar um pouco antes de ir ao banco
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> atualizarLista());

        Button addAutorBtn = new Button("Novo Autor");
        addAutorBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addAutorBtn.addClickListener(e -> {
            abrirDialogoCadastro(null);
        });

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addAutorBtn);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        // 1. Coluna de FOTO (Renderizador Personalizado)
        grid.addColumn(new ComponentRenderer<>(autor -> {
            Image image;

            if (autor.getFoto() != null && autor.getFoto().length > 0) {

                // --- NOVA ABORDAGEM (Sem StreamResource) ---
                // Cria um handler que fornece o InputStream sob demanda
                DownloadHandler handler = DownloadHandler.fromInputStream(context ->
                        new DownloadResponse(
                                new ByteArrayInputStream(autor.getFoto()), // O Stream de dados
                                "foto_" + autor.getId() + ".png",          // Nome do arquivo (importante para cache/tipo)
                                "image/png",                               // Mime Type (opcional, mas recomendado)
                                autor.getFoto().length                     // Tamanho (opcional)
                        )
                );

                // Passa o handler diretamente para a imagem
                image = new Image(handler, "Foto de " + autor.getNome());

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
        grid.addColumn(Autor::getNome).setHeader("Nome").setSortable(true);

        // 3. Colunas de Data Formatada
        grid.addColumn(new LocalDateRenderer<>(
                Autor::getNascimento, "dd/MM/yyyy")
        ).setHeader("Nascimento");

        grid.addColumn(autor -> {
            if (autor.getMorte() == null) {
                return "-";
            }
            return autor.getMorte().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }).setHeader("Falecimento");

        // 4. Quantidade de Livros
        grid.addColumn(Autor::getQuantidadeLivros).setHeader("Nº Livros");

        // 5. Botão de Ação (Visualizar Livros)
        grid.addComponentColumn(autor -> {
            Button btnLivros = new Button(VaadinIcon.BOOK.create());
            btnLivros.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            btnLivros.setTooltipText("Visualizar Livros");

            btnLivros.addClickListener(e -> {
                // Exemplo: Navegar para uma rota de livros passando o ID do autor
                // UI.getCurrent().navigate(LivrosView.class, autor.getId());
                Notification.show("Visualizando livros de: " + autor.getNome());
            });
            return btnLivros;
        }).setHeader("Livros");


        grid.addComponentColumn(autor -> {
            Button bntEditarAutor = new Button(VaadinIcon.PENCIL.create());
            bntEditarAutor.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            bntEditarAutor.setTooltipText("Editar Autor");
            bntEditarAutor.addClickListener(e -> {
                abrirDialogoCadastro(autor);
            });


            return bntEditarAutor;
        }).setHeader("Editar Autor");
    }

    private void atualizarLista() {
        if (filterText.isEmpty()) {
            grid.setItems(autorService.buscarTodosAutores("nome"));
        } else {
            grid.setItems(autorService.buscarAutorPorNome(filterText.getValue()));
        }
    }


    private void abrirDialogoCadastro(Autor antigoAutor) {

        Dialog dialog = new Dialog();
        if (antigoAutor == null) {
            dialog.setHeaderTitle("Novo Autor");
        } else {
            dialog.setHeaderTitle("Editar Autor");
        }

        dialog.setCloseOnOutsideClick(false);
        // 1. Instância do objeto que será salvo

        Autor autor = new Autor();

        if (antigoAutor != null) {
            autor.setId(antigoAutor.getId());
        }
        // 2. Criação dos Campos

        TextField nomeField = new TextField("Nome");
        DatePicker dataNascimento = new DatePicker("Data de Nascimento");
        DatePicker dataFalecimento = new DatePicker("Data de Falecimento (Opcional)");
        if (antigoAutor != null) {
            nomeField.setValue(antigoAutor.getNome());
            nomeField.setReadOnly(true);
            dataNascimento.setValue(antigoAutor.getNascimento());
            dataNascimento.setReadOnly(true);
            if (antigoAutor.getMorte() != null) {
                dataFalecimento.setValue(antigoAutor.getMorte());
                dataFalecimento.setReadOnly(true);
            }

        }

        // 4. Configuração do Upload (Foto)


        if (antigoAutor != null) {
            autor.setFoto(antigoAutor.getFoto());
        }
        InMemoryUploadHandler uploadHandler = UploadHandler.inMemory((metadata, bytes) -> {
            // Este código roda assim que o upload termina com sucesso
            // 'bytes' é o array de bytes do arquivo (byte[])
            autor.setFoto(bytes);
            Notificacoes.sucesso("Foto recebida: " + metadata.fileName());
        });


        Upload uploadFoto = new Upload(uploadHandler);
        uploadFoto.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        uploadFoto.setMaxFiles(1);
        uploadFoto.setDropLabel(new Span("Arraste a foto aqui"));
// Limite de 5MB
        uploadFoto.setMaxFileSize(5 * 1024 * 1024);


        uploadFoto.addFileRemovedListener(e -> {
            autor.setFoto(null);
            Notificacoes.aviso("Foto removida.");
        });


// As configurações continuam as mesmas
        uploadFoto.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        uploadFoto.setMaxFiles(1);

        uploadFoto.setDropLabel(new Span("Arraste a foto aqui ou clique para selecionar"));

        // Limita o tamanho (ex: 5MB)
        int maxFileSizeInBytes = 5 * 1024 * 1024;
        uploadFoto.setMaxFileSize(maxFileSizeInBytes);

        uploadFoto.addFileRejectedListener(event -> {
            Notificacoes.aviso("Arquivo rejeitado: " + event.getErrorMessage());
        });

        // 4. Layout do Formulário
        FormLayout formLayout = new FormLayout();
        formLayout.add(nomeField, dataNascimento, dataFalecimento);

        VerticalLayout uploadLayout = new VerticalLayout(new Span("Foto do Autor"), uploadFoto);
        uploadLayout.setPadding(false);
        uploadLayout.setSpacing(false);

        VerticalLayout dialogLayout = new VerticalLayout(formLayout, uploadLayout);
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(false);
        dialogLayout.getStyle().set("width", "400px"); // Largura fixa para ficar bonito

        dialog.add(dialogLayout);

        // 5. Configuração do Binder (Validação e vínculo com a entidade)
        Binder<Autor> binder = new Binder<>(Autor.class);

        binder.forField(nomeField)
                .asRequired("O nome é obrigatório")
                .bind(Autor::getNome, Autor::setNome);

        binder.forField(dataNascimento)
                .asRequired("A data de nascimento é obrigatória")
                .bind(Autor::getNascimento, Autor::setNascimento);

        binder.forField(dataFalecimento)
                .bind(Autor::getMorte, Autor::setMorte);

        // 4. Botão Salvar (Simplificado)
        Button btnSalvar = new Button("Salvar", e -> {
            try {
                // Valida e preenche os campos de texto (Nome, Datas)
                binder.writeBean(autor);

                // NÃO precisamos mais ler buffer aqui.
                // A foto já foi preenchida pelo UploadHandler no passo 1.
                String mensagem;
                if (antigoAutor != null) {
                    autorService.atualizarAutor(autor);
                    mensagem = "Autor atualizado com sucesso!";
                } else {
                    List<String> info = new ArrayList<>();
                    autorService.cadastrarAutor(autor, info); // Salva no banco
                    mensagem = "Autor cadastrado com sucesso!";
                    if (!info.isEmpty()) {
                        mensagem += "\n" + String.join("\n", info);
                    }
                }

                Notificacoes.sucesso(mensagem);
                atualizarLista();
                dialog.close();

            } catch (ValidationException validationException) {
                Notificacoes.aviso("Verifique os campos obrigatórios.");
            } catch (DadosInvalidosException | ObjetoNaoEncontrado ex) {
                Notificacoes.aviso(ex.getMessage());
            }
        });


        btnSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnCancelar = new Button("Cancelar", e -> dialog.close());

        dialog.getFooter().add(btnCancelar, btnSalvar);

        dialog.open();
    }
}
