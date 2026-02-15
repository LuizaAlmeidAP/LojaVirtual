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
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Autor;
import pt.loualmeida.lojavirtual.model.Livro;
import pt.loualmeida.lojavirtual.repository.AutorRepository;
import pt.loualmeida.lojavirtual.repository.LivroRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LivroServiceTest {
    @Mock
    private LivroRepository livroRepository;
    @Mock
    private AutorRepository autorRepository;
    @InjectMocks
    private AutorService autorService;
    @InjectMocks
    private LivroService livroService;


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

    private void validarAssertions(Livro livroSalvo, Livro novoLivro) {
        assertNotNull(livroSalvo);
        assertNotNull(livroSalvo.getTitulo());
        assertNotNull(livroSalvo.getDescricao());
        assertNotNull(livroSalvo.getAutores());
        assertNotNull(livroSalvo.getCapa());
        assertNotNull(livroSalvo.getGenero());
        assertNotNull(livroSalvo.getClassificacao());
        assertNotNull(livroSalvo.getSinopse());
        assertEquals(livroSalvo.getTitulo(), novoLivro.getTitulo());
        assertEquals(livroSalvo.getDescricao(), novoLivro.getDescricao());
        assertEquals(livroSalvo.getAutores(), novoLivro.getAutores());
        assertEquals(livroSalvo.getCapa(), novoLivro.getCapa());
        assertEquals(livroSalvo.getGenero(), novoLivro.getGenero());
        assertEquals(livroSalvo.getClassificacao(), novoLivro.getClassificacao());
        assertEquals(livroSalvo.getSinopse(), novoLivro.getSinopse());
    }


    @Test
    public void cadastrarLivro() {
        Autor novoAutor = instanciarAutor();
        novoAutor.setNome("Rick Riordan");
        novoAutor.setNascimento(LocalDate.of(1964, Month.JUNE, 5));
        List<String> info = new ArrayList<>();
        when(autorRepository.save(any(Autor.class))).thenReturn(novoAutor);
        when(autorRepository.findAllById(any(Set.class))).thenReturn(List.of(novoAutor));
        try {
            novoAutor = autorService.cadastrarAutor(novoAutor, info);
            Livro novoLivro = instanciarLivro(novoAutor);
            when(livroRepository.save(any(Livro.class))).thenReturn(novoLivro);
            Livro livroSalvo = livroService.cadastrarLivro(novoLivro);
            validarAssertions(livroSalvo, novoLivro);
        } catch (DadosInvalidosException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void falhaAoCadastrarLivroTituloNull() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        novoLivro.setTitulo(null);

        DadosInvalidosException e = assertThrows(DadosInvalidosException.class, () -> {
            livroService.cadastrarLivro(novoLivro);
        });
        assertNotNull(e.getMessage());
        assertEquals("O título do livro não foi informado.", e.getMessage());

    }

    @Test
    public void falhaAoCadastrarLivroTituloBlank() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        novoLivro.setTitulo("");
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class, () -> {
            livroService.cadastrarLivro(novoLivro);
        });
        assertNotNull(e.getMessage());
        assertEquals("O título do livro não foi informado.", e.getMessage());

    }

    @Test
    public void falhaAoCadastrarLivroAutoresEmpty() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        novoLivro.getAutores().clear();
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class, () -> {
            livroService.cadastrarLivro(novoLivro);
        });
        assertNotNull(e.getMessage());
        assertEquals("Não foi informado nenhum autor para o livro.", e.getMessage());

    }

    @Test
    public void falhaAoCadastrarLivroAutoresNaoEncontrado() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        when(autorRepository.findAllById(any(Set.class))).thenReturn(Collections.EMPTY_LIST);
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class, () -> {
            livroService.cadastrarLivro(novoLivro);
        });
        assertNotNull(e.getMessage());
        assertEquals("Nem todos os autores foram encontrados.", e.getMessage());

    }

    @Test
    public void falhaAoCadastrarLivroSemCapa() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        novoLivro.setCapa(null);
        when(autorRepository.findAllById(any(Set.class))).thenReturn(List.of(novoAutor.getId()));
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class, () -> {
            livroService.cadastrarLivro(novoLivro);
        });
        assertNotNull(e.getMessage());
        assertEquals("A capa do livro não foi inserida.", e.getMessage());

    }

    @Test
    public void falhaAoCadastrarLivroSemClassificacao() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        novoLivro.setClassificacao(null);
        when(autorRepository.findAllById(any(Set.class))).thenReturn(List.of(novoAutor.getId()));
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class, () -> {
            livroService.cadastrarLivro(novoLivro);
        });
        assertNotNull(e.getMessage());
        assertEquals("A classificação do livro não foi informada.", e.getMessage());

    }

    @Test
    public void falhaAoCadastrarLivroSemGenero() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        novoLivro.setGenero(null);
        when(autorRepository.findAllById(any(Set.class))).thenReturn(List.of(novoAutor.getId()));
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class, () -> {
            livroService.cadastrarLivro(novoLivro);
        });
        assertNotNull(e.getMessage());
        assertEquals("O gênero do livro não foi informado.", e.getMessage());

    }

    @Test
    public void falhaAoCadastrarLivroSemSinopse() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        novoLivro.setSinopse(null);
        when(autorRepository.findAllById(any(Set.class))).thenReturn(List.of(novoAutor.getId()));
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class, () -> {
            livroService.cadastrarLivro(novoLivro);
        });
        assertNotNull(e.getMessage());
        assertEquals("A sinopse do livro não foi informada.", e.getMessage());

    }

    @Test
    public void falhaAoCadastrarLivroSemDescricao() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        novoLivro.setDescricao(null);
        when(autorRepository.findAllById(any(Set.class))).thenReturn(List.of(novoAutor.getId()));
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class, () -> {
            livroService.cadastrarLivro(novoLivro);
        });
        assertNotNull(e.getMessage());
        assertEquals("A descrição do livro não foi informada.", e.getMessage());

    }

    @Test
    public void falhaAoCadastrarLivroJaExiste() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        when(autorRepository.findAllById(any(Set.class))).thenReturn(List.of(novoAutor.getId()));
        when(livroRepository.existsByTituloAndAutoresAndGenero(any(String.class), any(Set.class), any(Genero.class))).thenReturn(true);
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class, () -> {
            livroService.cadastrarLivro(novoLivro);
        });
        assertNotNull(e.getMessage());
        assertEquals("Livro já existe no banco de dados.", e.getMessage());

    }


    @Test
    public void buscarLivroComSucesso() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        when(livroRepository.findById(any(Long.class))).thenReturn(Optional.of(novoLivro));
        try {
            Livro livro = livroService.buscarLivro(novoLivro.getId());
            assertNotNull(livro);
            assertEquals(livro, novoLivro);
        } catch (ObjetoNaoEncontrado e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void buscarLivroLivroNaoEncontrado() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        when(livroRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        ObjetoNaoEncontrado e = assertThrows(ObjetoNaoEncontrado.class, () -> {
            livroService.buscarLivro(novoLivro.getId());
        });
        assertNotNull(e.getMessage());
        assertEquals("Livro 2 não encontrado", e.getMessage());
    }

    @Test
    public void buscarLivroPorTituloComSucesso() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        when(livroRepository.findByTitulo(any(String.class))).thenReturn(Set.of(novoLivro));
        try {
            Collection<Livro> livros = livroService.buscarLivroPorTitulo("Percy Jackson e os Olimpianos: O Ladrão de Raios");
            assertNotNull(livros);

        } catch (ObjetoNaoEncontrado e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void buscarLivroPorGeneroComSucesso() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        when(livroRepository.findByGenero(any(Genero.class))).thenReturn(Set.of(novoLivro));
        try {
            Collection<Livro> livros = livroService.buscarLivroPorGenero(novoLivro.getGenero());
            assertNotNull(livros);

        } catch (ObjetoNaoEncontrado e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void buscarLivroPorClassificacaoComSucesso() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        when(livroRepository.findByClassificacao(any(FaixaEtaria.class))).thenReturn(Set.of(novoLivro));
        try {
            Collection<Livro> livros = livroService.buscarLivroPorClassificacao(novoLivro.getClassificacao());
            assertNotNull(livros);
            assertEquals(1, livros.size());
            assertEquals(novoLivro, livros.iterator().next());

        } catch (ObjetoNaoEncontrado e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void excluirLivroComSucesso() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        when(livroRepository.findById(any(Long.class))).thenReturn(Optional.of(novoLivro));

        try {
            livroService.excluirLivro(novoLivro.getId());
            verify(livroRepository).delete(novoLivro);

        } catch (ObjetoNaoEncontrado e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void excluirLivroSemSucesso() {
        Autor novoAutor = instanciarAutor();
        Livro novoLivro = instanciarLivro(novoAutor);
        when(livroRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        ObjetoNaoEncontrado e = assertThrows(ObjetoNaoEncontrado.class,
                () -> {
                    livroService.excluirLivro(novoLivro.getId());
                });
        assertNotNull(e);
        assertNotNull(e.getMessage());
        assertEquals("Livro 2 não encontrado", e.getMessage());
    }


}
