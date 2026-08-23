package pt.loualmeida.lojavirtual.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany (cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemVenda> itens = new ArrayList<>();
    private Double total;
    @ManyToOne
    private Cliente cliente;
    private LocalDateTime dataCadastro;
    private boolean enviada = false;



}
