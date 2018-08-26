package edu.proygrado.dto;

import java.util.Date;

import edu.proygrado.modelo.Evaluacion;

public class EvaluacionDTO {
	long evaluacionId;
    private String cedulaDocente;
    private Date fecha;
    private int nota;
    private String descripcion;
    
    public EvaluacionDTO(){}
    
    public EvaluacionDTO(Evaluacion eval){
    	this.evaluacionId = eval.getEvaluacionId();
    	this.cedulaDocente = eval.getDocente().getCedula();
    	this.fecha = eval.getFecha();
    	this.nota = eval.getNota();
    	this.descripcion = eval.getDescripcion();
    }
    
	public long getEvaluacionId() {
		return evaluacionId;
	}
	public void setEvaluacionId(long evaluacionId) {
		this.evaluacionId = evaluacionId;
	}
	public String getCedulaDocente() {
		return cedulaDocente;
	}
	public void setCedulaDocente(String cedulaDocente) {
		this.cedulaDocente = cedulaDocente;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
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
    
    
}
