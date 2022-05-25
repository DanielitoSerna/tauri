package co.com.udea.tauri.dtos;

public class DepartamentoDto {
	
	private Integer id;
	
	private String nombreDepartamento;
	
	private PaisDto pais;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombreDepartamento() {
		return nombreDepartamento;
	}

	public void setNombreDepartamento(String nombreDepartamento) {
		this.nombreDepartamento = nombreDepartamento;
	}

	public PaisDto getPais() {
		return pais;
	}

	public void setPais(PaisDto paisDto) {
		this.pais = paisDto;
	}
}
