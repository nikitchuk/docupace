package mas.models.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendingMail {
	@JsonProperty("address")
	private String address;
	@JsonProperty("password")
	private String password;
	
	public String getAddress() {
		return address;
	}
	
	public String getPassword() {
		return password;
	}
	
	public SendingMail setAddress(String address) {
		this.address = address;
		return this;
	}
	
	public SendingMail setPassword(String password) {
		this.password = password;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SendingMail [address=").append(address).append(", password=").append(password).append("]");
		return builder.toString();
	}	
}
