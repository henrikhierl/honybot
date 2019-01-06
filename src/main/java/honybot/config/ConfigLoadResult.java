package honybot.config;

public class ConfigLoadResult<T> {

	private boolean success;
	private T value;
	private String message;
	
	public ConfigLoadResult(boolean success, T value, String message) {
		super();
		this.success = success;
		this.value = value;
		this.message = message;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
