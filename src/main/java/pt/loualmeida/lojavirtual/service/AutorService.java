package pt.loualmeida.lojavirtual.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Autor;
import pt.loualmeida.lojavirtual.model.Livro;
import pt.loualmeida.lojavirtual.repository.AutorRepository;
import pt.loualmeida.lojavirtual.repository.LivroRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AutorService {
    private final AutorRepository autorRepository;
    private final LivroRepository livroRepository;

    public AutorService(AutorRepository autorRepository, LivroRepository livroRepository) {
        this.autorRepository = autorRepository;
        this.livroRepository = livroRepository;
    }
    /*
    Cadastrar
    Pesquisar Livros pelos autores
    Anexar Livros aos seus autores
     */

    public Autor cadastrarAutor(Autor autor, List<String> info) throws DadosInvalidosException {

        info.addAll(validarAutor(autor));
        return autorRepository.save(autor);
    }

    public List<String> validarAutor(Autor autor) throws DadosInvalidosException {
        if (autor.getNome() == null || autor.getNome().isBlank()) {
            throw new DadosInvalidosException("O nome do autor não foi informado.");
        }
        if (autor.getNascimento() == null) {
            throw new DadosInvalidosException("Não foi informada a data de nascimento do autor.");
        }
        if (autorExiste(autor)) {
            throw new DadosInvalidosException("Autor já existe no banco de dados.");
        }
        return informacoesIncompletas(autor);
    }


    public List<String> informacoesIncompletas(Autor autor) {

        List<String> informacoes = new ArrayList<>();

        if (autor.getFoto() == null) {
            informacoes.add("Não foi anexada foto ao perfil do autor.");
        }

        return informacoes;
    }

    public void atualizarAutor(Autor autor) throws ObjetoNaoEncontrado {
        Optional<Autor> optAutor = autorRepository.findById(autor.getId());
        if (optAutor.isPresent()) {
            Autor autorExistente = optAutor.get();
            autorExistente.setMorte(autor.getMorte());
            autorExistente.setFoto(autor.getFoto());
            autorRepository.save(autorExistente);

        } else {
            throw new ObjetoNaoEncontrado(String.format("Autor %d não encontrado", autor.getId()));
        }

    }

    private boolean autorExiste(Autor autor) {
        return autorRepository.existsByNomeAndNascimento(autor.getNome(), autor.getNascimento());
    }

    public void adicionarLivro(Autor autor, Livro livro) throws ObjetoNaoEncontrado {
        if (!livroRepository.existsById(livro.getId())) {
            throw new ObjetoNaoEncontrado(String.format("Livro %d não encontrado", livro.getId()));
        }
        autor.getLivros().add(livro);
        autorRepository.save(autor);
    }


    public Set<Livro> pesquisarLivrosDoAutor(Long autorId) throws ObjetoNaoEncontrado {
        Optional<Autor> autor = autorRepository.findById(autorId);
        if (autor.isPresent()) {
            return autor.get().getLivros();
        }
        throw new ObjetoNaoEncontrado(String.format("Autor %d não encontrado", autorId));

    }

    public void excluirLivroAutor(Long autorId, Livro livro) throws ObjetoNaoEncontrado {
        Optional<Autor> optAutor = autorRepository.findById(autorId);
        if (optAutor.isEmpty()) {
            throw new ObjetoNaoEncontrado(String.format("Autor %d não encontrado", autorId));
        }
        Autor autor = optAutor.get();
        autor.getLivros().remove(livro);
        autorRepository.save(autor);
    }

    //O teste precisa de varios autores para procurar
    public List<Autor> buscarTodosAutores(String campoDeOrdenacao) {
        return autorRepository.findAll(Sort.by(campoDeOrdenacao));
    }

    public List<Autor> buscarAutorPorNome(String nome) {
        return autorRepository.findByNomeContainingIgnoreCase(nome);
    }


}
