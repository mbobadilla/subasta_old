package ar.com.pmp.subastados.domain;

public enum ActivityEventType {

	// TODO Cambiar detalle a fields

	// Evento
	// Monto
	// Producto
	// Mensaje titulo

	// ActivityEventType type, String login, String eventName, Double price, String productName, String messageName, String extra

	BID("Oferta") {
		// - Ofertas

		// String template = "El usuario [%s] ha realizado una oferta por [%s], monto U$S %d, evento [%s].";

		@Override
		public ActivityEvent createActivityEvent(String login, String extra, Object data) {
			Bid bid = (Bid) data;

			// String detail = String.format(template, login, bid.getLote().getProduct().getNombre(), bid.getPrice().longValue(),
			// bid.getLote().getEvent().getName());

			return new ActivityEvent(this, login, bid.getLote().getEvent().getName(), bid.getPrice(), bid.getLote().getProduct().getNombre(), null, extra);
		}
	},
	PURCHASE("Compra") {
		// - Compras

		// String template = "El usuario [%s] ha ganado el producto [%s], monto U$S %d, evento [%s].";

		@Override
		public ActivityEvent createActivityEvent(String login, String extra, Object data) {
			Bid bid = (Bid) data;

			// String detail = String.format(template, login, bid.getLote().getProduct().getNombre(), bid.getPrice().longValue(),
			// bid.getLote().getEvent().getName());
			return new ActivityEvent(this, login, bid.getLote().getEvent().getName(), bid.getPrice(), bid.getLote().getProduct().getNombre(), null, extra);
		}
	},
	EVENT("Participacion") {
		// - Participante

		// String template = "El usuario [%s] se ha declarado participante del evento [%s].";

		@Override
		public ActivityEvent createActivityEvent(String login, String extra, Object data) {
			Event event = (Event) data;

			// String detail = String.format(template, login, event.getName());
			return new ActivityEvent(this, login, event.getName(), null, null, null, extra);
		}
	},
	PRODUCT_VIEW("Producto visto") {
		// - Productos vistos
		// User anonimo - extra = IP

		// String template = "El usuario [%s] ha visualizado el producto [%s] del evento [%s].";

		@Override
		public ActivityEvent createActivityEvent(String login, String extra, Object data) {
			Lote lote = (Lote) data;
			Product product = lote.getProduct();
			Event event = lote.getEvent();

			// String detail = String.format(template, login, product.getNombre(), event.getName());
			return new ActivityEvent(this, login, event.getName(), null, product.getNombre(), null, extra);
		}
	},
	MESSAGE("Mensaje recibido") {
		// - Mensajes
		// extra = Id del mensaje

		String template = "%s: %s";

		@Override
		public ActivityEvent createActivityEvent(String login, String extra, Object data) {
			Message message = (Message) data;
			MessageTemplate messageTemplate = message.getMessageTemplate();

			String detail = String.format(template, message.getType().name(), messageTemplate.getName());
			return new ActivityEvent(this, login, null, null, null, detail, extra);
		}
	},
	DELETE_BID("Oferta eliminada") {
		// - Ofertas

		// String template = "Se ha borrado la oferta del usuario [%s]. Oferta por [%s], monto U$S %d, evento [%s].";

		@Override
		public ActivityEvent createActivityEvent(String login, String extra, Object data) {
			Bid bid = (Bid) data;

			// String detail = String.format(template, login, bid.getLote().getProduct().getNombre(), bid.getPrice().longValue(),
			// bid.getLote().getEvent().getName());

			return new ActivityEvent(this, login, bid.getLote().getEvent().getName(), bid.getPrice(), bid.getLote().getProduct().getNombre(), null, extra);
		}
	};

	private String description;

	private ActivityEventType(String description) {
		this.description = description;
	}

	public abstract ActivityEvent createActivityEvent(String login, String extra, Object data);

	public String getDescription() {
		return description;
	}
}
