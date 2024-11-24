package io.itit.smartjdbc.provider.where.operator;

import java.util.ArrayList;
import java.util.List;

import io.itit.smartjdbc.enums.DatabaseType;
import io.itit.smartjdbc.provider.where.Where.Condition;
import io.itit.smartjdbc.util.SmartJdbcUtils;

/**
 * 
 * @author skydu
 *
 */
public class OperatorContext {
	//
	private DatabaseType databaseType;
	private Condition condition;
	private List<Object> parameters;
	//
	//
	public OperatorContext(DatabaseType databaseType) {
		this.databaseType=databaseType;
		parameters=new ArrayList<>();
	}
	//

	/**
	 * @return the parameters
	 */
	public List<Object> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * 
	 * @param parameter
	 */
	public void addParameter(Object parameter) {
		this.parameters.add(parameter);
	}

	/**
	 * @return the condition
	 */
	public Condition getCondition() {
		return condition;
	}

	/**
	 * @param condition the condition to set
	 */
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	
	/**
	 * 
	 * @return
	 */
	public String identifier() {
		return SmartJdbcUtils.identifier(databaseType);
	}

	/**
	 * @return the databaseType
	 */
	public DatabaseType getDatabaseType() {
		return databaseType;
	}	

}
