package com.algaworks.brewer.model;

public enum Origem {

	//NACIONAL=como vai ser salvo no banco ou usuado no código. ("Nacional")= como vai ser mostrado ao usuário
	NACIONAL("Nacional"),
	INTERNACIONAL("Internacional");
	
	//Deve criar um contrutor para retornar a String que está dentro do ()
	private String descricao;
	
	Origem(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricao() { 
		return descricao;
	}
	
}