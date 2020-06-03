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
    IN,
    NOT_IN,
    LIKE,
    NOT_LIKE,
    LIKE_LEFT,
    NOT_LIKE_LEFT,
    LIKE_RIGHT,
    NOT_LIKE_RIGHT,
    EQ,//等于
    NE,//不等于
    GT,//大于
    GE,//大于等于
    LT,//小于
    LE,//小于等于
    IS_NULL,
    IS_NOT_NULL,
    BETWEEN,
    JSONCONTAINS,
    NOT_JSONCONTAINS;
}
