package ar.com.pmp.subastados.service.dto;

import ar.com.pmp.subastados.domain.DeviceRegistration;

public class DeviceRegistrationDTO {

	private String registrationId;
	private String platform;
	private String version;
	private String model;

	private String userLogin;

	public DeviceRegistrationDTO() {
		// Empty constructor needed for Jackson.
	}

	public DeviceRegistrationDTO(DeviceRegistration source) {
		this.model = source.getModel();
		this.platform = source.getPlatform();
		this.registrationId = source.getRegistrationId();
		this.version = source.getVersion();
		this.userLogin = source.getUser().getLogin();
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeviceRegistrationDTO [registrationId=");
		builder.append(registrationId);
		builder.append(", platform=");
		builder.append(platform);
		builder.append(", version=");
		builder.append(version);
		builder.append(", model=");
		builder.append(model);
		builder.append(", userLogin=");
		builder.append(userLogin);
		builder.append("]");
		return builder.toString();
	}

}
