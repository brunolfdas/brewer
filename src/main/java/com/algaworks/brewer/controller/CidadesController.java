package com.algaworks.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.Cidades;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.service.CadastroCidadeService;
import com.algaworks.brewer.service.exception.CidadeJaCadastradaException;

@Controller
@RequestMapping("/cidades")
public class CidadesController {

	@Autowired
	private Cidades cidades;

	@Autowired
	private Estados estados;

	@Autowired
	private CadastroCidadeService cadastroCidadeService;

	@RequestMapping("/novo")
	public ModelAndView nova(Cidade cidade) {

		ModelAndView mv = new ModelAndView("cidade/CadastroCidade");
		mv.addObject("estados", estados.findAll());

		return mv;
	}

	// coloca em cach
	@Cacheable(value = "cidades", key = "#codigoEstado")
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Cidade> pesquisarPorCodigoEstado(
			@RequestParam(name = "estado", defaultValue = "-1") Long codigoEstado) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		return cidades.findByEstadoCodigo(codigoEstado);
	}

	@PostMapping("/novo")
	// invalida o cach
	@CacheEvict(value = "cidades", key = "#cidade.estado.codigo", condition = "#cidade.temEstado()")
	public ModelAndView salvar(@Valid Cidade cidade, BindingResult result, RedirectAttributes attributs) {
		if (result.hasErrors()) {
			return nova(cidade);
		}

		try {
			cadastroCidadeService.salvar(cidade);
		} catch (CidadeJaCadastradaException e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return nova(cidade);
		}

		attributs.addFlashAttribute("mensagem", "Cidade cadastrada com sucesso!");
		return new ModelAndView("redirect:/cidades/novo");

	}

	@GetMapping
	public ModelAndView pesquisar(CidadeFilter cidadeFilter, BindingResult result,
			@PageableDefault(size = 10) Pageable pageable, HttpServletRequest httpServletRequest) {

		ModelAndView mv = new ModelAndView("cidade/PesquisaCidades");
		mv.addObject("estados", estados.findAll());

		PageWrapper<Cidade> paginaWrapper = new PageWrapper<>(cidades.filtrar(cidadeFilter, pageable),
				httpServletRequest);
		mv.addObject("pagina", paginaWrapper);

		return mv;

	}
	
	@GetMapping("{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Cidade cidade) {
		ModelAndView mv = nova(cidade);
		mv.addObject(cidade);
		return mv;
	}

}