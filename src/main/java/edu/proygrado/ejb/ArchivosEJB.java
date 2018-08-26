/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.proygrado.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

import edu.proygrado.dto.ArchivoDTO;
import edu.proygrado.dto.EvaluacionDTO;
import edu.proygrado.modelo.Alumno;
import edu.proygrado.modelo.Archivo;
import edu.proygrado.modelo.Docente;
import edu.proygrado.modelo.EstadoArchivo;
import edu.proygrado.modelo.Evaluacion;
import edu.proygrado.modelo.Usuario;

@Stateless
public class ArchivosEJB {

	@PersistenceContext(unitName = "matefunDS")
	private EntityManager em;

	public void persist(Object object) {
		em.persist(object);
	}

	public List<ArchivoDTO> getArchivosUsuario(String cedula) throws Exception {
		List<Archivo> archivos;

		Usuario user = em.find(Usuario.class, cedula);
		if (user == null) {
			throw new Exception("No existe el usuario con cedula " + cedula);
		}
		if (user instanceof Alumno) {
			archivos = em.createQuery(
							"select ar from Alumno al join al.archivos ar where LOWER(al.cedula)=LOWER(:cedula) and ar.eliminado=:statusEliminado")
					.setParameter("cedula", cedula).setParameter("statusEliminado", false).getResultList();
		} else {
			archivos = em.createQuery(
							"select ar from Docente d join d.archivos ar where LOWER(d.cedula)=LOWER(:cedula) and ar.eliminado=:statusEliminado")
					.setParameter("cedula", cedula).
					setParameter("statusEliminado", false).getResultList();
		}
		List<ArchivoDTO> archivosDTO = new ArrayList<>();
		archivos.stream().forEach((archivo) -> {
			archivosDTO.add(new ArchivoDTO(archivo));
		});

		return archivosDTO;
	}

	public List<ArchivoDTO> getArchivosCompartidosAlumno(String cedula) throws Exception {
		List<Archivo> archivos = em
				.createQuery("select ar from Alumno al join al.archivosCompartidos ar where al.cedula=:cedula")
				.setParameter("cedula", cedula).getResultList();

		List<Archivo> archivosGrupo = em
				.createQuery(
						"select archivos from Grupo g join g.archivos archivos join g.alumnos alumnos where alumnos.cedula =:cedula")
				.setParameter("cedula", cedula).getResultList();

		List<ArchivoDTO> archivosDTO = new ArrayList<>();
		archivos.stream().forEach((archivo) -> {
			archivosDTO.add(new ArchivoDTO(archivo));
		});
		archivosGrupo.stream().forEach((archivo) -> {
			archivosDTO.add(new ArchivoDTO(archivo));
		});
		return archivosDTO;
	}

	public ArchivoDTO crearArchivo(ArchivoDTO archivoDTO) throws Exception {
		boolean existeArchivo = 0 < em
				.createQuery(
						"select count(a) from Archivo a where lower(a.nombre)=lower(:nombre) and a.creador.cedula=:cedula and a.padre.id =:padreId and a.eliminado=0",
						Long.class)
				.setParameter("nombre", archivoDTO.getNombre()).setParameter("padreId", archivoDTO.getPadreId())
				.setParameter("cedula", archivoDTO.getCedulaCreador()).getSingleResult();
		if (!existeArchivo) {
			Usuario creador = em.find(Usuario.class, archivoDTO.getCedulaCreador());

			if (creador == null) {
				throw new Exception("No existe el usuario de cedula " + archivoDTO.getCedulaCreador());
			}
			Archivo padre = em.find(Archivo.class, archivoDTO.getPadreId());
			Archivo arch = new Archivo(archivoDTO.getNombre(), new Date(), archivoDTO.getContenido(),
					EstadoArchivo.Edicion, archivoDTO.isEditable(), archivoDTO.isDirectorio(), padre, creador);
			arch.setEliminado(archivoDTO.isEliminado());
			if (creador instanceof Docente) {
				((Docente) creador).addArchivo(arch);
			} else if (creador instanceof Alumno) {
				((Alumno) creador).addArchivo(arch);
			}
			em.persist(arch);
			em.flush();
			return new ArchivoDTO(arch);
		} else {
			throw new Exception("Ya existe un archivo de nombre" + archivoDTO.getNombre());
		}
	}

