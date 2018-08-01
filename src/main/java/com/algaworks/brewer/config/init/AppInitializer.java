/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.algaworks.brewer.config.init;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.algaworks.brewer.config.JPAConfig;
import com.algaworks.brewer.config.MailConfig;
import com.algaworks.brewer.config.SecurityConfig;
import com.algaworks.brewer.config.ServiceConfig;
import com.algaworks.brewer.config.WebConfig;

/**
 *
 * @author Bruno
 */
public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	//este metodo é carregado assim que a aplicação inicia
    @Override
    protected Class<?>[] getRootConfigClasses() {
    	return new Class<?>[] { JPAConfig.class, ServiceConfig.class, SecurityConfig.class };
    }

    //Este metodo ajuda o Spring a achar a classe de configuração,
    //para que ele consiga encontrar os Controllers da aplicação
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebConfig.class, MailConfig.class};
    }

    //Este metodo diz qual url será denegado para o DispatcherServlet
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
    	//libera usar o put para fazer as atualizacoes
    	HttpPutFormContentFilter httpPutFormContentFilter = new HttpPutFormContentFilter();
    	return new Filter[] { httpPutFormContentFilter };
    }

    //inicia o sistema com multipart para poder fazer upload de arquivos
    @Override
	protected void customizeRegistration(Dynamic registration) {
		registration.setMultipartConfig(new MultipartConfigElement(""));
}
    
    //inicia e mostra qual é o "hambiente" padrao para pegar as fotos
    @Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		servletContext.setInitParameter("spring.profiles.default", "local");
}
    
}
