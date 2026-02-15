package pt.loualmeida.lojavirtual.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Carrinho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    @JoinTable(
            name = "carrinho_produto",
            joinColumns = @JoinColumn(name = "carrinho_id"),
            inverseJoinColumns = @JoinColumn(name = "produto_id")
    )
    private List<Produto> produtos;
    private BigDecimal total;
    @OneToOne(cascade = CascadeType.ALL)
    private Cliente cliente;
    private LocalDateTime criacao;

    public BigDecimal getTotal() {
        total = BigDecimal.ZERO;
        for (Produto produto : produtos) {
            total = total.add(produto.getValor());
        }
        return total;
    }



}
