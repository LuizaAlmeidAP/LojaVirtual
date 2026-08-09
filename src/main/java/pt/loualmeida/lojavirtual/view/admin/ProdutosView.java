package pt.loualmeida.lojavirtual.view.admin;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Image;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;

import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;

import jakarta.annotation.security.RolesAllowed;

import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Autor;
import pt.loualmeida.lojavirtual.model.Livro;
import pt.loualmeida.lojavirtual.model.Produto;

import pt.loualmeida.lojavirtual.service.LivroService;
import pt.loualmeida.lojavirtual.service.ProdutoService;
import pt.loualmeida.lojavirtual.view.DialogoView;
import pt.loualmeida.lojavirtual.view.Notificacoes;
import pt.loualmeida.lojavirtual.view.PrincipalView;

import java.io.ByteArrayInputStream;

import java.util.List;
import java.util.stream.Collectors;


@Route(value = "produtos", layout = PrincipalView.class)
@RolesAllowed("ADMINISTRADOR")
public class ProdutosView extends VerticalLayout {

    private final ProdutoService produtoService;
    private final Grid<Produto> grid = new Grid<>(Produto.class, false);
    private final TextField filterText = new TextField();
    private final LivroService livroService;


    public ProdutosView(ProdutoService produtoService, LivroService livroService) throws ObjetoNaoEncontrado {
        this.produtoService = produtoService;
        this.livroService = livroService;
        setSizeFull();
        configureGrid();
        HorizontalLayout toolbar = getToolbar();
        add(toolbar, grid);
        atualizarLista();

    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filtrar por nome...");
        filterText.setClearButtonVisible(true);
        // PERFORMANCE: Espera o usuário parar de digitar um pouco antes de ir ao banco
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> {
            try {
                atualizarLista();
            } catch (ObjetoNaoEncontrado ex) {
                throw new RuntimeException(ex);
            }
        });

