package edu.proygrado.dto;

import java.util.List;

public class WarningResultDTO {
	
	private List<WarningDTO> warnings;

	public WarningResultDTO() {
		super();
	}
	
	public WarningResultDTO(List<WarningDTO> warnings) {
		super();
		this.warnings = warnings;
	}
	
	public List<WarningDTO> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<WarningDTO> warnings) {
		this.warnings = warnings;
	}
}
