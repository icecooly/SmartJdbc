package test.domain.query;

import java.util.List;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.enums.SqlOperator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import test.domain.entity.User;

/**
 * 
 * @author skydu
 *
 */
@EqualsAndHashCode(callSuper=true)
@Data
public class UserQuery extends Query<User>{

	@QueryField(operator = SqlOperator.LIKE)
	private String userName;
	
	@QueryField(operator = SqlOperator.LIKE)
	private String name;
	
	@QueryField(operator = SqlOperator.IS_NULL,field = "userName")
	private Boolean userNameIsNull;
	
	@QueryField
	private Integer gender;
	
	@QueryField(field ="age",operator=SqlOperator.GT)
	private Integer gtAge;
	
	@QueryField(field ="age",operator=SqlOperator.LT)
	private Integer ltAge;
	
	@QueryField(field ="status",operator=SqlOperator.IN)
	private List<Integer> statusInList;
	
	@QueryField(field ="status",operator=SqlOperator.NOT_IN)
	private List<Integer> statusNotInList;
	
	@QueryField(field ="status",operator=SqlOperator.IN)
	private Integer[] statusInList2;
	
	
	@QueryField(operator = SqlOperator.JSONCONTAINS,field = "roleIdList")
	private Integer roleId; 
	
	@QueryField(operator = SqlOperator.JSONCONTAINS)
	private Integer[] roleIdList; 
	
	@QueryField(operator = SqlOperator.NOT_JSONCONTAINS,field = "roleIdList")
	private Integer notRoleId; 
	
	@QueryField(operator = SqlOperator.NOT_JSONCONTAINS,field = "roleIdList")
	private Integer[] notRoleIdList; 
	
	@InnerJoin(table2 = User.class,table2Alias = "user",table1Fields ={"createUserId"},table2Fields ={"id"})
	@QueryField(field = "name")
	private String createUserName;
	
	@QueryField(foreignKeyFields = "departmentId",field = "name")
	private String departmentName;
	
	@QueryField(foreignKeyFields = "departmentId",field = "name",operator = SqlOperator.LIKE)
	private String likeDepartmentName;
}
