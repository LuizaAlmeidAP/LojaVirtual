package pt.loualmeida.lojavirtual.enums;

import static pt.loualmeida.lojavirtual.enums.Categoria.FICCAO;
import static pt.loualmeida.lojavirtual.enums.Categoria.NAO_FICCAO;

public enum Genero {

    AVENTURA("Obras em que os protagonistas são constantemente colocados em situações perigosas.", FICCAO),
    CHICK_LIT("Obras que abordam o universo feminino.", FICCAO),
    DRAMA("Obras que possuem conteúdos caracterizados por sofrimentos e/ou tragédias.", FICCAO),
    GRAPH_NOVEL("Obras, baseadas em um romance, contada por meio de quadros ilustrados.", FICCAO),
    COMIC("Obras de arte que conjuga texto e imagens com o objetivo de narrar histórias dos mais variados gêneros e estilos.", FICCAO),
    ROMANCE("Obras que envolve histórias de conteúdo amoroso.", FICCAO),
    FANTASIA("Obras que apresentam conteúdos conteúdos fantasticos/mágicos.", FICCAO),
    INFANTIL("Obras voltadas ao público infantil", FICCAO),
    INFANTO_JUVENIL("Obras voltadas ao público jovem.", FICCAO),
    NACIONAL("Obras nacionais.", FICCAO),
    NEW_ADULT("Obras que abordam uma fase de transição da adolescência para a vida adulta.", FICCAO),
    FICCAO_CIENTIFICA("Obras cujo conteúdo é imaginativo, mas baseado na ciência.", FICCAO),
    SICK_LIT("Obras em que o/os personagens possuem uma doença física ou psicológica.", FICCAO),
    TERROR("Obras criadas com o intuido de causar medo ao leitor.", FICCAO),
    SUSPENSE("Obras que prender leitor no medo do desconhecido criando uma tensão sobre os próximos acontecimentos.", FICCAO),
    EPOCA("Obras que retratam uma época do passado.", FICCAO),
    POLICIAL("Obras em que ocorre um ou mais crimes e o leitor tenta descobrir o/os culpados junto aos personagens.", FICCAO),
    JOVEM_ADULTO("Obras que abrangem personagens normalmente entre 14 e 21 anos e se concentram em temáticas mais adultas.", FICCAO),
    BIOGRAFIA("Obras narrativas de terceira pessoa, nas quais o autor conta a história de outra pessoa (embora mantendo-se fiel e objectivo).", NAO_FICCAO),
    AUTOBIOGRAFIA("Obras narrativas nas quais o autor conta sua história.", NAO_FICCAO),
    HISTORIA("Obras que envolve a recontagem de um acontecimento histórico específico ou de um período de tempo, de forma factual e historicamente precisa. ", NAO_FICCAO),
    MEMORIAS("Obras que relatam aos leitores momentos significativos da vida do autor.", NAO_FICCAO),
    AUTO_AJUDA("Obras em que o autor tem como proposito ajudar ao leitor em seu desenvolvimento pessoal.", NAO_FICCAO),
    VIAGEM("Obras que tem viagem como tema principal, seja um diário de viagem, memórias do autor ou um guia de viagem.", NAO_FICCAO),
    ESPIRITUALIDADE_RELIGIAO("Obras que retratam espiritualidade e/ou religião como tema principal.", NAO_FICCAO),
    CIENCIA("Obras cientificas que se baseiam na observação e na evidência.", NAO_FICCAO),
    INFORMATICA("Obras sobre o universo informático.", NAO_FICCAO),
    ARTE("Obras sobre o universo artistico.", NAO_FICCAO),
    MUSICA("Obras que tem música como seu tema principal.", NAO_FICCAO),
    FILOSOFIA("Obras filosóficas.", NAO_FICCAO),
    PSICOLOGIA("Obras que tem como tema principal a psicológia.", NAO_FICCAO),
    MEDICINA("Obras que tem como tema principal a medicina.", NAO_FICCAO),
    ARTESANATO("Obras que ensinam o leitor a criar artesanato.", NAO_FICCAO),
    FOTOGRAFIA("Obras sobre o universo fotográfico.", NAO_FICCAO),
    JARDINAGEM("Obras que tem como tema principal a jardinagem.", NAO_FICCAO),
    COZINHAR_COMIDA("Obras sobre o universo culinário.", NAO_FICCAO),
    ESPORTE("Obras que tem como tema principal o universo esportivo.", NAO_FICCAO),
    ECONOMIA_NEGOCIOS("Obras sobre economia e negócios.", NAO_FICCAO),
    PATERNIDADE("Obras sobre paternidade familia.", NAO_FICCAO),
    ANIMAIS("Obras sobre o universo animal.", NAO_FICCAO);

    private String descricao;
    private Categoria categoria;
    private Genero(String descricao, Categoria categoria) {
        this.descricao = descricao;
        this.categoria = categoria;
    }
    public String getDescricao() {
        return descricao;
    }
    public Categoria getCategoria() {
        return categoria;
    }

}
