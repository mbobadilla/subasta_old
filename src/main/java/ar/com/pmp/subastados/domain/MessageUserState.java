package ar.com.pmp.subastados.domain;

public enum MessageUserState {

	WAITING("En cola"), // En espera de ser enviado
	SENDED("Enviado"), // Enviado
	SENDING_ERROR("Error al enviar"), // Error al enviar
	CONTACT_ERROR("Contacto omitido"); // Contacto sin validar o no quiere notificaciones

	private String value;

	private MessageUserState(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
