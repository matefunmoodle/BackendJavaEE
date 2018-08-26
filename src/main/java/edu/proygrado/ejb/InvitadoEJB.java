package edu.proygrado.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.NotAuthorizedException;

import edu.proygrado.dto.ArchivoDTO;
import edu.proygrado.matefun.InvitadoSesion;
import edu.proygrado.modelo.Alumno;
import edu.proygrado.modelo.Archivo;
import edu.proygrado.modelo.EstadoArchivo;
import edu.proygrado.modelo.Usuario;

@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class InvitadoEJB {

	@PersistenceContext(unitName = "matefunDS")
	private EntityManager em;

	private Map<String, InvitadoSesion> sesiones = new HashMap<>();
	
	public Usuario getUsuario(String token){
		System.out.println("getUsuario"+token);
		InvitadoSesion invitadoSesion = sesiones.getOrDefault(token, null);
		if (invitadoSesion != null) {
			return invitadoSesion.getUsuario();
		}else{
			return null;
		}
	}

	public void setUsuario(String token, Usuario usuario){
		System.out.println("setUsuario"+token);
		sesiones.put(token,new InvitadoSesion(usuario));
	}
	
	public void eliminarRecursos(String token){
		sesiones.remove(token);
	}
	
	public List<ArchivoDTO> getArchivosUsuario(String token, String cedula) throws Exception {
		System.out.println("getArchivosUsuario"+token);
		InvitadoSesion invitadoSesion = sesiones.getOrDefault(token, null);
		if (invitadoSesion == null) {
			throw new NotAuthorizedException("No autorizado");
		}

		List<Archivo> archivos = invitadoSesion.getArchivos();
		if (archivos == null) {
			archivos = em
					.createQuery(
							"select ar from Alumno al join al.archivos ar where LOWER(al.cedula)=LOWER(:cedula) and ar.eliminado=0")
					.setParameter("cedula", cedula).getResultList();
			em.clear();
			invitadoSesion.setArchivos(archivos);
		}

		List<ArchivoDTO> archivosDTO = new ArrayList<>();
		archivos.stream().forEach((archivo) -> {
			archivosDTO.add(new ArchivoDTO(archivo));
		});

		return archivosDTO;
	}

	public List<ArchivoDTO> getArchivosCompartidosAlumno(String token, String cedula) throws Exception {
		System.out.println("getArchivosCompartidosAlumno"+token);
		InvitadoSesion invitadoSesion = sesiones.getOrDefault(token, null);
		if (invitadoSesion == null) {
			throw new NotAuthorizedException("No autorizado");
		}

		List<Archivo> archivosCompartidos = invitadoSesion.getArchivosCompartidos();
		if (archivosCompartidos == null) {
			archivosCompartidos = em
					.createQuery("select ar from Alumno al join al.archivosCompartidos ar where al.cedula=:cedula")
					.setParameter("cedula", cedula).getResultList();
			em.clear();
			
			invitadoSesion.setArchivosCompartidos(archivosCompartidos);
		}
		List<Archivo> archivosGrupo = invitadoSesion.getArchivosGrupo();
		if (archivosGrupo == null) {
			archivosGrupo = em
					.createQuery(
							"select archivos from Grupo g join g.archivos archivos join g.alumnos alumnos where alumnos.cedula =:cedula")
					.setParameter("cedula", cedula).getResultList();
			em.clear();
			invitadoSesion.setArchivosGrupo(archivosGrupo);
		}
		List<ArchivoDTO> archivosDTO = new ArrayList<>();
		archivosCompartidos.stream().forEach((archivo) -> {
			archivosDTO.add(new ArchivoDTO(archivo));
		});
		archivosGrupo.stream().forEach((archivo) -> {
			archivosDTO.add(new ArchivoDTO(archivo));
		});
		return archivosDTO;
	}

	
	public ArchivoDTO crearArchivo(String token, ArchivoDTO archivoDTO) throws Exception {
		System.out.println("crearArchivo"+token);
		InvitadoSesion invitadoSesion = sesiones.getOrDefault(token, null);
		if (invitadoSesion == null) {
			throw new NotAuthorizedException("No autorizado");
		}
		List<Archivo> archivos = invitadoSesion.getArchivos();
		if (archivos == null) {
			archivos = em
					.createQuery(
							"select ar from Alumno al join al.archivos ar where LOWER(al.cedula)=LOWER(:cedula) and ar.eliminado=0")
					.setParameter("cedula", archivoDTO.getCedulaCreador()).getResultList();
			em.clear();
			invitadoSesion.setArchivos(archivos);
		}

		boolean existeArchivo = false;
		long maxId = 0;
		for (Archivo archivo : archivos) {
			if (archivo.getNombre().equals(archivoDTO.getNombre())
					&& archivo.getCreador().getCedula().equals(archivoDTO.getCedulaCreador())
					&& archivo.getPadre().getId() == archivoDTO.getPadreId() && !archivo.isEliminado()) {
				existeArchivo = true;
			}
			if (archivo.getId() > maxId) {
				maxId = archivo.getId();
			}
		}
		if (!existeArchivo) {
			Usuario usuario = invitadoSesion.getUsuario();
			if (usuario == null) {
				usuario = em.find(Usuario.class, archivoDTO.getCedulaCreador());
				if (usuario == null) {
					throw new Exception("No existe el usuario de cedula " + archivoDTO.getCedulaCreador());
				}
				em.clear();

			}

			Archivo padre = null;
			for(Archivo arch:archivos){
				if(archivoDTO.getPadreId() == arch.getId()){
					padre = arch;
				}
			}
			Archivo arch = new Archivo(archivoDTO.getNombre(), new Date(), archivoDTO.getContenido(),
					EstadoArchivo.Edicion, archivoDTO.isEditable(), archivoDTO.isDirectorio(), padre, usuario);
			arch.setEliminado(archivoDTO.isEliminado());
			arch.setId(maxId + 1);
			archivos.add(arch);
			((Alumno) usuario).setArchivos(archivos);
			return new ArchivoDTO(arch);
		} else {
			throw new Exception("Ya existe un archivo de nombre" + archivoDTO.getNombre());
		}
	}

	public ArchivoDTO getCopiaCompartido(String token, String cedula, Long archivoId) throws Exception {
		System.out.println("getCopiaCompartido"+token);
		InvitadoSesion invitadoSesion = sesiones.getOrDefault(token, null);
		if (invitadoSesion == null) {
			throw new NotAuthorizedException("No autorizado");
		}
		List<Archivo> archivosCompartidos = invitadoSesion.getArchivosCompartidos();
		if (archivosCompartidos == null) {
			archivosCompartidos = em
					.createQuery("select ar from Alumno al join al.archivosCompartidos ar where al.cedula=:cedula")
					.setParameter("cedula", cedula).getResultList();
			em.clear();
			invitadoSesion.setArchivosCompartidos(archivosCompartidos);
		}

		Archivo archivo = em.find(Archivo.class, archivoId);
		if (archivo == null) {
			throw new Exception("No exite el archivo de id " + archivoId);
		}
		em.clear();

		Archivo copiaExistente = null;
		long maxId = 0;
		for (Archivo archivoCompartido : archivosCompartidos) {
			if (archivoCompartido.getArchivoOrigen().getId() == archivoId) {
				copiaExistente = archivoCompartido;
			}
			if (archivo.getId() > maxId) {
				maxId = archivo.getId();
			}
		}

		if (copiaExistente != null) {
			return new ArchivoDTO(copiaExistente);
		}

		Alumno alumno = (Alumno) invitadoSesion.getUsuario();

		Archivo root = null;
		for (Archivo a : alumno.getArchivos()) {
			if (a.getPadre() == null) {
				root = a;
			}
		}
		Archivo copia = new Archivo(archivo.getNombre(), new Date(), archivo.getContenido(), EstadoArchivo.Edicion,
				true, false, root, archivo.getCreador());
		copia.setArchivoOrigen(archivo);
		copia.setId(maxId + 1);
		archivosCompartidos.add(copia);
		alumno.setArchivosCompartidos(archivosCompartidos);
		return new ArchivoDTO(copia);
	}

	public ArchivoDTO editarArchivo(String token, long archivoId, ArchivoDTO archivoDTO) throws Exception {
		System.out.println("editarArchivo"+token);
		InvitadoSesion invitadoSesion = sesiones.getOrDefault(token, null);
		if (invitadoSesion == null) {
			throw new NotAuthorizedException("No autorizado");
		}
		List<Archivo> archivos = invitadoSesion.getArchivos();
		if (archivos == null) {
			archivos = em
					.createQuery(
							"select ar from Alumno al join al.archivos ar where LOWER(al.cedula)=LOWER(:cedula) and ar.eliminado=0")
					.setParameter("cedula", archivoDTO.getCedulaCreador()).getResultList();
			em.clear();
			invitadoSesion.setArchivos(archivos);
		}
		Archivo archivo = null;
		for (Archivo arch : archivos) {
			if (arch.getId() == archivoId) {
				archivo = arch;
				break;
			}
		}
		if (archivo != null) {
			archivo.setContenido(archivoDTO.getContenido());
			archivo.setNombre(archivoDTO.getNombre());
			archivo.setEditable(archivoDTO.isEditable());
			archivo.setEliminado(archivoDTO.isEliminado());
			for (Archivo arch : archivos) {
				if (arch.getId() == archivoDTO.getPadreId()) {
					archivo.setPadre(arch);
					break;
				}
			}
			archivo.setEstado(EstadoArchivo.valueOf(archivoDTO.getEstado()));
			return new ArchivoDTO(archivo);
		} else {
			throw new Exception("No se encuentra el archivo con id: " + archivoId);
		}
	}

	public void eliminarArchivo(String token, long archivoId) throws Exception {
		System.out.println("eliminarArchivo"+token);
		InvitadoSesion invitadoSesion = sesiones.getOrDefault(token, null);
		if (invitadoSesion == null) {
			throw new NotAuthorizedException("No autorizado");
		}
		List<Archivo> archivos = invitadoSesion.getArchivos();
		Archivo eliminar = null;

		for (Archivo arch : archivos) {
			if (arch.getId() == archivoId) {
				eliminar = arch;
				break;
			}
		}
		if (eliminar != null) {
			archivos.remove(eliminar);
		}
	}
	
	public ArchivoDTO getArchivo(String token, long archivoId) throws Exception {
		System.out.println("getArchivo"+token);
		InvitadoSesion invitadoSesion = sesiones.getOrDefault(token, null);
		if (invitadoSesion == null) {
			throw new NotAuthorizedException("No autorizado");
		}
		//Busco el archivo en los archivos del alumno
		List<Archivo> archivos = invitadoSesion.getArchivos();
		if (archivos != null) {
			for(Archivo arch:archivos){
				if(arch.getId() == archivoId){
					return new ArchivoDTO(arch);
				}
			}
		}
		//Busco el archivo en los archivos compartidos del alumno
		List<Archivo> archivosCompartidos = invitadoSesion.getArchivosCompartidos();
		if (archivosCompartidos != null) {
			for(Archivo arch: archivosCompartidos){
				if(arch.getId() == archivoId){
					return new ArchivoDTO(arch);
				}
			}
		}
		//Busco el archivo en los archivos compartidos del grupo
		List<Archivo> archivosGrupo = invitadoSesion.getArchivosGrupo();
		if (archivosGrupo != null) {
			for(Archivo arch: archivosGrupo){
				if(arch.getId() == archivoId){
					return new ArchivoDTO(arch);
				}
			}
		}
		throw new Exception("No se encuentra el archivo con id: " + archivoId);
	}

}
