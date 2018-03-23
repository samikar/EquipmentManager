package model;

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
public class EquipmenttypeDao {
	private String url;
	private String user;
	private String password;
	private String driver;
	
	@PersistenceContext
	private EntityManager entityManager;
	private EntityManagerFactory entityManagerFactory;
	private Equipmenttype dao;
	
	public EquipmenttypeDao() {
	}

	public Equipmenttype getDao() {
		return dao;
	}
	
	public void setDao(Equipmenttype dao) {
		this.dao = dao;
	}
	
	public void init(){
		DatabaseUtil.setProperties(url, user, password, driver);
		try {
			entityManagerFactory = DatabaseUtil.getSessionFactory();
		}
		catch (Exception e) {
			// TODO: logger
		}
		entityManager = entityManagerFactory.createEntityManager();
	}
	
	public void setProperties(String url, String user, String password, String driver) {
		this.url = url;
		this.user = user;
		this.password = password;
		this.driver = driver;
	}
	
//	public void initTest() {
//		try {
//	        entityManagerFactory = DatabaseUtil.getTestSessionFactory();
//	     }
//		catch (Exception e) {
//			// TODO: logger
//		}
//		entityManager = entityManagerFactory.createEntityManager();
//	}
	
	public List<Equipmenttype> getDaos(){
		entityManager.getTransaction().begin();
		Query query = entityManager.createNamedQuery("Equipmenttype.findAll");
		//Query query = entityManager.createQuery("from Product c", Product.class);
		List<Equipmenttype> result = query.getResultList(); 
		entityManager.getTransaction().commit();
		return result;
	}
	
	public int persist(Equipmenttype dao){
		entityManager.getTransaction().begin();
		entityManager.persist(dao);
		entityManager.getTransaction().commit();
		return dao.getEquipmentTypeId();
	}
	
	public void initialize(int daoNumber){
		dao = entityManager.find(Equipmenttype.class, daoNumber);
		  if(dao == null)throw new IllegalStateException
		   ("Dao number ("+daoNumber+") not found");		
	}
	
	public void update(Equipmenttype dao){
		//just checking that the dao really has is
		if(dao.getEquipmentTypeId()>0){
			//get the actual entity from database to a dao-named attribute
			initialize(dao.getEquipmentTypeId());
			//start database transaction
			entityManager.getTransaction().begin();
			dao.setTypeCode(dao.getTypeCode());
			dao.setTypeName(dao.getTypeName());			
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
	
	public List<Equipmenttype> getAll() {
		List<Equipmenttype> result = entityManager.createNamedQuery("Equipmenttype.findAll", Equipmenttype.class)
				.getResultList();
		return result;
	}
	
	public int getEquipmentTypeIdByTypeCode(int typeCode) {
		List<Equipmenttype> equipmentTypeList = entityManager
				.createNamedQuery("Equipmenttype.findByTypeCode", Equipmenttype.class)
				.setParameter(1, typeCode).getResultList();
		if (equipmentTypeList.size() > 0) {
			Equipmenttype e = equipmentTypeList.get(0);
			return e.getEquipmentTypeId();
		}
		else
			return 0;
	}
	
	public boolean typeCodeExists(int typeCode) {
		List<Equipmenttype> equipmentTypeList = entityManager
				.createNamedQuery("Equipmenttype.findByTypeCode", Equipmenttype.class)
				.setParameter(1, typeCode).getResultList();
		if (equipmentTypeList.size() > 0) {
			return true;
		}
		else
			return false;
	}
	
	public List<Equipmenttype> getEquipmentTypesWithEquipment() {
		List<Equipmenttype> result = entityManager.createNamedQuery("Equipmenttype.findEquipmenttypesWithEquipment", Equipmenttype.class)
				.getResultList();
		return result;
	}
}
