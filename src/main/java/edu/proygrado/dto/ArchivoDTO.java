/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.proygrado.dto;

import java.io.Serializable;
import java.util.Date;
import edu.proygrado.modelo.Archivo;
import edu.proygrado.modelo.EstadoArchivo;

/**
 *
 * @author gonzalo
 */
public class ArchivoDTO implements Serializable {
    
	private static final long serialVersionUID = 1L;

	long id;
    String nombre;
    Date fechaCreacion;
    String contenido;
    String cedulaCreador;
    String moodleFilePath;
    boolean editable;
    boolean eliminado;
    long padreId;
    long archivoOrigenId;
	boolean directorio;
    String estado;
    EvaluacionDTO evaluacion;
    TipoArchivo tipo;
    boolean puedeCompartir;
    String directorioMatefun;
    
    @Override
    public String toString() {
    	return 
    	"id: " + id +
        ", nombre: " + nombre +
        ", fechaCreacion: " + fechaCreacion +
        ", cedulaCreador: " + cedulaCreador +
        ", editable: " + editable +
        ", moodleFilePath: " + moodleFilePath +
        ", contenido: " + contenido +
        ", eliminado: " + eliminado +
        ", padreId: " + padreId +
        ", archivoOrigenId: " + archivoOrigenId +
        ", directorio: " + directorio +
        ", estado: " + estado +
        ", evaluacion: " + evaluacion +
        ", puedeCompartir: " + puedeCompartir +
        ", directorioMatefun: " + directorioMatefun +
    	", tipo: " + (tipo!=null ? tipo.toString() : "null") + "]";
    	
    }
    
    public ArchivoDTO(){
    }
    
    public boolean isEliminado() {
		return eliminado;
	}

	public void setEliminado(boolean eliminado) {
		this.eliminado = eliminado;
	}

    
	
    public ArchivoDTO(long id, String nombre, Date fechaCreacion, String contenido, String cedulaCreador,
			boolean editable, boolean eliminado, long padreId, long archivoOrigenId, boolean directorio, String estado,
			String moodleFilePath, EvaluacionDTO evaluacion, TipoArchivo tipo, boolean puedeCompartir, String directorioMatefun) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.fechaCreacion = fechaCreacion;
		this.contenido = contenido;
		this.cedulaCreador = cedulaCreador;
		this.editable = editable;
		this.eliminado = eliminado;
		this.padreId = padreId;
		this.archivoOrigenId = archivoOrigenId;
		this.directorio = directorio;
		this.estado = estado;
		this.moodleFilePath = moodleFilePath;
		this.evaluacion = evaluacion;
		this.tipo = tipo;
		this.puedeCompartir = puedeCompartir;
		this.directorioMatefun = directorioMatefun;
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

	public static ArchivoDTO rootDir(Long rootId, String creador) {
		ArchivoDTO root = new ArchivoDTO();
		
		root.setId(rootId);
		root.setNombre("root");
		root.setFechaCreacion(new Date());
		root.setContenido("Carpeta raiz");
		root.setCedulaCreador(creador);
		root.setEditable(true);
		root.setEliminado(false);
		root.setPadreId(-1l);
		root.setArchivoOrigenId(-1l);
		root.setDirectorio(true);
		root.setEstado(EstadoArchivo.Edicion.toString());
		root.setEvaluacion(new EvaluacionDTO());
		root.setMoodleFilePath("/");//TODO:OJO
		return root;
	}

	public String getMoodleFilePath() {
		return moodleFilePath;
	}

	public void setMoodleFilePath(String moodleFilePath) {
		this.moodleFilePath = moodleFilePath;
	}

	public TipoArchivo getTipo() {
		return tipo;
	}

	public void setTipo(TipoArchivo tipo) {
		this.tipo = tipo;
	}

	public boolean isPuedeCompartir() {
		return puedeCompartir;
	}

	public void setPuedeCompartir(boolean puedeCompartir) {
		this.puedeCompartir = puedeCompartir;
	}

	public String getDirectorioMatefun() {
		return directorioMatefun;
	}

	public void setDirectorioMatefun(String directorioMatefun) {
		this.directorioMatefun = directorioMatefun;
	}

}
