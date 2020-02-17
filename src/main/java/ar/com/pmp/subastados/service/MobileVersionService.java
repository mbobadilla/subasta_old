package ar.com.pmp.subastados.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.pmp.subastados.domain.MobileVersion;
import ar.com.pmp.subastados.repository.MobileVersionRepository;
import ar.com.pmp.subastados.web.rest.errors.EntityNotFoundException;

@Service
@Transactional
public class MobileVersionService {

	private final Logger log = LoggerFactory.getLogger(MobileVersionService.class);

	@Autowired
	private MobileVersionRepository mobileVersionRepository;

	@Transactional
	public void create(String platform, String version) {
		log.info("new MobileVersion: {} - {}", platform, version);
		Optional<MobileVersion> optional = mobileVersionRepository.findOneByPlatformAndVersion(platform, version);
		if (!optional.isPresent()) {
			MobileVersion mobileVersion = new MobileVersion(platform, version);
			MobileVersion created = mobileVersionRepository.save(mobileVersion);
			log.info("New mobile version was created: {}", created);
		}
	}

	@Transactional(readOnly = true)
	public MobileVersion findOneByPlatformAndVersion(String platform, String version) throws EntityNotFoundException {
		log.info("Buscando mobile version {} - {}", platform, version);
		return mobileVersionRepository.findOneByPlatformAndVersion(platform, version).orElseThrow(() -> new EntityNotFoundException("Version mobile no enctontrada"));
	}

	@Transactional
	public void deprecate(String platform, String version) {
		log.info("Buscando mobile version {} - {}", platform, version);
		MobileVersion mobileVersion = mobileVersionRepository.findOneByPlatformAndVersion(platform, version).orElseThrow(() -> new EntityNotFoundException("Version mobile no enctontrada"));
		mobileVersion.setActive(false);
		mobileVersionRepository.save(mobileVersion);
	}

}
