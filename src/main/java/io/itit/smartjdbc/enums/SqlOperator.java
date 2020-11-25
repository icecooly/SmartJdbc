/**
 * 
 */
package io.itit.smartjdbc.enums;


/**
 * 
 * @author skydu
 *
 */
public enum SqlOperator {
	CUSTOM,
    IN,
    NOT_IN,
    LIKE,
    NOT_LIKE,
    LIKE_LEFT,//key LIKE '%值'
    NOT_LIKE_LEFT,
    LIKE_RIGHT,//key LIKE '值%'
    NOT_LIKE_RIGHT,
    EQ,//等于
    NE,//不等于
    GT,//大于
    GE,//大于等于
    LT,//小于
    LE,//小于等于
    IS_NULL,
    IS_NOT_NULL,
    JSON_CONTAINS_ANY,
    JSON_NOT_CONTAINS_ANY,
	JSON_CONTAINS_ALL,//全部包含
	ARRAY_ANY,
	ARRAY_NOT_ANY,//
	ARRAY_CONTAINS,//数组包含
	ARRAY_NOT_CONTAINS,//数组不包含
}
