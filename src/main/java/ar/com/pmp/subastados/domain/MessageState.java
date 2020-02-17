package ar.com.pmp.subastados.domain;

public enum MessageState {

	SENDING("Enviando"), FINISHED("Finalizado"), FINISHED_ERROR("Finalizado con error");

	private String value;

	private MessageState(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
