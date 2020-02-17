package ar.com.pmp.subastados.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.pmp.subastados.domain.GeneralConfiguration;
import ar.com.pmp.subastados.domain.GeneralConfigurationKey;
import ar.com.pmp.subastados.repository.GeneralConfigurationRepository;

/**
 * Service class for managing Subscriber.
 */
@Service
@Transactional
public class GeneralConfigurationService {

	@Autowired
	private GeneralConfigurationRepository generalConfigurationRepository;

	@Transactional(readOnly = true)
	public GeneralConfiguration findByKey(GeneralConfigurationKey key) throws Exception {
		GeneralConfiguration configuration = generalConfigurationRepository.findOne(key.name());
		if (configuration == null)
			throw new Exception(key.name() + " does not have configuration record in database...");
		return configuration;
	}

	@Transactional
	public GeneralConfiguration update(GeneralConfigurationKey key, String value) throws Exception {
		if (StringUtils.isBlank(value))
			throw new Exception(key.name() + " does not allow empty values.");

		GeneralConfiguration configuration = generalConfigurationRepository.findOne(key.name());
		if (configuration == null)
			throw new Exception(key.name() + " does not have configuration record in database...");

		configuration.setValue(value);
		generalConfigurationRepository.save(configuration);

		return configuration;
	}

}
