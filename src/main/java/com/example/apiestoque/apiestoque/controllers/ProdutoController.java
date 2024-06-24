package com.example.apiestoque.apiestoque.controllers;

import com.example.apiestoque.apiestoque.models.Produto;
import com.example.apiestoque.apiestoque.repository.ProdutoRepository;
import com.example.apiestoque.apiestoque.services.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.validation.Validator;
@RestController //Rest: devolve um json e boas praticas, Controller: informa que recebe requisição, valida dados
@RequestMapping("/api/produtos")
public class ProdutoController {
    private final ProdutoRepository produtoRepository;
    private final ProdutoService produtoService;
    private final Validator validator;

    @Autowired //Declara que todos os parametros que estão no métodos injetam dependencias
    public ProdutoController(ProdutoRepository produtoRepository, ProdutoService produtoService, Validator validator) {
        this.produtoRepository = produtoRepository;
        this.produtoService = produtoService;
        this.validator = validator;
    }
    @GetMapping("/selecionar")
    public List<Produto> listarProdutos() {
        return produtoService.buscarTodosOsProdutos();
    }
    @PostMapping("/inserir")
    public ResponseEntity<String> inserirProduto(@Valid @RequestBody Produto produto) {
        try {
            produtoService.salvarProduto(produto);
            return ResponseEntity.ok("Produto inserido com sucesso");
        } catch (NullPointerException nullPointerException) {
            System.out.println("Valor vazio passado");
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<String> excluirProduto(@Valid @PathVariable Long id) {
            if (!produtoRepository.existsById(id)){
                return ResponseEntity.badRequest().body("id não existe");
            } else {
                produtoRepository.deleteById(id);
                return ResponseEntity.ok("Produto excluído com sucesso");
            }
    }
    public ResponseEntity<String> excluirProdutoCerto(@Valid @PathVariable Long id) {
        Produto produto = produtoService.excluirProduto(id);
        if (produto != null){
            produtoService.excluirProduto(id);
            return ResponseEntity.ok("Produto excluido com sucesso");
        } else {
            return ResponseEntity.status(404).body("erro burro d+++++");
        }
    }
    @PutMapping("atualizar/{id}")
    public ResponseEntity<String> atualizarProduto(@PathVariable Long id,
                                                   @RequestBody @Valid Produto produtoAtualizado) {
        Produto produtoBusca = produtoService.atualizarProduto(id, produtoAtualizado);
        if (produtoBusca != null) {
            return ResponseEntity.ok("Produto encontrado e atualizado com sucesso.");
        } else {
            return ResponseEntity.status(404).body("Produto não encontrado.");
        }
    }

    @PatchMapping("/atualizarParcial/{id}")
    public ResponseEntity<?> atualizarProdutoParcial(@PathVariable Long id,
                                                     @RequestBody Map<String, Object> updates) {
        Produto produto = produtoService.verificarAtualizacaoParcial(id, updates);
        if (produto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ID não encontrado");
        } else {
            DataBinder binder = new DataBinder(produto);
            binder.setValidator(validator);
            binder.validate();
            BindingResult result = binder.getBindingResult();
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(validarProduto(result));
            }
        }

        Produto produtoBusca = produtoService.atualizarProdutoParcialmente(id, updates);
        if (produtoBusca != null) {
            return ResponseEntity.ok(produtoBusca);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    public Map<String, String> validarProduto(BindingResult resultado){
        Map<String, String> erros = new HashMap<>();
        for (FieldError error:resultado.getFieldErrors()){
            erros.put(error.getField(), error.getDefaultMessage());
        }
        return erros;
    }
}
