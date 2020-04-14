/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.proygrado.dto;

import java.util.List;
import edu.proygrado.modelo.Configuracion;
import edu.proygrado.modelo.Usuario;
/**
 *
 * @author gonzalo
 */
public class UsuarioDTO {
	Long moodleUserId;
	String token;
    String cedula;
    String nombre;
    String apellido;
    String tipo;
    List<MoodleCourseDTO> todosLosCursos;
    ConfiguracionDTO configuracion;
    
    public UsuarioDTO(){}
    
    public UsuarioDTO(Long moodleUserId, String token, Usuario usuario,List<MoodleCourseDTO> todosLosCursos){
    	this.token = token;
        this.cedula = usuario.getCedula();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.tipo = usuario.getTipo();
        this.moodleUserId = moodleUserId;
        this.todosLosCursos = todosLosCursos;
        if(usuario.getConfiguracion()!=null){
        	this.configuracion = new ConfiguracionDTO(usuario.getConfiguracion());
        }else {
        	this.configuracion = new ConfiguracionDTO(Configuracion.getConfiguracionDefault());
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

	public Long getMoodleUserId() {
		return moodleUserId;
	}

	public void setMoodleUserId(Long moodleUserId) {
		this.moodleUserId = moodleUserId;
	}

	public List<MoodleCourseDTO> getTodosLosCursos() {
		return todosLosCursos;
	}

	public void setTodosLosCursos(List<MoodleCourseDTO> todosLosCursos) {
		this.todosLosCursos = todosLosCursos;
	}
        
}
