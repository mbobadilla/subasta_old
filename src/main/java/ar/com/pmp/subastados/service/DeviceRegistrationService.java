package ar.com.pmp.subastados.service;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.pmp.subastados.domain.DeviceRegistration;
import ar.com.pmp.subastados.domain.User;
import ar.com.pmp.subastados.repository.DeviceRegistrationRepository;
import ar.com.pmp.subastados.repository.UserRepository;
import ar.com.pmp.subastados.security.SecurityUtils;
import ar.com.pmp.subastados.service.dto.DeviceRegistrationDTO;
import ar.com.pmp.subastados.web.rest.errors.EntityNotFoundException;

@Service
@Transactional
public class DeviceRegistrationService {

	private final Logger log = LoggerFactory.getLogger(DeviceRegistrationService.class);

	@Autowired
	private DeviceRegistrationRepository deviceRegistrationRepository;
	@Autowired
	private UserRepository userRepository;

	@Transactional
	public void create(DeviceRegistrationDTO dto) {
		log.info("new device registration: [{}]", dto);

		Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
		Optional<User> optional = userRepository.findOneByLogin(userLogin.get());
		User user = optional.orElseThrow(() -> new EntityNotFoundException("User not found"));

		DeviceRegistration dr = deviceRegistrationRepository.findOneByRegistrationId(dto.getRegistrationId());
		if (dr != null) {
			// El dispositivo está registrado.
			if (!dr.getUser().equals(user)) {
				// se logeo otra persona a la persona. Quito el device anterior y se lo asigno al nuevo usuario

				deviceRegistrationRepository.delete(dr);
				addDeviceRegistrationToUser(dto, user);
			}

		} else {
			// La persona se registra en nuevo dispositivo
			addDeviceRegistrationToUser(dto, user);
		}

	}

	@Transactional
	private void addDeviceRegistrationToUser(DeviceRegistrationDTO dto, User user) {
		DeviceRegistration dr;
		dr = new DeviceRegistration();
		dr.setUser(user);
		dr.setModel(dto.getModel());
		dr.setPlatform(dto.getPlatform());
		dr.setRegistrationId(dto.getRegistrationId());
		dr.setVersion(dto.getVersion());
		deviceRegistrationRepository.save(dr);
	}

	@Transactional
	public Set<DeviceRegistration> findByUserLogin(String userLogin) {
		Optional<User> optional = userRepository.findOneByLogin(userLogin);
		User user = optional.orElseThrow(() -> new EntityNotFoundException("User not found"));
		return user.getDeviceRegistrations();
	}

	@Transactional
	public Set<DeviceRegistration> get(String userLogin) {
		Optional<User> optional = userRepository.findOneByLogin(userLogin);
		User user = optional.orElseThrow(() -> new EntityNotFoundException("User not found"));
		return user.getDeviceRegistrations();
	}

	@Transactional
	public void removeDevices(Set<DeviceRegistration> devicesToRemove) {
		for (DeviceRegistration deviceRegistration : devicesToRemove) {
			deviceRegistrationRepository.delete(deviceRegistration);
		}
	}
}