        Button addAutorBtn = new Button("Novo Produto");
        addAutorBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addAutorBtn.addClickListener(e -> {
            abrirDialogoCadastro(null);
        });
        Button addSalvarBtn = new Button("Salvar alterações");
        addSalvarBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addSalvarBtn.addClickListener(e -> {
            salvarAlteracoes();
        });

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addAutorBtn,addSalvarBtn);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void salvarAlteracoes() {
        produtoService.atualizarProdutos(grid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()));
        DialogoView.sucesso("Estoque atualizado com sucesso!");

    }


    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addColumn(new ComponentRenderer<>(produto -> {
            Image image;


            if (produto.getLivro() != null && produto.getLivro().getCapa() != null && produto.getLivro().getCapa().length > 0) {


                DownloadHandler handler = DownloadHandler.fromInputStream(context ->
                        new DownloadResponse(
                                new ByteArrayInputStream(produto.getLivro().getCapa()), // O Stream de dados
                                "foto_" + produto.getId() + ".png",          // Nome do arquivo (importante para cache/tipo)
                                "image/png",                               // Mime Type (opcional, mas recomendado)
                                produto.getLivro().getCapa().length                     // Tamanho (opcional)
                        )
                );


                image = new Image(handler, "Foto de " + produto.getLivro().getTitulo());

            } else {

                image = new Image("imagens/user-placeholder.png", "Sem foto");
            }
            image.setWidth("50px");
            image.setHeight("50px");
            image.getStyle().set("border-radius", "50%");
            image.getStyle().set("object-fit", "cover");

            return image;

        })).setHeader("Capa").setFlexGrow(0).setWidth("80px");


        grid.addColumn(produto -> {
            return produto.getLivro().getTitulo();
        }).setHeader("Título").setSortable(true);
        grid.addColumn(produto -> {
            List<String> nomesAutores = produto.getLivro().getAutores().stream().map(Autor::getNome).toList();
            return String.join(", ", nomesAutores);
        }).setHeader("Autores");
        grid.addColumn(Produto::getValor).setHeader("Valor").setSortable(true);



        grid.addColumn(new ComponentRenderer<> (produto -> {
            IntegerField estoqueField = new IntegerField();
            estoqueField.setMaxWidth(150, Unit.PIXELS);
            estoqueField.setMin(0);
            estoqueField.setStep(1);
            estoqueField.setValue(produto.getEstoque());
            grid.addItemDoubleClickListener(item -> {
                estoqueField.setStepButtonsVisible(true);
                grid.getEditor().editItem(item.getItem());
                estoqueField.focus();
            });
            estoqueField.addValueChangeListener(e -> {
                if(e.getValue() != null) {
                    produto.setEstoque(e.getValue());
                }

            });

            return estoqueField;
        })).setHeader("Estoque");






        grid.addComponentColumn(produto -> {
            Button btnProduto = new Button(VaadinIcon.BOOK.create());
            btnProduto.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            btnProduto.setTooltipText("Detalhes");

            btnProduto.addClickListener(e -> {
                Notification.show("Visualizando produto: " + produto.getLivro().getTitulo());
            });
            return btnProduto;
        }).setHeader("Detalhes");


        grid.addComponentColumn(produto -> {
            Button bntEditarProduto = new Button(VaadinIcon.PENCIL.create());
            bntEditarProduto.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            bntEditarProduto.setTooltipText("Editar Produto");
            bntEditarProduto.addClickListener(e -> {
                abrirDialogoCadastro(produto);
            });


            return bntEditarProduto;
        }).setHeader("Editar Produto");

    }

    private void atualizarLista() throws ObjetoNaoEncontrado {
        if (filterText.isEmpty()) {
            grid.setItems(produtoService.buscarTodosProdutos("livro.titulo"));
        } else {
            grid.setItems(produtoService.buscarProdutoPorTitulo(filterText.getValue()));
        }
    }

    private void abrirDialogoCadastro(Produto antigoProduto) {


        Dialog dialog = new Dialog();

        if (antigoProduto == null) {
            dialog.setHeaderTitle("Novo Produto");
        } else {
            dialog.setHeaderTitle("Editar Produto");
        }

        dialog.setCloseOnOutsideClick(false);
        // 1. Instância do objeto que será salvo

        Produto produto = new Produto();
        boolean novoProduto = true;
        if (antigoProduto != null) {
            produto.setId(antigoProduto.getId());
            novoProduto = false;
        }
        // 2. Criação dos Campos



        ComboBox<Livro> comboLivro = new ComboBox<>();
        comboLivro.setLabel("Livro");

        List<Livro> listaLivros = livroService.buscarTodosLivros("titulo");
        comboLivro.setItems(listaLivros);

        comboLivro.setItemLabelGenerator(Livro::getTitulo);

        NumberField valor = new NumberField("Valor");
        IntegerField estoque = new IntegerField("Estoque");


        if (!novoProduto) {
           comboLivro.setValue(antigoProduto.getLivro());
           comboLivro.setReadOnly(true);
           valor.setValue(antigoProduto.getValor());
           estoque.setValue( antigoProduto.getEstoque());
        }

        // 4. Configuração do Upload (Foto)





        // 4. Layout do Formulário
        FormLayout formLayout = new FormLayout();

        formLayout.add(comboLivro, valor, estoque);







        VerticalLayout dialogLayout = new VerticalLayout(formLayout);
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(false);
        dialogLayout.getStyle().set("width", "400px"); // Largura fixa para ficar bonito

        dialog.add(dialogLayout);

        // 5. Configuração do Binder (Validação e vínculo com a entidade)
        Binder<Produto> binder = new Binder<>(Produto.class);

        binder.forField(comboLivro)
                .asRequired("O livro é obrigatório")
                .bind(Produto::getLivro, Produto::setLivro);
        binder.forField(valor)
                .asRequired("O valor é obrigatório")
                .bind(Produto::getValor, Produto::setValor);


        binder.forField(estoque)
                .asRequired("O estoque é obrigatório")
                .bind(Produto::getEstoque, Produto::setEstoque);



        // 4. Botão Salvar (Simplificado)
        Button btnSalvar = new Button("Salvar", e -> {
            try {
                // Valida e preenche os campos de texto (Nome, Datas)
                binder.writeBean(produto);

                // NÃO precisamos mais ler buffer aqui.
                // A foto já foi preenchida pelo UploadHandler no passo 1.
                String mensagem;
                if (antigoProduto != null) {
                    produtoService.atualizarProduto(produto);
                    mensagem = "Produto atualizado com sucesso!";
                } else {

                    produtoService.cadastrarProduto(produto); // Salva no banco
                    mensagem = "Produto cadastrado com sucesso!";

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



