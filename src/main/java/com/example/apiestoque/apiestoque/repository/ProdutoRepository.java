package com.example.apiestoque.apiestoque.repository;

import com.example.apiestoque.apiestoque.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProdutoRepository extends JpaRepository<Produto,Long> {
    //é necessario criar uma interface para injetar dependencias e especificar que a entidade criada é Produto e seu id é long
    // anotação de injeção de dependencia
    @Modifying
    @Query("DELETE FROM Produto e WHERE e.id = ?1")
    void deleteById(Long id);
}
