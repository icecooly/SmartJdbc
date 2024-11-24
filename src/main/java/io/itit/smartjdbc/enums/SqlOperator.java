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
    BETWEEN_AND,
    NOT_BETWEEN_AND,
    IS_NULL,
    IS_NOT_NULL,
    IS_EMPTY,
    IS_NOT_EMPTY,
    JSON_CONTAINS_ANY,//右边包含任意一个左边的元素
    JSON_NOT_CONTAINS_ANY,//不包含任意一个
	JSON_CONTAINS_ALL,//左侧json对象全部被包含于右侧json
	JSON_NOT_CONTAINS_ALL,//不是全部包含
	JSON_EQ,//等于
	JSON_NE,//不等于
    JSON_FIELD_EQ,
    JSON_FIELD_NE,
    JSON_FIELD_LIKE,// 对象字段字符串模糊匹配
    JSON_FIELD_NOT_LIKE,// 对象字段字符串模糊匹配
    JSON_FIELD_LIKE_LEFT,// 对象字段字符串以xxx开头
    JSON_FIELD_LIKE_RIGHT,// 对象字段字符串以xxx结尾
    JSON_FIELD_IN,// 对象字段字符串在列表中
    ARRAY_EQ,//数组等于
    ARRAY_NE,//数组不等于
    ARRAY_IS_EMPTY,
    ARRAY_IS_NOT_EMPTY,
	ARRAY_ANY,//数组中包含任意一个元素
	ARRAY_NOT_ANY,//数组中不包含任意一个
	ARRAY_IN,//数组被包含 ARRAY[2,7] <@ ARRAY[1,7,4,2,6]
	ARRAY_NOT_IN,//数组不被包含
	JSON_ARRAY_EQ,
	JSON_ARRAY_NE,
    JSON_ARRAY_CONTAINS_ALL,//左侧json对象全部被包含于右侧json
    JSON_ARRAY_NOT_CONTAINS_ALL,//左侧json对象不全部被包含于右侧json
    JSON_ARRAY_CONTAINS_ANY,
    JSON_ARRAY_NOT_CONTAINS_ANY,
    JSON_ARRAY_IS_EMPTY,
    JSON_ARRAY_IS_NOT_EMPTY,
	//
	LTREE_BELONG,//属于
	
}
