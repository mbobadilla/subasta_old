package ar.com.pmp.subastados.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "product")
public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "id_caballo")
	private String idcaballo;

	@Column(name = "orden")
	private Integer orden;

	@Column(name = "nombre")
	private String nombre;

	@Column(name = "destacado")
	private String destacado;

	@Column(name = "foto_lista")
	private String foto_lista;

	@Column(name = "servida_por")
	private String servida_por;

	@Column(name = "sexo")
	private String sexo;

	@Column(name = "fecha_nacimiento")
	private String fecha_nacimiento;

	@Column(name = "id_padre")
	private String idpadre;

	@Column(name = "padre")
	private String padre;

	@Column(name = "id_madre")
	private String idmadre;

	@Column(name = "madre")
	private String madre;

	@Column(name = "id_padre_madre")
	private String idpadremadre;

	@Column(name = "padre_madre")
	private String padremadre;

	@Column(name = "youtube")
	private String youtube;

	@Column(name = "aafe")
	private String aafe;

	@Column(name = "lote_nro")
	private String loteNro;
	
	@Column(name = "microchip")
	private String microchip;

	@Column(name = "alzada")
	private String alzada;

	@Column(name = "comentario_breve")
	private String comentarioBreve;

	@Column(name = "comentarios")
	private String comentarios;

	@Column(name = "pelo")
	private String pelo;

	@Column(name = "studbook")
	private String studbook;

	@Column(name = "anio_nacimiento")
	private String nacimiento;

	@Column(name = "idservida_por")
	private String idservida_por;

	@Column(name = "fecha_ultimo_servicio")
	private String fecha_ultimo_servicio;

	public String getDestacado() {
		return destacado;
	}

	public void setDestacado(String destacado) {
		this.destacado = destacado;
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

	public String getLoteNro() {
		return loteNro;
	}

	public void setLoteNro(String loteNro) {
		this.loteNro = loteNro;
	}
	
	public String getMicrochip() {
		return microchip;
	}

	public void setMicrochip(String microchip) {
		this.microchip = microchip;
	}

	public String getAlzada() {
		return alzada;
	}

	public void setAlzada(String alzada) {
		this.alzada = alzada;
	}

	public String getComentarioBreve() {
		return comentarioBreve;
	}

	public void setComentarioBreve(String comentarioBreve) {
		this.comentarioBreve = comentarioBreve;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	public String getPelo() {
		return pelo;
	}

	public void setPelo(String pelo) {
		this.pelo = pelo;
	}

	public String getStudbook() {
		return studbook;
	}

	public void setStudbook(String studbook) {
		this.studbook = studbook;
	}

	public String getNacimiento() {
		return nacimiento;
	}

	public void setNacimiento(String nacimiento) {
		this.nacimiento = nacimiento;
	}

	public String getIdservida_por() {
		return idservida_por;
	}

	public void setIdservida_por(String idservida_por) {
		this.idservida_por = idservida_por;
	}

	public String getFecha_ultimo_servicio() {
		return fecha_ultimo_servicio;
	}

	public void setFecha_ultimo_servicio(String fecha_ultimo_servicio) {
		this.fecha_ultimo_servicio = fecha_ultimo_servicio;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Product) {
			final Product other = (Product) obj;
			return new EqualsBuilder().append(id, other.id).append(idcaballo, other.idcaballo).isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(31, 37).append(id).append(idcaballo).toHashCode();
	}
}
