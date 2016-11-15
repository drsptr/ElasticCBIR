package it.unipi.ing.drsptr.elastic.utilities;

/*
 * JsonDocParserFieldNotFoundException is an exception class. It is used to triggered if the JSON document to parse
 * does not contain a field with the specified name.
 * @author		Pietro De Rosa
 */
public class JsonDocParserFieldNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;





/*
 * Constructor.
 */
	public JsonDocParserFieldNotFoundException() {}

/*
 * Constructor.
 * @param		message				-	message
 */
	public JsonDocParserFieldNotFoundException(String message) {
		super(message);
	}

/*
 * Constructor.
 * @param		message				-	message
 * @param		cause				-	cause
 */
	public JsonDocParserFieldNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

/*
 * Constructor.
 * @param		cause				-	cause
 */
	public JsonDocParserFieldNotFoundException(Throwable cause) {
		super(cause);		
	}

/*
 * Constructor.
 * @param		message				-	message
 * @param		cause				-	cause
 * @param		enableSuppression	-	enable suppression
 * @param		writableStackTrace	-	writeable stack trace
 */
	protected JsonDocParserFieldNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
