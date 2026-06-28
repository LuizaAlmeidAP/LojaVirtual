package pt.loualmeida.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import pt.loualmeida.lojavirtual.enums.FaixaEtaria;
import pt.loualmeida.lojavirtual.enums.Genero;
import pt.loualmeida.lojavirtual.model.Livro;
import pt.loualmeida.lojavirtual.model.Produto;

import java.math.BigDecimal;
import java.util.Set;


public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {

    boolean existsByLivro(Livro livro);

    Set<Produto> findByValorBetween(BigDecimal valorInicial, BigDecimal valorFinal);

    Set<Produto> findByLivroGenero(Genero genero);

    Set<Produto> findByLivroTitulo(String titulo);

    @Query("select p from Produto p join p.livro l join l.autores a where a.id = :autorID")
    Set<Produto> findByLivroAutor(Long autorID);

    Set<Produto> findByLivroClassificacao(FaixaEtaria classificacao);

    Set<Produto> findByLivroTituloContainingIgnoreCase(String titulo);
}
