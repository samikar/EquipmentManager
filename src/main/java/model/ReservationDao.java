package model;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import db.DatabaseUtil;

@Repository
@Transactional
public class ReservationDao {
	@PersistenceContext
	private EntityManager entityManager;
	private EntityManagerFactory entityManagerFactory;
	private Reservation dao;
	
	
	public ReservationDao() {
		try{
	         entityManagerFactory = DatabaseUtil.getSessionFactory();
	     }
		catch (Exception e) {
			// TODO: logger
		}
		
	}

	public Reservation getDao() {
		return dao;
	}

	public void setDao(Reservation dao) {
		this.dao = dao;
	}
	
	public void init() {
		try {
			entityManagerFactory = DatabaseUtil.getSessionFactory();
		}
		catch (Exception e) {
			// TODO: logger
		}
		entityManager = entityManagerFactory.createEntityManager();
	}
	
	public void initTest() {
		try {
	        entityManagerFactory = DatabaseUtil.getTestSessionFactory();
	     }
		catch (Exception e) {
			// TODO: logger
		}
		entityManager = entityManagerFactory.createEntityManager();
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
			dao.setEmployee(dao.getEmployee());
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
		List<Reservation> reservations = entityManager.createNamedQuery("Reservation.findAll", Reservation.class)
				.getResultList();
		return reservations;
	}
	
	public List<Reservation> getBySerial(String serial) {
		List<Reservation> reservations = entityManager.createNamedQuery("Reservation.findBySerial", Reservation.class)
				.setParameter(1, serial)
				.getResultList();
		return reservations;
	}
	
	public List<Reservation> getByEquipmentId(String equipmentId) {
		List<Reservation> reservations = entityManager.createNamedQuery("Reservation.findByEquipmentId", Reservation.class)
				.setParameter(1, equipmentId)
				.getResultList();
		return reservations;
	}
	
	public List<Reservation> getByType(String type) {
		List<Reservation> reservations = entityManager.createNamedQuery("Reservation.findByType", Reservation.class)
				.setParameter(1, type)
				.getResultList();
		return reservations;
	}
	
	public List<Reservation> getOpen() {
		List<Reservation> reservations = entityManager.createNamedQuery("Reservation.findOpen", Reservation.class)
				.getResultList();
		return reservations;
	}
	
	public List<Reservation> getOpenByEmployeeId(String employeeId) {
		List<Reservation> reservations = entityManager.createNamedQuery("Reservation.findOpenByEmployeeId", Reservation.class)
				.setParameter(1, employeeId)
				.getResultList();
		return reservations;
	}
	
	public List<Reservation> getBySerialAndDate(String serial, Date start, Date end) {
		List<Reservation> reservations = entityManager.createNamedQuery("Reservation.findBySerialAndDate", Reservation.class)
				.setParameter(1, serial)
				.setParameter(2, end)
				.setParameter(3, start)
				.getResultList();
		return reservations;
	}

	public boolean serialHasOpenReservation(String serial) {
		List<Reservation> reservations = entityManager.createNamedQuery("Reservation.findOpenBySerial", Reservation.class)
				.setParameter(1, serial)
				.getResultList();
		if (reservations.size() > 0)
			return true;
		else
			return false;
	}
	
	public int getOpenReservationIdBySerial(String serial) {
		List<Reservation> reservations = entityManager.createNamedQuery("Reservation.findOpenBySerial", Reservation.class)
				.setParameter(1, serial)
				.getResultList();
		if (reservations.size() > 0)
			return reservations.get(0).getReservationId();
		else
			return 0;
	}
}
