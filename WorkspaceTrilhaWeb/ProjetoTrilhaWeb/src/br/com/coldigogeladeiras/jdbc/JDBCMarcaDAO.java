package br.com.coldigogeladeiras.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import br.com.coldigogeladeiras.jdbcinterface.MarcaDAO;
import br.com.coldigogeladeiras.modelo.Marca;
import br.com.coldigogeladeiras.modelo.Produto;



public class JDBCMarcaDAO implements MarcaDAO{
	
	private Connection conexao;
	
	public JDBCMarcaDAO(Connection conexao) {
		this.conexao = conexao;
	}
	
	public List<Marca> buscar(){
		//Criação da instrução SQL para busca de todas as marcas
		String comando = "SELECT * FROM marcas";
		
		//Criação de uma lista para armazenar cada marca encontrada
		List<Marca> listMarcas = new ArrayList<Marca>();
		
		//Criação do objeto marca com valor null (ou seja, sem instanciá-lo)
		Marca marca = null;
		
		//Abertura do try-catch
		try {
			//Uso da conexão do banco para prepara-lo para uma instrução SQL
			Statement stmt = conexao.createStatement();
			
			//Execução da instrução criada previamente
			//e armazenamento do resultado no objeto rs
			ResultSet rs = stmt.executeQuery(comando);
			
			//Enquanto houver uma próxima linha no resultado
			while(rs.next()) {
				
				//Criação de intância da classe Marca
				marca = new Marca();
				
				//Recebimento dos 2 dados retornados do BD para cada linha encontrada
				int id = rs.getInt("id");
				String nome = rs.getString("nome");
				int status = rs.getInt("status");
				
				//Setando no objeto marca os valores encontrados
				marca.setId(id);
				marca.setNome(nome);
				marca.setStatus(status);
				//Adição da instância contida no objeto Marca na lista de marcas
				listMarcas.add(marca);
			}
			
		//Caso alguma Exception seja gerada no try, recebe-a no objeto "ex"
		}catch(Exception ex) {
			//Exibe a exceção na console
			ex.printStackTrace();
		}
		
		//Retorna para quem chamou o método a lista criada
		return listMarcas;
	}
	//teste inserir marcas
	
	public List<JsonObject> buscarPorNome(String nome){
		
		//Inicia criação do comando SQL de busca
		String comando = "SELECT marcas.* FROM marcas ";
		//Se o nome não estiver vazio...
		if(!nome.equals("")) {
			//concatena no comando o WHERE buscando no nome do produto
			//o texto da variável nome
			comando += "WHERE nome LIKE '%" + nome + "%'";
		}
		//Finaliza o comando ordenado alfabeticamente por
		//categoria, marca e depois modelo.
		comando += "ORDER BY marcas.nome ASC";
		
		List<JsonObject> listaMarcas = new ArrayList<JsonObject>();
		JsonObject marcas = null;
		
		try {
			Statement stmt = conexao.createStatement();
			ResultSet rs = stmt.executeQuery(comando);
			
			while(rs.next()) {
				
				int id = rs.getInt("id");
				String nomeMarca = rs.getString("nome");
				int status = rs.getInt("status");
				
				
				marcas = new JsonObject();
				marcas.addProperty("id", id);
				marcas.addProperty("nome", nomeMarca);
				marcas.addProperty("status", status);
				listaMarcas.add(marcas);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return listaMarcas;
	}
	
	
	
public boolean inserir(Marca marca) {
		
		String comando = "INSERT INTO marcas " + "(id, nome)" + "VALUES (?,?)";
		System.out.println(marca+"?200");
		PreparedStatement m;
		
		
		try {
			
			//Prepara o comando para a execução no BD em que nos conectamos
			m = this.conexao.prepareStatement(comando);
			
			//Substitui no comando os "?" pelos valores do produto
			m.setInt(1, marca.getId());
			m.setString(2, marca.getNome());
			
			System.out.println(marca.getNome()+"?500");
			//Executa o comando no BD
			m.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

public boolean deletar(int id) {
	String comando = "DELETE FROM marcas WHERE id = ?";
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


public Marca buscarPorId(int id) {
	String comando = "SELECT * FROM marcas WHERE marcas.id = ?";
	Marca marca = new Marca();
	
	try {
		PreparedStatement p = this.conexao.prepareStatement(comando);
		p.setInt(1, id);
		ResultSet rs = p.executeQuery();
		
		while (rs.next()) {
			
			String nome = rs.getString("nome");
			int status = rs.getInt("status");
			
			marca.setId(id);
			marca.setNome(nome);
			marca.setStatus(status);
			
			
		}
	}catch (Exception e) {
		e.printStackTrace();
	}
	return marca;
}

public boolean alterar(Marca marca) {
	
	String comando = "UPDATE marcas "
			+ "SET nome=?"
			+ " WHERE id=?";
	PreparedStatement p;
	try {
		p = this.conexao.prepareStatement(comando);
		p.setString(1, marca.getNome());
		p.setInt(2, marca.getId());
		p.executeUpdate();
	} catch (SQLException e) {
		e.printStackTrace();
		return false;
	}
	return true;
}

////////////////////////////////////teste///////////////////////////////////////
public int verificaIdMarca(int id) {
	String comando = "SELECT * FROM marcas WHERE marcas.id = ?";
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

public int verificaIdProduto(int id) {
	String comando = "SELECT * FROM Produtos WHERE produtos.marcas_id = ?";
	int  idProduto =-2;
	try {
		PreparedStatement p = this.conexao.prepareStatement(comando);
		p.setInt(1, id);
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			idProduto = rs.getInt("marcas_id");
		}	
	}catch (Exception e) {
		e.printStackTrace();
	}
	return idProduto;
}


public boolean mudaStatus(Marca marca) {
	
	int status = marca.getStatus();
	
	if(status == 0) {
		status = 1;
	}else {
		status = 0;
	}
	
	String comando = "UPDATE marcas "
			+ "SET status=?"
			+ " WHERE id=?";
		
	PreparedStatement p;
	try {
		
		p = this.conexao.prepareStatement(comando);
		p.setInt(1, status);
		p.setInt(2, marca.getId());
		p.executeUpdate();
		
	} catch (SQLException e) {
		e.printStackTrace();
		return false;
	}
	return true;
}

public boolean verificaNome(Marca marca) {
	String comando = "SELECT * FROM marcas WHERE marcas.nome = ?";
	boolean retorno = true;
	
	try {
		PreparedStatement p = this.conexao.prepareStatement(comando);
		p.setString(1,  marca.getNome());
		
		ResultSet rs = p.executeQuery();
		
		while (rs.next()) {
			
			retorno= false;
			
			
		}
	}catch (Exception e) {
		e.printStackTrace();
	}
	return retorno;
}
public boolean verificaNomeIgual(Marca marca) {
	String comando = "SELECT * FROM marcas WHERE marcas.nome = ?";
	int id = 0;
	
	boolean  retorno = true;
	try {
		PreparedStatement p = this.conexao.prepareStatement(comando);
		p.setString(1, marca.getNome());
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			id = rs.getInt("id");
		}	
		if(marca.getId()!=id && id!= 0) {
			retorno = false;
		}
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	return retorno;
}



}

