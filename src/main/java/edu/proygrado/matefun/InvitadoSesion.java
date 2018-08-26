package edu.proygrado.matefun;

import java.util.Date;
import java.util.List;

import edu.proygrado.modelo.Archivo;
import edu.proygrado.modelo.Usuario;

public class InvitadoSesion {
	private Date creada;
	private List<Archivo> archivos;
	private List<Archivo> archivosCompartidos;
	private List<Archivo> archivosGrupo;
	private Usuario usuario;
	
	public InvitadoSesion(Usuario usuario){
		this.creada = new Date();
		this.usuario = usuario;		
	}
	
	public Date getCreada() {
		return creada;
	}
	public void setCreada(Date creada) {
		this.creada = creada;
	}
	public List<Archivo> getArchivos() {
		return archivos;
	}
	public void setArchivos(List<Archivo> archivos) {
		this.archivos = archivos;
	}
	public List<Archivo> getArchivosCompartidos() {
		return archivosCompartidos;
	}
	public void setArchivosCompartidos(List<Archivo> archivosCompartidos) {
		this.archivosCompartidos = archivosCompartidos;
	}
	public List<Archivo> getArchivosGrupo() {
		return archivosGrupo;
	}
	public void setArchivosGrupo(List<Archivo> archivosGrupo) {
		this.archivosGrupo = archivosGrupo;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}
