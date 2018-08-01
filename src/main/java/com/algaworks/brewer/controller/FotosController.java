package com.algaworks.brewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.dto.FotoDTO;
import com.algaworks.brewer.storage.FotoStorage;
import com.algaworks.brewer.storage.FotoStorageRunnable;

//restcontroller é igual o controller normal, a unica dferenca que ele já vem c a anotação @responsebody
@RestController
@RequestMapping("/fotos")
public class FotosController {

	// injeta o bean fotos
	@Autowired
	private FotoStorage fotoStorage;

	@PostMapping
	public DeferredResult<FotoDTO> upload(@RequestParam("files[]") MultipartFile[] files) {
		DeferredResult<FotoDTO> resultado = new DeferredResult<>();

		Thread thread = new Thread(new FotoStorageRunnable(files, resultado, fotoStorage));
		thread.start();

		return resultado;
	}

	// mostra a foto na navegador -- ":.*" é uma expressao para poder pegar qualquer
	// extensao de arquivo
//	@GetMapping("/temp/{nome:.*}")
//	public byte[] recuperarFotoTemporaria(@PathVariable String nome) {
//		return fotoStorage.recuperarFotoTemporaria(nome);
//	}

	@GetMapping("/{nome:.*}")
	public byte[] recuperar(@PathVariable String nome) {
		return fotoStorage.recuperar(nome);
	}
}