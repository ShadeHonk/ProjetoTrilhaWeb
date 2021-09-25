package br.com.coldigogeladeiras.rest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import br.com.coldigogeladeiras.bd.Conexao;
import br.com.coldigogeladeiras.jdbc.JDBCMarcaDAO;

import br.com.coldigogeladeiras.modelo.Marca;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.coldigogeladeiras.bd.Conexao;
import br.com.coldigogeladeiras.jdbc.JDBCProdutoDAO;
import br.com.coldigogeladeiras.modelo.Produto;

@Path("marca")
public class MarcaRest extends UtilRest{
	
	@GET
	@Path("/buscar")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buscar() {
		
		try {
			List<Marca> listaMarcas = new ArrayList<Marca>();
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			listaMarcas = jdbcMarca.buscar();
			
			conec.fecharConexao();
			
			return this.buildResponse(listaMarcas);
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
	

	@POST
	@Path("/inserir")
	@Consumes("application/*")
	public Response inserir(String marcaParam) {
	
		 try {
			 	String msg = "";
				Marca marca = new Gson().fromJson(marcaParam, Marca.class);
				Conexao conec = new Conexao();
				Connection conexao = conec.abrirConexao();
			 	
				JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
				
				boolean verificaMarca = jdbcMarca.verificaNome(marca);
				
				if(verificaMarca) {
					
					boolean retorno = jdbcMarca.inserir(marca);
					conec.fecharConexao();
					if(retorno) {
					
						msg = "Marca cadastrada com sucesso!";
					
					}else {
						msg = "Erro ao cadastrar marca.";
						return this.buildErrorResponse(msg);
					}
				}else {
					conec.fecharConexao();
					msg = "Marca já existente!";
					return this.buildErrorResponse(msg);
				}
				
				
				return this.buildResponse(msg);
				
		}catch(Exception e) {
			e.printStackTrace();
		
			return this.buildErrorResponse(e.getMessage());
			}
	 }

	@GET
	@Path("/buscarPorNome")
	@Consumes("application/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buscarPorNome(@QueryParam("valorBusca") String nome) {
		
		try {
			
			List<JsonObject> listaMarcas = new ArrayList<JsonObject>();
			
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			listaMarcas = jdbcMarca.buscarPorNome(nome);
			conec.fecharConexao();
			String json = new Gson().toJson(listaMarcas);
			return this.buildResponse(json);
			
		}catch (Exception e){
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
	@DELETE
	@Path("/excluir/{id}")
	@Consumes("application/*")
	public Response excluir(@PathParam("id") int id) {
		
		String retorno = "";
		
		try {
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			
			int retornoM = jdbcMarca.verificaIdMarca(id);  //Verifica o id da marca	
			int retornoP = jdbcMarca.verificaIdProduto(id); //Verifica se a marca tem produtos cadastrados
			
			if(retornoM != -1) { //caso o retorno do id de marca seja o id da marca
				//retorno = 1;
				
				if(retornoM == retornoP) { //se a marca a ser excluida existir nos produtos cadastrados
					conec.fecharConexao();
					retorno = "Essa marca possui produtos cadastrados!";
					return this.buildErrorResponse(retorno);
				
				}else {
					retorno = "Marca excluída com sucesso!";
					boolean retornoDeletar = jdbcMarca.deletar(id); //faz a exclusão
					conec.fecharConexao();
						if(retornoDeletar!=true) { //valida se deu excessao no metodo de deletar
							retorno = "Erro ao excluir marca!";
							return this.buildErrorResponse(retorno);
						}
				}
			
			}else { //se o retorno do id da marca não possuir o id da marca 
				conec.fecharConexao();
				retorno = "Essa marca já foi excluída!";
				return this.buildErrorResponse(retorno);
			}
		
			
			
			return this.buildResponse(retorno);
			
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
	@GET
	@Path("/buscarPorId")
	@Consumes("application/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buscarPorId(@QueryParam("id") int id) {
		
		try {
			Marca marca = new Marca();
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			String msg = "";
			
			
			marca = jdbcMarca.buscarPorId(id);
			
			int verificaMarcaExistente = marca.getId();
			conec.fecharConexao();
			if(verificaMarcaExistente == 0) {
				msg = "Essa marca não consta em nosso sistema!";
				return this.buildErrorResponse(msg);
			}

			return this.buildResponse(marca);

		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
	@PUT
	@Path("/alterar")
	@Consumes("application/*")
	public Response alterar(String produtoParam) {
		try {
			Marca marca = new Gson().fromJson(produtoParam, Marca.class);
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			String msg = "";
			int idMarca = marca.getId(); //pego o id da marca
			int test = jdbcMarca.verificaIdMarca(idMarca); //Verifica se a marca é existe para alterá-la.
			boolean verificaNomeIgual= jdbcMarca.verificaNomeIgual(marca); //Verifica se existe alguma marca com o mesmo nome

			if(test!=-1 && verificaNomeIgual == true) {
				
			boolean retorno = jdbcMarca.alterar(marca);
			conec.fecharConexao();
			
				if(retorno) {
					msg = "Marca alterada com sucesso!";
				}else {
					msg = "Erro ao alterar a marca.";
					return this.buildErrorResponse(msg);
				}
			}else {
				if(test==-1) {
					msg = "A marca selecionada não existe";
					return this.buildErrorResponse(msg);
				}else {
					msg = "Marca com esse nome jé consta no sistema!";
					return this.buildErrorResponse(msg);
				}
				
			}
			
			
			return this.buildResponse(msg);
			
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}


	
////////////////////////////////teste////////////////////////////////////////////
	/*@GET
	@Path("/verificaMarca/{id}")
	@Consumes("application/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response verificaExclusao(@PathParam("id") int id) {
	
		int retorno = 0;
		try {
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			
			int retornoIdMarca = jdbcMarca.verificaIdMarca(id);
			int retornoIdProduto = jdbcMarca.verificaIdProduto(id);
			if(retornoIdMarca!=-1) {
				retorno = 1; //A marca existe
				if(retornoIdMarca==retornoIdProduto) {
					retorno = 2; //A marca está sendo utilizada em produtos existentes.
					 //Marca excluída com sucesso!
				}
			}else {
				retorno = 3; // A marca não existe.
			}
		
			conec.fecharConexao();
			return this.buildResponse(retorno);

		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}*/
	
	@PUT
	@Path("/ativacaoMarca/{id}")
	@Consumes("application/*")
	public Response ativacaoMarca(@PathParam("id") int id) {
		
		
		try {
			Marca marca = new Marca();
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCMarcaDAO jdbcMarca = new JDBCMarcaDAO(conexao);
			
			marca = jdbcMarca.buscarPorId(id);
			String msg="";
			int idMarca = jdbcMarca.verificaIdMarca(id);
			
			if(idMarca!= -1) {
			boolean retorno = jdbcMarca.mudaStatus(marca);
			
			conec.fecharConexao();
			if(retorno== true) {
				msg = "O status da marca foi alterado com sucesso";
			}else {
				msg = "Houve um erro ao alterar o status da marca";
				return this.buildErrorResponse(msg);
			}
			}else {
				conec.fecharConexao();
				msg = "Marca não econtrada, recarregue a página.";
				return this.buildErrorResponse(msg);
			}
			
			
			return this.buildResponse(msg);

		}catch(Exception e) {
			e.printStackTrace();
			
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
}