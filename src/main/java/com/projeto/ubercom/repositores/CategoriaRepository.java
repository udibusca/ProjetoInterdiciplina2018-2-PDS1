package com.projeto.ubercom.repositores;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.projeto.ubercom.domain.Categoria;
import com.projeto.ubercom.domain.Produto;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
	
	/**
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	@Query("SELECT DISTINCT prod FROM Categoria obj INNER JOIN obj.produtos prod WHERE obj.id = :id")
	List<Produto> findProdutosPorCategorias(@Param("id") Integer id);
	
}
