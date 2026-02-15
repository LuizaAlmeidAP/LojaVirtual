package pt.loualmeida.lojavirtual.service;

import org.springframework.stereotype.Service;
import pt.loualmeida.lojavirtual.enums.FaixaEtaria;
import pt.loualmeida.lojavirtual.enums.Genero;
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Autor;
import pt.loualmeida.lojavirtual.model.Livro;
import pt.loualmeida.lojavirtual.repository.AutorRepository;
import pt.loualmeida.lojavirtual.repository.LivroRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LivroService {
    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    public LivroService(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    /*
    Pesquisar Livros pelo Título
    Filtrar Livros por: Classificação, Gênero e Tags
    Pesquisar por autor
    Excluir Livro
     */

    public Livro cadastrarLivro(Livro livro) throws DadosInvalidosException {
        validarLivro(livro);
        return livroRepository.save(livro);
    }

    private void validarLivro(Livro livro) throws DadosInvalidosException {
        if (livro.getTitulo() == null || livro.getTitulo().isBlank()) {
            throw new DadosInvalidosException("O título do livro não foi informado.");
        }
        if (livro.getAutores().isEmpty()) {
            throw new DadosInvalidosException("Não foi informado nenhum autor para o livro.");
        }
        if (!autoresExistem(livro.getAutores().stream().map(Autor::getId).collect(Collectors.toSet()))) {
            throw new DadosInvalidosException("Nem todos os autores foram encontrados.");
        }
        if (livro.getCapa() == null) {
            throw new DadosInvalidosException("A capa do livro não foi inserida.");
        }
        if (livro.getClassificacao() == null) {
            throw new DadosInvalidosException("A classificação do livro não foi informada.");
        }
        if (livro.getGenero() == null) {
            throw new DadosInvalidosException("O gênero do livro não foi informado.");
        }
        if (livro.getSinopse() == null) {
            throw new DadosInvalidosException("A sinopse do livro não foi informada.");
        }
        if (livro.getDescricao() == null) {
            throw new DadosInvalidosException("A descrição do livro não foi informada.");
        }
        if (livroExiste(livro)) {
            throw new DadosInvalidosException("Livro já existe no banco de dados.");
        }
        /*
        if(livro.getTags() == null) {
            throw new DadosInvalidosException("Não foi anexada nenhuma tag ao livro.");
        }

         */
    }

    public boolean autoresExistem(Set<Long> autoresId) {
        List<Autor> autores = autorRepository.findAllById(autoresId);
        return (autores.size() == autoresId.size());
    }

    private boolean livroExiste(Livro livro) {
        return livroRepository.existsByTituloAndAutoresAndGenero(livro.getTitulo(), livro.getAutores(), livro.getGenero());

    }

    public Livro buscarLivro(Long livroId) throws ObjetoNaoEncontrado {
        Optional<Livro> optLivro = livroRepository.findById(livroId);
        if (optLivro.isPresent()) {
            return optLivro.get();
        }
        throw new ObjetoNaoEncontrado(String.format("Livro %d não encontrado", livroId));

    }


    public Set<Livro> buscarLivroPorTitulo(String titulo) throws ObjetoNaoEncontrado {
        return livroRepository.findByTitulo(titulo);
    }


    public Set<Livro> buscarLivroPorGenero(Genero genero) throws ObjetoNaoEncontrado {
        return livroRepository.findByGenero(genero);
    }


    public Set<Livro> buscarLivroPorClassificacao(FaixaEtaria classificacao) throws ObjetoNaoEncontrado {
        return livroRepository.findByClassificacao(classificacao);
    }

    public void excluirLivro(Long livroId) throws ObjetoNaoEncontrado {
        Optional<Livro> optLivro = livroRepository.findById(livroId);
        if (optLivro.isEmpty()) {
            throw new ObjetoNaoEncontrado(String.format("Livro %d não encontrado", livroId));
        }
        Livro livro = optLivro.get();
        livroRepository.delete(livro);

    }


}
