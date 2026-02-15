package pt.loualmeida.lojavirtual.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Usuario;
import pt.loualmeida.lojavirtual.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {
    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
    Atualização de Dados
    Diferenças de permições de acesso entre administrador e clientes
    Excluir conta de usuario - Feito
     */

    public Usuario cadastrarUsuario(Usuario usuario) throws DadosInvalidosException {
        validarUsuario(usuario);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    private void validarUsuario(Usuario usuario) throws DadosInvalidosException {
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new DadosInvalidosException("Não foi informado email para criação da conta do usuário.");
        }
        if (emailExiste(usuario)) {
            throw new DadosInvalidosException("Já existe uma conta registrada com esse email.");
        }
        if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            throw new DadosInvalidosException("Não foi informada senha para a criação da conta do usuário.");
        }
        if (usuario.getPerfil() == null) {
            throw new DadosInvalidosException("Não foi informado o tipo de perfil do usuário.");
        }

    }

    private boolean emailExiste(Usuario usuario) {
        return usuarioRepository.existsByEmail(usuario.getEmail());

    }

    public void excluirUsuario(Long usuarioID) throws ObjetoNaoEncontrado {
        Optional<Usuario> optUsuario = usuarioRepository.findById(usuarioID);
        if (optUsuario.isEmpty()) {
            throw new ObjetoNaoEncontrado(String.format("Usuário %d não encontrado", usuarioID));
        }
        Usuario usuario = optUsuario.get();
        usuarioRepository.delete(usuario);
    }


    public Usuario buscarUsuarioPorEmail(String email) throws ObjetoNaoEncontrado {
        Optional<Usuario> optUsuario = usuarioRepository.findByEmail(email);
        if (optUsuario.isPresent()) {
            return optUsuario.get();
        }
        throw new ObjetoNaoEncontrado("Usuario com email " + email + " não foi encontrado");

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Usuario usuario = buscarUsuarioPorEmail(username);
            return User.builder()
                    .username(usuario.getEmail())
                    .password(usuario.getSenha())
                    .authorities(List.of(new SimpleGrantedAuthority(usuario.getPerfil().name())))
                    .build();

        } catch (ObjetoNaoEncontrado e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
