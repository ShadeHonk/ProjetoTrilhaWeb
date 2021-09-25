package br.com.coldigogeladeiras.jdbcinterface;

import java.util.List;

import br.com.coldigogeladeiras.modelo.Marca;
import br.com.coldigogeladeiras.modelo.Produto;


public interface MarcaDAO {

	public List<Marca> buscar();
	public boolean inserir(Marca marca);
	public boolean deletar(int id);
	public Marca buscarPorId(int id);
	public boolean alterar(Marca marca);
	public int verificaIdMarca(int id);
	public int verificaIdProduto(int id);
}
