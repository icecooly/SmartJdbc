package io.itit.smartjdbc.provider.where.operator;

import java.util.ArrayList;
import java.util.List;

import io.itit.smartjdbc.SmartDataSource;
import io.itit.smartjdbc.provider.where.Where.Condition;

/**
 * 
 * @author skydu
 *
 */
public class OperatorContext {
	//
	private SmartDataSource smartDataSource;
	private Condition condition;
	private List<Object> parameters;
	//
	//
	public OperatorContext(SmartDataSource smartDataSource) {
		this.smartDataSource=smartDataSource;
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
	 * @return the smartDataSource
	 */
	public SmartDataSource getSmartDataSource() {
		return smartDataSource;
	}

	/**
	 * @param smartDataSource the smartDataSource to set
	 */
	public void setSmartDataSource(SmartDataSource smartDataSource) {
		this.smartDataSource = smartDataSource;
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

}
