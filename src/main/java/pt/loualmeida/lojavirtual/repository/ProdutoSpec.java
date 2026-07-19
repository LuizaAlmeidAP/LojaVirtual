package pt.loualmeida.lojavirtual.repository;

import org.springframework.data.jpa.domain.Specification;
import pt.loualmeida.lojavirtual.enums.Categoria;
import pt.loualmeida.lojavirtual.enums.FaixaEtaria;
import pt.loualmeida.lojavirtual.enums.Genero;
import pt.loualmeida.lojavirtual.model.Produto;

import java.util.ArrayList;
import java.util.List;

public class ProdutoSpec {

    public static Specification<Produto> tituloContem(String titulo) {
        return (root, query, builder) -> {
            // Se o nome não foi informado, ignora este filtro
            if (titulo == null || titulo.trim().isEmpty()) {
                return null;
            }

            return builder.like(
                    builder.lower(root.get("livro").get("titulo")),
                    "%" + titulo.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Produto> autorContem(String autor) {
        return (root, query, builder) -> {
            // Se o nome não foi informado, ignora este filtro
            if (autor == null || autor.trim().isEmpty()) {
                return null;
            }
            // Ao pesquisar por coleções precisamos fazer join e marcar distinct para evitar duplicatas
            query.distinct(true);
            jakarta.persistence.criteria.Join<?,?> livroJoin = root.join("livro");
            jakarta.persistence.criteria.Join<?,?> autorJoin = livroJoin.join("autores");
            return builder.like(
                    builder.lower(autorJoin.get("nome")),
                    "%" + autor.toLowerCase() + "%"
            );

        };
    }

    public static Specification<Produto> faixaEtariaIgual(FaixaEtaria faixaEtaria) {
        return (root, query, builder) -> {

            if (faixaEtaria == null) {
                return null;
            }
            return builder.equal(root.get("livro").get("classificacao"), faixaEtaria);

        };
    }

    public static Specification<Produto> categoriaIgual(Categoria categoria) {
        return (root, query, builder) -> {
            // Se a categoria não foi informada, ignora este filtro
            if (categoria == null) {
                return null;
            }
            List<Genero> generos = Genero.getGenerosDaCategoria(categoria);

            return root.get("livro").get("genero").in(generos);
        };
    }
    public static Specification<Produto> generoIgual(Genero genero) {
        return (root, query, builder) -> {
            // Se a categoria não foi informada, ignora este filtro
            if (genero == null) {
                return null;
            }
            return builder.equal(root.get("livro").get("genero"), genero);
        };
    }

    public static Specification<Produto> precoMaiorQue(Double precoMinimo) {
        return (root, query, builder) -> {
            if (precoMinimo == null) {
                return null;
            }
            return builder.greaterThanOrEqualTo(root.get("valor"), precoMinimo);
        };
    }
    public static Specification<Produto> precoMenorQue(Double precoMaximo) {
        return (root, query, builder) -> {
            if (precoMaximo == null) {
                return null;
            }
            return builder.lessThanOrEqualTo(root.get("valor"), precoMaximo);
        };
    }
}