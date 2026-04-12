package pt.loualmeida.lojavirtual.enums;

import java.util.ArrayList;
import java.util.List;

import static pt.loualmeida.lojavirtual.enums.Categoria.FICCAO;
import static pt.loualmeida.lojavirtual.enums.Categoria.NAO_FICCAO;

public enum Genero {

    AVENTURA("Aventura","Obras em que os protagonistas são constantemente colocados em situações perigosas.", FICCAO),
    CHICK_LIT("Chick lit","Obras que abordam o universo feminino.", FICCAO),
    DRAMA("Drama", "Obras que possuem conteúdos caracterizados por sofrimentos e/ou tragédias.", FICCAO),
    GRAPH_NOVEL("Romance gráfico","Obras, baseadas em um romance, contada por meio de quadros ilustrados.", FICCAO),
    COMIC("Historias em quadrinho","Obras de arte que conjuga texto e imagens com o objetivo de narrar histórias dos mais variados gêneros e estilos.", FICCAO),
    ROMANCE("Romance", "Obras que envolve histórias de conteúdo amoroso.", FICCAO),
    FANTASIA("Fantasia","Obras que apresentam conteúdos conteúdos fantasticos/mágicos.", FICCAO),
    INFANTIL("Infantil","Obras voltadas ao público infantil", FICCAO),
    INFANTO_JUVENIL("Infanto juvenil","Obras voltadas ao público jovem.", FICCAO),
    NACIONAL("Nacional","Obras nacionais.", FICCAO),
    NEW_ADULT("Novo adulto","Obras que abordam uma fase de transição da adolescência para a vida adulta.", FICCAO),
    FICCAO_CIENTIFICA("Ficção ciêntifica","Obras cujo conteúdo é imaginativo, mas baseado na ciência.", FICCAO),
    SICK_LIT("Sick lit","Obras em que o/os personagens possuem uma doença física ou psicológica.", FICCAO),
    TERROR("Terror","Obras criadas com o intuido de causar medo ao leitor.", FICCAO),
    SUSPENSE("Suspense","Obras que prender leitor no medo do desconhecido criando uma tensão sobre os próximos acontecimentos.", FICCAO),
    EPOCA("Época","Obras que retratam uma época do passado.", FICCAO),
    POLICIAL("Policial","Obras em que ocorre um ou mais crimes e o leitor tenta descobrir o/os culpados junto aos personagens.", FICCAO),
    JOVEM_ADULTO("Jovem adulto","Obras que abrangem personagens normalmente entre 14 e 21 anos e se concentram em temáticas mais adultas.", FICCAO),
    BIOGRAFIA("Biografia","Obras narrativas de terceira pessoa, nas quais o autor conta a história de outra pessoa (embora mantendo-se fiel e objectivo).", NAO_FICCAO),
    AUTOBIOGRAFIA("Autobiografia","Obras narrativas nas quais o autor conta sua história.", NAO_FICCAO),
    HISTORIA("História","Obras que envolve a recontagem de um acontecimento histórico específico ou de um período de tempo, de forma factual e historicamente precisa. ", NAO_FICCAO),
    MEMORIAS("Memórias","Obras que relatam aos leitores momentos significativos da vida do autor.", NAO_FICCAO),
    AUTO_AJUDA("Auto-ajuda","Obras em que o autor tem como proposito ajudar ao leitor em seu desenvolvimento pessoal.", NAO_FICCAO),
    VIAGEM("Viagem","Obras que tem viagem como tema principal, seja um diário de viagem, memórias do autor ou um guia de viagem.", NAO_FICCAO),
    ESPIRITUALIDADE_RELIGIAO("Espiritualidade e Religião","Obras que retratam espiritualidade e/ou religião como tema principal.", NAO_FICCAO),
    CIENCIA("Ciência","Obras cientificas que se baseiam na observação e na evidência.", NAO_FICCAO),
    INFORMATICA("Informática","Obras sobre o universo informático.", NAO_FICCAO),
    ARTE("Arte","Obras sobre o universo artistico.", NAO_FICCAO),
    MUSICA("Música","Obras que tem música como seu tema principal.", NAO_FICCAO),
    FILOSOFIA("Filosofia","Obras filosóficas.", NAO_FICCAO),
    PSICOLOGIA("Psicologia","Obras que tem como tema principal a psicológia.", NAO_FICCAO),
    MEDICINA("Medicina","Obras que tem como tema principal a medicina.", NAO_FICCAO),
    ARTESANATO("Artesanato","Obras que ensinam o leitor a criar artesanato.", NAO_FICCAO),
    FOTOGRAFIA("Fotografia","Obras sobre o universo fotográfico.", NAO_FICCAO),
    JARDINAGEM("Jardinagem","Obras que tem como tema principal a jardinagem.", NAO_FICCAO),
    CULINARIA("Culinária","Obras sobre o universo culinário.", NAO_FICCAO),
    ESPORTE("Esporte","Obras que tem como tema principal o universo esportivo.", NAO_FICCAO),
    ECONOMIA_NEGOCIOS("Economia e negócios","Obras sobre economia e negócios.", NAO_FICCAO),
    PATERNIDADE("Paternidade","Obras sobre paternidade familia.", NAO_FICCAO),
    ANIMAIS("Animais","Obras sobre o universo animal.", NAO_FICCAO);

    private String titulo;
    private String descricao;
    private Categoria categoria;
    private Genero(String titulo, String descricao, Categoria categoria) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }
    public String getDescricao() {
        return descricao;
    }
    public Categoria getCategoria() {
        return categoria;
    }
    public static List<Genero> getGenerosDaCategoria(Categoria categoria) {
        List<Genero> generos = new ArrayList<>();
        for (Genero genero : Genero.values()) {
            if (categoria.equals(genero.getCategoria())) {
                generos.add(genero);
            }
        }
        return generos;
    }

}
