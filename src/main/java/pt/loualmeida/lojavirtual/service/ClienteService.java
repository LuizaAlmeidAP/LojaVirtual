package pt.loualmeida.lojavirtual.service;

import org.springframework.stereotype.Service;
import pt.loualmeida.lojavirtual.enums.Perfil;
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Cliente;
import pt.loualmeida.lojavirtual.model.Usuario;
import pt.loualmeida.lojavirtual.repository.ClienteRepository;

import java.util.Optional;

@Service
public class ClienteService {
    private ClienteRepository clienteRepository;
    private UsuarioService usuarioService;

    public ClienteService(ClienteRepository clienteRepository, UsuarioService usuarioService) {

        this.clienteRepository = clienteRepository;
        this.usuarioService = usuarioService;
    }

    /*
    Atualização de Dados.
    Carrinho de compras.
    Historico de Compras.
    Favoritos.
    Exluir- FEITO
     */

    public Cliente cadastrarCliente(Cliente cliente) throws DadosInvalidosException {
        validarCliente(cliente);
        return clienteRepository.save(cliente);

    }

    private void validarCliente(Cliente cliente) throws DadosInvalidosException {
        if (cliente.getUsuario().getNome() == null || cliente.getUsuario().getNome().isBlank()) {
            throw new DadosInvalidosException("Não foi informado nome à conta do usuario.");
        }
        if (cliente.getDocumento() == null || cliente.getDocumento().isBlank()) {
            throw new DadosInvalidosException("Não foi anexado nenhum documento a conta do cliente.");
        }
        if (cliente.getNascimento() == null) {
            throw new DadosInvalidosException("Não foi informada data de nascimento do cliente.");
        }

        if (Perfil.ADMINISTRADOR.equals(cliente.getUsuario().getPerfil())) {
            throw new DadosInvalidosException("Tipo de perfil invalido.");
        }
    }


    public void excluirCliente(Long clienteID) throws ObjetoNaoEncontrado {
        Optional<Cliente> optCliente = clienteRepository.findById(clienteID);

        if (optCliente.isEmpty()) {
            throw new ObjetoNaoEncontrado(String.format("Cliente %d não encontrado", clienteID));
        }
        Cliente cliente = optCliente.get();
        Usuario usuario = cliente.getUsuario();
        usuarioService.excluirUsuario(usuario.getId());
        clienteRepository.delete(cliente);
    }


}
