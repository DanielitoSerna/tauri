package co.com.udea.tauri.dtos;

public class MunicipioDto {
	
	private Integer id;
	
	private String municipio;
	
	private DepartamentoDto departamentoDto;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public DepartamentoDto getDepartamentoDto() {
		return departamentoDto;
	}

	public void setDepartamentoDto(DepartamentoDto departamentoDto) {
		this.departamentoDto = departamentoDto;
	}

}
