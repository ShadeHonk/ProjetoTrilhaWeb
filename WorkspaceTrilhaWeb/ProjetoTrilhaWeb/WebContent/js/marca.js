COLDIGO.marca = new Object();

COLDIGO.marca.cadastrar = function(){

	var marca = new Object();
	
	marca.nome = document.frmAddMarca.marca.value;

	if((marca.nome=="")){
		
		COLDIGO.exibirAviso("Preencha todos os campos!");
		
	}else{
	
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
			
			COLDIGO.exibirAviso(info.responseText);
		}
	});
	}
	}
	
//Busca no BD e exibe na página as marcas que atendam à solicitação do usuário
	COLDIGO.marca.buscar = function(){
		
		var valorBusca = $("#campoBuscaMarca").val();
		
		$.ajax({
			type: "GET",
			url: COLDIGO.PATH + "marca/buscarPorNome",
			data: "valorBusca=" + valorBusca,
			success: function(dados){
				
				
				
				dados = JSON.parse(dados);
				$("#listaMarcas").html(COLDIGO.marca.exibir(dados));
				
			},
			error: function(info){
				COLDIGO.exibirAviso("Erro ao consultar os contatos: "+ info.status + " - " + info.responseText);
			}
		});
		
	};
	//Executa a função de busca ao carregar a página
	COLDIGO.marca.buscar();
	
	//Transforma os dados dos produtos recebidos do servidor em uma tabela HTML
	COLDIGO.marca.exibir = function(listaDeMarcas){
		
		var tabela = "<table>" +
		"<tr>"+ 
		"<th>Marca</th>" +
		"<th>Teste</th>" +
		"<th class='acoes'>Ações</th>" +
		"</tr>";
		
		if (listaDeMarcas != undefined && listaDeMarcas.length > 0){
			for(var i=0; i<listaDeMarcas.length; i++){
				var status = "";
				
				if(listaDeMarcas[i].status == 1){

				 status = "checked";
				
				}
				tabela += "<tr>" +
						"<td>"+listaDeMarcas[i].nome+"</td>" +
						"<td>" +		
								"<label class='switch'>"+
 								//"<input type='checkbox' class='slider' id='check' onclick=\"COLDIGO.marca.ativacaoMarca('"+listaDeMarcas[i].id+"')\  "+status+">"+
 								"<input  type='checkbox' class='slider' id='check' \ onclick=\"COLDIGO.marca.ativacaoMarca("+listaDeMarcas[i].id+",(this))\""+status+">" +
  								"<span class='slider' ></span>"+
								"</label>"+
								
								
						"</td>"+
						"<td>"+
							"<a onclick=\"COLDIGO.marca.exibirEdicao('"+listaDeMarcas[i].id+"')\"><img src='../../imgs/edit.png' alt='Editar registro'></a> " +
							"<a onclick=\"COLDIGO.marca.confirmaExclusao('"+listaDeMarcas[i].id+"')\"><img src='../../imgs/delete.png' alt='Excluir registro'></a>" +
							//"<a onclick=\"COLDIGO.marca.excluir('"+listaDeMarcas[i].id+"')\"><img src='../../imgs/delete.png' alt='Excluir registro'></a>" +
						"</td>" +
						"</tr>"
			
				
					
				
			}
			
		} else if(listaDeMarcas == ""){
			tabela += "<tr><td colspan='3'>Nenhum registro encontrado</td></tr>";
		}
		tabela += "</table>";
		
		return tabela;
	};
	
	
	
	//Exclui a marca selecionada
	COLDIGO.marca.excluir = function(id){
		
		$.ajax({
			type: "DELETE",
			url: COLDIGO.PATH + "marca/excluir/"+id,
			success: function(msg){	
				
				COLDIGO.exibirAviso(msg);
				
				/*if(msg==1){
					COLDIGO.exibirAviso("Marca excluída com sucesso!");
				}else if(msg==2){
					COLDIGO.exibirAviso("Essa marca possui produtos cadastrados!");
				}else if(msg==3){
					COLDIGO.exibirAviso("Essa marca já foi excluída,"+
										"recarregue a página!");
				}else{
					COLDIGO.exibirAviso("Erro ao excluir marca!");
				}*/
				COLDIGO.marca.buscar();
			},
			error: function(info){
				COLDIGO.exibirAviso("Erro ao excluir marca: "+ info.status + " - " + info.responseText);
			}
		
		});
		
	};
	
	//Carrega no BD os dados do produto selecionado para alteração e coloca-os no formulário de alteração
	COLDIGO.marca.exibirEdicao = function(id){
		
		$.ajax({
			type: "GET",
			url: COLDIGO.PATH + "marca/buscarPorId",
			data: "id="+id, //leva ao metodo
			success: function(marca){
				
				//console.log(produto);
				
				document.frmEditaMarca.idMarca.value = marca.id;
				document.frmEditaMarca.marca.value = marca.nome;
				//Utiliza os dados do banco de dados para mostar na tela.
				
				
				
				var modalEditaMarca = {
						title: "Editar Marca",
						height: 400,
						width: 480,
						modal: true,
						buttons:{
							"Salvar": function(){
								$(this).dialog("close");
								COLDIGO.marca.editar();
								
							},
							"Cancelar": function(){
								$(this).dialog("close");
							}
						},
						close: function(){
							//caso o usuário simplesmente feche a caixa de edição
							//não deve acontecer nada
						}
				};
				
				$("#modalEditaMarca").dialog(modalEditaMarca);
			},
			error: function(info){
				COLDIGO.exibirAviso("Erro ao buscar a marca para edição: "+ info.status + " - " + info.responseText);
			}
		});
	};

	
	//Realiza a edição dos dados no BD
	COLDIGO.marca.editar = function(){
		
		var marca = new Object();
		marca.id = document.frmEditaMarca.idMarca.value;
		marca.nome = document.frmEditaMarca.marca.value;
		//Utiliza os as informações da tela para tranferir para o banco de dados.
		
		
		$.ajax({
			type:"PUT",
			url: COLDIGO.PATH + "marca/alterar",
			data: JSON.stringify(marca),
			success: function(msg){
				COLDIGO.exibirAviso(msg);
				COLDIGO.marca.buscar();
				$("#modalEditaMarca").dialog("close");
			
			},
			error: function(info){
				COLDIGO.exibirAviso("Erro ao editar a marca: "+ info.status + " - " + info.responseText);
				
			}
		});
		
	};
