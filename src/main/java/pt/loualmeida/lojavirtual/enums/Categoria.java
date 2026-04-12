package pt.loualmeida.lojavirtual.enums;

import java.util.ArrayList;
import java.util.List;

public enum Categoria {
    FICCAO("Ficção", "Categoria de livros que reúne fatos sobre a realidade."),
    NAO_FICCAO("Não-ficção", "Categória de livros que conta com elementos de conteúdo imaginário.");

    private String descricao;
    private String titulo;

    Categoria(String titulo,String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
    }
    public String getDescricao() {
        return descricao;
    }
    public String getTitulo() {
        return titulo;
    }
}
