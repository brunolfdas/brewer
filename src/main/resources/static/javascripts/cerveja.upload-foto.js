var Brewer = Brewer || {};

Brewer.UploadFoto = (function() {
	
	function UploadFoto() {
		this.inputNomeFoto = $('input[name=foto]');
		this.inputContentType = $('input[name=contentType]');
		this.novaFoto = $('input[name=novaFoto]');
		this.inputUrlFoto = $('input[name=urlFoto]');
		
		this.htmlFotoCervejaTemplate = $('#foto-cerveja').html();
		this.template = Handlebars.compile(this.htmlFotoCervejaTemplate);
		
		this.containerFotoCerveja = $('.js-container-foto-cerveja');
		
		this.uploadDrop = $('#upload-drop');
		this.imgLoading = $('.js-img-loading');
	}
	
	UploadFoto.prototype.iniciar = function () {
		var settings = {
			type: 'json',
			filelimit: 1,
			allow: '*.(jpg|jpeg|png)',
			action: this.containerFotoCerveja.data('url-fotos'),
			complete: onUploadCompleto.bind(this),
			beforeSend: adicionarCsrfToken,
			loadstart: onLoadStart.bind(this)
		}
		
		UIkit.uploadSelect($('#upload-select'), settings);
		UIkit.uploadDrop(this.uploadDrop, settings);
		
		// mantem a imagem na tela após a validação
		//função call força o js a manter o objeto já instanciado
		if (this.inputNomeFoto.val()) {
			renderizarFoto.call(this, { 
				nome:  this.inputNomeFoto.val(),
				contentType: this.inputContentType.val(),
				url: this.inputUrlFoto.val()
			});
		}
	}
	
	function onLoadStart() {
		this.imgLoading.removeClass('hidden');
	}
	
	function onUploadCompleto(resposta) {
		this.novaFoto.val('true');
		this.inputUrlFoto.val(resposta.url);
		this.imgLoading.addClass('hidden');
		renderizarFoto.call(this, resposta);
	}
	
	function renderizarFoto(resposta) {
		this.inputNomeFoto.val(resposta.nome);
		this.inputContentType.val(resposta.contentType);
		
		//esconde a barra de upload e mostra a imagem
		this.uploadDrop.addClass('hidden');
		
//		var foto = '';
//		if (this.novaFoto.val() == 'true') {
//			foto = 'temp/';
//		}
//		foto += resposta.nome;
		
		var htmlFotoCerveja = this.template({url: resposta.url});
		this.containerFotoCerveja.append(htmlFotoCerveja);
		
		$('.js-remove-foto').on('click', onRemoverFoto.bind(this));
	}
	
	//remove a foto, zera as variaveis e retorna a barra de upload
	function onRemoverFoto() {
		$('.js-foto-cerveja').remove();
		this.uploadDrop.removeClass('hidden');
		this.inputNomeFoto.val('');
		this.inputContentType.val('');
		this.novaFoto.val('false');
	}
	
	//adiciona o token do csrf
	function adicionarCsrfToken(xhr) {
		var token = $('input[name=_csrf]').val();
		var header = $('input[name=_csrf_header]').val();
		xhr.setRequestHeader(header, token);
	}
	
	return UploadFoto;
	
})();

$(function() {
	var uploadFoto = new Brewer.UploadFoto();
	uploadFoto.iniciar();
});