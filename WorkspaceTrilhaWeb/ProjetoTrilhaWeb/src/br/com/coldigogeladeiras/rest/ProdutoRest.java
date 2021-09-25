package br.com.coldigogeladeiras.rest;

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

@Path("produto")
public class ProdutoRest extends UtilRest{
	
	@POST
	@Path("/inserir")
	@Consumes("application/*")
	public Response inserir(String produtoParam) {
		
		try {
			
			Produto produto = new Gson().fromJson(produtoParam, Produto.class);
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
				
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			int idMarca = produto.getMarcaId();
			
			int validaMarca = jdbcProduto.validaMarca(idMarca);
			
			String msg = "";
			boolean verificaProdutosIguais = false;
			
			if(validaMarca!=-1) {
				
				verificaProdutosIguais = jdbcProduto.verificaProdutosIguais(produto);
				//boolean verificaMarcaId = jdbcProduto.verificaMarcaId(produto);
				//boolean verificaCategoria = jdbcProduto.verificaCategoria(produto);
				
				//if(verificaModelo==true && verificaMarcaId==true && verificaCategoria==true) {
				//	verificaProduto= true;
				//}
				//System.out.println(verificaModelo +"teste"+ verificaMarcaId +"TESTE"+ verificaCategoria);
				if(verificaProdutosIguais) {
					
					boolean retorno = jdbcProduto.inserir(produto);
					conec.fecharConexao();
					if(retorno) {
						msg = "Produto cadastrado com sucesso!";//Produto cadastrado
					}else{
						msg = "Erro ao cadastrar produto!";//houve uma exeção
						return this.buildErrorResponse(msg);
					}
				}else {
					conec.fecharConexao();
					msg = "Produto já existente!";
					return this.buildErrorResponse(msg);
				}
				
			}else {
				conec.fecharConexao();
					//Marca não encontrada
				msg = "Marca não encontrada, recarregue a marca!";
				return this.buildErrorResponse(msg);
					
			}
				
			
			return this.buildResponse(msg);
			
		}catch(Exception e) {
		
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}
	
	@GET
	@Path("/buscar")
	@Consumes("application/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response buscarPorNome(@QueryParam("valorBusca")String nome) {
		
		try {
			
			List<JsonObject> listaProdutos = new ArrayList<JsonObject>();
			
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			listaProdutos = jdbcProduto.buscarPorNome(nome);
			conec.fecharConexao();
			
			String json = new Gson().toJson(listaProdutos);
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
	
		try {
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			String msg = "";
			int teste = jdbcProduto.verificaProduto(id);
			
			if(teste != -1) {
				boolean retorno = jdbcProduto.deletar(id);
				
				conec.fecharConexao();
				if(retorno) {
					msg = "Produto excluído com sucesso!";
				}else {
					msg = "Erro ao excluir produto.";
					return this.buildErrorResponse(msg);
				}
				
			}else {
				conec.fecharConexao();
				msg = "O produto selecionado não existe, recarregue a página.";
				return this.buildErrorResponse(msg);
			}
			
			
			
			return this.buildResponse(msg);
		
		}catch (Exception e) {
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
			Produto produto = new Produto();
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			String msg = ""; 
			produto = jdbcProduto.buscarPorId(id);
			
			int verificaProdutoExistente = produto.getId();
			conec.fecharConexao();
			if(verificaProdutoExistente==0) {
				
				msg = "Esse produto não consta em nosso sistema!";
				return this.buildErrorResponse(msg);
			}
			
			
			
			return this.buildResponse(produto);

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
			Produto produto = new Gson().fromJson(produtoParam, Produto.class);
			Conexao conec = new Conexao();
			Connection conexao = conec.abrirConexao();
			JDBCProdutoDAO jdbcProduto = new JDBCProdutoDAO(conexao);
			
			int idMarca = produto.getMarcaId(); //pego o id da marca
			int test = jdbcProduto.validaMarca(idMarca);
			boolean verificaAlterarProdutosIguais = jdbcProduto.verificaProdutosIguais(produto);
			
			int verificaProduto = jdbcProduto.verificaProduto(produto.getId());
			
			String msg = "";
			if(verificaProduto!=-1) {
			if(test!=-1) {
				if(verificaAlterarProdutosIguais==true) {
					boolean retorno = jdbcProduto.alterar(produto);
					
					if(retorno) {
						msg = "Produto alterado com sucesso!";
					}else {
						msg = "Erro ao alterar produto.";
						return this.buildErrorResponse(msg);
					}
				}else {
					msg = "Produto já existente.";
					return this.buildErrorResponse(msg);
				}
			
			}else {
				msg = "A marca selecionada não existe, recarregue a página!";
				return this.buildErrorResponse(msg);
			}
			}else {
				msg="O produto selecionado não existe!";
			}
			conec.fecharConexao();
			return this.buildResponse(msg);
			
		}catch(Exception e) {
			e.printStackTrace();
			return this.buildErrorResponse(e.getMessage());
		}
	}


	
}


