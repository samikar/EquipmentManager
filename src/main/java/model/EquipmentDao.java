package model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import db.DatabaseUtil;

public class EquipmentDao {
	private Equipment dao;
	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;

	public EquipmentDao() {
		try{
	         entityManagerFactory = DatabaseUtil.getSessionFactory();
	     }
		catch (Exception e) {
			// TODO: logger
		}
		
	}
	
	public Equipment getDao() {
		return dao;
	}

	public void setDao(Equipment dao) {
		this.dao = dao;
	}
	
	public void init(){
		entityManager = entityManagerFactory.createEntityManager();
	}
	
	public List<Equipment> getDaos(){
		entityManager.getTransaction().begin();
		Query query = entityManager.createNamedQuery("Equipment.findAll");
		//Query query = entityManager.createQuery("from Product c", Product.class);
		List<Equipment> result = query.getResultList(); 
		entityManager.getTransaction().commit();
		return result;
	}
	

	public int persist(Equipment dao){
		entityManager.getTransaction().begin();
		entityManager.persist(dao);
		entityManager.getTransaction().commit();
		return dao.getEquipmentId();
	}
	
	public void initialize(int daoNumber){
		dao = entityManager.find(Equipment.class, daoNumber);
		  if(dao == null)throw new IllegalStateException
		   ("Dao number ("+daoNumber+") not found");		
	}
	
	public void update(Equipment dao){
		//just checking that the dao really has is
		if(dao.getEquipmentId()>0){
			//get the actual entity from database to a dao-named attribute
			initialize(dao.getEquipmentId());
			//start database transaction
			entityManager.getTransaction().begin();
			dao.setName(dao.getName());
			
			dao.setEquipmenttype(dao.getEquipmenttype());
			dao.setStatus(dao.getStatus());			
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
	
	public List<Equipment> getAll() {
		List<Equipment> equipmentList = entityManager.createNamedQuery("Equipment.findAll", Equipment.class)
				.getResultList();
		return equipmentList;
	}
	
	public int getEquipmentIdBySerial(String serial) {
		List<Equipment> equipmentList = entityManager.createNamedQuery("Equipment.findBySerial", Equipment.class)
				.setParameter(1, serial).getResultList();
		if (equipmentList.size() > 0) {
			Equipment e = equipmentList.get(0);
			return e.getEquipmentId();
		}		
		else
			return 0;
	}
	
	public Equipment getBySerial(String serial) {
		List<Equipment> equipment = entityManager.createNamedQuery("Equipment.findBySerial", Equipment.class)
				.setParameter(1, serial)
				.getResultList();
		return equipment.get(0);
	}
	
	public List<Equipment> getByType(int typeCode) {
		List<Equipment> equipment = entityManager.createNamedQuery("Equipment.findByType", Equipment.class)
				.setParameter(1, typeCode)
				.getResultList();
		return equipment;
	}
	
	public List<Equipment> getEnabledByType(int typeCode) {
		List<Equipment> equipment = entityManager.createNamedQuery("Equipment.findEnabledByType", Equipment.class)
				.setParameter(1, typeCode)
				.getResultList();
		return equipment;
	}
	
	public List<Equipment> getOrderedByType() {
		List<Equipment> equipment = entityManager.createNamedQuery("Equipment.findAllOrderedByType", Equipment.class)
				.getResultList();
		return equipment;
	}
	
	public boolean serialExists(String serial) {
		List<Equipment> equipment = entityManager.createNamedQuery("Equipment.findBySerial", Equipment.class)
				.setParameter(1, serial)
				.getResultList();
		if (equipment.size() > 0)
			return true;
		else
			return false;
	}
	
	public Equipment getRandomAvailable() {
		List<Equipment> equipment = entityManager.createNamedQuery("Equipment.findRandomAvailable", Equipment.class)
				.getResultList();
		return equipment.get(0);
	}
}
