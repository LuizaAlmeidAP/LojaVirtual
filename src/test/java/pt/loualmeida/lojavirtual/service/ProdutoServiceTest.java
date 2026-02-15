package pt.loualmeida.lojavirtual.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.loualmeida.lojavirtual.enums.FaixaEtaria;
import pt.loualmeida.lojavirtual.enums.Genero;
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.model.Autor;
import pt.loualmeida.lojavirtual.model.Livro;
import pt.loualmeida.lojavirtual.model.Produto;
import pt.loualmeida.lojavirtual.repository.ProdutoRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static com.github.javaparser.utils.Utils.assertNotNull;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;



    private Autor instanciarAutor(){
        Autor novoAutor = new Autor();
        novoAutor.setId(1L);
        novoAutor.setNome("Machado de Assis");
        novoAutor.setNascimento(LocalDate.of(1839, Month.JUNE, 21));
        novoAutor.setMorte(LocalDate.of(1908, Month.SEPTEMBER, 29));
        return novoAutor;
    }

    private Livro instanciarLivro(Autor novoAutor){
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
        }
        catch(IOException e){
            Assertions.fail(e.getMessage());
        }
        return novoLivro;
    }

    private Produto instanciarProduto(Livro livro){
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setLivro(livro);
        produto.setValor(new BigDecimal(25.00));
        produto.setEstoque(20);
        return produto;
    }

    @Test
    public void testProdutoCadastradoComSucesso(){
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);
        when(produtoRepository.save(any(Produto.class))).thenReturn(novoProduto);
        try{
            Produto outroProduto = produtoService.cadastrarProduto(novoProduto);
            assertNotNull(outroProduto);
            assertEquals(outroProduto, novoProduto);
        }
        catch(DadosInvalidosException e){
            fail(e.getMessage());
        }

    }

    @Test
    public void testProdutoFalhaCadastrarSemLivro(){

        Produto novoProduto = instanciarProduto(null);
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {produtoService.cadastrarProduto(novoProduto);});
        assertNotNull(e.getMessage());
        assertEquals("Não foi informado livro para o produto",e.getMessage());

    }

    @Test
    public void testProdutoFalhaCadastrarSemValor(){
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);
        novoProduto.setValor(null);
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {produtoService.cadastrarProduto(novoProduto);});
        assertNotNull(e.getMessage());
        assertEquals("Não foi informado o valor para o produto",e.getMessage());

    }

    @Test
    public void testProdutoFalhaCadastrarSemEstoque(){
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);
        novoProduto.setEstoque(0); //preciso que ele possa ser zero eventualmente, apenas não no inicio
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {produtoService.cadastrarProduto(novoProduto);});
        assertNotNull(e.getMessage());
        assertEquals("Não foi adicionado estoque para o produto",e.getMessage());

    }

    @Test
    public void testProdutoFalhaCadastrarProdutoJaExiste(){
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);
        when(produtoRepository.existsByLivro(any(Livro.class))).thenReturn(true);
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {produtoService.cadastrarProduto(novoProduto);});
        assertNotNull(e.getMessage());
        assertEquals("Produto já existe no banco de dados.",e.getMessage());

    }





    @Test
    public void testFiltraProdutoPorAutor(){
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);
        when(produtoRepository.findByLivroAutor(any(Long.class))).thenReturn(Set.of(novoProduto));
        try{
            Set<Produto> produtos = produtoService.filtraProdutoPorAutor(novoAutor);
            assertNotNull(produtos);
            assertEquals(1, produtos.size());
            assertEquals(novoProduto, produtos.iterator().next());
        }
        catch(Exception e){
            fail(e.getMessage());
        }

    }

    @Test
    public void testFiltraProdutoPorGenero(){
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);
        when(produtoRepository.findByLivroGenero(any(Genero.class))).thenReturn(Set.of(novoProduto));
        try{
            Set<Produto> produtos = produtoService.filtraProdutoPorGenero(novoProduto.getLivro().getGenero());
            assertNotNull(produtos);
            assertEquals(1, produtos.size());
            assertEquals(novoProduto, produtos.iterator().next());
        }
        catch(Exception e){
            fail(e.getMessage());
        }

    }

    @Test
    public void testFiltraProdutoPorTitulo(){
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);
        when(produtoRepository.findByLivroTitulo(any(String.class))).thenReturn(Set.of(novoProduto));
        try{
            Set<Produto> produtos = produtoService.filtraProdutoPorTitulo(novoProduto.getLivro().getTitulo());
            assertNotNull(produtos);
            assertEquals(1, produtos.size());
            assertEquals(novoProduto, produtos.iterator().next());
        }
        catch(Exception e){
            fail(e.getMessage());
        }

    }

    @Test
    public void testFiltraProdutoPorClassificacao(){
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);
        when(produtoRepository.findByLivroClassificacao(any(FaixaEtaria.class))).thenReturn(Set.of(novoProduto));
        try{
            Set<Produto> produtos = produtoService.filtraProdutoPorClassificacao(novoProduto.getLivro().getClassificacao());
            assertNotNull(produtos);
            assertEquals(1, produtos.size());
            assertEquals(novoProduto, produtos.iterator().next());
        }
        catch(Exception e){
            fail(e.getMessage());
        }

    }
    @Test
    public void testFiltraProdutoPorValores(){
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);
        when(produtoRepository.findByValorBetween(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(Set.of(novoProduto));
        try{
            Set<Produto> produtos = produtoService.filtraProdutoPorValores(new BigDecimal("10.00"), new BigDecimal("40.00"));
            assertNotNull(produtos);
            assertEquals(1, produtos.size());
            assertEquals(novoProduto, produtos.iterator().next());
        }
        catch(Exception e){
            fail(e.getMessage());
        }

    }

    @Test
    public void testFiltraProdutoPorValoresValorInicialNull(){
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);
        when(produtoRepository.findByValorBetween(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(Set.of(novoProduto));
        try{
            Set<Produto> produtos = produtoService.filtraProdutoPorValores(null, new BigDecimal("40.00"));
            assertNotNull(produtos);
            assertEquals(1, produtos.size());
            assertEquals(novoProduto, produtos.iterator().next());
        }
        catch(Exception e){
            fail(e.getMessage());
        }

    }



    @Test
    public void testFiltraProdutoPorValoresValorFinalNull(){
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);
        when(produtoRepository.findByValorBetween(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(Set.of(novoProduto));
        try{
            Set<Produto> produtos = produtoService.filtraProdutoPorValores(new BigDecimal("40.00"), null);
            assertNotNull(produtos);
            assertEquals(1, produtos.size());
            assertEquals(novoProduto, produtos.iterator().next());
        }
        catch(Exception e){
            fail(e.getMessage());
        }

    }
    @Test
    public void testFiltraProdutoPorValoresComValoresNull(){
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        Produto novoProduto = instanciarProduto(novoLivro);

        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                ()-> {
            produtoService.filtraProdutoPorValores(null, null);
        });
        assertNotNull(e);
        assertEquals("Não é possível filtrar por valores se nenhum valor for informado.",e.getMessage());

    }






}

