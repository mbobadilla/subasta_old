package ar.com.pmp.subastados.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.exceptions.InvalidSSLConfig;

import ar.com.pmp.subastados.domain.DeviceRegistration;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.service.gcm.Message;
import ar.com.pmp.subastados.service.gcm.Result;
import ar.com.pmp.subastados.service.gcm.Sender;

@Service
@Transactional
public class PushNotificationService {

	private final Logger log = LoggerFactory.getLogger(PushNotificationService.class);

	@Autowired
	private DeviceRegistrationService deviceRegistrationService;

	/**
	 * API KEY Google - Android
	 */
	protected static final Sender gcmSender = new Sender("AIzaSyDjAJm41NAgQNxxEXa5VKAl9GhVNBAaHVE");

	/**
	 * Certificado Push Desarrollo - iOS
	 */
	protected static ApnsService iosDevPushService;

	/**
	 * Certificado Push Producción - iOS
	 */
	protected static ApnsService iosProdPushService;

	public PushNotificationService() throws InvalidSSLConfig, IOException {
		iosDevPushService = APNS.newService()
				.withCert(new ClassPathResource("/certs/push/subastados-noti-ios-development.p12").getInputStream(),
						"edumobile546")
				.withSandboxDestination().build();

		iosProdPushService = APNS.newService()
				.withCert(this.getClass().getClassLoader().getResourceAsStream("/certs/push/subastados-noti-prod.p12"),
						"edumobile546")
				.withProductionDestination().build();

	}

	@Transactional
	public void notificateMessageToUsers(List<User> users, String mensaje) {

		Message message = new Message.Builder().addData("message", mensaje)
				.addData("notificationType", " Tipo de notificacion ")
				.addData("title", "Solaguayre - ".concat("Tipo de Notificacion")).build();

		Set<DeviceRegistration> devicesToRemove = new HashSet<DeviceRegistration>();
		for (User user : users) {

			try {

				Set<DeviceRegistration> registrations = user.getDeviceRegistrations();
				for (DeviceRegistration dr : registrations) {
					if (dr.getPlatform() != null) {
						if (dr.getPlatform().equals("Android")) {
							Result res = gcmSender.send(message, dr.getRegistrationId(), 2);
							if (res.getErrorCodeName() != null) {
								devicesToRemove.add(dr);
							}

						} else if (dr.getPlatform().equals("iOS")) {

							String payload = APNS.newPayload().alertBody(mensaje).build();
							getAPNS().push(dr.getRegistrationId(), payload);

							Map<String, Date> inactiveDevices = iosDevPushService.getInactiveDevices();
							for (String deviceToken : inactiveDevices.keySet()) {
								Date inactiveAsOf = inactiveDevices.get(deviceToken);
								System.out.println("Inactive device token :" + deviceToken);
								System.out.println("Tried to send notification at : " + inactiveAsOf);
							}

						}
					}

					if (!devicesToRemove.isEmpty()) {
						deviceRegistrationService.removeDevices(devicesToRemove);
					}
				}

			} catch (IOException e) {
				log.error("Error al enviar notificacion push", e);
			}

		}
	}

	public ApnsService getAPNS() {
		Boolean devMode = Boolean.TRUE;
		if (devMode) {
			return iosDevPushService;
		}
		return iosProdPushService;
	}

}
