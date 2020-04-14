package edu.proygrado.dto;

public class CompartirArchivoInputDTO {

	
	String tipoDestinatario;
	Long[] gruposDestinatarios;
	Long[] usuariosDestinatarios;
	ArchivoDTO archivo;
	Long cursoDestinatario;
	
	public CompartirArchivoInputDTO() {
		super();
	}
	
	public CompartirArchivoInputDTO(String tipoDestinatario, Long[] gruposDestinatarios, Long[] usuariosDestinatarios, ArchivoDTO archivo,
			Long cursoDestinatario) {
		super();
		this.tipoDestinatario = tipoDestinatario;
		this.gruposDestinatarios = gruposDestinatarios;
		this.usuariosDestinatarios = usuariosDestinatarios;
		this.archivo = archivo;
		this.cursoDestinatario = cursoDestinatario;
	}
	
	public String getTipoDestinatario() {
		return tipoDestinatario;
	}
	public void setTipoDestinatario(String tipoDestinatario) {
		this.tipoDestinatario = tipoDestinatario;
	}

	public ArchivoDTO getArchivo() {
		return archivo;
	}
	public void setArchivo(ArchivoDTO archivo) {
		this.archivo = archivo;
	}

	public Long[] getGruposDestinatarios() {
		return gruposDestinatarios;
	}

	public void setGruposDestinatarios(Long[] gruposDestinatarios) {
		this.gruposDestinatarios = gruposDestinatarios;
	}

	public Long[] getUsuariosDestinatarios() {
		return usuariosDestinatarios;
	}

	public void setUsuariosDestinatarios(Long[] usuariosDestinatarios) {
		this.usuariosDestinatarios = usuariosDestinatarios;
	}

	public Long getCursoDestinatario() {
		return cursoDestinatario;
	}

	public void setCursoDestinatario(Long cursoDestinatario) {
		this.cursoDestinatario = cursoDestinatario;
	}
	
	
}
