package pt.loualmeida.lojavirtual.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.loualmeida.lojavirtual.model.Cliente;
import pt.loualmeida.lojavirtual.model.Usuario;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {


    List<Cliente> findByAtivo(Boolean ativo, Sort sort);

    List<Cliente> findByAtivoAndUsuarioNomeContainingIgnoreCase(boolean ativo, String nome);

    Cliente findByUsuario(Usuario usuario);
}
