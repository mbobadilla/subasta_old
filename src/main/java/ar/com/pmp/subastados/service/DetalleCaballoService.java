package ar.com.pmp.subastados.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class DetalleCaballoService {


	// @Autowired
	// private DestacadoCaballoRepository destacadoCaballoRepository;
	// @Autowired
	// private DestacadoCaballoMapper destacadoCaballoMapper;

	// public DestacadoCaballo persistDestacadoCaballo(DestacadoCaballoDTO dto) {
	// DestacadoCaballo destacadoCaballo = destacadoCaballoMapper.DTOtoPersistence(dto);
	// destacadoCaballoRepository.save(destacadoCaballo);
	// return destacadoCaballo;
	// }

	// @Transactional(readOnly = true)
	// public List<DestacadoCaballoDTO> getAll() {
	// return destacadoCaballoRepository.findAll().stream().map(DestacadoCaballoDTO::new).collect(Collectors.toList());
	// }


	// public static void main(String[] args) {
	// try {
	// ObjectMapper mapper = new ObjectMapper();
	//
	// String url2 = URL.replace("{ID}", "1680856");
	// String content = IOUtils.toString(new URL(url2), Charset.forName("iso-8859-1")).replaceAll("\\n", "").replaceAll("\\r",
	// "").replaceAll("\\t", "");
	//
	// JSONObject detalle = new JSONObject(content);
	//
	// System.out.println(detalle.toString());
	//
	// JSONArray array = detalle.getJSONArray("$caballo.fotos");
	// for (int i = 0; i < array.length(); i++) {
	// System.out.println(array.getString(i));
	//
	// }
	// } catch (IOException | JSONException e) {
	// e.printStackTrace();
	// }
	// }
}
