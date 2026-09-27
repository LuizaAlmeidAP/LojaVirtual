package pt.loualmeida.lojavirtual.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.loualmeida.lojavirtual.utils.FormatadorUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    private boolean recebida = false;

    public String getValorTotalFormatado() {
        return FormatadorUtils.formataValorMonetario(total);
    }



}
