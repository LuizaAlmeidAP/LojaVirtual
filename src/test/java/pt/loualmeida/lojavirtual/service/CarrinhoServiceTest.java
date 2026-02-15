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
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.*;
import pt.loualmeida.lojavirtual.repository.CarrinhoRepository;
import pt.loualmeida.lojavirtual.repository.ClienteRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.javaparser.utils.Utils.assertNotNull;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CarrinhoServiceTest {
    @Mock
    private CarrinhoRepository carrinhoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
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

    @Test
    public void testCarrinhoCadastradoComSucesso() {
        Carrinho novoCarrinho = preencherCarrinho();
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(novoCarrinho);
        try {
            Carrinho outroCarrinho = carrinhoService.cadastrarCarrinho(novoCarrinho);
            assertNotNull(outroCarrinho);
            assertEquals(novoCarrinho, outroCarrinho);
        } catch (DadosInvalidosException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCarrinhoCasdastradoSemSucessoSemCliente() {
        Carrinho novoCarrinho = preencherCarrinho();
        novoCarrinho.setCliente(null);
        DadosInvalidosException e = assertThrows(
                DadosInvalidosException.class,
                () -> {
                    carrinhoService.cadastrarCarrinho(novoCarrinho);
                }
        );
        assertNotNull(e.getMessage());
        assertEquals("Não foi informado um o proprietário deste carrinho.", e.getMessage());
    }

    @Test
    public void testCarrinhoCasdastradoSemSucessoProdutosNull() {
        Carrinho novoCarrinho = preencherCarrinho();
        novoCarrinho.setProdutos(null);
        DadosInvalidosException e = assertThrows(
                DadosInvalidosException.class,
                () -> {
                    carrinhoService.cadastrarCarrinho(novoCarrinho);
                }
        );
        assertNotNull(e.getMessage());
        assertEquals("Nenhum produto foi colocado no carrinho.", e.getMessage());
    }

    @Test
    public void testCarrinhoCasdastradoSemSucessoProdutosVazio() {
        Carrinho novoCarrinho = preencherCarrinho();
        novoCarrinho.setProdutos(List.of());
        DadosInvalidosException e = assertThrows(
                DadosInvalidosException.class,
                () -> {
                    carrinhoService.cadastrarCarrinho(novoCarrinho);
                }
        );
        assertNotNull(e.getMessage());
        assertEquals("Nenhum produto foi colocado no carrinho.", e.getMessage());
    }

    @Test
    public void testCarrinhoSucessoAoAdicionarProdutos() {
        Carrinho novoCarrinho = preencherCarrinho();
        int antigoSize = novoCarrinho.getProdutos().size();
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);
        when(carrinhoRepository.findById(any(Long.class))).thenReturn(Optional.of(novoCarrinho));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(novoCarrinho);
        try {
            List<Produto> novosProdutos = List.of(novoProduto);
            carrinhoService.adicionarItens(novoCarrinho.getId(),
                    List.of(novoProduto));
            assertEquals(novoCarrinho.getProdutos().size(),
                    antigoSize + novosProdutos.size());

        } catch (ObjetoNaoEncontrado e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCarrinhoFalhaAoAdicionarProdutos() {
        Carrinho novoCarrinho = preencherCarrinho();
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);
        when(carrinhoRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        ObjetoNaoEncontrado e = assertThrows(
                ObjetoNaoEncontrado.class,
                () -> {
                    carrinhoService.adicionarItens(novoCarrinho.getId(), List.of(novoProduto));
                }
        );
        assertNotNull(e.getMessage());
        assertEquals("O carrinho 1 não foi encontrado.", e.getMessage());
    }

    @Test
    public void testCarrinhoSucessoAoRemoverProduto() {
        Carrinho novoCarrinho = preencherCarrinho();
        int antigoSize = novoCarrinho.getProdutos().size();
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);
        when(carrinhoRepository.findById(any(Long.class))).thenReturn(Optional.of(novoCarrinho));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(novoCarrinho);
        try {

            carrinhoService.removerItem(novoCarrinho.getId(),
                    novoProduto);
            assertEquals(novoCarrinho.getProdutos().size(),
                    antigoSize - 1);

        } catch (ObjetoNaoEncontrado e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCarrinhoFalhaAoRemoverProduto() {
        Carrinho novoCarrinho = preencherCarrinho();
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);
        when(carrinhoRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        ObjetoNaoEncontrado e = assertThrows(
                ObjetoNaoEncontrado.class,
                () -> {
                    carrinhoService.removerItem(novoCarrinho.getId(), novoProduto);
                }
        );
        assertNotNull(e.getMessage());
        assertEquals("O carrinho 1 não foi encontrado.", e.getMessage());
    }

    @Test
    public void testCarrinhoSucessoAoLimparCarrinho() {
        Carrinho novoCarrinho = preencherCarrinho();
        when(carrinhoRepository.findById(any(Long.class))).thenReturn(Optional.of(novoCarrinho));
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(novoCarrinho);
        try {

            carrinhoService.limparCarrinho(novoCarrinho.getId());
            assertEquals(0,
                    novoCarrinho.getProdutos().size());

        } catch (ObjetoNaoEncontrado e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCarrinhoFalhaAoLimparCarrinho() {
        Carrinho novoCarrinho = preencherCarrinho();

        when(carrinhoRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        ObjetoNaoEncontrado e = assertThrows(
                ObjetoNaoEncontrado.class,
                () -> {
                    carrinhoService.limparCarrinho(novoCarrinho.getId());
                }
        );
        assertNotNull(e.getMessage());
        assertEquals("O carrinho 1 não foi encontrado.", e.getMessage());
    }


    @Test
    public void testCarrinhoSucessoAoBuscarCarrinhoPorCliente() {

        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        Carrinho novoCarrinho = preencherCarrinho();
        Cliente novoCliente = instanciarCliente(novoUsuario);
        when(clienteRepository.findById(any(Long.class))).thenReturn(Optional.of(novoCliente));
        when(carrinhoRepository.findByCliente(any(Cliente.class))).thenReturn(Optional.of(novoCarrinho));

        try {

            Carrinho outroCarrinho = carrinhoService.buscarCarrinhoPorCliente(novoCliente.getId());
            assertNotNull(outroCarrinho);
            assertEquals(novoCarrinho, outroCarrinho);

        } catch (ObjetoNaoEncontrado e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCarrinhoFalhaAoBuscarCarrinhoPorClienteComClienteNaoEncontrado() {
        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        Carrinho novoCarrinho = preencherCarrinho();
        Cliente novoCliente = instanciarCliente(novoUsuario);
        when(clienteRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        //when(carrinhoRepository.findByCliente(any(Cliente.class))).thenReturn(Optional.of(novoCarrinho));

        ObjetoNaoEncontrado e = assertThrows(
                ObjetoNaoEncontrado.class,
                () -> {
                    carrinhoService.buscarCarrinhoPorCliente(novoCliente.getId());
                }
        );
        assertNotNull(e.getMessage());
        assertEquals("O cliente 1 não foi encontrado.", e.getMessage());
    }

    @Test
    public void testCarrinhoFalhaAoBuscarCarrinhoPorClienteComCarrinhoNaoEncontrado() {
        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        Carrinho novoCarrinho = preencherCarrinho();
        Cliente novoCliente = instanciarCliente(novoUsuario);
        when(clienteRepository.findById(any(Long.class))).thenReturn(Optional.of(novoCliente));
        when(carrinhoRepository.findByCliente(any(Cliente.class))).thenReturn(Optional.empty());

        ObjetoNaoEncontrado e = assertThrows(
                ObjetoNaoEncontrado.class,
                () -> {
                    carrinhoService.buscarCarrinhoPorCliente(novoCliente.getId());
                }
        );
        assertNotNull(e.getMessage());
        assertEquals("O carrinho do cliente 1 não foi encontrado.", e.getMessage());
    }

    @Test
    public void testCarrinhoSucessoAoDeletarCarrinho() {
        Carrinho novoCarrinho = preencherCarrinho();
        when(carrinhoRepository.findById(any(Long.class))).thenReturn(Optional.of(novoCarrinho));
        try {

            carrinhoService.deletarCarrinho(novoCarrinho.getId());
            verify(carrinhoRepository).delete(novoCarrinho);

        } catch (ObjetoNaoEncontrado e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCarrinhoFalhaAoDeletarCarrinho() {
        Carrinho novoCarrinho = preencherCarrinho();

        when(carrinhoRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        ObjetoNaoEncontrado e = assertThrows(
                ObjetoNaoEncontrado.class,
                () -> {
                    carrinhoService.deletarCarrinho(novoCarrinho.getId());
                }
        );
        assertNotNull(e.getMessage());
        assertEquals("O carrinho 1 não foi encontrado.", e.getMessage());
    }


}
