package co.com.udea.tauri.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "biblioteca", schema = "taurischema")
public class Biblioteca {
	
	@Id
	@SequenceGenerator(name="biblioteca_id_seq",sequenceName="taurischema.biblioteca_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "biblioteca_id_seq")
	@Column(name = "id", updatable=false)
	private Integer id;
	
	@Column(name = "nombre")
	private String nombre;
	
	@Column(name = "categoria")
	private String categoria;
	
	@Column(name = "tipo")
	private String tipo;
	
	@Column(name = "ms")
	private Double ms;
	
	@Column(name = "ed")
	private Double ed;
	
	@Column(name = "fda")
	private Double fda;
	
	@Column(name = "fdn")
	private Double fdn;
	
	@Column(name = "pb")
	private Double pb;
	
	@Column(name = "almidon")
	private Double almidon;
	
	@Column(name = "fraccion_a")
	private Double fraccionA;
	
	@Column(name = "fraccion_b")
	private Double fraccionB;

	@Column(name = "fraccion_c")
	private Double fraccionC;
	
	@Column(name = "digestabilidad_pndr")
	private Double digestibilidadPndr;
	
	@Column(name = "kd_fraccion_b")
	private Double kdFraccionB;
	
	@Column(name = "pndr")
	private Double pndr;
	
	@Column(name = "grasa_cruda")
	private Double grasaCruda;
	
	@Column(name = "cenizas")
	private Double ceniza;
	
	@Column(name = "porcentaje_ca")
	private Double porcentajeCa;
	
	@Column(name = "porcentaje_p")
	private Double porcentajeP;
	
	@Column(name = "porcentaje_na")
	private Double porcentajeNa;
	
	@Column(name = "porcentaje_cl")
	private Double porcentajeCl;
	
	@Column(name = "porcentaje_k")
	private Double porcentajeK;
	
	@Column(name = "porcentaje_mg")
	private Double porcentajeMg;
	
	@Column(name = "porcentaje_s")
	private Double porcentajeS;
	
	@Column(name = "coeficiente_absorcion_ca")
	private Double coeficienteAbsorcionCa;
	
	@Column(name = "coeficiente_absorcion_p")
	private Double coeficienteAbsorcionP;
	
	@Column(name = "coeficiente_absorcion_na")
	private Double coeficienteAbsorcionNa;
	
	@Column(name = "coeficiente_absorcion_cl")
	private Double coeficienteAbsorcionCl;
	
	@Column(name = "coeficiente_absorcion_k")
	private Double coeficienteAbsorcionK;
	
	@Column(name = "coeficiente_absorcion_mg")
	private Double coeficienteAbsorcionMg;
	
	@Column(name = "usuario")
	private String usuario;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Double getMs() {
		return ms;
	}

	public void setMs(Double ms) {
		this.ms = ms;
	}

	public Double getEd() {
		return ed;
	}

	public void setEd(Double ed) {
		this.ed = ed;
	}

	public Double getFda() {
		return fda;
	}

	public void setFda(Double fda) {
		this.fda = fda;
	}

	public Double getFdn() {
		return fdn;
	}

	public void setFdn(Double fdn) {
		this.fdn = fdn;
	}

	public Double getAlmidon() {
		return almidon;
	}

	public void setAlmidon(Double almidon) {
		this.almidon = almidon;
	}

	public Double getFraccionA() {
		return fraccionA;
	}

	public void setFraccionA(Double fraccionA) {
		this.fraccionA = fraccionA;
	}

	public Double getFraccionB() {
		return fraccionB;
	}

	public void setFraccionB(Double fraccionB) {
		this.fraccionB = fraccionB;
	}

	public Double getFraccionC() {
		return fraccionC;
	}

	public void setFraccionC(Double fraccionC) {
		this.fraccionC = fraccionC;
	}

	public Double getDigestibilidadPndr() {
		return digestibilidadPndr;
	}

	public void setDigestibilidadPndr(Double digestibilidadPndr) {
		this.digestibilidadPndr = digestibilidadPndr;
	}

	public Double getKdFraccionB() {
		return kdFraccionB;
	}

	public void setKdFraccionB(Double kdFraccionB) {
		this.kdFraccionB = kdFraccionB;
	}

	public Double getPndr() {
		return pndr;
	}

	public void setPndr(Double pndr) {
		this.pndr = pndr;
	}

	public Double getGrasaCruda() {
		return grasaCruda;
	}

	public void setGrasaCruda(Double grasaCruda) {
		this.grasaCruda = grasaCruda;
	}

	public Double getCeniza() {
		return ceniza;
	}

	public void setCeniza(Double ceniza) {
		this.ceniza = ceniza;
	}

	public Double getPorcentajeCa() {
		return porcentajeCa;
	}

	public void setPorcentajeCa(Double porcentajeCa) {
		this.porcentajeCa = porcentajeCa;
	}

	public Double getPorcentajeP() {
		return porcentajeP;
	}

	public void setPorcentajeP(Double porcentajeP) {
		this.porcentajeP = porcentajeP;
	}

	public Double getPorcentajeNa() {
		return porcentajeNa;
	}

	public void setPorcentajeNa(Double porcentajeNa) {
		this.porcentajeNa = porcentajeNa;
	}

	public Double getPorcentajeCl() {
		return porcentajeCl;
	}

	public void setPorcentajeCl(Double porcentajeCl) {
		this.porcentajeCl = porcentajeCl;
	}

	public Double getPorcentajeK() {
		return porcentajeK;
	}

	public void setPorcentajeK(Double porcentajeK) {
		this.porcentajeK = porcentajeK;
	}

	public Double getPorcentajeMg() {
		return porcentajeMg;
	}

	public void setPorcentajeMg(Double porcentajeMg) {
		this.porcentajeMg = porcentajeMg;
	}

	public Double getPorcentajeS() {
		return porcentajeS;
	}

	public void setPorcentajeS(Double porcentajeS) {
		this.porcentajeS = porcentajeS;
	}

	public Double getCoeficienteAbsorcionCa() {
		return coeficienteAbsorcionCa;
	}

	public void setCoeficienteAbsorcionCa(Double coeficienteAbsorcionCa) {
		this.coeficienteAbsorcionCa = coeficienteAbsorcionCa;
	}

	public Double getCoeficienteAbsorcionP() {
		return coeficienteAbsorcionP;
	}

	public void setCoeficienteAbsorcionP(Double coeficienteAbsorcionP) {
		this.coeficienteAbsorcionP = coeficienteAbsorcionP;
	}

	public Double getCoeficienteAbsorcionNa() {
		return coeficienteAbsorcionNa;
	}

	public void setCoeficienteAbsorcionNa(Double coeficienteAbsorcionNa) {
		this.coeficienteAbsorcionNa = coeficienteAbsorcionNa;
	}

	public Double getCoeficienteAbsorcionCl() {
		return coeficienteAbsorcionCl;
	}

	public void setCoeficienteAbsorcionCl(Double coeficienteAbsorcionCl) {
		this.coeficienteAbsorcionCl = coeficienteAbsorcionCl;
	}

	public Double getCoeficienteAbsorcionK() {
		return coeficienteAbsorcionK;
	}

	public void setCoeficienteAbsorcionK(Double coeficienteAbsorcionK) {
		this.coeficienteAbsorcionK = coeficienteAbsorcionK;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Double getPb() {
		return pb;
	}

	public void setPb(Double pb) {
		this.pb = pb;
	}

	public Double getCoeficienteAbsorcionMg() {
		return coeficienteAbsorcionMg;
	}

	public void setCoeficienteAbsorcionMg(Double coeficienteAbsorcionMg) {
		this.coeficienteAbsorcionMg = coeficienteAbsorcionMg;
	}
	
}