	public ArchivoDTO getCopiaCompartido(String cedula, Long archivoId) throws Exception {
		Archivo archivo = em.find(Archivo.class, archivoId);
		try {
			Archivo copiaExistente = (Archivo) em
					.createQuery(
							"select ar from Alumno a join a.archivosCompartidos ar where a.cedula =:cedula and ar.archivoOrigen.id =:archivoId ")
					.setParameter("cedula", cedula).setParameter("archivoId", archivoId).getSingleResult();
			return new ArchivoDTO(copiaExistente);
		} catch (NoResultException nr) {
			// no existe la copia. No se hace nada con esta excepcion.
		}
		if (archivo == null) {
			throw new Exception("No exite el archivo de id " + archivoId);
		}

		Alumno alumno = em.find(Alumno.class, cedula);
		if (alumno == null) {
			throw new Exception("No existe el alumno de cedula " + cedula);
		}
		Archivo root = null;
		for (Archivo a : alumno.getArchivos()) {
			if (a.getPadre() == null) {
				root = a;
			}
		}
		Archivo copia = new Archivo(archivo.getNombre(), new Date(), archivo.getContenido(), EstadoArchivo.Edicion,
				true, false, root, archivo.getCreador());
		copia.setArchivoOrigen(archivo);
		alumno.addArchivoCompartido(copia);
		em.persist(copia);
		em.flush();
		return new ArchivoDTO(copia);
	}

	public ArchivoDTO editarArchivo(long archivoId, ArchivoDTO archivoDTO) throws Exception {
		Archivo archivo = em.find(Archivo.class, archivoId);
		if (archivo != null) {
			archivo.setContenido(archivoDTO.getContenido());
			archivo.setNombre(archivoDTO.getNombre());
			archivo.setEditable(archivoDTO.isEditable());
			archivo.setEliminado(archivoDTO.isEliminado());
			Archivo padre = em.find(Archivo.class, archivoDTO.getPadreId());
			archivo.setPadre(padre);
			archivo.setEstado(EstadoArchivo.valueOf(archivoDTO.getEstado()));
			return new ArchivoDTO(archivo);
		} else {
			throw new Exception("No se encuentra el archivo con id: " + archivoId);
		}
	}

	public ArchivoDTO getArchivo(long archivoId) throws Exception {
		Archivo archivo = em.find(Archivo.class, archivoId);
		if (archivo != null) {
			return new ArchivoDTO(archivo);
		} else {
			throw new Exception("No se encuentra el archivo con id: " + archivoId);
		}
	}

	public void eliminarArchivo(long archivoId) throws Exception {
		Archivo archivo = em.find(Archivo.class, archivoId);
		if (archivo != null) {
			em.remove(archivo);
		}
	}

	public EvaluacionDTO evaluarArchivo(Long archivoId, EvaluacionDTO evaluacion) throws Exception {
		Archivo archivo = em.find(Archivo.class, archivoId);
		if (archivo == null) {
			throw new Exception("No se encuentra el archivo con id: " + archivoId);
		}
		Docente docente = em.find(Docente.class, evaluacion.getCedulaDocente());
		if (docente == null) {
			throw new Exception("No se encuentra el docente de cedula: " + evaluacion.getCedulaDocente());
		}
		Evaluacion eval;
		if (archivo.getEvaluacion() == null) {
			eval = new Evaluacion();
			em.persist(eval);
		} else {
			eval = archivo.getEvaluacion();
		}
		eval.setDescripcion(evaluacion.getDescripcion());
		eval.setDocente(docente);
		eval.setFecha(new Date());
		eval.setNota(evaluacion.getNota());
		archivo.setEvaluacion(eval);
		archivo.setEstado(EstadoArchivo.Corregido);
		return evaluacion;
	}

}
