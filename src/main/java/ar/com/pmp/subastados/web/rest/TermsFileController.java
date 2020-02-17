package ar.com.pmp.subastados.web.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TermsFileController {

	private final Logger log = LoggerFactory.getLogger(TermsFileController.class);

	@Value("${global.pdf-event-folder}")
	private String pdfEventFolder;

	@Autowired
	private ResourceLoader resourceLoader;

	@GetMapping("/downloadTermsFile")
	public ResponseEntity<Resource> downloadTermsFile(@RequestParam(name = "eventName", required = false) String eventName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.debug("Request to downloadTermsFile {}", eventName);

		File file;
		if (StringUtils.isBlank(eventName)) {
			file = new File(pdfEventFolder + "\\activo\\condiciones.pdf");
		} else {
			file = new File(pdfEventFolder + eventName + "\\condiciones.pdf");
		}

		return getResource(file);
	}

	@GetMapping("/downloadCatalogoFile")
	public ResponseEntity<Resource> downloadCatalogoFile(@RequestParam(name = "eventName", required = false) String eventName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.debug("Request to downloadTermsFile {}", eventName);

		File file;
		if (StringUtils.isBlank(eventName)) {
			file = new File(pdfEventFolder + "\\activo\\catalogo.pdf");
		} else {
			file = new File(pdfEventFolder + eventName + "\\catalogo.pdf");
		}

		return getResource(file);
	}

	private ResponseEntity<Resource> getResource(final File file) throws IOException {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "filename=" + file.getName());
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");

		Path path = Paths.get(file.getPath());
		ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

		return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.parseMediaType("application/pdf")).body(resource);
	}
	
	
	@RequestMapping(path = "/imageFile", method = RequestMethod.GET)
	public ResponseEntity<Resource> downloadEventImage(@RequestParam(name = "eventName", required = false) String eventName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		log.debug("Request to download Event image {}", eventName);
		
		File file = new File(pdfEventFolder + eventName + "\\imagen.JPG");

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "filename=" + file.getName());
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");

		Path path = Paths.get(file.getPath());
		ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

		return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.IMAGE_JPEG).body(resource);
	}
}
