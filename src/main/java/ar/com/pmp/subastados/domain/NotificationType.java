package ar.com.pmp.subastados.domain;

public enum NotificationType {

	GENERAL {
		@Override
		public String getMessage() {
			return "GENERAL";
		}
	},
	PHONE_VALIDATION {
		@Override
		public String getMessage() {
			return "Su telefono no se encuentra validado.";
		}
	},
	MAIL_VALIDATION {
		@Override
		public String getMessage() {
			return "Su e-mail no se encuentra validado.";
		}
	};

	public abstract String getMessage();
}
