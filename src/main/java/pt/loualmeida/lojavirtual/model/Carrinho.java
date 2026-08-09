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
public class Carrinho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "carrinho_produto",
            joinColumns = @JoinColumn(name = "carrinho_id"),
            inverseJoinColumns = @JoinColumn(name = "produto_id")
    )
    private List<Produto> produtos;
    private Double total;
    @OneToOne
    private Cliente cliente;
    private LocalDateTime criacao;

    public Double getTotal() {
        total = 0.0;
        for (Produto produto : produtos) {
            total += produto.getValor();
        }
        return total;
    }

    public void adicionarProduto(Produto produto) {
        if(produtos == null){
            produtos = new ArrayList<Produto>();
        }
        produtos.add(produto);
    }

    public void removerProduto(Produto produto) {
        if(produtos == null){
            return;
        }
        produtos.remove(produto);
    }



    public int getQuatidadeItens(){
        return produtos.size();
    }



}
