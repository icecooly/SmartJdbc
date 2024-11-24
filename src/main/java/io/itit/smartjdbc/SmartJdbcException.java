package io.itit.smartjdbc;

/**
 * 
 * @author icecooly
 *
 */
public class SmartJdbcException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//
	public SmartJdbcException(){
		super();
	}
	public SmartJdbcException(Throwable e){
		super(e);
	}
	//
	public SmartJdbcException(String s){
		super(s);
	}
	//
	public SmartJdbcException(String s,Throwable e){
		super(s,e);
	}
}
