package edu.proygrado.modelo;

import edu.proygrado.dto.AdminLiceoDTO;

public class AdminLiceo extends Usuario {

	private static final long serialVersionUID = 1L;

	public AdminLiceo() { }

	public AdminLiceo(String cedula, String nombre, String apellido, Liceo liceo, Configuracion configuracion) {
		super(cedula, 0l, nombre, apellido, liceo, configuracion);
	}

	@Override
	public String getTipo() {
		return "adminliceo";
	}

	public AdminLiceoDTO toDto() {
		return new AdminLiceoDTO(this);
	}
	
	public void addArchivo (Archivo a) {
		throw new UnsupportedOperationException("No se puede crear un archivo para un administrador de liceo");
	}
}
