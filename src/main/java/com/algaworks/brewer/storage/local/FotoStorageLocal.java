package com.algaworks.brewer.storage.local;

import static java.nio.file.FileSystems.getDefault;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.storage.FotoStorage;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;


@Profile("local")
@Component
public class FotoStorageLocal implements FotoStorage {

	private static final Logger logger = LoggerFactory.getLogger(FotoStorageLocal.class);

	private Path local;
	//private Path localTemporario;

	public FotoStorageLocal() {
		this(getDefault().getPath(System.getenv("HOME"), ".brewerfotos"));
	}

	public FotoStorageLocal(Path path) {
		this.local = path;
		criarPastas();
	}

	// faz o upload para a pasta temporario
	@Override
	public String salvar(MultipartFile[] files) {
		String novoNome = null;
		if (files != null && files.length > 0) {
			MultipartFile arquivo = files[0];
			novoNome = renomearArquivo(arquivo.getOriginalFilename());
			try {
				arquivo.transferTo(new File(
						this.local.toAbsolutePath().toString() + getDefault().getSeparator() + novoNome));
			} catch (IOException e) {
				throw new RuntimeException("Erro ao salvar a foto na pasta temporária", e);
			}
		}
		
		try {
			Thumbnails.of(this.local.resolve(novoNome).toString()).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
		} catch (IOException e) {
			throw new RuntimeException("Erro gerando thumbnail", e);
		}

		return novoNome;
	}

//	@Override
//	public byte[] recuperarFotoTemporaria(String nome) {
//		try {
//			return Files.readAllBytes(this.localTemporario.resolve(nome));
//		} catch (IOException e) {
//			throw new RuntimeException("Erro lendo a foto temporária", e);
//		}
//	}
	
	//move a foto da pasta temp para a pasta principal e cria a thumbnail
//	@Override
//	public void salvar(String foto) {
//		try {
//			Files.move(this.localTemporario.resolve(foto), this.local.resolve(foto));
//		} catch (IOException e) {
//			throw new RuntimeException("Erro movendo a foto para destino final", e);
//		}
//		
//		try {
//			Thumbnails.of(this.local.resolve(foto).toString()).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
//		} catch (IOException e) {
//			throw new RuntimeException("Erro gerando thumbnail", e);
//		}
//	}
	
	//recupera a foto na pasta principal
	@Override
	public byte[] recuperar(String nome) {
		try {
			return Files.readAllBytes(this.local.resolve(nome));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo a foto", e);
		}
}
	
	@Override
	public byte[] recuperarThumbnail(String fotoCerveja) {
		return recuperar(THUMBNAIL_PREFIX + fotoCerveja);
	}
	
	@Override
	public void excluir(String foto) {
		try {
			Files.deleteIfExists(this.local.resolve(foto));
			Files.deleteIfExists(this.local.resolve(THUMBNAIL_PREFIX + foto));
		} catch (Exception e) {
			logger.warn(String.format("Erro apagando foto '%s' . Mensagem %s", foto, e.getMessage()));
		}
		
	}
	
	@Override
	public String getUrl(String foto) {
		return "http://localhost:8080/brewer/fotos/" + foto;
	}

	// cria as pastas
	private void criarPastas() {
		try {
			Files.createDirectories(this.local);
			//this.localTemporario = getDefault().getPath(this.local.toString(), "temp");
			//Files.createDirectories(this.localTemporario);

			if (logger.isDebugEnabled()) {
				logger.debug("Pastas criadas para salvar fotos.");
				logger.debug("Pasta default: " + this.local.toAbsolutePath());
				//logger.debug("Pasta temporária: " + this.localTemporario.toAbsolutePath());
			}
		} catch (IOException e) {
			throw new RuntimeException("Erro ao criar pasta para salvar foto", e);
		}
	}

	
	// cria o novo nome com uma string randomica
//	private String renomearArquivo(String nomeOriginal) {
//		String novoNome = UUID.randomUUID().toString() + "_" + nomeOriginal;
//
//		if (logger.isDebugEnabled()) {
//			logger.debug(String.format("Nome original: %s, novo nome: %s", nomeOriginal, novoNome));
//		}
//
//		return novoNome;
//
//	}

	

	

}