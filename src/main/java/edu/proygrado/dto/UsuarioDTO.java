/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.proygrado.dto;
import edu.proygrado.modelo.Alumno;
import edu.proygrado.modelo.Docente;
import edu.proygrado.modelo.Usuario;
/**
 *
 * @author gonzalo
 */
public class UsuarioDTO {
	String token;
    String cedula;
    String nombre;
    String apellido;
    String tipo;
    ConfiguracionDTO configuracion;
    
    
    public UsuarioDTO(){}
    
    public UsuarioDTO(String token, Usuario usuario){
    	this.token = token;
        this.cedula = usuario.getCedula();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        if(usuario instanceof Docente){
            this.tipo = "docente";
        } else if( usuario instanceof Alumno){
            this.tipo = "alumno";
        }
        if(usuario.getConfiguracion()!=null){
        	this.configuracion = new ConfiguracionDTO(usuario.getConfiguracion());
        }
    }

    public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

	public ConfiguracionDTO getConfiguracion() {
		return configuracion;
	}

	public void setConfiguracion(ConfiguracionDTO configuracion) {
		this.configuracion = configuracion;
	}
        
}
