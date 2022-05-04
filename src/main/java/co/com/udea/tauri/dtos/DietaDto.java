package co.com.udea.tauri.dtos;

public class DietaDto {

	private Integer id;
	
	private Integer idBiblioteca;
	
	private Integer idEntrada;
	
	private Double cantidad;
	
	private Double cantidadOfrecido;
	
	private Double precio;
	
	private Double cmsActual;
	
	private Double cmsForraje;
	
	private Double cmsConcentrado;
	
	private Double totalPrecio;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdBiblioteca() {
		return idBiblioteca;
	}

	public void setIdBiblioteca(Integer idBiblioteca) {
		this.idBiblioteca = idBiblioteca;
	}

	public Integer getIdEntrada() {
		return idEntrada;
	}

	public void setIdEntrada(Integer idEntrada) {
		this.idEntrada = idEntrada;
	}

	public Double getCantidad() {
		return cantidad;
	}

	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}

	public Double getCantidadOfrecido() {
		return cantidadOfrecido;
	}

	public void setCantidadOfrecido(Double cantidadOfrecido) {
		this.cantidadOfrecido = cantidadOfrecido;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Double getCmsActual() {
		return cmsActual;
	}

	public void setCmsActual(Double cmsActual) {
		this.cmsActual = cmsActual;
	}

	public Double getCmsForraje() {
		return cmsForraje;
	}

	public void setCmsForraje(Double cmsForraje) {
		this.cmsForraje = cmsForraje;
	}

	public Double getCmsConcentrado() {
		return cmsConcentrado;
	}

	public void setCmsConcentrado(Double cmsConcentrado) {
		this.cmsConcentrado = cmsConcentrado;
	}

	public Double getTotalPrecio() {
		return totalPrecio;
	}

	public void setTotalPrecio(Double totalPrecio) {
		this.totalPrecio = totalPrecio;
	}

}
