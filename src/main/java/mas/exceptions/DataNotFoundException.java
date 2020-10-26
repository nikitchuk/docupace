package mas.exceptions;

public class DataNotFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3218343675441166713L;
	private Throwable cause;
	private String message;

	public DataNotFoundException(String message) {
		this.message = message;	
	}

	public DataNotFoundException(Throwable cause, String message) {
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
