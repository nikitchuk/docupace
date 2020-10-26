package mas.exceptions;

public class InvalidConfigException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1833339086320673078L;
	
	private Throwable cause;
	private String message;
	
	public InvalidConfigException(String message) {
		this.message = message;	
	}

	public InvalidConfigException(Throwable cause, String message) {
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
