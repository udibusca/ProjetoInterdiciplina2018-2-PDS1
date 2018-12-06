package com.projeto.ubercom.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto.ubercom.domain.Cidade;
import com.projeto.ubercom.domain.Cliente;
import com.projeto.ubercom.domain.Endereco;
import com.projeto.ubercom.domain.enums.TipoCliente;
import com.projeto.ubercom.dto.ClienteDTO;
import com.projeto.ubercom.dto.ClienteNewDTO;
import com.projeto.ubercom.repositores.ClienteRepository;
import com.projeto.ubercom.repositores.EnderecoRepository;
import com.projeto.ubercom.services.exceptions.DataIntegrityException;
import com.projeto.ubercom.services.exceptions.ObjectNotFoundException;

/**
 * @author andre
 *
 */
@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repo;

	@Autowired
	private EnderecoRepository enderecoRepository;
	/**
	 * @param id
	 * @return
	 */
	public Cliente find(Integer id) {
		Optional<Cliente> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}

	/**
	 * @param obj
	 * @return
	 */
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repo.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}	
	
	/**
	 * @param obj
	 * @return
	 */
	@Transactional
	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj, obj);
		return repo.save(newObj);
	}

	/**
	 * @param id
	 */
	public void delete(Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há pedidos relacionados.");
		}
	}

	/**
	 * @return
	 */
	public List<Cliente> findAll() {
		return repo.findAll();
	}

	/**
	 * @param page
	 * @param linesPerPage
	 * @param orderBy
	 * @param direction
	 * @return
	 */
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}

	/**
	 * @param objDto
	 * @return
	 */
	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null);
	}

	/**
	 * @param objDto
	 * @return
	 */
	public Cliente fromDTO(ClienteNewDTO objDto) {
		Cliente cli = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfOuCnpj(),
				TipoCliente.toEnum(objDto.getTipo()));
		Cidade cid = new Cidade(objDto.getCidadeId(), null, null);
		Endereco end = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), 
				objDto.getComplemento(), objDto.getBairro(), objDto.getCep(), cli, cid);
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDto.getTelefone1());
		if(objDto.getTelefone2()!=null) {
			cli.getTelefones().add(objDto.getTelefone2());	
		}
		if(objDto.getTelefone3()!=null) {
			cli.getTelefones().add(objDto.getTelefone3());	
		}
		return cli;
	}
	
	/**
	 * @param newObj
	 * @param obj
	 */
	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}

	/**
	 * @param email
	 * @return
	 */
	public Cliente BuscaClientePorNomeCpfOuCnpj(String email) {
		Cliente cli = repo.findByEmail(email);
		if (cli != null) {
			return cli;
		} else {
			throw new ObjectNotFoundException(
					"Endereço de e-mail : " + email + " não encontrado! , Tipo: " + Cliente.class.getName());
		}

	}

}
