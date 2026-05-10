package pt.loualmeida.lojavirtual.exception;

public class UsuarioInativoException extends RuntimeException {
    public UsuarioInativoException(String mensagem) {
        super(mensagem);
    }
}
