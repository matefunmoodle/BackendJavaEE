/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.proygrado.dto;

import java.io.Serializable;
import java.util.Date;
import edu.proygrado.modelo.Archivo;

/**
 *
 * @author gonzalo
 */
public class ArchivoDTO implements Serializable {
    long id;
    String nombre;
    Date fechaCreacion;
    String contenido;
    String cedulaCreador;
    boolean editable;
    boolean eliminado;
    long padreId;
    long archivoOrigenId;
    boolean directorio;
    String estado;
    EvaluacionDTO evaluacion;
    
    public ArchivoDTO(){
    }
    
    public boolean isEliminado() {
		return eliminado;
	}

	public void setEliminado(boolean eliminado) {
		this.eliminado = eliminado;
	}

	public ArchivoDTO(Archivo archivo){
        this.id = archivo.getId();
        this.nombre = archivo.getNombre();
        this.fechaCreacion = archivo.getFechaCreacion();
        this.contenido = archivo.getContenido();
        if(archivo.getCreador()!=null){
        	this.cedulaCreador = archivo.getCreador().getCedula();
        }else{
        	this.cedulaCreador = null;
        }
        this.editable = archivo.isEditable();
        this.eliminado = archivo.isEliminado();
        if(archivo.getPadre()!=null){
        	this.padreId = archivo.getPadre().getId();
        }else{
        	this.padreId = -1;
        }
        if(archivo.getArchivoOrigen() !=null){
        	this.archivoOrigenId = archivo.getArchivoOrigen().getId();
        }else{
        	this.archivoOrigenId = -1;
        }
        this.directorio = archivo.isDirectorio();
        this.estado = archivo.getEstado().name();
        if(archivo.getEvaluacion()!=null){
        	this.evaluacion = new EvaluacionDTO(archivo.getEvaluacion());
        }
    }
    
    public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

	public long getPadreId() {
		return padreId;
	}

	public void setPadreId(long padreId) {
		this.padreId = padreId;
	}

	public String getCedulaCreador() {
		return cedulaCreador;
	}

	public void setCedulaCreador(String cedulaCreador) {
		this.cedulaCreador = cedulaCreador;
	}

	public boolean isDirectorio() {
		return directorio;
	}

	public void setDirectorio(boolean directorio) {
		this.directorio = directorio;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public long getArchivoOrigenId() {
		return archivoOrigenId;
	}

	public void setArchivoOrigenId(long archivoOrigenId) {
		this.archivoOrigenId = archivoOrigenId;
	}

	public EvaluacionDTO getEvaluacion() {
		return evaluacion;
	}

	public void setEvaluacion(EvaluacionDTO evaluacion) {
		this.evaluacion = evaluacion;
	}

}
