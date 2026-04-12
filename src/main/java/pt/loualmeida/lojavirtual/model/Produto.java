package pt.loualmeida.lojavirtual.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Livro livro;
    private Double valor;
    private Integer estoque;


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Produto produto)) return false;
        return Objects.equals(getId(), produto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
