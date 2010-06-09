package br.ueg.openodonto.persistencia.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.ueg.openodonto.dominio.Telefone;
import br.ueg.openodonto.persistencia.EntityManager;
import br.ueg.openodonto.util.MementoBuilder;

public class DaoCrudTelefone extends BaseDAO<Telefone> implements EntityManager<Telefone> {

	private static final long serialVersionUID = -8028356632411640718L;

	private static Map<String , String>      storedQuerysMap;
	
	private static Map<Telefone, Telefone>   cachedSession;
	
	private Telefone                         managed;
	
	public static void main(String[] args) {
		DaoCrudTelefone dao = new DaoCrudTelefone();
		System.out.println(dao.getSelectRoot("ddd", "numero"));
	}
	
	static{		
		storedQuerysMap = new HashMap<String, String>();
		cachedSession = new HashMap<Telefone, Telefone>();
		initQuerymap();
		
	}
	
	public DaoCrudTelefone() {
		super(Telefone.class);
	}
	
	private static void initQuerymap(){
		storedQuerysMap.put("findByKey","SELECT * FROM telefones WHERE id = ?");
		storedQuerysMap.put("findByPessoa","SELECT * FROM telefones WHERE id_pessoa = ?");
	}
	
	@Override
	public Map<String, Object> format(Telefone entry) {
		return entry.format();
	}

	@Override
	public Telefone parseEntry(ResultSet rs) throws SQLException{
		Telefone telefone = new Telefone();
		telefone.parse(super.formatResultSet(rs));
		return telefone;
		
	}
	
	@Override
	public void alterar(Telefone o) throws Exception {		
		if(o != null && o.getCodigo() != null &&  pesquisar(o.getCodigo()) != null){
			Savepoint save = null;
			try{
				if(o == null){
					managed = null;
					return;
				}
				getConnection().setAutoCommit(false);
				save = getConnection().setSavepoint("Before Update Telefoe - Savepoint"); 
				Map<String , Object> params = new LinkedHashMap<String, Object>();
				params.put("id", o.getCodigo());
				super.executeUpdate(o, params);
				managed = o;
				cachedSession.put(managed , MementoBuilder.deepClone(managed));
			}catch(Exception ex){
				ex.printStackTrace();
				if(save != null){
					getConnection().rollback(save);
				}
				throw ex;
			}
			getConnection().setAutoCommit(true);
		}else if(o != null){
			inserir(o);
		}
	}

	@Override
	public boolean contem(Telefone entity) {
		return entity != null && DaoCrudTelefone.cachedSession.get(entity) != null;
	}

	@Override
	public List<Telefone> executarQuery(String nomeQuery, String nomeParametrro, Object valorParametro) throws Exception {
		return executarQuery(nomeQuery , nomeParametrro , valorParametro , null);
	}

	@Override
	public List<Telefone> executarQuery(String nomeQuery,String nomeParametrro, Object valorParametro, Integer quant)throws Exception {
		Map<String , Object> params = new LinkedHashMap<String, Object>();
		params.put(nomeParametrro, valorParametro);
		return executarQuery(nomeQuery, params, quant);
	}

	@Override
	public List<Telefone> executarQuery(String nomeQuery,Map<String, Object> params) {
		return executarQuery(nomeQuery, params, null);
	}

	@Override
	public List<Telefone> executarQuery(String nomeQuery,Map<String, Object> params, Integer quant) {
		List<Telefone> pList = new ArrayList<Telefone>();
		if(params == null){
			return pList;
		}
		try{
			getConnection().setReadOnly(true);
			ResultSet rs = super.executeQuery(
					DaoCrudTelefone.storedQuerysMap.get(nomeQuery),
					new ArrayList<Object>(params.values()) , quant);
			getConnection().setReadOnly(false);
			while(rs.next()) {
				Telefone telefone = this.parseEntry(rs);
				pList.add(telefone);
			}
			getConnection().setReadOnly(false);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return pList;
	}

	@Override
	public Telefone getEntityBean() {
		return cachedSession.get(managed);
	}

	@Override
	public void inserir(Telefone o) throws Exception {
		if (o != null && o.getCodigo() != null && pesquisar(o.getCodigo()) != null) {
			alterar(o);
		} else if (o != null) {
			Savepoint save = null;
			try {
				if (o == null) {
					managed = null;
					return;
				}
				getConnection().setAutoCommit(false);
				save = getConnection().setSavepoint("Before Insert Telefone - Savepoint");
				Map<String, Object> generated = super.executeInsert(o);
				o.setCodigo((Long) generated.values().iterator().next());
				managed = o;
				cachedSession.put(managed, MementoBuilder.deepClone(managed));
			} catch (Exception ex) {
				ex.printStackTrace();
				if (save != null) {
					getConnection().rollback(save);
				}
				throw ex;
			}
			getConnection().setAutoCommit(true);
		}
	}

	@Override
	public List<Telefone> listar(Object key) throws Exception {
		List<Telefone> pList = new ArrayList<Telefone>();
		if(key == null){
			return pList;
		}
		try{
			getConnection().setReadOnly(true);
			ResultSet rs = super.executeQuery(
					DaoCrudTelefone.storedQuerysMap.get("findByKey"),
					new Object[]{key});
			getConnection().setReadOnly(false);
			while(rs.next()) {
				Telefone telefone = this.parseEntry(rs);
				pList.add(telefone);
			}			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return pList;
	}

	@Override
	public List<Telefone> listar() {
		List<Telefone> telefone = new ArrayList<Telefone>();
		try{
			getConnection().setReadOnly(true);
			telefone = super.listar();
			getConnection().setReadOnly(false);
		}catch (Exception e) {
            e.printStackTrace();
		}
		return telefone;
	}

	@Override
	public Telefone pesquisar(Object key) {
		Telefone telefone = null;
		try {
			if (key != null) {
				getConnection().setReadOnly(true);
				List<Object> params = new ArrayList<Object>();
				params.add(key);
				ResultSet rs = super.executeQuery(
						DaoCrudTelefone.storedQuerysMap.get("findByKey"),
						params);
				getConnection().setReadOnly(false);
				if (rs.next()) {
					telefone = this.parseEntry(rs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return telefone;
	}

	@Override
	public void remover(Telefone o) throws Exception {
		Savepoint save = null;
		try{
			Map<String , Object> params = null;
			if(o != null && o.getCodigo() != null && o.getCodigo() > 0){
				params = new HashMap<String, Object>();
				params.put("id", o.getCodigo());
			}else{
				return;
			}
			getConnection().setAutoCommit(false);
			save = getConnection().setSavepoint("Before Remove Telefone - Savepoint");
			super.executeRemove(params);
		}catch(Exception ex){
			ex.printStackTrace();
			if(save != null){
				getConnection().rollback(save);
			}
			throw ex;
		}
		getConnection().setAutoCommit(true);
	}
	

}