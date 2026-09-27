package pt.loualmeida.lojavirtual.service;

import org.springframework.stereotype.Service;
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.EstoqueIndisponivelException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.*;
import pt.loualmeida.lojavirtual.repository.CarrinhoRepository;
import pt.loualmeida.lojavirtual.repository.ClienteRepository;
import pt.loualmeida.lojavirtual.repository.ProdutoRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarrinhoService {
    private final CarrinhoRepository carrinhoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository,
                           ClienteRepository clienteRepository,
                           ProdutoRepository produtoRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;

    }

    public Carrinho cadastrarCarrinho(Carrinho carrinho) throws DadosInvalidosException {
        validarCarrinho(carrinho);
        carrinho.setCriacao(LocalDateTime.now());
        atualizarEstoque(carrinho.getProdutos().getFirst(), true); //Fragil
        return carrinhoRepository.save(carrinho);
    }

    private void validarCarrinho(Carrinho carrinho) throws DadosInvalidosException {

        if(carrinho.getCliente() == null){
            throw new DadosInvalidosException("Não foi informado um o proprietário deste carrinho.");
        }
        if(carrinho.getProdutos() == null|| carrinho.getProdutos().isEmpty()){
            throw new DadosInvalidosException("Nenhum produto foi colocado no carrinho.");
        }
    }


    private void atualizarEstoque(Produto produto, boolean remocao){
        if(remocao) {
            if (produto.getEstoque() == 0) {
                throw new EstoqueIndisponivelException(produto);
            }
            produto.setEstoque(produto.getEstoque() - 1);
        }
        else{
            produto.setEstoque(produto.getEstoque() + 1);
        }
        produtoRepository.save(produto);

    }

    public Carrinho adicionarItem(Long carrinhoID, Produto produto) throws ObjetoNaoEncontrado {
        Optional<Carrinho> optCarrinho = carrinhoRepository.findById(carrinhoID);
        if(optCarrinho.isPresent()){
            Carrinho carrinho = optCarrinho.get();
            carrinho.getProdutos().add(produto);
            atualizarEstoque(produto, true);
            carrinhoRepository.save(carrinho);

            return carrinho;
        }
        throw new ObjetoNaoEncontrado(String.format("O carrinho %d não foi encontrado.", carrinhoID));
    }

    public void removerItem(Long carrinhoID, Produto produto) throws ObjetoNaoEncontrado {
        Optional<Carrinho> optCarrinho = carrinhoRepository.findById(carrinhoID);
        if(optCarrinho.isPresent()){
            Carrinho carrinho = optCarrinho.get();
            carrinho.getProdutos().remove(produto);
            atualizarEstoque(produto, false);
            carrinhoRepository.save(carrinho);
            return;
        }
        throw new ObjetoNaoEncontrado(String.format("O carrinho %d não foi encontrado.", carrinhoID));

    }

    public void limparCarrinho(Long carrinhoID) throws ObjetoNaoEncontrado {
        Optional<Carrinho> optCarrinho = carrinhoRepository.findById(carrinhoID);
        if(optCarrinho.isPresent()){
            Carrinho carrinho = optCarrinho.get();
            for(Produto produto : carrinho.getProdutos()){
                atualizarEstoque(produto, false);
            }
            carrinho.getProdutos().clear();
            carrinhoRepository.save(carrinho);
            return;
        }
        throw new ObjetoNaoEncontrado(String.format("O carrinho %d não foi encontrado.", carrinhoID));

    }

    public Carrinho buscarCarrinhoPorCliente(Long clienteID) throws ObjetoNaoEncontrado {
        Optional<Cliente> optCliente = clienteRepository.findById(clienteID);
        if(optCliente.isEmpty()){
            throw new ObjetoNaoEncontrado(String.format("O cliente %d não foi encontrado.", clienteID));
        }
        Cliente cliente = optCliente.get();
        Optional<Carrinho> optCarrinho = carrinhoRepository.findByCliente(cliente);
        return optCarrinho.orElse(null);

    }

    public boolean existeCarrinhoDoCliente(Long clienteId) throws ObjetoNaoEncontrado {
        return buscarCarrinhoPorCliente(clienteId) != null;
    }

    public void deletarCarrinho(Long carrinhoID) throws ObjetoNaoEncontrado {
        Optional<Carrinho> optCarrinho = carrinhoRepository.findById(carrinhoID);
        if(optCarrinho.isPresent()){
            Carrinho carrinho = optCarrinho.get();
            carrinhoRepository.delete(carrinho);
            return;
        }
        throw new ObjetoNaoEncontrado(String.format("O carrinho %d não foi encontrado.", carrinhoID));

    }

    public Map<Produto, Long> mapaDeCompras(Carrinho carrinho){

        List<Produto> produtos = carrinho.getProdutos();
        return produtos.stream()
                .collect(Collectors.groupingBy(produto -> produto, Collectors.counting()));
    }




}
