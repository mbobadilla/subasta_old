package ar.com.pmp.subastados.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.pmp.subastados.domain.DestacadoCaballo;
import ar.com.pmp.subastados.repository.DestacadoCaballoRepository;
import ar.com.pmp.subastados.service.dto.DestacadoCaballoDTO;
import ar.com.pmp.subastados.service.mapper.DestacadoCaballoMapper;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class DestacadoCaballoService {

	@Autowired
	private DestacadoCaballoRepository destacadoCaballoRepository;
	@Autowired
	private DestacadoCaballoMapper destacadoCaballoMapper;

	public DestacadoCaballo persistDestacadoCaballo(DestacadoCaballoDTO dto) {
		DestacadoCaballo destacadoCaballo = destacadoCaballoMapper.DTOtoPersistence(dto);
		destacadoCaballoRepository.save(destacadoCaballo);
		return destacadoCaballo;
	}

	@Transactional(readOnly = true)
	public List<DestacadoCaballoDTO> getAll() {
		return destacadoCaballoRepository.findAll().stream().map(DestacadoCaballoDTO::new).collect(Collectors.toList());
	}

	/**
	 * Automaticamente se sincroniza a las 00:00 cada dia
	 */
	// @Scheduled(cron = "0 0 0 * * ?")
	// public void sincronizar() {
	// try {
	// ObjectMapper mapper = new ObjectMapper();
	// String content = IOUtils.toString(new URL(URL), Charset.forName("iso-8859-1")).replaceAll("\\n", "")
	// .replaceAll("\\r", "").replaceAll("\\t", "");
	//
	// JSONObject destacados = new JSONObject(content);
	//
	// System.out.println(destacados.toString());
	//
	// JSONArray array = destacados.getJSONArray("$caballos");
	// for (int i = 0; i < array.length(); i++) {
	// JSONObject object = array.getJSONObject(i);
	//
	// DestacadoCaballoDTO dto = mapper.readValue(object.toString(), DestacadoCaballoDTO.class);
	// DestacadoCaballo destacadoCaballo = destacadoCaballoRepository.findByIdcaballo(dto.getIdcaballo());
	// if (destacadoCaballo == null) {
	// dto.setId(null);
	// persistDestacadoCaballo(dto);
	// } else {
	// dto.setId(destacadoCaballo.getId());
	// persistDestacadoCaballo(dto);
	// }
	// }
	// } catch (IOException | JSONException e) {
	// e.printStackTrace();
	// }
	// }

}
