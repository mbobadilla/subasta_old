package ar.com.pmp.subastados.domain;

public enum MessageType {

	PHONE ("Whatsapp"),
	MAIL ("Mail");

	private String value;

	private MessageType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
