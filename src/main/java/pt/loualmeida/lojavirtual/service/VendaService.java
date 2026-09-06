package pt.loualmeida.lojavirtual.service;

import org.springframework.stereotype.Service;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Carrinho;
import pt.loualmeida.lojavirtual.model.ItemVenda;
import pt.loualmeida.lojavirtual.model.Produto;
import pt.loualmeida.lojavirtual.model.Venda;
import pt.loualmeida.lojavirtual.repository.ProdutoRepository;
import pt.loualmeida.lojavirtual.repository.VendaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VendaService {
    private final VendaRepository vendaRepository;

    private final CarrinhoService carrinhoService;

    public VendaService(VendaRepository vendaRepository, CarrinhoService carrinhoService)
    {
        this.vendaRepository = vendaRepository;

        this.carrinhoService = carrinhoService;
    }

    public Venda cadastrarVenda(Carrinho carrinho) throws ObjetoNaoEncontrado {
        Venda venda = new Venda();
        venda.setCliente(carrinho.getCliente());
        venda.setItens(criarItensVenda(carrinho.getProdutos()));
        venda.setDataCadastro(LocalDateTime.now());
        venda.setTotal(carrinho.getTotal());

        carrinhoService.deletarCarrinho(carrinho.getId());
        return vendaRepository.save(venda);
    }

    public List<ItemVenda> criarItensVenda(List<Produto> produtos) {
        List<ItemVenda> itens = new ArrayList<>();
        for (Produto produto : produtos) {
            itens.stream().filter(item ->
                            item.getProduto().getId().equals(produto.getId())).
                    findFirst().ifPresentOrElse(item -> {
                                item.setQuantidade(item.getQuantidade() + 1);
                            },
                            () -> itens.add(criarItem(produto))
                    );


        }
        return itens;
    }


    private ItemVenda criarItem(Produto produto) {
        ItemVenda item = new ItemVenda();
        item.setProduto(produto);
        item.setQuantidade(1);
        return item;
    }

    public List<Venda> buscarPendentes() {
        return vendaRepository.findByEnviadaFalseOrderByDataCadastroAsc();
    }

    public void marcarComoEnviada(Venda venda) {
        venda.setEnviada(true);
        vendaRepository.save(venda);
    }


}
