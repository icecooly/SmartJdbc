package test.domain.query;

import io.itit.smartjdbc.annotations.QueryConditionType;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.enums.ConditionType;
import io.itit.smartjdbc.enums.SqlOperator;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author skydu
 *
 */
@EqualsAndHashCode(callSuper=true)
@Data
public class UserComplexQuery extends UserQuery{
	//
	@Data
	public static class StatusAndMobile{
		@QueryField
		private Integer status;
		
		@QueryField(operator = SqlOperator.LIKE)
		private String mobileNo;
	}
	//
	@Data
	public static class NameOrUserNameOrDeptName{
		@QueryField
		private String name;
		
		@QueryField
		private String userName;
		
		@QueryField(foreignKeyFields = "departmentId",field = "name")
		private String deptName;
		
		@QueryConditionType(ConditionType.AND)
		private StatusAndMobile statusAndMobile;
	};
	
	@QueryConditionType(ConditionType.OR)
	private NameOrUserNameOrDeptName nameOrUserName;
	
	
	//
	@QueryField(operator = SqlOperator.IS_NOT_NULL)
	private Boolean nameIsNotNull;
}
