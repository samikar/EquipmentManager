package model;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.Table;

import java.util.List;

public class ReservationDao {
private Reservation dao;

EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("EquipmentManager");
EntityManager em = entityManagerFactory.createEntityManager();
	
	public Reservation getDao() {
		return dao;
	}
	
	public void setDao(Reservation dao) {
		this.dao = dao;
	}
	
	private EntityManager entityManager;
	
	public void init(){
		entityManager = Persistence.createEntityManagerFactory("EquipmentManager").createEntityManager();
	}
	
	public List<Reservation> getDaos(){
		entityManager.getTransaction().begin();
		Query query = entityManager.createNamedQuery("Reservation.findAll");
		//Query query = entityManager.createQuery("from Product c", Product.class);
		List<Reservation> result = query.getResultList(); 
		entityManager.getTransaction().commit();
		return result;
	}
	
	public int persist(Reservation dao){
		entityManager.getTransaction().begin();
		entityManager.persist(dao);
		entityManager.getTransaction().commit();
		return dao.getReservationId();
	}
	
	public void initialize(int daoNumber){
		dao = entityManager.find(Reservation.class, daoNumber);
		  if(dao == null)throw new IllegalStateException
		   ("Dao number ("+daoNumber+") not found");		
	}
	
	public void update(Reservation dao){
		//just checking that the dao really has is
		if(dao.getReservationId()>0){
			//get the actual entity from database to a dao-named attribute
			initialize(dao.getReservationId());
			//start database transaction
			entityManager.getTransaction().begin();
			dao.setDateReturn(dao.getDateReturn());
			dao.setDateTake(dao.getDateTake());
			dao.setEmployeeId(dao.getEmployeeId());
			dao.setEquipment(dao.getEquipment());
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
	
	public List<Reservation> getAll() {
		List<Reservation> reservations = em.createNamedQuery("Reservation.findAll", Reservation.class)
				.getResultList();
		return reservations;
	}
	
	public List<Reservation> getByEmployeeId(String employeeId) {
		List<Reservation> reservations = em.createNamedQuery("Reservation.findByEmployeeId", Reservation.class)
				.setParameter(1, employeeId)
				.getResultList();
		return reservations;
	}
	
	public List<Reservation> getByEquipmentId(String equipmentId) {
		List<Reservation> reservations = em.createNamedQuery("Reservation.findByEquipmentId", Reservation.class)
				.setParameter(1, equipmentId)
				.getResultList();
		return reservations;
	}
	
	public List<Reservation> getByType(String type) {
		List<Reservation> reservations = em.createNamedQuery("Reservation.findByType", Reservation.class)
				.setParameter(1, type)
				.getResultList();
		return reservations;
	}
	
	public List<Reservation> getOpen() {
		List<Reservation> reservations = em.createNamedQuery("Reservation.findOpen", Reservation.class)
				.getResultList();
		return reservations;
	}
	
	public boolean reservationOpenBySerial(String serial) {
		List<Reservation> reservations = em.createNamedQuery("Reservation.findOpenBySerial", Reservation.class)
				.setParameter(1, serial)
				.getResultList();
		if (reservations.size() > 0)
			return true;
		else
			return false;
	}
	
}
