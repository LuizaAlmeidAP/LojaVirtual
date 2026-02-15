package pt.loualmeida.lojavirtual.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.loualmeida.lojavirtual.enums.Perfil;
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Cliente;
import pt.loualmeida.lojavirtual.model.Usuario;
import pt.loualmeida.lojavirtual.repository.ClienteRepository;
import pt.loualmeida.lojavirtual.repository.UsuarioRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static com.github.javaparser.utils.Utils.assertNotNull;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioService usuarioService;
    @InjectMocks
    private ClienteService clienteService;


    private Usuario instanciarUsuario(Perfil perfil) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("email@email.com");
        usuario.setNome("Cliente");
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

    private Cliente instanciarCliente(Usuario usuario) {
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setId(1L);
        cliente.setDocumento("NIF");
        cliente.setNascimento(LocalDate.of(1973, Month.DECEMBER, 20));
        cliente.setImagem(usuario.getImagem());
        return cliente;
    }

    @Test
    public void testClienteCadastradoComSucesso() {
        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        Cliente novoCliente = instanciarCliente(novoUsuario);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(novoCliente);
        try {
            Cliente outroCliente = clienteService.cadastrarCliente(novoCliente);
            assertNotNull(outroCliente);
            assertEquals(outroCliente, novoCliente);
        } catch (DadosInvalidosException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testUsuarioFalhaCadastrarNomeNull() {

        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        Cliente novoCliente = instanciarCliente(novoUsuario);
        novoUsuario.setNome(null);
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {
                    clienteService.cadastrarCliente(novoCliente);
                });
        assertNotNull(e.getMessage());
        assertEquals("Não foi informado nome à conta do usuario.", e.getMessage());

    }

    @Test
    public void testUsuarioFalhaCadastrarNomeBlank() {

        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        Cliente novoCliente = instanciarCliente(novoUsuario);
        novoUsuario.setNome("");
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {
                    clienteService.cadastrarCliente(novoCliente);
                });
        assertNotNull(e.getMessage());
        assertEquals("Não foi informado nome à conta do usuario.", e.getMessage());

    }

    @Test
    public void testClienteFalhaCadastrarDocumentoNull() {

        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        Cliente novoCliente = instanciarCliente(novoUsuario);
        novoCliente.setDocumento(null);
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {
                    clienteService.cadastrarCliente(novoCliente);
                });
        assertNotNull(e.getMessage());
        assertEquals("Não foi anexado nenhum documento a conta do cliente.", e.getMessage());

    }

    @Test
    public void testClienteFalhaCadastrarDocumentoBlank() {

        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        Cliente novoCliente = instanciarCliente(novoUsuario);
        novoCliente.setDocumento("");
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {
                    clienteService.cadastrarCliente(novoCliente);
                });
        assertNotNull(e.getMessage());
        assertEquals("Não foi anexado nenhum documento a conta do cliente.", e.getMessage());

    }

    @Test
    public void testClienteFalhaCadastrarNascimentoNull() {

        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        Cliente novoCliente = instanciarCliente(novoUsuario);
        novoCliente.setNascimento(null);
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {
                    clienteService.cadastrarCliente(novoCliente);
                });
        assertNotNull(e.getMessage());
        assertEquals("Não foi informada data de nascimento do cliente.", e.getMessage());

    }

    @Test
    public void testClienteFalhaCadastrarTipoDePerfilInvalido() {

        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        Cliente novoCliente = instanciarCliente(novoUsuario);
        novoUsuario.setPerfil(Perfil.ADMINISTRADOR);
        DadosInvalidosException e = assertThrows(DadosInvalidosException.class,
                () -> {
                    clienteService.cadastrarCliente(novoCliente);
                });
        assertNotNull(e.getMessage());
        assertEquals("Tipo de perfil invalido.", e.getMessage());

    }

    @Test
    public void excluirClienteComSucesso() {
        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        Cliente novoCliente = instanciarCliente(novoUsuario);
        when(clienteRepository.findById(any(Long.class))).thenReturn(Optional.of(novoCliente));


        try {

            clienteService.excluirCliente(novoCliente.getId());
            verify(usuarioService).excluirUsuario(novoUsuario.getId());
            verify(clienteRepository).delete(novoCliente);


        } catch (ObjetoNaoEncontrado e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void excluirClienteSemSucesso() {
        Usuario novoUsuario = instanciarUsuario(Perfil.CLIENTE);
        Cliente novoCliente = instanciarCliente(novoUsuario);
        when(clienteRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        ObjetoNaoEncontrado e = assertThrows(ObjetoNaoEncontrado.class,
                () -> {
                    clienteService.excluirCliente(novoCliente.getId());
                });
        Assertions.assertNotNull(e);
        Assertions.assertNotNull(e.getMessage());
        assertEquals("Cliente 1 não encontrado", e.getMessage());
    }


}
