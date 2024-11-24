package io.itit.smartjdbc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author skydu
 *
 */
@Target(ElementType.TYPE)  
@Retention(RetentionPolicy.RUNTIME)  
@Documented
@Inherited  
public @interface Entity {
	
	/**注释*/
	String comment() default "";
	
	/**表名*/
	String tableName() default "";//tableName first than entityClass
}
