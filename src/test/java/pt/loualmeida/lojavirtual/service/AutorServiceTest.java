package pt.loualmeida.lojavirtual.service;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AutorServiceTest {
    @Mock
    private AutorRepository autorRepository;
    @Mock
    private LivroRepository livroRepository;
    @InjectMocks
    private AutorService autorService;
    @InjectMocks
    private LivroService livroService;

    /*
    Testar se o autor ja existe
     */

    private Autor instanciarAutor() {
        Autor novoAutor = new Autor();
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
        try {
            byte[] imagem = Files.readAllBytes(Paths.get("src/test/resources/CapaLadaroRaios.jpg"));
            novoLivro.setCapa(imagem);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return novoLivro;
    }

    @Test
    public void cadastrarAutor() {
        Autor novoAutor = instanciarAutor();
        List<String> info = new ArrayList<>();
        when(autorRepository.existsByNomeAndNascimento(any(String.class), any(LocalDate.class))).thenReturn(false);
        when(autorRepository.save(any(Autor.class))).thenReturn(novoAutor);

        try {
            novoAutor = autorService.cadastrarAutor(novoAutor, info);
            assertNotNull(novoAutor);
            assertEquals("Machado de Assis", novoAutor.getNome());
            assertNotNull(info);
            assertFalse(info.isEmpty());

        } catch (DadosInvalidosException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void cadastrarAutorComFoto() {
        Autor novoAutor = instanciarAutor();
        List<String> info = new ArrayList<>();
        when(autorRepository.existsByNomeAndNascimento(any(String.class), any(LocalDate.class))).thenReturn(false);
        when(autorRepository.save(any(Autor.class))).thenReturn(novoAutor);

        try {
            byte[] imagem = Files.readAllBytes(Paths.get("src/test/resources/CapaLadaroRaios.jpg"));
            novoAutor.setFoto(imagem);
            novoAutor = autorService.cadastrarAutor(novoAutor, info);
            assertNotNull(novoAutor);
            assertEquals("Machado de Assis", novoAutor.getNome());
            assertNotNull(info);
            assertTrue(info.isEmpty());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void cadastrarAutorComNomeNull() {
        Autor novoAutor = instanciarAutor();
        novoAutor.setNome(null);
        List<String> info = new ArrayList<>();

        DadosInvalidosException e = assertThrows(DadosInvalidosException.class, () -> {
            autorService.cadastrarAutor(novoAutor, info);
        });
        assertNotNull(e.getMessage());
        assertEquals("O nome do autor não foi informado.", e.getMessage());

    }


    @Test
    public void cadastrarAutorComNomeBlank() {
        Autor novoAutor = instanciarAutor();
        novoAutor.setNome("");
        List<String> info = new ArrayList<>();

        DadosInvalidosException e = assertThrows(DadosInvalidosException.class, () -> {
            autorService.cadastrarAutor(novoAutor, info);
        });
        assertNotNull(e.getMessage());
        assertEquals("O nome do autor não foi informado.", e.getMessage());

    }

    @Test
    public void cadastrarAutorSemNascimento() {
        Autor novoAutor = instanciarAutor();
        novoAutor.setNascimento(null);
        List<String> info = new ArrayList<>();

        DadosInvalidosException e = assertThrows(DadosInvalidosException.class, () -> {
            autorService.cadastrarAutor(novoAutor, info);
        });
        assertNotNull(e.getMessage());
        assertEquals("Não foi informada a data de nascimento do autor.", e.getMessage());

    }

    @Test
    public void cadastrarAutorExistente() {
        Autor novoAutor = instanciarAutor();
        List<String> info = new ArrayList<>();
        when(autorRepository.existsByNomeAndNascimento(any(String.class), any(LocalDate.class))).thenReturn(true);
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {
                    autorService.cadastrarAutor(novoAutor, info);
                });
        assertNotNull(e.getMessage());
        assertEquals("Autor já existe no banco de dados.", e.getMessage());
    }


    @Test
    public void adicionarLivroAoAutor() {
        Autor novoAutor = instanciarAutor();
        when(livroRepository.existsById(any())).thenReturn(true);

        try {

            Livro novoLivro = instanciarLivro(novoAutor);
            autorService.adicionarLivro(novoAutor, novoLivro);
            assertTrue(novoAutor.getLivros().contains(novoLivro));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void adicionarLivroNaoExistenteAoAutor() {
        Autor novoAutor = instanciarAutor();
        List<String> info = new ArrayList<>();
        when(livroRepository.existsById(any())).thenReturn(false);

        Livro novoLivro = instanciarLivro(novoAutor);
        novoLivro.setId(1L);

        ObjetoNaoEncontrado e = assertThrows(ObjetoNaoEncontrado.class,
                () -> {
                    autorService.adicionarLivro(novoAutor, novoLivro);
                });
        assertNotNull(e.getMessage());
        assertEquals("Livro 1 não encontrado", e.getMessage());
    }


    @Test
    public void excluirLivroAoAutor() {
        Autor novoAutor = instanciarAutor();
        novoAutor.setId(2L);
        Livro novoLivro = instanciarLivro(novoAutor);
        when(autorRepository.findById(any())).thenReturn(Optional.of(novoAutor));
        when(autorRepository.save(any(Autor.class))).thenReturn(novoAutor);
        when(livroRepository.existsById(any())).thenReturn(true);
        try {
            autorService.adicionarLivro(novoAutor, novoLivro);
            Set<Livro> livrosOg = novoAutor.getLivros();
            autorService.excluirLivroAutor(novoAutor.getId(), novoLivro);
            assertFalse(novoAutor.getLivros().contains(novoLivro));
            livrosOg.remove(novoLivro);
            assertEquals(novoAutor.getLivros().size(), livrosOg.size());
            assertEquals(novoAutor.getLivros(), livrosOg);
        } catch (ObjetoNaoEncontrado e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void excluirLivroAoAutorSemAutor() {
        Autor novoAutor = instanciarAutor();
        novoAutor.setId(2L);
        Livro novoLivro = instanciarLivro(novoAutor);
        when(autorRepository.findById(any())).thenReturn(Optional.empty());
        ObjetoNaoEncontrado e = assertThrows(ObjetoNaoEncontrado.class,
                () -> {
                    autorService.excluirLivroAutor(novoAutor.getId(), novoLivro);
                });
        assertNotNull(e.getMessage());
        assertEquals("Autor 2 não encontrado", e.getMessage());
    }

    @Test
    public void pesquisarLivrosDoAutor() {
        Autor novoAutor = instanciarAutor();
        novoAutor.setId(2L);
        Livro novoLivro = instanciarLivro(novoAutor);
        when(autorRepository.findById(any())).thenReturn(Optional.of(novoAutor));
        when(autorRepository.save(any(Autor.class))).thenReturn(novoAutor);
        when(livroRepository.existsById(any())).thenReturn(true);
        try {
            autorService.adicionarLivro(novoAutor, novoLivro);
            Collection<Livro> livros = autorService.pesquisarLivrosDoAutor(novoAutor.getId());
            assertNotNull(livros);
            assertEquals(novoAutor.getLivros(), livros);


        } catch (ObjetoNaoEncontrado e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void pesquisarLivrosDoAutorNaoExistente() {
        Autor novoAutor = instanciarAutor();
        novoAutor.setId(2L);

        Livro novoLivro = instanciarLivro(novoAutor);
        when(autorRepository.findById(any())).thenReturn(Optional.empty());
        when(livroRepository.existsById(any())).thenReturn(true);
        try {
            autorService.adicionarLivro(novoAutor, novoLivro);
            assertTrue(novoAutor.getLivros().contains(novoLivro));
        } catch (Exception e) {
            fail(e.getMessage());
        }
        ObjetoNaoEncontrado e = assertThrows(ObjetoNaoEncontrado.class,
                () -> {
                    autorService.pesquisarLivrosDoAutor(novoAutor.getId());
                });
        assertNotNull(e.getMessage());
        assertEquals("Autor 2 não encontrado", e.getMessage());
    }


}
