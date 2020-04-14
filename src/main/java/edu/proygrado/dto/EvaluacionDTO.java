package edu.proygrado.dto;

import java.util.Date;

import edu.proygrado.modelo.Evaluacion;

public class EvaluacionDTO {
	
	long evaluacionId;
    private String cedulaDocente;
    private Date fecha;    
    private Long assignmentid;
    private Long userid;
    private String nota;
    private Long attemptnumber;
    private Boolean addattempt;
    private String workflowstate;
    private Boolean applytoall;
    private String descripcion;
    boolean corregido;
    boolean esGrupal;
    Integer idDocenteQueCorrigio;
    Integer fechaCorreccion;
    String assignmentName;
    
    
    public EvaluacionDTO(){}
    
    public EvaluacionDTO(Evaluacion eval){
    	this.evaluacionId = eval.getEvaluacionId();
    	this.cedulaDocente = eval.getDocente().getCedula();
    	this.fecha = eval.getFecha();
    	// this.nota = eval.getNota(); TODO: arraglar aqui el tipo
    	this.descripcion = eval.getDescripcion();
    }

	public EvaluacionDTO(Long assignmentid, Long userid, String nota,
			Long attemptnumber, Boolean addattempt, String workflowstate, String descripcion, boolean corregido, Integer idDocenteQueCorrigio, Integer fechaCorreccion, String assignmentName, boolean esGrupal) {
		super();
		this.evaluacionId = -1;
		this.cedulaDocente = "";
		this.fecha = new Date();
		this.assignmentid = assignmentid;
		this.userid = userid;
		this.nota = nota;
		this.attemptnumber = attemptnumber;
		this.addattempt = addattempt;
		this.workflowstate = workflowstate;
		this.applytoall = esGrupal;
		this.descripcion = descripcion;
		this.corregido = corregido;
		this.idDocenteQueCorrigio = idDocenteQueCorrigio;
		this.fechaCorreccion = fechaCorreccion;
		this.assignmentName = assignmentName;
		this.esGrupal = esGrupal;
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
	public String getNota() {
		return nota;
	}
	public void setNota(String nota) {
		this.nota = nota;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Long getAssignmentid() {
		return assignmentid;
	}

	public void setAssignmentid(Long assignmentid) {
		this.assignmentid = assignmentid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getAttemptnumber() {
		return attemptnumber;
	}

	public void setAttemptnumber(Long attemptnumber) {
		this.attemptnumber = attemptnumber;
	}

	public Boolean getAddattempt() {
		return addattempt;
	}

	public void setAddattempt(Boolean addattempt) {
		this.addattempt = addattempt;
	}

	public String getWorkflowstate() {
		return workflowstate;
	}

	public void setWorkflowstate(String workflowstate) {
		this.workflowstate = workflowstate;
	}

	public Boolean getApplytoall() {
		return applytoall;
	}

	public void setApplytoall(Boolean applytoall) {
		this.applytoall = applytoall;
	}

	public boolean isCorregido() {
		return corregido;
	}

	public void setCorregido(boolean corregido) {
		this.corregido = corregido;
	}

	public Integer getIdDocenteQueCorrigio() {
		return idDocenteQueCorrigio;
	}

	public void setIdDocenteQueCorrigio(Integer idDocenteQueCorrigio) {
		this.idDocenteQueCorrigio = idDocenteQueCorrigio;
	}

	public Integer getFechaCorreccion() {
		return fechaCorreccion;
	}

	public void setFechaCorreccion(Integer fechaCorreccion) {
		this.fechaCorreccion = fechaCorreccion;
	}

	public String getAssignmentName() {
		return assignmentName;
	}

	public void setAssignmentName(String assignmentName) {
		this.assignmentName = assignmentName;
	}

	public boolean isEsGrupal() {
		return esGrupal;
	}

	public void setEsGrupal(boolean esGrupal) {
		this.esGrupal = esGrupal;
	}

}
