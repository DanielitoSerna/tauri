package co.com.udea.tauri.dtos;

public class DietaDto {

	private Integer id;
	
	private Integer idBiblioteca;
	
	private Integer idEntrada;
	
	private Double cantidad;
	
	private Double cantidadOfrecido;
	
	private Double precio;

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
	
}
