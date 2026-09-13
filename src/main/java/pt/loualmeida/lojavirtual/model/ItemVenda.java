package pt.loualmeida.lojavirtual.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.loualmeida.lojavirtual.utils.FormatadorUtils;

import java.math.BigDecimal;
import java.util.Locale;

@Entity
@Data
@NoArgsConstructor
public class ItemVenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Produto produto;
    private int quantidade;
    private Double precoUnitario;
    private Double precoTotal;


    public String getPrecoUnitarioFormatado() {
        return FormatadorUtils.formataValorMonetario(precoUnitario);
    }

    public String getPrecoTotalFormatado() {
        return FormatadorUtils.formataValorMonetario(precoTotal);
    }

}
