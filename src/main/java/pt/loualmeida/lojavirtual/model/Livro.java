package pt.loualmeida.lojavirtual.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.loualmeida.lojavirtual.enums.FaixaEtaria;
import pt.loualmeida.lojavirtual.enums.Genero;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private byte[] capa;
    @ManyToMany
    @JoinTable(
            name = "livro_autor",
            joinColumns = @JoinColumn(name = "livro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private Set<Autor> autores = new HashSet<>();
    private String sinopse;
    @Enumerated(EnumType.STRING)
    private FaixaEtaria classificacao;
    @Enumerated(EnumType.STRING)
    private Genero genero;
    private String descricao;
    private String tags;

    public Livro(Long id, String titulo, byte[] capa,
                 Set<Autor> autores, String sinopse, FaixaEtaria classificacao,
                 Genero genero, String descricao) {
        this.id = id;
        this.titulo = titulo;
        this.capa = capa;
        this.autores = autores;
        this.sinopse = sinopse;
        this.classificacao = classificacao;
        this.genero = genero;
        this.descricao = descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Livro livro)) return false;
        return Objects.equals(getId(), livro.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
