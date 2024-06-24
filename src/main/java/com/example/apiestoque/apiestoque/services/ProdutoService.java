package com.example.apiestoque.apiestoque.services;

import com.example.apiestoque.apiestoque.models.Produto;
import com.example.apiestoque.apiestoque.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
//Todos os modelos que fazem interação com o banco de dados
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    public ProdutoService(ProdutoRepository produtoRepository){
        this.produtoRepository = produtoRepository;
    }
    public List<Produto> buscarTodosOsProdutos(){
        return produtoRepository.findAll();
    }
    public Produto salvarProduto(Produto produto){
        return produtoRepository.save(produto);
    }
    public Produto buscarProdutoPorId(Long id){
        return produtoRepository.findById(id).orElseThrow(()->
                new RuntimeException("Produto não enconytado"));
    }
    public void findByNomeIgnoreCaseContainingAndPrecoLessThan(String nome, double preco){

    }
    public Produto excluirProduto(Long id){
        Optional<Produto> prod = produtoRepository.findById(id);
        if (prod.isPresent()){
            produtoRepository.deleteById(id);
            return prod.get();
        }
        return null;
    }
    public Produto atualizarProduto(Long id, Produto produtoAtualizado) {
        Optional<Produto> produtoExistente = produtoRepository.findById(id);
        if (produtoExistente.isPresent()) {
            Produto produto = produtoExistente.get();
            if (produtoAtualizado.getNome() != null || !produtoAtualizado.getNome().equals("")) {
                produto.setNome(produtoAtualizado.getNome());
            }
            if (produtoAtualizado.getDescricao() != null || !produtoAtualizado.getDescricao().equals("")) {
                produto.setDescricao(produtoAtualizado.getDescricao());
            }
            if (produtoAtualizado.getPreco() > 0) {
                produto.setPreco(produtoAtualizado.getPreco());
            }
            if (produtoAtualizado.getQuantidadeEstoque() >= 0) {
                produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
            }
            produtoRepository.save(produto);
            return produto;
        }
        return null;
    }

    public Produto atualizarProdutoParcialmente (Long id, Map<String, Object> updates) {
        Optional<Produto> produtoExistente = produtoRepository.findById(id);

        if(produtoExistente.isPresent()) {
            Produto produto = produtoExistente.get();
            produtoRepository.save(produto);
            return produto;
        } else {
            return null;
        }
    }

    public Produto verificarAtualizacaoParcial(Long id, Map<String, Object> requisicao) {
        Optional<Produto> produtoBanco = produtoRepository.findById(id);
        if (produtoBanco.isPresent()) {
            Produto produto = produtoBanco.get();
            if (requisicao.containsKey("nome")) {
                produto.setNome((String) requisicao.get("nome"));
            }
            if (requisicao.containsKey("descricao")) {
                produto.setDescricao((String) requisicao.get("descricao"));
            }
            if (requisicao.containsKey("preco")) {
                try {
                    produto.setPreco((Double) requisicao.get("preco"));
                } catch (ClassCastException cne) {
                    produto.setPreco(Double.parseDouble((String) requisicao.get("preco")));
                }
            }
            if (requisicao.containsKey("quantidadeEstoque")) {
                produto.setQuantidadeEstoque((Integer) requisicao.get("quantidadeEstoque"));
            }
            return produto;
        } else {
            return null;
        }
    }
}
