package pt.loualmeida.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.loualmeida.lojavirtual.model.Cliente;
import pt.loualmeida.lojavirtual.model.Venda;

import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByEnviadaFalseOrderByDataCadastroAsc();

    List<Venda> findByClienteOrderByDataCadastroAsc(Cliente cliente);
}
