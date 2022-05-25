package co.com.udea.tauri.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "entrada", schema = "taurischema")
public class Entrada {
	
	@Id
	@SequenceGenerator(name="entrada_id_seq",sequenceName="taurischema.entrada_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entrada_id_seq")
	@Column(name = "id", updatable=false)
	private Integer id;
	
	@Column(name = "nombre_reporte")
	private String nombreReporte;
	
	@ManyToOne
	@JoinColumn(name = "departamento", referencedColumnName = "codigo", nullable = false)
	private Departamento departamento;
	
	@ManyToOne
	@JoinColumn(name = "municipio", referencedColumnName = "id", nullable = false)
	private Municipio municipio;
	
	@Column(name = "raza")
	private String raza;
	
	@Column(name = "tipo_animal")
	private String tipoAnimal;
	
	@Column(name = "peso_corporal")
	private Double pesoCorporal;
	
	@Column(name = "condicion_corporal")
	private Double condicionCorporal;
	
	@Column(name = "dias_leche")
	private Double diasLeche;
	
	@Column(name = "dias_prenez")
	private Double diasPrenez;
	
	@Column(name = "numero_partos")
	private Integer numeroParto;
	
	@Column(name = "intervalo_partos")
	private Integer intervaloParto;
	
	@Column(name = "produccion_leche")
	private Double produccionLeche;
	
	@Column(name = "grasa")
	private Double grasa;
	
	@Column(name = "proteina_cruda")
	private Double proteinaCruda;
	
	@Column(name = "lactosa")
	private Double lactosa;
	
	@Column(name = "precio_venta")
	private Double precioVenta;
	
	@Column(name = "manejo")
	private String manejo;
	
	@Column(name = "distancia")
	private Double distancia;
	
	@Column(name = "numero_viajes")
	private Double numeroViajes;
	
	@Column(name = "tipografia")
	private String tipografia;
	
	@Column(name = "usuario")
	private String usuario;
	
	@Column(name = "fecha_creacion")
	private Date fechaCreacion;
	
	@Column(name = "ganancia_peso")
	private Double gananciaPeso;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombreReporte() {
		return nombreReporte;
	}

	public void setNombreReporte(String nombreReporte) {
		this.nombreReporte = nombreReporte;
	}

	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public String getRaza() {
		return raza;
	}

	public void setRaza(String raza) {
		this.raza = raza;
	}

	public String getTipoAnimal() {
		return tipoAnimal;
	}

	public void setTipoAnimal(String tipoAnimal) {
		this.tipoAnimal = tipoAnimal;
	}

	public Double getPesoCorporal() {
		return pesoCorporal;
	}

	public void setPesoCorporal(Double pesoCorporal) {
		this.pesoCorporal = pesoCorporal;
	}

	public Double getCondicionCorporal() {
		return condicionCorporal;
	}

	public void setCondicionCorporal(Double condicionCorporal) {
		this.condicionCorporal = condicionCorporal;
	}

	public Double getDiasLeche() {
		return diasLeche;
	}

	public void setDiasLeche(Double diasLeche) {
		this.diasLeche = diasLeche;
	}

	public Double getDiasPrenez() {
		return diasPrenez;
	}

	public void setDiasPrenez(Double diasPrenez) {
		this.diasPrenez = diasPrenez;
	}

	public Integer getNumeroParto() {
		return numeroParto;
	}

	public void setNumeroParto(Integer numeroParto) {
		this.numeroParto = numeroParto;
	}

	public Integer getIntervaloParto() {
		return intervaloParto;
	}

	public void setIntervaloParto(Integer intervaloParto) {
		this.intervaloParto = intervaloParto;
	}

	public Double getProduccionLeche() {
		return produccionLeche;
	}

	public void setProduccionLeche(Double produccionLeche) {
		this.produccionLeche = produccionLeche;
	}

	public Double getGrasa() {
		return grasa;
	}

	public void setGrasa(Double grasa) {
		this.grasa = grasa;
	}

	public Double getProteinaCruda() {
		return proteinaCruda;
	}

	public void setProteinaCruda(Double proteinaCruda) {
		this.proteinaCruda = proteinaCruda;
	}

	public Double getLactosa() {
		return lactosa;
	}

	public void setLactosa(Double lactosa) {
		this.lactosa = lactosa;
	}

	public Double getPrecioVenta() {
		return precioVenta;
	}

	public void setPrecioVenta(Double precioVenta) {
		this.precioVenta = precioVenta;
	}

	public String getManejo() {
		return manejo;
	}

	public void setManejo(String manejo) {
		this.manejo = manejo;
	}

	public Double getDistancia() {
		return distancia;
	}

	public void setDistancia(Double distancia) {
		this.distancia = distancia;
	}

	public Double getNumeroViajes() {
		return numeroViajes;
	}

	public void setNumeroViajes(Double numeroViajes) {
		this.numeroViajes = numeroViajes;
	}

	public String getTipografia() {
		return tipografia;
	}

	public void setTipografia(String tipografia) {
		this.tipografia = tipografia;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Double getGananciaPeso() {
		return gananciaPeso;
	}

	public void setGananciaPeso(Double gananciaPeso) {
		this.gananciaPeso = gananciaPeso;
	}

}
