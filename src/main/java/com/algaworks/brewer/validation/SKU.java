package com.algaworks.brewer.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

//anotações obrigatórias para poder criar uma anotação
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
//[a-zA-Z]=letras minusculas e maiusculas, {2}= qtdade de caracteres. \\d{4}= seguido de 4 digitos. 
//? significa que só vai ser validado caso tenha conteudo
@Pattern(regexp = "([a-zA-Z]{2}\\d{4})?")
public @interface SKU {

	//sobreescrevendo a mensagem de erro
	@OverridesAttribute(constraint = Pattern.class, name = "message")
	String message() default "SKU deve seguir o padrão XX9999";
	
	//metodos obrigatorios para poder criar a anotação
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	
}
