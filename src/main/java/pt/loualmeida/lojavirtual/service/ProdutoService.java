package pt.loualmeida.lojavirtual.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pt.loualmeida.lojavirtual.enums.Categoria;
import pt.loualmeida.lojavirtual.enums.FaixaEtaria;
import pt.loualmeida.lojavirtual.enums.Genero;
import pt.loualmeida.lojavirtual.exception.DadosInvalidosException;
import pt.loualmeida.lojavirtual.exception.ObjetoNaoEncontrado;
import pt.loualmeida.lojavirtual.model.Autor;
import pt.loualmeida.lojavirtual.model.Produto;
import pt.loualmeida.lojavirtual.repository.ProdutoRepository;
import pt.loualmeida.lojavirtual.repository.ProdutoSpec;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }


    /*
    Cadastrar Produto - FEITO
    Pesquisar produto por valor, titulo, autor. - FEITO
    Gestão de estoque
    Atualização de Dados
    Adições de promoções
    Excluir produto -
     */

    public Produto cadastrarProduto(Produto produto) throws DadosInvalidosException {
        validarProduto(produto);
        return produtoRepository.save(produto);
    }


    private void validarProduto(Produto produto) throws DadosInvalidosException {
        if(produto.getLivro() == null){
            throw new DadosInvalidosException("Não foi informado livro para o produto");
        }
        if(produto.getValor() == null){
            throw new DadosInvalidosException("Não foi informado o valor para o produto");
        }
        if(produto.getEstoque() == 0){
            throw new DadosInvalidosException("Não foi adicionado estoque para o produto");
        }
        if(produtoExiste(produto)){
            throw new DadosInvalidosException("Produto já existe no banco de dados.");
        }

    }

    private boolean produtoExiste(Produto produto) {
        return produtoRepository.existsByLivro(produto.getLivro());
    }

    public Set<Produto> filtraProdutoPorValores(BigDecimal valorInicial, BigDecimal valorFinal) throws DadosInvalidosException {
        if(valorInicial == null && valorFinal == null){
            throw new DadosInvalidosException("Não é possível filtrar por valores se nenhum valor for informado.");
        }
        if (valorInicial == null) {
            valorInicial = BigDecimal.ZERO;
        }
        if (valorFinal == null) {
            valorFinal = new BigDecimal(Integer.MAX_VALUE);
        }
        return produtoRepository.findByValorBetween(valorInicial, valorFinal);


    }

    public List<Produto> filtraProdutos(String titulo, String autor,Categoria categoria, Genero genero, FaixaEtaria faixaEtaria, Double minimo, Double maximo)  {
        Specification<Produto> spec = Specification.allOf(
                ProdutoSpec.tituloContem(titulo),
                ProdutoSpec.autorContem(autor),
                ProdutoSpec.categoriaIgual(categoria),
                ProdutoSpec.generoIgual(genero),
                ProdutoSpec.faixaEtariaIgual(faixaEtaria),
                ProdutoSpec.precoMenorQue(maximo),
                ProdutoSpec.precoMaiorQue(minimo)
        );

        return produtoRepository.findAll(spec);


    }


    public Set<Produto> filtraProdutoPorGenero(Genero genero)  {
        return produtoRepository.findByLivroGenero(genero);

    }
    public Set<Produto> filtraProdutoPorTitulo(String titulo)  {

        return produtoRepository.findByLivroTitulo(titulo);
    }

    public Set<Produto> filtraProdutoPorAutor(Autor autor) {

        return produtoRepository.findByLivroAutor(autor.getId());
    }

    public Set<Produto> filtraProdutoPorClassificacao(FaixaEtaria classificacao)  {
        return produtoRepository.findByLivroClassificacao(classificacao);
    }

    public boolean produtoEmEstoque(Produto produto){
        if(!produtoExiste(produto)) return false;
        return produto.getEstoque() > 0;
    }

    public void atualizarProduto(Produto produto) throws ObjetoNaoEncontrado {

        Optional<Produto> optProduto = produtoRepository.findById(produto.getId());
        if (optProduto.isPresent()) {
            Produto produtoExistente = optProduto.get();
            produtoExistente.setLivro(produto.getLivro());
            produtoExistente.setValor(produto.getValor());
            produtoExistente.setEstoque(produto.getEstoque());
            produtoRepository.save(produto);
        }
        else{
            throw new ObjetoNaoEncontrado(String.format("Produto %d não encontrado", produto.getId()));
        }


    }



    public List<Produto> buscarTodosProdutos(String campoDeOrdenacao) {
        return produtoRepository.findAll(Sort.by(campoDeOrdenacao));
    }

    public List<Produto> buscarProdutoPorTitulo(String titulo) {
        String tituloSemAcentos = StringUtils.stripAccents(titulo).toLowerCase();
        return produtoRepository.findAll().stream()
                .filter(p -> StringUtils.stripAccents(p.getLivro().getTitulo())
                        .toLowerCase()
                        .contains(tituloSemAcentos))
                .toList();

    }

    public void atualizarProdutos(List<Produto> produtos) {
        for(Produto produto: produtos) System.out.println(produto.getLivro().getTitulo() + " " + produto.getEstoque());
        produtoRepository.saveAll(produtos);
    }
}
