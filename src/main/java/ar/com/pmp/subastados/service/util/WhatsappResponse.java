package ar.com.pmp.subastados.service.util;

public class WhatsappResponse {
	boolean success;
	String custom_uid;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getCustom_uid() {
		return custom_uid;
	}

	public void setCustom_uid(String custom_uid) {
		this.custom_uid = custom_uid;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WhatsappResponse [success=").append(success).append(", custom_uid=").append(custom_uid).append("]");
		return builder.toString();
	}

}
