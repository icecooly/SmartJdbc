package test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.itit.smartjdbc.annotations.QueryField;
import io.itit.smartjdbc.enums.SqlOperator;
import io.itit.smartjdbc.util.JSONUtil;

/*
 * 
 */
public class SelectProviderTestCase {

	//
	public static class TestQuery{
		
		public List<Integer> statusInList;
		
		@QueryField(operator = SqlOperator.EQ)
		public List<String> status3InList;
 
		
		public int[] statusInList2;
	}
	//
	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		Field field=TestQuery.class.getField("statusInList");
		Class<?> fieldType = field.getType();
		System.out.println(fieldType);
		
		Type genericType=field.getGenericType();
		if ( genericType instanceof ParameterizedType ) {
			System.out.println("111111");
		}
		int a[] =new int[] {1};
		Class<?> aClass=a.getClass();
		if(aClass.equals(int[].class)) {
			System.out.println("222222");
		}
		
		List<Integer> b=new ArrayList<>();
		Collection<String> ss=new ArrayList<>();
		ss.add("123");
		if (b instanceof Collection) {
			System.out.println("3333333");
		}
		System.out.println(JSONUtil.dump(ss.toArray()));
		
		
		System.out.println(JSONUtil.dump(a.getClass().getInterfaces()));
	}
	
}
