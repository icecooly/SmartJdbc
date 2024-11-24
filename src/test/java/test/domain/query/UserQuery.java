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
	
	
	@QueryField(operator = SqlOperator.JSON_CONTAINS_ANY,field = "roleIdList")
	private Integer roleId; 
	
	@QueryField(operator = SqlOperator.JSON_CONTAINS_ANY)
	private Integer[] roleIdList; 
	
	@QueryField(operator = SqlOperator.JSON_NOT_CONTAINS_ANY,field = "roleIdList")
	private Integer notRoleId; 
	
	@QueryField(operator = SqlOperator.JSON_NOT_CONTAINS_ANY,field = "roleIdList")
	private Integer[] notRoleIdList; 
	
	@InnerJoin(table2 = User.class,table2Alias = "b1",table1Fields ={"createUserId"},table2Fields ={"id"})
	@QueryField(field = "name")
	private String createUserName;
	
	@QueryField(foreignKeyFields = "departmentId",field = "name")
	private String departmentName;
	
	@QueryField(foreignKeyFields = "departmentId",field = "name",operator = SqlOperator.LIKE)
	private String likeDepartmentName;
	
	@QueryField(field = "height",operator = SqlOperator.GE)
	private Double heightStart;
	
	@QueryField(field = "height",operator = SqlOperator.LE)
	private Double heightEnd;
	
	@QueryField
	private Boolean isStudent;
	
	@QueryField
	private Long no;
	
	@QueryField(whereSql = "a.setting is null")
	public Boolean settingIsNull;
	
	@QueryField(operator = SqlOperator.ARRAY_ANY)
	public List<Integer> intArray;
	
	@QueryField(operator = SqlOperator.ARRAY_ANY)
	public List<String> stringArray;
	
	@QueryField(operator = SqlOperator.ARRAY_ANY,field = "stringArray")
	public String[] stringContains;
	
	@QueryField(operator = SqlOperator.ARRAY_NOT_ANY,field = "stringArray")
	public String[] stringNotContains;
}
