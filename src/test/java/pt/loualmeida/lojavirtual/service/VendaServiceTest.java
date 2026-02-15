package pt.loualmeida.lojavirtual.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.loualmeida.lojavirtual.enums.FaixaEtaria;
import pt.loualmeida.lojavirtual.enums.Genero;
import pt.loualmeida.lojavirtual.enums.Perfil;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.*;
import pt.loualmeida.lojavirtual.repository.ProdutoRepository;
import pt.loualmeida.lojavirtual.repository.VendaRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static com.github.javaparser.utils.Utils.assertNotNull;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VendaServiceTest {
    @Mock
    private VendaRepository vendaRepository;
    @InjectMocks
    private VendaService vendaService;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private CarrinhoService carrinhoService;


    private Usuario instanciarUsuario(Perfil perfil) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("email@email.com");
        usuario.setSenha("senha");
        usuario.setPerfil(perfil);
        try {
            byte[] imagem = Files.readAllBytes(Paths.get("src/test/resources/CapaLadaroRaios.jpg"));
            usuario.setImagem(imagem);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return usuario;
    }

    private Cliente instanciarCliente(Usuario usuario) {
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setId(1L);
        cliente.getUsuario().setNome("Cliente");
        cliente.setDocumento("NIF");
        cliente.setNascimento(LocalDate.of(1973, Month.DECEMBER, 20));
        cliente.setImagem(usuario.getImagem());
        return cliente;
    }

    private Autor instanciarAutor() {
        Autor novoAutor = new Autor();
        novoAutor.setId(1L);
        novoAutor.setNome("Machado de Assis");
        novoAutor.setNascimento(LocalDate.of(1839, Month.JUNE, 21));
        novoAutor.setMorte(LocalDate.of(1908, Month.SEPTEMBER, 29));
        return novoAutor;
    }

    private Livro instanciarLivro(Autor novoAutor) {
        Livro novoLivro = new Livro();
        novoLivro.setTitulo("Percy Jackson e os Olimpianos: O Ladrão de Raios");
        novoLivro.setDescricao("20x30");
        novoLivro.setSinopse("Percy Jackson é acusado de roubar o raio de Zeus.");
        novoLivro.setGenero(Genero.AVENTURA);
        novoLivro.setClassificacao(FaixaEtaria.INFANTIL);
        novoLivro.getAutores().add(novoAutor);
        novoLivro.setId(2L);
        try {
            byte[] imagem = Files.readAllBytes(Paths.get("src/test/resources/CapaLadaroRaios.jpg"));
            novoLivro.setCapa(imagem);
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
        return novoLivro;
    }

    private Produto instanciarProduto(Livro livro) {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setLivro(livro);
        produto.setValor(new BigDecimal(25.00));
        produto.setEstoque(20);
        return produto;
    }

    private Carrinho instanciarCarrinho(Cliente cliente,
                                        List<Produto> produtos) {
        Carrinho novoCarrinho = new Carrinho();
        novoCarrinho.setId(1L);
        novoCarrinho.setCliente(cliente);
        novoCarrinho.setProdutos(produtos);
        return novoCarrinho;
    }

    private Carrinho preencherCarrinho() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Livro segundoLivro = instanciarLivro(novoAutor);
        segundoLivro.setTitulo("Percy Jackson e os Olimpianos: O Mar de Monstros");
        segundoLivro.setSinopse("A árvore de Thalia está morrendo.");
        Produto novoProduto = instanciarProduto(novoLivro);
        Produto segundoProduto = instanciarProduto(segundoLivro);
        segundoProduto.setEstoque(50);
        segundoProduto.setValor(new BigDecimal(27.00));
        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        Cliente novoCliente = instanciarCliente(novoUsuario);
        List<Produto> produtos = new ArrayList<>();
        produtos.add(novoProduto);
        produtos.add(segundoProduto);
        Carrinho novoCarrinho = instanciarCarrinho(novoCliente, produtos);
        return novoCarrinho;
    }

    private Venda instanciarVenda(Carrinho carrinho) {
        Venda venda = new Venda();
        venda.setCliente(carrinho.getCliente());
        venda.setItens(vendaService.criarItensVenda(carrinho.getProdutos()));
        venda.setDataCadastro(LocalDateTime.now());
        return venda;
    }

    @Test
    public void testVendaCadastradaComSucesso() {
        Carrinho novoCarrinho = preencherCarrinho();
        Venda novaVenda = instanciarVenda(novoCarrinho);

        when(vendaRepository.save(any(Venda.class))).thenReturn(novaVenda);
        when(produtoRepository.save(any(Produto.class))).thenReturn(null);

        try {
            Venda outraVenda = vendaService.cadastrarVenda(novoCarrinho);
            verify(carrinhoService).deletarCarrinho(novoCarrinho.getId());
            assertNotNull(outraVenda);
            assertEquals(novaVenda.getCliente(), outraVenda.getCliente());
            assertEquals(novaVenda.getItens(), outraVenda.getItens());


        } catch (ObjetoNaoEncontrado e) {
            fail(e.getMessage());
        }
    }


}
