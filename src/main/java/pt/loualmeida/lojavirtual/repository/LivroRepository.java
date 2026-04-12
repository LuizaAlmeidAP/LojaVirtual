package pt.loualmeida.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.loualmeida.lojavirtual.enums.FaixaEtaria;
import pt.loualmeida.lojavirtual.enums.Genero;
import pt.loualmeida.lojavirtual.model.Autor;
import pt.loualmeida.lojavirtual.model.Livro;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface LivroRepository extends JpaRepository<Livro, Long> {




    Set<Livro> findByGenero(Genero genero);

    Set<Livro> findByClassificacao(FaixaEtaria classificacao);




    boolean existsByTituloAndAutoresAndGenero(String titulo, Set<Autor> autores, Genero genero);

    Set<Livro> findByTituloContainingIgnoreCase(String titulo);
}
