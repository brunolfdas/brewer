package com.algaworks.brewer.service.exception;

public class NomeEstiloJaCadastradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	//cria a mensagem de erro
	public NomeEstiloJaCadastradoException(String message) {
		super(message);
	}

}
