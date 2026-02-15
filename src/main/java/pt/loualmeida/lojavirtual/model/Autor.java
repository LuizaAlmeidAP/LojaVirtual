package pt.loualmeida.lojavirtual.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @Lob // <--- Indica ao banco que é um objeto grande (BLOB)
    @Column(length = 10000000) // <--- Define um tamanho máximo (ex: 10MB)
    private byte[] foto;
    private LocalDate nascimento;
    private LocalDate morte;
    @ManyToMany(mappedBy = "autores", fetch = FetchType.EAGER)
    private Set<Livro> livros = new HashSet<>();


    public int getQuantidadeLivros() {
        return livros.size();
    }
}
