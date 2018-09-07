package com.projeto.ubercom.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.projeto.ubercom.domain.Cliente;
import com.projeto.ubercom.domain.enums.TipoCliente;
import com.projeto.ubercom.dto.ClienteNewDTO;
import com.projeto.ubercom.repositores.ClienteRepository;
import com.projeto.ubercom.resources.exception.FieldMessage;
import com.projeto.ubercom.services.validation.utils.BR;

 public class ClienteInsertValidator implements ConstraintValidator<ClienteInsert, ClienteNewDTO> {
 	
	@Autowired
	private ClienteRepository repo;  
	 
	@Override
	public void initialize(ClienteInsert ann) {
	}
 	
	@Override
	public boolean isValid(ClienteNewDTO objDto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();
		
		if (objDto.getTipo().equals(TipoCliente.PESSOAFISICA.getCod()) && !BR.isValidCPF(objDto.getCpfOuCnpj())) {
			list.add(new FieldMessage("cpfOuCnpj", "CPF inválido"));
		}
 		if (objDto.getTipo().equals(TipoCliente.PESSOAJURIDICA.getCod()) && !BR.isValidCNPJ(objDto.getCpfOuCnpj())) {
			list.add(new FieldMessage("cpfOuCnpj", "CNPJ inválido"));
		}
 		
 		Cliente aux = repo.findByEmail(objDto.getEmail());
 		if(aux != null) {
 			list.add(new FieldMessage("email","Email já existe"));
 		}
 		
 		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}
