/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.algaworks.brewer.config;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.cache.Caching;

import org.springframework.beans.BeansException;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.algaworks.brewer.controller.CervejasController;
import com.algaworks.brewer.controller.converter.CidadeConverter;
import com.algaworks.brewer.controller.converter.EstadoConverter;
import com.algaworks.brewer.controller.converter.EstiloConverter;
import com.algaworks.brewer.controller.converter.GrupoConverter;
import com.algaworks.brewer.session.TabelasItensSession;
import com.algaworks.brewer.thymeleaf.BrewerDialect;
import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;

import nz.net.ultraq.thymeleaf.LayoutDialect;

/**
 *
 * @author Bruno
 */
// A anotação @Configuration mostra para o spring que esta classe é de
// configuração
@Configuration

// A anotação @ComponentScan mostra para o Spring em qual pacote está as classes
// controladoras "Controller".
// podemos configurar ela com uma string desta forma:
// @ComponentScan("com.algaworks.brewer.controller"),
// porem desta forma se ocorrer erro de digitação o sistema não ira achar.
// Comfigurando usando o basePackageClasses ele indica que os controllers ficam
// no msm pacote que a classe referida
@ComponentScan(basePackageClasses = { CervejasController.class, TabelasItensSession.class })

// Esta anotação habilita o mvc para web
@EnableWebMvc

// Habilita os recursos web do spring data, ex: paginação
@EnableSpringDataWebSupport

// autoriza a utilização de cach
@EnableCaching

//habilita transações assincronas
@EnableAsync
public class WebConfig implements ApplicationContextAware, WebMvcConfigurer  {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	//não é usado mais assim a partir do spring 5
	//viewresolver para o jasper reports
//	@Bean
//	public ViewResolver jasperReportsViewResolver(DataSource dataSource) {
//		JasperReportsViewResolver resolver = new JasperReportsViewResolver();
//		resolver.setPrefix("classpath:/relatorios/");
//		resolver.setSuffix(".jasper");
//		resolver.setViewNames("relatorio_*");
//		resolver.setViewClass(JasperReportsMultiFormatView.class);
//		resolver.setJdbcDataSource(dataSource);
//		resolver.setOrder(0);
//		return resolver;
//	}
	
	// a viewResolver é o metodo que encontra e processa as paginas html
	@Bean
	public ViewResolver viewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		resolver.setCharacterEncoding("UTF-8");
		resolver.setOrder(1);
		return resolver;
	}

	// Esta anotação serve para este metodo ficar disponivel para o spring
	@Bean
	// ele que processa os arquivos html
	public TemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setEnableSpringELCompiler(true);
		engine.setTemplateResolver(templateResolver());

		// adiciona o novo layoutdialect para poder usar o thymeleaf-layout-dialect
		engine.addDialect(new LayoutDialect());
		// adiona o dialeto criado para o projeto
		engine.addDialect(new BrewerDialect());
		// dialeto do thymeleaf extra data
		engine.addDialect(new DataAttributeDialect());
		engine.addDialect(new SpringSecurityDialect());
		return engine;
	}

	// mostra onde vai ficar o template do projeto
	private ITemplateResolver templateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setApplicationContext(applicationContext);
		resolver.setPrefix("classpath:/templates/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		return resolver;
	}

	// metodo que mostra onde vai ficar os recursos "css, javascrpt, etc"
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}

	// Bean para fazer a conversao de entidades para que possam ser usadas em outras
	@Bean
	public FormattingConversionService mvcConversionService() {
		DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
		conversionService.addConverter(new EstiloConverter());
		conversionService.addConverter(new CidadeConverter());
		conversionService.addConverter(new EstadoConverter());
		conversionService.addConverter(new GrupoConverter());

		// conversor para o tipo bigdecimal no modelo internacional
		NumberStyleFormatter bigDecimalFormatter = new NumberStyleFormatter("#,##0.00");
		conversionService.addFormatterForFieldType(BigDecimal.class, bigDecimalFormatter);

		// cpmversor para inteiro no modelo internacional
		NumberStyleFormatter integerFormatter = new NumberStyleFormatter("#,##0");
		conversionService.addFormatterForFieldType(Integer.class, integerFormatter);
		
		// API de Datas do Java 8
		DateTimeFormatterRegistrar dateTimeFormatter = new DateTimeFormatterRegistrar();
		dateTimeFormatter.setDateFormatter(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		dateTimeFormatter.setTimeFormatter(DateTimeFormatter.ofPattern("HH:mm"));
		dateTimeFormatter.registerFormatters(conversionService);

		return conversionService;
	}

	// força o spring a tranformar tudo para o portugues do brasil
	@Bean
	public LocaleResolver localeResolver() {
		return new FixedLocaleResolver(new Locale("pt", "BR"));
	}
	
	@Bean
	public CacheManager cacheManager() throws Exception {
		//cach simples
		//return new ConcurrentMapCacheManager();
		
		// o cache guava foi descontinuado a partir do spring 5
		//cach avançado usando o Guava
//		CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
//				.maximumSize(3)
//				.expireAfterAccess(20, TimeUnit.SECONDS);
//		
//		GuavaCacheManager cacheManager = new GuavaCacheManager();
//		cacheManager.setCacheBuilder(cacheBuilder);
//		return cacheManager;
		
		//---------metodo para cach usando o Jcache e o Ehcache 
		return new JCacheCacheManager(Caching.getCachingProvider().getCacheManager(
				getClass().getResource("/cache/ehcache.xml").toURI(),
				getClass().getClassLoader()));
	}
	
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource bundle = new ReloadableResourceBundleMessageSource();
		bundle.setBasename("classpath:/messages");
		bundle.setDefaultEncoding("UTF-8"); // http://www.utf8-chartable.de/
		
		return bundle;
	}
	
	//faz a "conversão e integração" do Spring mvc com o Spring Jpa
	@Bean
	public DomainClassConverter<FormattingConversionService> domainClassConverter() {
		return new DomainClassConverter<FormattingConversionService>(mvcConversionService());
}
}
