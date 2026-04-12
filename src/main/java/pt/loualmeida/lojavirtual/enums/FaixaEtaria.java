package pt.loualmeida.lojavirtual.enums;

public enum FaixaEtaria {
    LIVRE("Livre","Para todas as idades."),
    INFANTIL("Infantil", "Para crianças maiores que 6 anos."),
    INFANTO_JUVENIL("Infanto Juvenil", "Para crianças e adolescentes maiores que 11 anos."),
    JOVEM_ADULTO("Jovem Adulto", "Para leitores maiores que 15 anos."),
    ADULTO("Adulto", "Para leitores acima de 18 anos.");

    private String titulo;
    private String descricao;

    private FaixaEtaria(String titulo, String descricao) {
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
