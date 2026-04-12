package pt.loualmeida.lojavirtual.view.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
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
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
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
import pt.loualmeida.lojavirtual.enums.Categoria;
import pt.loualmeida.lojavirtual.enums.FaixaEtaria;
import pt.loualmeida.lojavirtual.enums.Genero;
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Autor;
import pt.loualmeida.lojavirtual.model.Livro;
import pt.loualmeida.lojavirtual.repository.LivroRepository;
import pt.loualmeida.lojavirtual.service.AutorService;
import pt.loualmeida.lojavirtual.service.LivroService;
import pt.loualmeida.lojavirtual.view.Notificacoes;
import pt.loualmeida.lojavirtual.view.PrincipalView;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "livros", layout = PrincipalView.class)
@RolesAllowed("ADMINISTRADOR")
public class LivrosView extends VerticalLayout {

    private final LivroService livroService;
    private final Grid<Livro> grid = new Grid<>(Livro.class, false);
    private final TextField filterText = new TextField();
    private final AutorService autorService;

    public LivrosView(LivroService livroService, AutorService autorService) throws ObjetoNaoEncontrado {
        this.livroService = livroService;

        setSizeFull();
        configureGrid();

        // Cria a barra de topo (Filtro + Botão)
        HorizontalLayout toolbar = getToolbar();

        add(toolbar, grid);

        // Carrega dados iniciais
        atualizarLista();

        this.autorService = autorService;
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filtrar por título...");
        filterText.setClearButtonVisible(true);
        // PERFORMANCE: Espera o usuário parar de digitar um pouco antes de ir ao banco
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> {
            atualizarLista();
        });

        Button addAutorBtn = new Button("Novo Livro");
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
        grid.addColumn(new ComponentRenderer<>(livro -> {
            Image image;

            if (livro.getCapa() != null && livro.getCapa().length > 0) {

                // --- NOVA ABORDAGEM (Sem StreamResource) ---
                // Cria um handler que fornece o InputStream sob demanda
                DownloadHandler handler = DownloadHandler.fromInputStream(context ->
                        new DownloadResponse(
                                new ByteArrayInputStream(livro.getCapa()), // O Stream de dados
                                "foto_" + livro.getId() + ".png",          // Nome do arquivo (importante para cache/tipo)
                                "image/png",                               // Mime Type (opcional, mas recomendado)
                                livro.getCapa().length                     // Tamanho (opcional)
                        )
                );

                // Passa o handler diretamente para a imagem
                image = new Image(handler, "Foto de " + livro.getTitulo());

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

        })).setHeader("Capa").setFlexGrow(0).setWidth("80px");


        grid.addColumn(Livro::getTitulo).setHeader("Título").setSortable(true);
        grid.addColumn(livro -> {
            List<String> nomesAutores = livro.getAutores().stream().map(Autor::getNome).toList();
            return String.join(", ", nomesAutores);
        }).setHeader("Autores");


        grid.addColumn(livro -> livro.getClassificacao().getTitulo()).setHeader("Classificação").setSortable(true);

        grid.addColumn(livro -> livro.getGenero().getCategoria().getTitulo()).setHeader("Categoria");
        grid.addColumn(livro -> livro.getGenero().getTitulo()).setHeader("Género");




        // 5. Botão de Ação (Visualizar Livros)
        grid.addComponentColumn(livro -> {
            Button btnLivros = new Button(VaadinIcon.BOOK.create());
            btnLivros.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            btnLivros.setTooltipText("Detalhes");

            btnLivros.addClickListener(e -> {
                // Exemplo: Navegar para uma rota de livros passando o ID do autor
                // UI.getCurrent().navigate(LivrosView.class, autor.getId());
                Notification.show("Visualizando livro: " + livro.getTitulo());
            });
            return btnLivros;
        }).setHeader("Detalhes");


        grid.addComponentColumn(livro -> {
            Button bntEditarLivro = new Button(VaadinIcon.PENCIL.create());
            bntEditarLivro.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            bntEditarLivro.setTooltipText("Editar Livro");
            bntEditarLivro.addClickListener(e -> {
                abrirDialogoCadastro(livro);
            });


            return bntEditarLivro;
        }).setHeader("Editar Livro");
    }

