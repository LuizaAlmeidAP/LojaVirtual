package pt.loualmeida.lojavirtual.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pt.loualmeida.lojavirtual.enums.Perfil;
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Usuario;
import pt.loualmeida.lojavirtual.repository.UsuarioRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static com.github.javaparser.utils.Utils.assertNotNull;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @InjectMocks
    private UsuarioService usuarioService;
    @Spy
    private PasswordEncoder passwordEncoder;

    private Usuario instanciarUsuario(Perfil perfil) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("email@email.com");
        usuario.setSenha("senha");
        usuario.setPerfil(perfil);
        try {
            byte[] imagem = Files.readAllBytes(Paths.get("src/test/resources/CapaLadaroRaios.jpg"));
            usuario.setImagem(imagem);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return usuario;
    }

    @Test
    public void testUsuarioCadastradoComSucesso() {
        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(novoUsuario);
        when(usuarioRepository.existsByEmail(any(String.class))).thenReturn(false);
        try {
            Usuario outroUsuario = usuarioService.cadastrarUsuario(novoUsuario);
            assertNotNull(outroUsuario);
            assertEquals(outroUsuario, novoUsuario);
        } catch (DadosInvalidosException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testUsuarioFalhaCadastrarEmailNull() {

        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        novoUsuario.setEmail(null);

        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {
                    usuarioService.cadastrarUsuario(novoUsuario);
                });
        assertNotNull(e.getMessage());
        assertEquals("Não foi informado email para criação da conta do usuário.", e.getMessage());

    }

    @Test
    public void testUsuarioFalhaCadastrarEmailBlank() {

        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        novoUsuario.setEmail("");
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {
                    usuarioService.cadastrarUsuario(novoUsuario);
                });
        assertNotNull(e.getMessage());
        assertEquals("Não foi informado email para criação da conta do usuário.", e.getMessage());

    }


    @Test
    public void testUsuarioFalhaCadastrarSenhaNull() {

        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        novoUsuario.setSenha(null);
        when(usuarioRepository.existsByEmail(any(String.class))).thenReturn(false);
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {
                    usuarioService.cadastrarUsuario(novoUsuario);
                });
        assertNotNull(e.getMessage());
        assertEquals("Não foi informada senha para a criação da conta do usuário.", e.getMessage());

    }

    @Test
    public void testUsuarioFalhaCadastrarUsuarioJaExiste() {

        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        when(usuarioRepository.existsByEmail(any(String.class))).thenReturn(true);
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {
                    usuarioService.cadastrarUsuario(novoUsuario);
                });
        assertNotNull(e.getMessage());
        assertEquals("Já existe uma conta registrada com esse email.", e.getMessage());

    }

    @Test
    public void testUsuarioFalhaCadastrarSenhaBlank() {

        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        novoUsuario.setSenha("");
        when(usuarioRepository.existsByEmail(any(String.class))).thenReturn(false);
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {
                    usuarioService.cadastrarUsuario(novoUsuario);
                });
        assertNotNull(e.getMessage());
        assertEquals("Não foi informada senha para a criação da conta do usuário.", e.getMessage());

    }

    @Test
    public void testUsuarioFalhaCadastrarPerfilNull() {

        Usuario novoUsuario = instanciarUsuario(null);
        when(usuarioRepository.existsByEmail(any(String.class))).thenReturn(false);
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {
                    usuarioService.cadastrarUsuario(novoUsuario);
                });
        assertNotNull(e.getMessage());
        assertEquals("Não foi informado o tipo de perfil do usuário.", e.getMessage());

    }

    @Test
    public void excluirUsuarioComSucesso() {
        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        when(usuarioRepository.findById(any(Long.class))).thenReturn(Optional.of(novoUsuario));

        try {
            usuarioService.excluirUsuario(novoUsuario.getId());
            verify(usuarioRepository).delete(novoUsuario);

        } catch (ObjetoNaoEncontrado e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void excluirUsuarioSemSucesso() {
        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        when(usuarioRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        ObjetoNaoEncontrado e = assertThrows(ObjetoNaoEncontrado.class,
                () -> {
                    usuarioService.excluirUsuario(novoUsuario.getId());
                });
        Assertions.assertNotNull(e);
        Assertions.assertNotNull(e.getMessage());
        assertEquals("Usuário 1 não encontrado", e.getMessage());
    }


}
