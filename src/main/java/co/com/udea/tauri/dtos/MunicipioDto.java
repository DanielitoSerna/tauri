package co.com.udea.tauri.dtos;

public class MunicipioDto {
	
	private Integer id;
	
	private String municipio;
	
	private DepartamentoDto departamentoDto;
	
	private Integer codigo;

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

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

}
