package mas.models.common.enums;

public enum ApiEndpoint {
	AUTO("/api/v1/auto");
	
	private ApiEndpoint(String address) {
		this.address = address;
	}

	private String address;

	public String address() {
		return address;
	}
}
