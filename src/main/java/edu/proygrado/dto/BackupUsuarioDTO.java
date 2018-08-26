package edu.proygrado.dto;

import edu.proygrado.modelo.Alumno;
import edu.proygrado.modelo.Usuario;

public class BackupUsuarioDTO {
	String rol;
	String cedula;
	String nombre;
	String apellido;
	String password;
	ConfiguracionDTO configuracion;
	
	public BackupUsuarioDTO(){}
	
	public BackupUsuarioDTO(Usuario usuario){
		if (usuario instanceof Alumno) {
			this.rol = "Alumno";
		}else{
			this.rol = "Docente";
		}
		this.cedula = usuario.getCedula();
		this.nombre = usuario.getNombre();
		this.apellido = usuario.getApellido();
		this.password = usuario.getPassword();
		this.configuracion = new ConfiguracionDTO(usuario.getConfiguracion());
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
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

	public ConfiguracionDTO getConfiguracion() {
		return configuracion;
	}

	public void setConfiguracion(ConfiguracionDTO configuracion) {
		this.configuracion = configuracion;
	}
	
	
}
