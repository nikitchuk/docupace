package mas.exceptions;

public class JsonParsingException extends Exception {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6640943696606550178L;
	private Throwable cause;
	private String message;
	
	public JsonParsingException(String message) {
		this.message = message;	
	}

	public JsonParsingException(String message, Throwable cause) {
		this.cause = cause;
		this.message = message;
	}

	public Throwable getCause() {
		return cause;
	}

	public String getMessage() {
		return message;
	}
}
