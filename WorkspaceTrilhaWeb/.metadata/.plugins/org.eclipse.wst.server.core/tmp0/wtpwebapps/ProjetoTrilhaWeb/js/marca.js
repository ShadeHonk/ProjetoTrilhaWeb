COLDIGO.marca = new Object();

COLDIGO.marca.cadastrar = function(){

	var marca = new Object();
	
	marca.nome = document.frmAddMarca.marca.value;

	if((marca.marca=="")){
		COLDIGO.exibirAviso("Preencha todos os campos!");
		}
	
	$.ajax({
		
		type: "POST",
		url: COLDIGO.PATH + "marca/inserir",
		data: JSON.stringify(marca),
		success: function(msg){
			COLDIGO.marca.buscar();
			COLDIGO.exibirAviso(msg);
			$("#addMarca").trigger("reset"); //reseta o formulário
			
		},
		error: function(info){
			console.log(info);
			COLDIGO.exibirAviso("Erro ao cadastrar uma nova marca: "+ info.status + " - " + info.statusText);
		}
	});
	
	}
	COLDIGO.marca.buscar = function(){
		
		var valorBusca = $("#campoBuscaMarca").val();
		
		$.ajax({
			type: "GET",
			url: COLDIGO.PATH + "marca/buscar",
			data: "valorBusca=" + valorBusca,
			success: function(dados){
				
				dados = JSON.parse(dados);
				
				$("#listaMarcas").html(COLDIGO.marca.exibir(dados));
				
			},
			error: function(info){
				COLDIGO.exibirAviso("Erro ao consultar os contatos: "+ info.status + " - " + info.statusText);
			}
		});
		
	};
	
	
