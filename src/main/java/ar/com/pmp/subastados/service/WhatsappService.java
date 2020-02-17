package ar.com.pmp.subastados.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import ar.com.pmp.subastados.service.util.WhatsappResponse;

@Service
public class WhatsappService {

	@Value("${notifications.whatsapp.enabled}")
	private Boolean enabled;

	@Value("${notifications.whatsapp.waboxapp.send.text}")
	private String WABOXAPP_SEND_TEXT;

	@Value("${notifications.whatsapp.waboxapp.send.link}")
	private String WABOXAPP_SEND_LINK;

	@Value("${notifications.whatsapp.waboxapp.token}")
	public String WABOXAPP_TOKEN;

	@Value("${notifications.whatsapp.waboxapp.uid}")
	public String WABOXAPP_UID;

	@Autowired
	private MailService mailService;

	private final Logger log = LoggerFactory.getLogger(WhatsappService.class);

	private RestTemplate restTemplate = new RestTemplate();

	// Envia un whastsapp, al fallar envia un mail.
	public void sendMessage(String to, String text) {
		try {
			newWhatsapp(to, text);
		} catch (RestClientException e) {
			// enviar mail a admin con error
			log.error("Problemas al enviar whatsapp", e);
			mailService.sendMailToAdmin("Problemas al enviar whatsapp", e.getMessage());
		}
	}

	// Envia un whastsapp, retorna true si esta deshabilitado o se envio correctamente, false si fallo
	public boolean sendWhatsappMessage(String to, String text) {
		try {
			newWhatsapp(to, text);
			return true;
		} catch (RestClientException e) {
			log.error("Problemas al enviar whatsapp", e);
			return false;
		}
	}

	private synchronized void newWhatsapp(String to, String text) throws RestClientException {
		if (enabled) {
			log.info(String.format("Sending whatsapp to cell phone: '%s', message: '%s'", to, text));

			HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

			String customId = UUID.randomUUID().toString();

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(WABOXAPP_SEND_TEXT).queryParam("token", WABOXAPP_TOKEN).queryParam("uid", WABOXAPP_UID).queryParam("to", to).queryParam("custom_uid", customId).queryParam("text", text);
			HttpEntity<?> entity = new HttpEntity<>(headers);

			HttpEntity<WhatsappResponse> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, WhatsappResponse.class);
			WhatsappResponse whatsappResponse = response.getBody();

			log.info(whatsappResponse.toString());
		} else {
			log.info("Whatsapp service is disabled!!!!");
		}
	}

	// public synchronized void sendLink(String to, String url, Boolean caption) {
	// if (enabled) {
	// log.info(String.format("Sending whatsapp to cell phone: '%s', url: '%s'", to, url));
	//
	// HttpHeaders headers = new HttpHeaders();
	// headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
	//
	// String customId = UUID.randomUUID().toString();
	//
	// UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(WABOXAPP_SEND_LINK).queryParam("token",
	// WABOXAPP_TOKEN).queryParam("uid", WABOXAPP_UID).queryParam("to", to).queryParam("custom_uid", customId).queryParam("url",
	// url).queryParam("caption", caption);
	//
	// HttpEntity<?> entity = new HttpEntity<>(headers);
	// HttpEntity<WhatsappResponse> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity,
	// WhatsappResponse.class);
	//
	// WhatsappResponse whatsappResponse = response.getBody();
	//
	// log.info(whatsappResponse.toString());
	//
	// } else {
	// log.info("Whatsapp service is disabled!!!!");
	// }
	// }

}