//////////////////////////////////////////teste//////////////////////////////////////
	/*COLDIGO.marca.verificaExclusao = function(id){
		$.ajax({
			type: "GET",
			url: COLDIGO.PATH + "marca/verificaMarca/"+id,
			success:function(msg){
				if(msg==1){
					COLDIGO.marca.confirmaExclusao(id);
				}else if(msg==2){
					COLDIGO.exibirAviso("A marca tem ligações com produtos cadastrados.")
				}else{
					COLDIGO.exibirAviso("A marca não consta mais em nossos registros, Recarregue a página.")
				}
			
				
			},
			error: function(info){
				COLDIGO.exibirAviso("Erro ao excluir produto: "+ info.status + " - " + info.statusText);
			}
		});
	};*/
	
	COLDIGO.marca.confirmaExclusao = function(id){
		
		var modalExcluirMarca = {
				title: "Excluir Marca",
				height: 300,
				width: 400,
				modal: true,
				buttons:{
					"Ok": function(){
						COLDIGO.marca.excluir(id);
						
						
					},
					"Cancelar": function(){
						$(this).dialog("close");
					}
				},
				close: function(){
					//caso o usuário simplesmente feche a caixa de edição
					//não deve acontecer nada
				}
		};
		
		$("#modalAviso").html("Você deseja realmete excluir esse marca?");
		$("#modalAviso").dialog(modalExcluirMarca);
		
	};
	
COLDIGO.marca.ativacaoMarca = function(id, checkbox){
		console.log("teste123456789");
		
	$.ajax({
		type: "PUT",
		url: COLDIGO.PATH + "marca/ativacaoMarca/"+id,
		
		success: function(msg){
				COLDIGO.exibirAviso(msg);
				
			},
			error: function(info){
				checkbox.checked=!checkbox.checked;
				COLDIGO.exibirAviso("Erro ao editar a marca: "+ info.status + " - " + info.responseText);
				
			}
		});
		
	};