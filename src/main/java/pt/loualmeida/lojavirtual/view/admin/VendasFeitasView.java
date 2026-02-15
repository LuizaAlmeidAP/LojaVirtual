package pt.loualmeida.lojavirtual.view.admin;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import pt.loualmeida.lojavirtual.model.ItemVenda;
import pt.loualmeida.lojavirtual.model.Venda;
import pt.loualmeida.lojavirtual.service.VendaService;
import pt.loualmeida.lojavirtual.view.PrincipalView;

import java.util.List;

@Route(value = "admin/historico-vendas", layout = PrincipalView.class)// A rota que o admin acessará
@RolesAllowed("ADMINISTRADOR") // Restrição do Spring Security
public class VendasFeitasView extends VerticalLayout {

    private final VendaService vendaService;
    private final Grid<Venda> grid = new Grid<>(Venda.class, false);

    public VendasFeitasView(VendaService vendaService) {
        this.vendaService = vendaService;

        setSizeFull();
        configureGrid();
        atualizarLista();

        add(new H2("Pedidos Pendentes de Envio"), grid);
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        // 1. Colunas Principais da Venda
        grid.addColumn(Venda::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);

        grid.addColumn(new LocalDateTimeRenderer<>(
                Venda::getDataCadastro,
                "dd/MM/yyyy HH:mm"
        )).setHeader("Data do Pedido");

        grid.addColumn(venda -> venda.getCliente() != null ? venda.getCliente().getUsuario().getNome() : "N/A")
                .setHeader("Cliente");

        grid.addColumn(venda -> "R$ " + venda.getTotal())
                .setHeader("Total");

        // 2. Coluna de Ação (Botão Enviar)
        grid.addComponentColumn(venda -> {
            Button btnEnviar = new Button("Marcar como Enviado", VaadinIcon.TRUCK.create());
            btnEnviar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            btnEnviar.addClickListener(event -> {
                vendaService.marcarComoEnviada(venda);
                Notification.show("Venda #" + venda.getId() + " marcada como enviada.");
                atualizarLista(); // Remove o item da lista visualmente
            });

            return btnEnviar;
        }).setHeader("Ações");

        // 3. Detalhes da Linha (Para mostrar os Itens da Venda)
        grid.setItemDetailsRenderer(new ComponentRenderer<>(this::criarGridItens));
    }

    // Cria uma sub-grid para mostrar os itens quando expandir a linha
    private Grid<ItemVenda> criarGridItens(Venda venda) {
        Grid<ItemVenda> gridItens = new Grid<>(ItemVenda.class, false);
        gridItens.setItems(venda.getItens());
        gridItens.addThemeVariants(GridVariant.LUMO_COMPACT);
        //gridItens.setHeightByRows(true); // Altura dinâmica

        gridItens.addColumn(item -> item.getProduto().getLivro().getTitulo()).setHeader("Produto");
        gridItens.addColumn(ItemVenda::getQuantidade).setHeader("Qtd");
        gridItens.addColumn(item -> "R$ " + item.getPrecoUnitario()).setHeader("Valor Unit.");
        gridItens.addColumn(item -> "R$ " + item.getPrecoTotal()).setHeader("Total Item");

        return gridItens;
    }

    private void atualizarLista() {
        // Busca apenas as não enviadas, ordenadas pela data (antigas primeiro)
        List<Venda> vendas = vendaService.buscarPendentes();
        grid.setItems(vendas);
    }
}