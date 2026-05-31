package pt.loualmeida.lojavirtual.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.loualmeida.lojavirtual.enums.Perfil;

@Entity
@Data
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String nome;
    public String senha;
    private byte[] foto;
    @Enumerated(EnumType.STRING)
    private Perfil perfil;
    private Boolean ativo;
}
