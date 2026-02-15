package pt.loualmeida.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.loualmeida.lojavirtual.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);
}
