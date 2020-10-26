package mas.models.common;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import mas.models.common.enums.EnvironmentType;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class Context {

	private EnvironmentType environment;

	@JsonProperty("test_results_recipients")
	private List<String> resultRecipients;
	@JsonProperty("sending_mail")
	private SendingMail sender;
	@JsonProperty("testrail_url")
	private String testRailUrl;
	@JsonProperty("testrail_token")
	private String testRailToken;
	@JsonProperty("WebDriver")
	private WebDriver driver;


	public Context() {}
	
	public Context(EnvironmentType environment) {
		this.environment = environment;
	}

	public EnvironmentType getEnvironment() {
		return environment;
	}

	public Context setEnvironment(EnvironmentType environment) {
		this.environment = environment;
		return this;
	}

	@JsonGetter("test_results_recipients")
	public List<String> getResultRecipients() {
		return resultRecipients;
	}

	@JsonGetter("sending_mail")
	public SendingMail getSendingMail() {
		return sender;
	}

	@JsonGetter("testrail_url")
	public String getTestRailUrl() {
		return testRailUrl;
	}

	@JsonGetter("testrail_token")
	public String getTestRailToken() {
		return testRailToken;
	}


	@JsonSetter("test_results_recipients")
	public Context setResultRecipients(List<String> resultRecipients) {
		this.resultRecipients = resultRecipients;
		return this;
	}

	@JsonSetter("sending_mail")
	public Context setSendingMail(SendingMail sender) {
		this.sender = sender;
		return this;
	}

	@JsonSetter("testrail_url")
	public Context setTestRailUrl(String testRailUrl) {
		this.testRailUrl = testRailUrl;
		return this;
	}

	@JsonSetter("testrail_token")
	public Context setTestRailToken(String testRailToken) {
		this.testRailToken = testRailToken;
		return this;
	}

	@JsonSetter("WebDriver")
	public Context setDriver() {
		this.driver = driver;
		return this;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Context ");
		builder.append(", resultRecipients=");
		builder.append(resultRecipients);
		builder.append(", sender=");
		builder.append(sender);
		builder.append(", testRailUrl=");
		builder.append(testRailUrl);
		builder.append(", testRailToken=");
		builder.append(testRailToken);
		builder.append("]");
		return builder.toString();
	}

}
