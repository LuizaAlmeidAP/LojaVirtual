package pt.loualmeida.lojavirtual.enums;

public enum Categoria {
    FICCAO("Categoria de livros que reúne fatos sobre a realidade."),
    NAO_FICCAO("Categória de livros que conta com elementos de conteúdo imaginário.");

    private String descricao;


    Categoria(String descricao) {
        this.descricao = descricao;
    }
    public String getDescricao() {
        return descricao;
    }
}
