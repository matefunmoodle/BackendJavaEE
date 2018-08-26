package edu.proygrado.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import edu.proygrado.dto.GrupoDTO;
import edu.proygrado.modelo.Archivo;
import edu.proygrado.modelo.Grupo;
import edu.proygrado.modelo.GrupoPK;

@Stateless
public class GruposEJB {

	@PersistenceContext(unitName = "matefunDS")
	private EntityManager em;

	public void agregarArchivoGrupo(long archivoId, GrupoPK grupoPK) throws Exception {
		Grupo grupo = em.find(Grupo.class, grupoPK);
		if (grupo == null) {
			throw new Exception(
					"No existe el grupo " + grupoPK.getAnio() + " " + grupoPK.getGrado() + grupoPK.getGrupo());
		}
		Archivo archivo = em.find(Archivo.class, archivoId);
		if (archivo == null) {
			throw new Exception("No existe el archivo" + archivoId);
		}
		if (!grupo.getArchivos().contains(archivo)) {
			grupo.addArchivo(archivo);
		}
	}
	
	
	public List<GrupoDTO> getGruposDocente(String cedulaDocente){
		List<Grupo> grupos = em.createQuery("select ga from Docente d join d.gruposAsignados ga where d.cedula =:cedulaDocente")
				.setParameter("cedulaDocente", cedulaDocente)
				.getResultList();
		List<GrupoDTO> gruposDTO = new ArrayList<>();
		for(Grupo g: grupos){
			gruposDTO.add(new GrupoDTO(g));
		}
		return gruposDTO;
	}
}
