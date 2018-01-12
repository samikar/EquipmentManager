package model;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.Table;

import java.util.List;

public class EquipmentreservationDao {
private Equipmentreservation dao;

EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("EquipmentManager");
EntityManager em = entityManagerFactory.createEntityManager();
	
	public Equipmentreservation getDao() {
		return dao;
	}
	
	public void setDao(Equipmentreservation dao) {
		this.dao = dao;
	}
	
	private EntityManager entityManager;
	
	public void init(){
		entityManager = Persistence.createEntityManagerFactory("EquipmentManager").createEntityManager();
	}
	
	public List<Equipmentreservation> getDaos(){
		entityManager.getTransaction().begin();
		Query query = entityManager.createNamedQuery("Equipmentreservation.findAll");
		//Query query = entityManager.createQuery("from Product c", Product.class);
		List<Equipmentreservation> result = query.getResultList(); 
		entityManager.getTransaction().commit();
		return result;
	}
	
	public int persist(Equipmentreservation dao){
		System.out.println("\n***** Reservaatio: " + dao.getIdEquipmentReservation() + "/" + dao.getDateReturn() + "/" + dao.getDateTake()  + "/" + dao.getEmployeeId_return()  + "/" + dao.getEmployeeId_take()  + "/" + dao.getEquipmentId()  + "/" + dao.getReservationType() + " *****\n");
		entityManager.getTransaction().begin();
		entityManager.persist(dao);
		entityManager.getTransaction().commit();
		return dao.getIdEquipmentReservation();
	}
	
	public void initialize(int daoNumber){
		dao = entityManager.find(Equipmentreservation.class, daoNumber);
		  if(dao == null)throw new IllegalStateException
		   ("Dao number ("+daoNumber+") not found");		
	}
	
	public void update(Equipmentreservation dao){
		//just checking that the dao really has is
		if(dao.getIdEquipmentReservation()>0){
			//get the actual entity from database to a dao-named attribute
			initialize(dao.getIdEquipmentReservation());
			//start database transaction
			entityManager.getTransaction().begin();
			dao.setDateReturn(dao.getDateReturn());
			dao.setDateTake(dao.getDateTake());
			dao.setEmployeeId_return(dao.getEmployeeId_return());
			dao.setEmployeeId_take(dao.getEmployeeId_take());
			dao.setEquipmentId(dao.getEquipmentId());
			dao.setReservationType(dao.getReservationType());			
			entityManager.merge(dao);
			entityManager.getTransaction().commit();
		}
	}
	
	public void delete(){
		entityManager.getTransaction().begin();
		entityManager.remove(dao);
		entityManager.getTransaction().commit();
	}
	
	public void destroy(){
		entityManager.close();
	}
	
	public List<Equipmentreservation> queryTest(String reservationType) {
		List<Equipmentreservation> reservations = em.createNamedQuery("Equipmentreservation.getReservationsByType", Equipmentreservation.class)
				.setParameter(1, reservationType)
				.getResultList();
		return reservations;
	}
	
}
