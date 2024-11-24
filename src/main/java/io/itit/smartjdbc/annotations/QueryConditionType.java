package io.itit.smartjdbc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.itit.smartjdbc.enums.ConditionType;

/**
 * 
 * @author skydu
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface QueryConditionType {
	
	/**where逻辑运算符 默认and*/
	ConditionType value() default ConditionType.AND;
}
