package fi.danfoss.equipmentmanager.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.danfoss.equipmentmanager.controller.ChartController;
import fi.danfoss.equipmentmanager.db.DatabaseUtil;

@Repository
@Transactional
public class EquipmenttypeDao {
	final static Logger logger = Logger.getLogger(ChartController.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	private Equipmenttype dao;
	
	public EquipmenttypeDao() {
	}

	public Equipmenttype getDao() {
		return dao;
	}
	
	public void setDao(Equipmenttype dao) {
		this.dao = dao;
	}
	
	public void init() {
		entityManager = DatabaseUtil.getEntityManager();
	}
	
	public void refresh() {
		this.destroy();
		this.init();
	}
		
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
	
	/**
	 * Returns all EquipmentTypes
	 * 
	 * @return					EquipmentTypes in a List
	 */
	public List<Equipmenttype> getAll() {
		List<Equipmenttype> result = entityManager.createNamedQuery("Equipmenttype.findAll", Equipmenttype.class)
				.getResultList();
		return result;
	}
	
	/**
	 * Returns equipmentTypeId with given typeCode
	 * 
	 * @param typeCode			TypeCode to search
	 * @return					EquipmentTypeId as an int
	 */
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
	
	/**
	 * Returns EquipmentType with given typeCode
	 * 
	 * @param typeCode			TypeCode to search
	 * @return					Found Equipmenttype, null if not found
	 */
	public Equipmenttype getByTypeCode(int typeCode) {
		List<Equipmenttype> equipmentTypeList = entityManager
				.createNamedQuery("Equipmenttype.findByTypeCode", Equipmenttype.class)
				.setParameter(1, typeCode).getResultList();
		if (equipmentTypeList.size() == 0)
			return null;
		else
			return equipmentTypeList.get(0);
	}
	
	/**
	 * Checks if typeCode already exists
	 * 
	 * @param typeCode			TypeCode to search
	 * @return					True if found, false if not
	 */
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
	
	/**
	 * Checks if equipmentTypeId exists
	 * 
	 * @param equipmentTypeId	EquipmentTypeId to search 
	 * @return					True if found, false if not
	 */
	public boolean equipmentTypeIdExists(int equipmentTypeId) {
		List<Equipmenttype> equipmentTypeList = entityManager
				.createNamedQuery("Equipmenttype.findByEquipmentTypeId", Equipmenttype.class)
				.setParameter(1, equipmentTypeId).getResultList();
		if (equipmentTypeList.size() > 0) {
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Returns all Equipmenttypes which have enabled Equipment
	 * 
	 * @return					Equipmenttypes in a List
	 */
	public List<Equipmenttype> getEquipmentTypesWithEquipment() {
		entityManager.getTransaction().begin();
		List<Equipmenttype> result = entityManager.createNamedQuery("Equipmenttype.findEquipmenttypesWithEquipment", Equipmenttype.class)
				.getResultList();
		entityManager.getTransaction().commit();
		return result;
	}
}
