package ar.com.pmp.subastados.web.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.com.pmp.subastados.domain.GeneralConfiguration;
import ar.com.pmp.subastados.domain.GeneralConfigurationKey;
import ar.com.pmp.subastados.service.GeneralConfigurationService;

@RestController
@RequestMapping("/api")
public class GeneralConfigurationResource {
	private final Logger log = LoggerFactory.getLogger(GeneralConfigurationResource.class);

	@Autowired
	GeneralConfigurationService generalConfigurationService;

	@Value("${global.home-carousel-folder}")
	private String carouselFolder;

	@Value("${global.condiciones-carousel-folder}")
	private String condicionesCarouselFolder;

	@Value("${global.home-carousel-file-extension}")
	private String[] carouselFileExtension;

	@GetMapping("/generalConfiguration")
	public GeneralConfiguration findByKey(@RequestParam("key") GeneralConfigurationKey key) throws Exception {
		return generalConfigurationService.findByKey(key);
	}

	@PutMapping("/generalConfiguration")
	public GeneralConfiguration updateValue(@RequestParam("key") GeneralConfigurationKey key, @RequestParam("value") String value) throws Exception {
		return generalConfigurationService.update(key, value);
	}

	@GetMapping("/carouselImages")
	public List<String> findImages() throws Exception {
		Iterator<File> iterateFiles = FileUtils.iterateFiles(new File(carouselFolder), carouselFileExtension, false);
		Stream<File> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterateFiles, Spliterator.ORDERED), false);
		List<String> list = stream.map(File::getName).sorted().collect(Collectors.toList());
		log.info("Imagenes de carousel principal: [{}]", list);
		return list;
	}

	@GetMapping("/carouselImagesCondiciones")
	public List<String> findCondicionesImages() throws Exception {
		Iterator<File> iterateFiles = FileUtils.iterateFiles(new File(condicionesCarouselFolder), carouselFileExtension, false);
		Stream<File> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterateFiles, Spliterator.ORDERED), false);
		List<String> list = stream.map(File::getName).sorted().collect(Collectors.toList());
		log.info("Imagenes de carousel condiciones: [{}]", list);
		return list;
	}

	@RequestMapping(path = "/carouselImage/catalogo", method = RequestMethod.GET)
	public ResponseEntity<Resource> downloadHomeImage(@RequestParam(name = "image") String image, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		log.debug("Request to download carousel image {}", image);
		File file = new File(carouselFolder + image);
		return getResource(file);
	}

	@RequestMapping(path = "/carouselImage/condiciones", method = RequestMethod.GET)
	public ResponseEntity<Resource> downloadCondicionesImage(@RequestParam(name = "image") String image, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		log.debug("Request to download carousel image {}", image);
		File file = new File(condicionesCarouselFolder + image);
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

		return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.IMAGE_JPEG).body(resource);
	}
}
