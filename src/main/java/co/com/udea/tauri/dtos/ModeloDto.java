package co.com.udea.tauri.dtos;

public class ModeloDto {
	
	private Double actualDMI;
	private Double totalDMFeed;
	private Double scurfRequirement;
	private Double urinaryRequirement;
	private Double metabolicFecalProteinReq;

	public Double getActualDMI() {
		return actualDMI;
	}

	public void setActualDMI(Double actualDMI) {
		this.actualDMI = actualDMI;
	}

	public Double getTotalDMFeed() {
		return totalDMFeed;
	}

	public void setTotalDMFeed(Double totalDMFeed) {
		this.totalDMFeed = totalDMFeed;
	}

	public Double getScurfRequirement() {
		return scurfRequirement;
	}

	public void setScurfRequirement(Double scurfRequirement) {
		this.scurfRequirement = scurfRequirement;
	}

	public Double getUrinaryRequirement() {
		return urinaryRequirement;
	}

	public void setUrinaryRequirement(Double urinaryRequirement) {
		this.urinaryRequirement = urinaryRequirement;
	}

	public Double getMetabolicFecalProteinReq() {
		return metabolicFecalProteinReq;
	}

	public void setMetabolicFecalProteinReq(Double metabolicFecalProteinReq) {
		this.metabolicFecalProteinReq = metabolicFecalProteinReq;
	}
	
}
