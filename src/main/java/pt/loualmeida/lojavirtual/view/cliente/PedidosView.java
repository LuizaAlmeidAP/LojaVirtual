package pt.loualmeida.lojavirtual.view.cliente;

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
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Cliente;
import pt.loualmeida.lojavirtual.model.ItemVenda;
import pt.loualmeida.lojavirtual.model.Usuario;
import pt.loualmeida.lojavirtual.model.Venda;
import pt.loualmeida.lojavirtual.service.ClienteService;
import pt.loualmeida.lojavirtual.service.SegurancaService;
import pt.loualmeida.lojavirtual.service.VendaService;
import pt.loualmeida.lojavirtual.view.PrincipalView;

import java.util.List;

@Route(value = "pedidos", layout = PrincipalView.class)
@RolesAllowed("CLIENTE")
public class PedidosView extends VerticalLayout {

    private final VendaService vendaService;
    private final SegurancaService segurancaService;
    private final ClienteService clienteService;
    private final Grid<Venda> grid = new Grid<>(Venda.class, false);

    public PedidosView(VendaService vendaService, SegurancaService segurancaService, ClienteService clienteService) throws ObjetoNaoEncontrado {
        this.vendaService = vendaService;
        this.segurancaService = segurancaService;
        this.clienteService = clienteService;

        setSizeFull();
        configureGrid();
        atualizarLista();

        add(new H2("Meus pedidos"), grid);
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);


        grid.addColumn(Venda::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);

        grid.addColumn(new LocalDateTimeRenderer<>(
                Venda::getDataCadastro,
                "dd/MM/yyyy HH:mm"
        )).setHeader("Data do Pedido");

        grid.addColumn(venda -> venda.getCliente() != null ? venda.getCliente().getUsuario().getNome() : "N/A")
                .setHeader("Cliente");

        grid.addColumn(Venda :: getValorTotalFormatado)
                .setHeader("Total");

        grid.addComponentColumn(venda -> {


            Button btnRecebida = new Button("Marcar como Recebido", VaadinIcon.PACKAGE.create());
            btnRecebida.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            if(!venda.isEnviada()){
                btnRecebida.setEnabled(false);
                return btnRecebida;
            }
            if(venda.isRecebida()){
                btnRecebida.setEnabled(false);
                btnRecebida.setText("Recebida");
                return btnRecebida;
            }
            btnRecebida.addClickListener(event -> {
                vendaService.marcarComoRecebida(venda);
                Notification.show("Compra #" + venda.getId() + " marcada como recebida.");
               btnRecebida.setEnabled(false);
               btnRecebida.setText("Recebida");

            });

            return btnRecebida;
        }).setHeader("Ações");


        grid.setItemDetailsRenderer(new ComponentRenderer<>(this::criarGridItens));
    }


    private Grid<ItemVenda> criarGridItens(Venda venda) {
        Grid<ItemVenda> gridItens = new Grid<>(ItemVenda.class, false);
        gridItens.setItems(venda.getItens());
        gridItens.addThemeVariants(GridVariant.LUMO_COMPACT);


        gridItens.addColumn(item -> item.getProduto().getLivro().getTitulo()).setHeader("Produto");
        gridItens.addColumn(ItemVenda::getQuantidade).setHeader("Qtd");
        gridItens.addColumn(ItemVenda :: getPrecoUnitarioFormatado).setHeader("Valor Unit.");
        gridItens.addColumn(ItemVenda::getPrecoTotalFormatado).setHeader("Total Item");

        gridItens.setAllRowsVisible(true);

        return gridItens;
    }

    private void atualizarLista() throws ObjetoNaoEncontrado {
        Usuario usuario = segurancaService.getUsuarioLogado();
        Cliente cliente = clienteService.buscarClientePorUsuario(usuario);
        List<Venda> vendas = vendaService.buscarPedidosCliente(cliente);
        grid.setItems(vendas);
    }



}
