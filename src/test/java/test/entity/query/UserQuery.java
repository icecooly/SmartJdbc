package test.entity.query;

import java.util.List;

import io.itit.smartjdbc.Query;
import io.itit.smartjdbc.annotations.InnerJoin;
import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.enums.SqlOperator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import test.entity.Department;
import test.entity.User;

/**
 * 
 * @author skydu
 *
 */
@EqualsAndHashCode(callSuper=true)
@Data
public class UserQuery extends Query<User>{

	@QueryField
	private String userName;
	
	@QueryField
	private Integer gender;
	
	@QueryField(field="name,userName")
	private String nameOrUserName;
	
	@QueryField(field ="status",operator=SqlOperator.IN)
	private List<Integer> statusInList;
	
	@QueryField(field ="status",operator=SqlOperator.NOT_IN)
	private List<Integer> statusNotInList;
	
	@QueryField(whereSql="json_contains(a.role_id_list,'${roleId}')")
	private Integer roleId; 
	
	@QueryField(whereSql="a.status=#{orStatus} or json_contains(a.role_id_list,#{orRoleId})")
	private Boolean statusOrRoleId;
	
	@InnerJoin(table2 = User.class,table2Alias = "user",table1Fields ={"createUserId"},table2Fields ={"id"})
	@QueryField(field = "name")
	private String createUserName;
	
	@QueryField(foreignKeyFields = "departmentId",field = "name",operator = SqlOperator.LIKE)
	private String departmentName;
	
	@InnerJoin(table2 = Department.class,table2Alias = "dep",table1Fields ={"departmentId"},table2Fields ={"id"})
	@QueryField(field = "name",operator = SqlOperator.LIKE)
	private String departmentName2;
	
	@QueryField(alias = "dep",field = "status")
	private Integer departmentStatus;
}
