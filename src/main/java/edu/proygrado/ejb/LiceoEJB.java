package edu.proygrado.ejb;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import edu.proygrado.dto.LiceoDTO;
import edu.proygrado.dto.SimplePostResultDTO;
import edu.proygrado.matefun.MatefunException;
import edu.proygrado.modelo.Liceo;
import edu.proygrado.modelo.LiceoPK;

@Stateless
@SuppressWarnings("unchecked")
@TransactionManagement(value=TransactionManagementType.BEAN)
public class LiceoEJB {
	
	public LiceoEJB() { super(); }

	@PersistenceContext(unitName = "matefunDS")
	private EntityManager em;

	@EJB
	private InvitadoEJB invitadoEJB;
	
	@Resource
	private UserTransaction userTransaction;
	
	
	@SuppressWarnings("finally")
	public List<LiceoDTO> getAllLiceos() {
		List<LiceoDTO> listaLiceos = new ArrayList<LiceoDTO>();
		try {
			List<Liceo> liceos = em.createQuery("select l from Liceo l").getResultList();
			for (Liceo liceo : liceos)
				listaLiceos.add(new LiceoDTO(liceo));
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally {
			return listaLiceos;
		}
	}
	
	@SuppressWarnings("finally")
	public SimplePostResultDTO eliminarLiceo(String liceoId) {
		try {
			userTransaction.begin();
				em.createQuery("DELETE FROM Liceo l WHERE l.liceoPK = :l")
					.setParameter("l", new LiceoPK(Long.parseLong(liceoId)))
						.executeUpdate();			  
			userTransaction.commit();
			
			return new SimplePostResultDTO("Liceo Eliminado, id: " + liceoId, true);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				userTransaction.rollback();
			} catch (Exception e1) {
				System.out.println("No se pudo hacer rollback de la transaccion.");
			}finally {
				return new SimplePostResultDTO("Error eliminar Liceo id:" + liceoId, false);
			}
		}	
	}
	
	public SimplePostResultDTO addNewSchool(LiceoDTO liceo) {
		
		try {
			liceo.setServicename(liceo.getServicename().toLowerCase());
			//TODO: NO HACER NATIVO
			userTransaction.begin();

			    em.createNativeQuery("INSERT INTO LICEO (MOODLEAPIUSERTOKEN, MOODLEURI,MOODLEWSSERVICE,NOMBRE) VALUES (?,?,?,?)")
			    .setParameter(1, liceo.getServicetoken())
			    .setParameter(2, liceo.getMoodleuri())
			    .setParameter(3, liceo.getServicename().startsWith("https://")  ? liceo.getServicename() : "https://" + liceo.getServicename() )
			    .setParameter(4, liceo.getNombre()).executeUpdate();
		    
				List<Liceo> liceos = em.createQuery("select l from Liceo l where l.nombre=:nombre")
						.setParameter("nombre", liceo.getNombre() ).getResultList();
			    
				if (liceos.isEmpty()) throw new MatefunException("addNewSchool liceos es vacio");
				Long lastLiceoId = 0l;
			    for (Liceo lic : liceos)
			    	if (lic.getLiceoPK().getLiceoId() > lastLiceoId)
			    		lastLiceoId = lic.getLiceoPK().getLiceoId();
			    if (lastLiceoId==0l) throw new MatefunException("no se actualiza lastLiceoId");
			    
			userTransaction.commit();
			
			return new SimplePostResultDTO("Liceo agregado",true);
		}catch(Exception e) {
			try {
				userTransaction.rollback();
			} catch(Exception e1) {
				System.out.println("No Se Puede Rollback");
			}
			return new SimplePostResultDTO("No se pudo agregar nuevo liceo: " + e.getMessage(),false);
		}
	}
	
	@SuppressWarnings("finally")
	public SimplePostResultDTO updateSchool(LiceoDTO liceo) throws Exception {
		try {
			liceo.setServicename(liceo.getServicename().toLowerCase());
			userTransaction.begin();			
			Query query = em.createQuery("UPDATE Liceo SET nombre = :newName, moodleuri = :newUri, moodlewsservice = :newServiceName, moodleapiusertoken = :newServiceToken WHERE liceoPK = :liceokey")
							  .setParameter("newName", liceo.getNombre())
							  .setParameter("newUri", liceo.getMoodleuri())							  
							  .setParameter("newServiceName", liceo.getServicename().startsWith("https://")  ? liceo.getServicename() : "https://" + liceo.getServicename() )
							  .setParameter("newServiceToken", liceo.getServicetoken())
							  .setParameter("liceokey", new LiceoPK(liceo.getLiceoid()) );

			int updated = query.executeUpdate();
			userTransaction.commit();
			if (updated==1)
				return new SimplePostResultDTO("Liceo actualizado, id: " + liceo.getLiceoid() , true);
			throw new MatefunException("No se pudo actualizar liceo, id: " + liceo.getLiceoid() + "\nupdated=" + updated);
		}catch(Exception e) {
			try {
				userTransaction.rollback();
			}catch(Exception e2) {
				 System.out.println("Cannot rollback transaction in method LiceoEJB.updateSchool");
			}finally {
				throw e;	
			}
		}
	}
}
