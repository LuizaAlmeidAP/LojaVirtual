package pt.loualmeida.lojavirtual.service;

import org.springframework.stereotype.Service;
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Carrinho;
import pt.loualmeida.lojavirtual.model.Cliente;
import pt.loualmeida.lojavirtual.model.Produto;
import pt.loualmeida.lojavirtual.repository.CarrinhoRepository;
import pt.loualmeida.lojavirtual.repository.ClienteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CarrinhoService {
    private final CarrinhoRepository carrinhoRepository;
    private final ClienteRepository clienteRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository,
                           ClienteRepository clienteRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.clienteRepository = clienteRepository;

    }

    public Carrinho cadastrarCarrinho(Carrinho carrinho) throws DadosInvalidosException {
        validarCarrinho(carrinho);
        carrinho.setCriacao(LocalDateTime.now());

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

    public Carrinho adicionarItens(Long carrinhoID, List<Produto> produtos) throws ObjetoNaoEncontrado {
        Optional<Carrinho> optCarrinho = carrinhoRepository.findById(carrinhoID);
        if(optCarrinho.isPresent()){
            Carrinho carrinho = optCarrinho.get();
            carrinho.getProdutos().addAll(produtos);
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
            carrinhoRepository.save(carrinho);
            return;
        }
        throw new ObjetoNaoEncontrado(String.format("O carrinho %d não foi encontrado.", carrinhoID));

    }

    public void limparCarrinho(Long carrinhoID) throws ObjetoNaoEncontrado {
        Optional<Carrinho> optCarrinho = carrinhoRepository.findById(carrinhoID);
        if(optCarrinho.isPresent()){
            Carrinho carrinho = optCarrinho.get();
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

    public void deletarCarrinho(Long carrinhoID) throws ObjetoNaoEncontrado {
        Optional<Carrinho> optCarrinho = carrinhoRepository.findById(carrinhoID);
        if(optCarrinho.isPresent()){
            Carrinho carrinho = optCarrinho.get();
            carrinhoRepository.delete(carrinho);
            return;
        }
        throw new ObjetoNaoEncontrado(String.format("O carrinho %d não foi encontrado.", carrinhoID));

    }




}
