package com.algaworks.brewer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.algaworks.brewer.service.CadastroCervejaService;
import com.algaworks.brewer.storage.FotoStorage;

@Configuration
@ComponentScan(basePackageClasses = { CadastroCervejaService.class, FotoStorage.class})
public class ServiceConfig {

	
	//inicializa o bean de fotos
//	@Bean
//	public FotoStorage fotoStorage() {
//		return new FotoStorageLocal();
//	}

}
