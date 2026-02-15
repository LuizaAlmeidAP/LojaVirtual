package pt.loualmeida.lojavirtual.enums;

public enum FaixaEtaria {
    LIVRE("Para todas as idades."),
    INFANTIL("Para crianças maiores que 6 anos."),
    INFANTO_JUVENIL("Para crianças e adolescentes maiores que 11 anos."),
    JOVEM_ADULTO("Para leitores maiores que 15 anos."),
    ADULTO("Para leitores acima de 18 anos.");

    private String descricao;

    private FaixaEtaria(String descricao) {
        this.descricao = descricao;
    }
    public String getDescricao() {
        return descricao;
    }
}
