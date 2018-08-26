/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.proygrado.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author gonzalo
 */
@Entity
public class Archivo implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    long id;
    String nombre;
    boolean editable;
    boolean eliminado;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Date fechaCreacion;
    @Lob
    String contenido;
    
    EstadoArchivo estado;
    
    //Para el caso de archivos compartidos, referencia al archivo orginal
    @ManyToOne
    Archivo archivoOrigen;
    
    @ManyToOne
    Archivo padre;
    
    @OneToOne
    Evaluacion evaluacion;
    
    @ManyToOne
    Usuario creador;
    
    boolean directorio;

    public Archivo() {
    }

    public Archivo(String nombre, Date fechaCreacion, String contenido,EstadoArchivo estado,  boolean editable, boolean directorio,Archivo padre, Usuario creador) {
        this.nombre = nombre;
        this.fechaCreacion = fechaCreacion;
        this.contenido = contenido;
        this.padre = padre;
        this.directorio = directorio;
        this.editable = editable;
        this.estado = estado;
        this.eliminado = false;
        this.creador = creador;
    }

	public boolean isEliminado() {
		return eliminado;
	}

	public void setEliminado(boolean eliminado) {
		this.eliminado = eliminado;
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

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
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

	public EstadoArchivo getEstado() {
		return estado;
	}

	public void setEstado(EstadoArchivo estado) {
		this.estado = estado;
	}

	public Archivo getArchivoOrigen() {
		return archivoOrigen;
	}

	public void setArchivoOrigen(Archivo archivoOrigen) {
		this.archivoOrigen = archivoOrigen;
	}

	public Archivo getPadre() {
		return padre;
	}

	public void setPadre(Archivo padre) {
		this.padre = padre;
	}

	public boolean isDirectorio() {
		return directorio;
	}

	public void setDirectorio(boolean directorio) {
		this.directorio = directorio;
	}

	public Evaluacion getEvaluacion() {
		return evaluacion;
	}

	public void setEvaluacion(Evaluacion evaluacion) {
		this.evaluacion = evaluacion;
	}

	public Usuario getCreador() {
		return creador;
	}

	public void setCreador(Usuario creador) {
		this.creador = creador;
	}
	
}