package pt.loualmeida.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.loualmeida.lojavirtual.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
