package it.unipi.ing.drsptr.elastic.utilities;

public class JsonDocParserFieldNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public JsonDocParserFieldNotFoundException() {}
	
	public JsonDocParserFieldNotFoundException(String message) {
		super(message);
	}
	
	public JsonDocParserFieldNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public JsonDocParserFieldNotFoundException(Throwable cause) {
		super(cause);		
	}
	
	protected JsonDocParserFieldNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
