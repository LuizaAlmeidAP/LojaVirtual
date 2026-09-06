package pt.loualmeida.lojavirtual.exception;

import pt.loualmeida.lojavirtual.model.Produto;

public class EstoqueIndisponivelException extends RuntimeException {
    public EstoqueIndisponivelException(Produto produto) {
        super(String.format("Estoque indisponível para o produto %s", produto));

    }
}
