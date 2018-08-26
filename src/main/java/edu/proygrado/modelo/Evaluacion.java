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
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 *
 * @author gonzalo
 */
@Entity
public class Evaluacion implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    long evaluacionId;
    @ManyToOne
    private Docente docente;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fecha;
    private int nota;
    private String descripcion;

    public long getEvaluacionId() {
        return evaluacionId;
    }

    public void setEvaluacionId(long evaluacionId) {
        this.evaluacionId = evaluacionId;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
    
    
}
