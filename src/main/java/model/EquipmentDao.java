package model;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.Table;

import java.util.List;

public class EquipmentDao {
private Equipment dao;

EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("EquipmentManager");
EntityManager em = entityManagerFactory.createEntityManager();
	
	public Equipment getDao() {
		return dao;
	}
	
	public void setDao(Equipment dao) {
		this.dao = dao;
	}
	
	private EntityManager entityManager;
	
	public void init(){
		entityManager = Persistence.createEntityManagerFactory("EquipmentManager").createEntityManager();
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
	
	public int getEquipmentIdBySerial(String serial) {
		List<Equipment> equipmentList = em.createNamedQuery("Equipment.getBySerial", Equipment.class)
				.setParameter(1, serial).getResultList();
		if (equipmentList.size() > 0) {
			Equipment e = equipmentList.get(0);
			return e.getEquipmentId();
		}		
		else
			return 0;
	}
	
	public List<Equipment> getEquipmentOrderedByType() {
		List<Equipment> equipment = entityManager.createNamedQuery("Equipment.getOrderedByType", Equipment.class)
				.getResultList();
		return equipment;
	}
}
