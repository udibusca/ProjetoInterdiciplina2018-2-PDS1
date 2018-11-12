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
import com.projeto.ubercom.dto.ProdutoDTO;
import com.projeto.ubercom.repositores.CategoriaRepository;
import com.projeto.ubercom.repositores.ProdutoRepository;
import com.projeto.ubercom.services.exceptions.DataIntegrityException;
import com.projeto.ubercom.services.exceptions.ObjectNotFoundException;

@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository repo;

	@Autowired
	private CategoriaRepository categoriaRepository;

	/**
	 * @param id
	 * @return
	 */
	public Produto find(Integer id) {
		Optional<Produto> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Produto.class.getName()));
	}

	/**
	 * @param id
	 */
	public void delete(Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir o produto");
		}
	}
	
	/**
	 * @param nome
	 * @param ids
	 * @param page
	 * @param linesPerPage
	 * @param orderBy
	 * @param direction
	 * @return
	 */
	public Page<Produto> search(String nome, List<Integer> ids, Integer page, Integer linesPerPage, String orderBy,
			String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		List<Categoria> categorias = categoriaRepository.findAllById(ids);
		return repo.findDistinctByNomeContainingAndCategoriasIn(nome, categorias, pageRequest);
	}

	/**
	 * @param objDto
	 * @return
	 */
	public Produto fromDto(ProdutoDTO objDto) {
		return new Produto(objDto.getId(),objDto.getNome(),objDto.getPreco());
	}
	
	/**
	 * @param obj
	 * @return
	 */
	public Produto insert(Produto obj) {
		obj.setId(null);
		return repo.save(obj);
	}
	
	/**
	 * @return
	 */
	public List<Produto> findAll() {
		return repo.findAll();
	}
}