    private void atualizarLista()  {
        if (filterText.isEmpty()) {
            grid.setItems(livroService.buscarTodosLivros("titulo"));
        } else {
            grid.setItems(livroService.buscarLivroPorTitulo(filterText.getValue()));
        }
    }

    private void abrirDialogoCadastro(Livro antigoLivro) {


        Dialog dialog = new Dialog();

        if (antigoLivro == null) {
            dialog.setHeaderTitle("Novo Livro");
        } else {
            dialog.setHeaderTitle("Editar Livro");
        }

        dialog.setCloseOnOutsideClick(false);
        // 1. Instância do objeto que será salvo

        Livro livro = new Livro();
        boolean novoLivro = true;
        if (antigoLivro != null) {
            livro.setId(antigoLivro.getId());
            novoLivro = false;
        }
        // 2. Criação dos Campos

        TextField tituloField = new TextField("Título");


        TextArea autoresField = new TextArea("Autores selecionados");
        autoresField.setReadOnly(true);
        MultiSelectComboBox<Autor> autoresSeletor = null;
        autoresSeletor = new MultiSelectComboBox<>("Autores");
        List<Autor> listaAutores = autorService.buscarTodosAutores("nome");
        autoresSeletor.setItems(listaAutores);
        autoresSeletor.setItemLabelGenerator(Autor::getNome);


        autoresSeletor.addValueChangeListener(e -> {
            String autoresSelecionados = e.getValue().stream()
                    .map(Autor::getNome)
                    .collect(Collectors.joining(", "));
            autoresField.setValue(autoresSelecionados);
        });




        RadioButtonGroup<Categoria> categoriasField = new RadioButtonGroup<>("Categoria");
        categoriasField.setItems(Categoria.values());
        categoriasField.setItemLabelGenerator(Categoria::getTitulo);
        ComboBox<Genero> generoField = new ComboBox<>("Gênero");
        generoField.setEnabled(false);
        categoriasField.addValueChangeListener(e -> {
            Categoria categoriaEscolhida = e.getValue();
            List<Genero> listaDeGenero = Genero.getGenerosDaCategoria(categoriaEscolhida);
            generoField.setItems(listaDeGenero);
            generoField.setItemLabelGenerator(Genero::getTitulo);
            generoField.setEnabled(true);
        });
        ComboBox<FaixaEtaria> faixaEtariaComboBox = new ComboBox<>("Faixa Etaria");
        faixaEtariaComboBox.setItems(FaixaEtaria.values());
        faixaEtariaComboBox.setItemLabelGenerator(FaixaEtaria::getTitulo);
        TextArea descricaoField = new TextArea("Descricao");
        TextArea sinopseField = new TextArea("Sinopse");

        if (!novoLivro) {
            tituloField.setValue(antigoLivro.getTitulo());
            tituloField.setReadOnly(true);
            autoresField.setValue(antigoLivro.getAutores().stream().map(Autor::getNome).collect(Collectors.joining(", ")));
            autoresField.setReadOnly(true);
            autoresSeletor.setValue(antigoLivro.getAutores());
            autoresSeletor.setReadOnly(true);

            faixaEtariaComboBox.setValue(antigoLivro.getClassificacao());
            descricaoField.setValue(antigoLivro.getDescricao());
            sinopseField.setValue(antigoLivro.getSinopse());
            categoriasField.setValue(antigoLivro.getGenero().getCategoria());
            generoField.setValue(antigoLivro.getGenero());

        }

        // 4. Configuração do Upload (Foto)


        if (!novoLivro) {
            livro.setCapa(antigoLivro.getCapa());
        }

        Upload uploadFoto = uploadFoto(livro);

        // 4. Layout do Formulário
        FormLayout formLayout = new FormLayout();

        formLayout.add(tituloField, autoresSeletor, autoresField, categoriasField, generoField, faixaEtariaComboBox, descricaoField, sinopseField);

        //    formLayout.add(tituloField, autoresField, categoriasField, generoField, faixaEtariaComboBox, descricaoField, sinopseField);



        VerticalLayout uploadLayout = new VerticalLayout(new Span("Capa"), uploadFoto);
        uploadLayout.setPadding(false);
        uploadLayout.setSpacing(false);

        VerticalLayout dialogLayout = new VerticalLayout(formLayout, uploadLayout);
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(false);
        dialogLayout.getStyle().set("width", "400px"); // Largura fixa para ficar bonito

        dialog.add(dialogLayout);

        // 5. Configuração do Binder (Validação e vínculo com a entidade)
        Binder<Livro> binder = new Binder<>(Livro.class);

        binder.forField(tituloField)
                .asRequired("O título é obrigatório")
                .bind(Livro::getTitulo, Livro::setTitulo);


        binder.forField(autoresSeletor)
                .asRequired("Preencher com os autores é obrigatório")
                .bind(Livro::getAutores, Livro::setAutores);


        binder.forField(generoField)
                .asRequired("Preencher com o gênero")
                        .bind(Livro::getGenero, Livro::setGenero);
        binder.forField(faixaEtariaComboBox)
                .asRequired("Preencher com a faixa etária")
                        .bind(Livro::getClassificacao, Livro::setClassificacao);
        binder.forField(descricaoField)
                .asRequired("Preencher com a descrição")
                        .bind(Livro::getDescricao, Livro::setDescricao);
        binder.forField(sinopseField)
                .asRequired("Preencher com a sinopse")
                        .bind(Livro::getSinopse, Livro::setSinopse);



        // 4. Botão Salvar (Simplificado)
        Button btnSalvar = new Button("Salvar", e -> {
            try {
                // Valida e preenche os campos de texto (Nome, Datas)
                binder.writeBean(livro);

                // NÃO precisamos mais ler buffer aqui.
                // A foto já foi preenchida pelo UploadHandler no passo 1.
                String mensagem;
                if (antigoLivro != null) {
                    livroService.atualizarLivro(livro);
                    mensagem = "Livro atualizado com sucesso!";
                } else {

                    livroService.cadastrarLivro(livro); // Salva no banco
                    mensagem = "Livro cadastrado com sucesso!";

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

    public Upload uploadFoto(Livro livro) {
        InMemoryUploadHandler uploadHandler = UploadHandler.inMemory((metadata, bytes) -> {
            // Este código roda assim que o upload termina com sucesso
            // 'bytes' é o array de bytes do arquivo (byte[])
            livro.setCapa(bytes);
            Notificacoes.sucesso("Capa recebida: " + metadata.fileName());
        });


        Upload uploadFoto = new Upload(uploadHandler);
        uploadFoto.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        uploadFoto.setMaxFiles(1);
        uploadFoto.setDropLabel(new Span("Arraste a capa aqui"));
// Limite de 5MB
        uploadFoto.setMaxFileSize(5 * 1024 * 1024);


        uploadFoto.addFileRemovedListener(e -> {
            livro.setCapa(null);
            Notificacoes.aviso("Capa removida.");
        });


// As configurações continuam as mesmas
        uploadFoto.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        uploadFoto.setMaxFiles(1);

        uploadFoto.setDropLabel(new Span("Arraste a capa aqui ou clique para selecionar"));

        // Limita o tamanho (ex: 5MB)
        int maxFileSizeInBytes = 5 * 1024 * 1024;
        uploadFoto.setMaxFileSize(maxFileSizeInBytes);

        uploadFoto.addFileRejectedListener(event -> {
            Notificacoes.aviso("Arquivo rejeitado: " + event.getErrorMessage());
        });
      return uploadFoto;
    }

}


