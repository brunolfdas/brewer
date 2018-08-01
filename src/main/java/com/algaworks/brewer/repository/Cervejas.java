package com.algaworks.brewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.helper.cerveja.CervejasQueries;

@Repository
//repositorio da entidade cerveja. long é a mesma tipo de variavel do ID do model cerveja
//uma interface pode extender varias interfaces
public interface Cervejas extends JpaRepository<Cerveja, Long>, CervejasQueries {

}