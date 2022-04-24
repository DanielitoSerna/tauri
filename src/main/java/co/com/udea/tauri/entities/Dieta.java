package co.com.udea.tauri.entities;

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
@Table(name = "dieta", schema = "taurischema")
public class Dieta {
	
	@Id
	@SequenceGenerator(name="dieta_id_seq",sequenceName="taurischema.dieta_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dieta_id_seq")
	@Column(name = "id", updatable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "id_biblioteca", referencedColumnName = "id", nullable = false)
	private Biblioteca idBiblioteca;
	
	@ManyToOne
	@JoinColumn(name = "id_entrada", referencedColumnName = "id", nullable = false)
	private Entrada idEntrada;
	
	@Column(name = "cantidad")
	private Double cantidad;
	
	@Column(name = "cantidad_ofrecido")
	private Double cantidadOfrecido;
	
	@Column(name = "precio")
	private Double precio;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Biblioteca getIdBiblioteca() {
		return idBiblioteca;
	}

	public void setIdBiblioteca(Biblioteca idBiblioteca) {
		this.idBiblioteca = idBiblioteca;
	}

	public Entrada getIdEntrada() {
		return idEntrada;
	}

	public void setIdEntrada(Entrada idEntrada) {
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
