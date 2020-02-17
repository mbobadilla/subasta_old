package ar.com.pmp.subastados.service.dto;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import ar.com.pmp.subastados.domain.DestacadoCaballo;

public class DestacadoCaballoDTO {

	private Long id;

	@JsonProperty("$idcaballo")
	private String idcaballo;

	@JsonProperty("$orden")
	private Integer orden;

	@JsonProperty("$nombre")
	private String nombre;

	@JsonProperty("$destacado")
	private String destacado;

	@JsonProperty("$foto_lista")
	private String foto_lista;

	@JsonProperty("$servida_por")
	private String servida_por;

	@JsonProperty("$sexo")
	private String sexo;

	@JsonProperty("$fecha_nacimiento")
	private String fecha_nacimiento;

	@JsonProperty("$idpadre")
	private String idpadre;

	@JsonProperty("$padre")
	private String padre;

	@JsonProperty("$idmadre")
	private String idmadre;

	@JsonProperty("$madre")
	private String madre;

	@JsonProperty("$idpadremadre")
	private String idpadremadre;

	@JsonProperty("$padremadre")
	private String padremadre;

	@JsonProperty("$youtube")
	private String youtube;

	@JsonProperty("$aafe")
	private String aafe;

	public DestacadoCaballoDTO() {
		// Empty constructor needed for Jackson.
	}

	public DestacadoCaballoDTO(DestacadoCaballo source) {
		BeanUtils.copyProperties(source, this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdcaballo() {
		return idcaballo;
	}

	public void setIdcaballo(String idcaballo) {
		this.idcaballo = idcaballo;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getFoto_lista() {
		return foto_lista;
	}

	public void setFoto_lista(String foto_lista) {
		this.foto_lista = foto_lista;
	}

	public String getServida_por() {
		return servida_por;
	}

	public void setServida_por(String servida_por) {
		this.servida_por = servida_por;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getFecha_nacimiento() {
		return fecha_nacimiento;
	}

	public void setFecha_nacimiento(String fecha_nacimiento) {
		this.fecha_nacimiento = fecha_nacimiento;
	}

	public String getIdpadre() {
		return idpadre;
	}

	public void setIdpadre(String idpadre) {
		this.idpadre = idpadre;
	}

	public String getPadre() {
		return padre;
	}

	public void setPadre(String padre) {
		this.padre = padre;
	}

	public String getIdmadre() {
		return idmadre;
	}

	public void setIdmadre(String idmadre) {
		this.idmadre = idmadre;
	}

	public String getMadre() {
		return madre;
	}

	public void setMadre(String madre) {
		this.madre = madre;
	}

	public String getIdpadremadre() {
		return idpadremadre;
	}

	public void setIdpadremadre(String idpadremadre) {
		this.idpadremadre = idpadremadre;
	}

	public String getPadremadre() {
		return padremadre;
	}

	public void setPadremadre(String padremadre) {
		this.padremadre = padremadre;
	}

	public String getYoutube() {
		return youtube;
	}

	public void setYoutube(String youtube) {
		this.youtube = youtube;
	}

	public String getAafe() {
		return aafe;
	}

	public void setAafe(String aafe) {
		this.aafe = aafe;
	}

	public String getDestacado() {
		return destacado;
	}

	public void setDestacado(String destacado) {
		this.destacado = destacado;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DestacadoCaballoDTO [id=");
		builder.append(id);
		builder.append(", idcaballo=");
		builder.append(idcaballo);
		builder.append(", orden=");
		builder.append(orden);
		builder.append(", nombre=");
		builder.append(nombre);
		builder.append(", destacado=");
		builder.append(destacado);
		builder.append(", foto_lista=");
		builder.append(foto_lista);
		builder.append(", servida_por=");
		builder.append(servida_por);
		builder.append(", sexo=");
		builder.append(sexo);
		builder.append(", fecha_nacimiento=");
		builder.append(fecha_nacimiento);
		builder.append(", idpadre=");
		builder.append(idpadre);
		builder.append(", padre=");
		builder.append(padre);
		builder.append(", idmadre=");
		builder.append(idmadre);
		builder.append(", madre=");
		builder.append(madre);
		builder.append(", idpadremadre=");
		builder.append(idpadremadre);
		builder.append(", padremadre=");
		builder.append(padremadre);
		builder.append(", youtube=");
		builder.append(youtube);
		builder.append(", aafe=");
		builder.append(aafe);
		builder.append("]");
		return builder.toString();
	}

}
