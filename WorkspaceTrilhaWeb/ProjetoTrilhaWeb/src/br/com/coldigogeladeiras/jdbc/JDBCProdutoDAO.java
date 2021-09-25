package br.com.coldigogeladeiras.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import br.com.coldigogeladeiras.jdbcinterface.ProdutoDAO;
import br.com.coldigogeladeiras.modelo.Marca;
import br.com.coldigogeladeiras.modelo.Produto;


import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

public class JDBCProdutoDAO implements ProdutoDAO {

	private Connection conexao;
	
	public JDBCProdutoDAO(Connection conexao) {
		this.conexao = conexao;
	}
	
public boolean inserir(Produto produto) {
		
		String comando = "INSERT INTO produtos " + "(id, categoria, modelo, capacidade, valor, marcas_id)" + "VALUES (?,?,?,?,?,?)";
		System.out.println(produto+"?200");
		PreparedStatement p;
		
		try {
			
			//Prepara o comando para a execução no BD em que nos conectamos
			p = this.conexao.prepareStatement(comando);
			
			//Substitui no comando os "?" pelos valores do produto
			p.setInt(1, produto.getId());
			p.setString(2, produto.getCategoria());
			p.setString(3, produto.getModelo());
			p.setInt(4, produto.getCapacidade());
			p.setFloat(5, produto.getValor());
			p.setInt(6, produto.getMarcaId());
			
			//Executa o comando no BD
			p.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

public List<JsonObject> buscarPorNome(String nome){
	
	//Inicia criação do comando SQL de busca
	String comando = "SELECT produtos.*, marcas.nome as marca FROM produtos "
			+ "INNER JOIN marcas ON produtos.marcas_id = marcas.id ";
	//Se o nome não estiver vazio...
	if(!nome.equals("")) {
		//concatena no comando o WHERE buscando no nome do produto
		//o texto da variável nome
		comando += "WHERE modelo LIKE '%" + nome + "%'";
	}
	//Finaliza o comando ordenado alfabeticamente por
	//categoria, marca e depois modelo.
	comando += "ORDER BY categoria ASC, marcas.nome ASC, modelo ASC";
	
	List<JsonObject> listaProdutos = new ArrayList<JsonObject>();
	JsonObject produto = null;
	
	try {
		Statement stmt = conexao.createStatement();
		ResultSet rs = stmt.executeQuery(comando);
		
		while(rs.next()) {
			
			int id = rs.getInt("id");
			String categoria = rs.getString("categoria");
			String modelo = rs.getString("modelo");
			int capacidade = rs.getInt("capacidade");
			float valor = rs.getFloat("valor");
			String marcaNome = rs.getString("marca");
			
			if(categoria.equals("1")) {
				categoria = "Geladeira";
			}else if(categoria.equals("2")) {
				categoria = "Freezer";
			}
			
			produto = new JsonObject();
			produto.addProperty("id", id);
			produto.addProperty("categoria", categoria);
			produto.addProperty("modelo", modelo);
			produto.addProperty("capacidade", capacidade);
			produto.addProperty("valor", valor);
			produto.addProperty("marcaNome", marcaNome);
			
			listaProdutos.add(produto);
		}
		
	}catch(Exception e){
		e.printStackTrace();
	}
	return listaProdutos;
}

public boolean deletar(int id) {
	String comando = "DELETE FROM produtos WHERE id = ?";
	PreparedStatement p;
	try {
		p = this.conexao.prepareStatement(comando);
		p.setInt(1, id);
		p.execute();
	} catch (SQLException e) {
		e.printStackTrace();
		return false;
	}
	return true;
	}

public Produto buscarPorId(int id) {
	String comando = "SELECT * FROM produtos WHERE produtos.id = ?";
	Produto produto = new Produto();
	try {
		PreparedStatement p = this.conexao.prepareStatement(comando);
		p.setInt(1, id);
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			
			String categoria = rs.getString("categoria");
			String modelo = rs.getString("modelo");
			int capacidade = rs.getInt("capacidade");
			float valor = rs.getFloat("valor");
			int marcaId = rs.getInt("marcas_id");
			
			produto.setId(id);
			produto.setCategoria(categoria);
			produto.setMarcaId(marcaId);
			produto.setModelo(modelo);
			produto.setCapacidade(capacidade);
			produto.setValor(valor);
			
		}
	}catch (Exception e) {
		e.printStackTrace();
	}
	return produto;
}

public boolean alterar(Produto produto) {
	
	String comando = "UPDATE produtos "
			+ "SET categoria=?, modelo=?, capacidade=?, valor=?, marcas_id=?"
			+ " WHERE id=?";
	PreparedStatement p;
	try {
		p = this.conexao.prepareStatement(comando);
		p.setString(1, produto.getCategoria());
		p.setString(2, produto.getModelo());
		p.setInt(3, produto.getCapacidade());
		p.setFloat(4, produto.getValor());
		p.setInt(5, produto.getMarcaId());
		p.setInt(6, produto.getId());
		p.executeUpdate();
	} catch (SQLException e) {
		e.printStackTrace();
		return false;
	}
	return true;
}
////////////////////////////teste///////////////////////////////
public int validaMarca(int id) {
	String comando = "SELECT * FROM Marcas WHERE marcas.id = ?";
	int  idMarca =-1;
	try {
		PreparedStatement p = this.conexao.prepareStatement(comando);
		p.setInt(1, id);
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			idMarca = rs.getInt("id");
		}	
	}catch (Exception e) {
		e.printStackTrace();
	}
	return idMarca;
}

public boolean verificaProdutosIguais(Produto produto) {
	String comando = "SELECT * FROM produtos WHERE produtos.modelo = ? and produtos.categoria = ? and produtos.marcas_Id = ?";
	int marca_id = 0;
	String modelo = "";
	String categoria = "";
	int id = 0;
	
	boolean retorno = true;
	
	try {
		PreparedStatement p = this.conexao.prepareStatement(comando);
		
		p.setString(1,  produto.getModelo());
		p.setString(2,  produto.getCategoria());
		p.setInt(3,  produto.getMarcaId());
		
		ResultSet rs = p.executeQuery();
		
			while (rs.next()) {
				marca_id = rs.getInt("marcas_Id");
				modelo = rs.getString("modelo");
				categoria = rs.getString("categoria");
				id = rs.getInt("id");
			}	
			if(marca_id!=0 && modelo!="" && categoria!="" && id!= produto.getId()) {
				retorno = false;
			}
			 

		
	}catch (Exception e) {
		e.printStackTrace();
	}
	return retorno;
}

public int verificaProduto(int id) {
	
	String comando = "SELECT * FROM produtos WHERE produtos.id = ?"; //tabela marcas
	int retorno=-1;
	
	try {
		PreparedStatement m = this.conexao.prepareStatement(comando);
		m.setInt(1, id);
		ResultSet rs = m.executeQuery(); 

		while(rs.next()) {
				
			retorno = rs.getInt("id"); //substitui o -1 pelo id caso exista o produto		
		}

	}catch(Exception e) {
		e.printStackTrace();		
	}
	return retorno;
}



}