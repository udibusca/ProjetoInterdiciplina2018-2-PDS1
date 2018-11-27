package com.projeto.ubercom.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.projeto.ubercom.domain.Categoria;
import com.projeto.ubercom.domain.Produto;
import com.projeto.ubercom.dto.CategoriaDTO;
import com.projeto.ubercom.repositores.CategoriaRepository;
import com.projeto.ubercom.services.exceptions.DataIntegrityException;
import com.projeto.ubercom.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository repo;

	/**
	 * @param id
	 * @return
	 */
	public Categoria find(Integer id) {
		Optional<Categoria> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Categoria.class.getName()));
	}

	/**
	 * @param obj
	 * @return
	 */
	public Categoria insert(Categoria obj) {
		obj.setId(null);
		return repo.save(obj);
	}

	/**
	 * @param obj
	 * @return
	 */
	public Categoria update(Categoria obj) {
		Categoria newObj = find(obj.getId());
 		updateData(newObj, obj);
 		return repo.save(newObj);
	}

	/**
	 * @param id
	 */
	public void delete(Integer id) {
		List<Produto> prod = produtoByCategoria(id);
		if (prod != null) {
			throw new DataIntegrityException(
					"Não é possível excluir a categoria pois associada a algum produto.");
		} else {
			repo.deleteById(id);
		}
	}
	
	/**
	 * @return
	 */
	public List<Categoria> findAll() {
		return repo.findAll();
	}
	
	/**
	 * @param page
	 * @param linesPerPage
	 * @param orderBy
	 * @param direction
	 * @return
	 */
	public Page<Categoria> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}
	
	/**
	 * @param objDto
	 * @return
	 */
	public Categoria fromDto(CategoriaDTO objDto) {
		return new Categoria(objDto.getId(),objDto.getNome());
	}
	
	/**
	 * @param newObj
	 * @param obj
	 */
	private void updateData(Categoria newObj, Categoria obj) {
 		newObj.setNome(obj.getNome());
 	}
	
	/**
	 * @param id
	 * @return
	 */
	public List<Produto> produtoByCategoria(Integer id){
		return repo.findProdutosPorCategorias(id);
	}

}
