package pt.loualmeida.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.loualmeida.lojavirtual.model.Autor;

import java.time.LocalDate;
import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    boolean existsByNomeAndNascimento(String nome, LocalDate nascimento);

    List<Autor> findByNomeContainingIgnoreCase(String nome);
}
