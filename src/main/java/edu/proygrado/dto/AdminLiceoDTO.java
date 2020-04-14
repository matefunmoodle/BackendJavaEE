package edu.proygrado.dto;

import edu.proygrado.modelo.AdminLiceo;

public class AdminLiceoDTO {
	
	String cedula;
	String nombre;
	String apellido;
	String password;
	ConfiguracionDTO config;

	public AdminLiceoDTO() { }

	public AdminLiceoDTO(AdminLiceo lic) {
		this.cedula = lic.getCedula();
		this.nombre = lic.getNombre();
		this.apellido = lic.getApellido();
		this.password = "";
		this.config = null; //TODO: usar un toDto
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ConfiguracionDTO getConfig() {
		return config;
	}

	public void setConfig(ConfiguracionDTO config) {
		this.config = config;
	}

}
