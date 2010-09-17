package br.ueg.openodonto.controle.busca;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ueg.openodonto.dominio.Colaborador;
import br.ueg.openodonto.dominio.Paciente;
import br.ueg.openodonto.persistencia.orm.OrmResolver;
import br.ueg.openodonto.persistencia.orm.OrmTranslator;
import br.ueg.openodonto.servico.busca.FieldFacade;
import br.ueg.openodonto.servico.busca.InputMask;
import br.ueg.openodonto.servico.busca.MessageDisplayer;
import br.ueg.openodonto.servico.busca.SearchFilter;
import br.ueg.openodonto.servico.busca.SearchFilterBase;
import br.ueg.openodonto.servico.busca.Searchable;
import br.ueg.openodonto.validator.Validator;
import br.ueg.openodonto.validator.ValidatorFactory;

public class SearchableColaborador implements Searchable<Colaborador> {

	private static List<FieldFacade>   facade;
	private Map<String,SearchFilter>   filtersMap;
	private Map<String,InputMask>      masksMap;
	private MessageDisplayer           displayer;
	
	private List<SearchFilter>         filtersList;
	private List<InputMask>            masksList;
	
	static{
		buildFacade();
	}	
	
	public SearchableColaborador(MessageDisplayer displayer) {
		this.displayer = displayer;
		buildFilter();
		filtersList = new ArrayList<SearchFilter>(filtersMap.values());
		masksList = new ArrayList<InputMask>(masksMap.values());
	}

	private static void buildFacade(){
		OrmTranslator translator = new OrmTranslator(OrmResolver.getAllFields(new ArrayList<Field>(), Paciente.class, true));
		facade = new ArrayList<FieldFacade>();
		facade.add(new FieldFacade("C�digo",translator.getColumn("codigo")));
		facade.add(new FieldFacade("Nome",translator.getColumn("nome")));
		facade.add(new FieldFacade("Categoria",translator.getColumn("categoria")));
		facade.add(new FieldFacade("Descri��o",translator.getColumn("descricao")));
	}
	
	private void buildFilter(){
		filtersMap = new HashMap<String,SearchFilter>();
		buildNameFilter();
		buildCategoriaFilter();
		buildDescricaoFilter();
	}

	private SearchFilterBase buildBasicFilter(String name,String label,Validator... validator){
		SearchFilterBase filter = new SearchFilterBase(null,name,label,displayer);
		SearchFilterBase.Field field = filter.new Field();
		filter.setField(field);
		SearchFilterBase.Input<String> input = filter.new Input<String>();
		input.getValidators().addAll(Arrays.asList(validator));
		field.getInputFields().add(input);
		return filter;
	}
	
	private void buildDescricaoFilter() {
		Validator validator = ValidatorFactory.newStrRangeLen(300,5, true);
		filtersMap.put("nomeFilter", buildBasicFilter("descricaoFilter","Descri��o",validator));
	}

	private void buildCategoriaFilter() {
	
	}

	private void buildNameFilter() {
		Validator validator = ValidatorFactory.newStrRangeLen(150,3, true);
		filtersMap.put("nomeFilter", buildBasicFilter("nomeFilter","Nome",validator));
	}

	@Override
	public List<FieldFacade> getFacade() {
		return facade;
	}

	@Override
	public List<SearchFilter> getFilters() {
		return filtersList;
	}

	@Override
	public List<InputMask> getMasks() {
		return masksList;
	}

	public Map<String, SearchFilter> getFiltersMap() {
		return filtersMap;
	}

	public void setFiltersMap(Map<String, SearchFilter> filtersMap) {
		this.filtersMap = filtersMap;
	}

	public Map<String, InputMask> getMasksMap() {
		return masksMap;
	}

	public void setMasksMap(Map<String, InputMask> masksMap) {
		this.masksMap = masksMap;
	}

	public List<SearchFilter> getFiltersList() {
		return filtersList;
	}

	public void setFiltersList(List<SearchFilter> filtersList) {
		this.filtersList = filtersList;
	}

	public List<InputMask> getMasksList() {
		return masksList;
	}

	public void setMasksList(List<InputMask> masksList) {
		this.masksList = masksList;
	}

}