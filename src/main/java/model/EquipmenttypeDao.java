package model;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.Table;

import java.util.List;

public class EquipmenttypeDao {
	private Equipmenttype dao;

	EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("EquipmenttypeManager");
	EntityManager em = entityManagerFactory.createEntityManager();

	public Equipmenttype getDao() {
		return dao;
	}

	public void setDao(Equipmenttype dao) {
		this.dao = dao;
	}

	private EntityManager entityManager;

	public void init() {
		entityManager = Persistence.createEntityManagerFactory("EquipmenttypeManager").createEntityManager();
	}

	public List<Equipmenttype> getDaos() {
		entityManager.getTransaction().begin();
		Query query = entityManager.createNamedQuery("Equipmenttype.findAll");
		// Query query = entityManager.createQuery("from Product c", Product.class);
		List<Equipmenttype> result = query.getResultList();
		entityManager.getTransaction().commit();
		return result;
	}

	public int persist(Equipmenttype dao) {
		entityManager.getTransaction().begin();
		entityManager.persist(dao);
		entityManager.getTransaction().commit();
		return dao.getEquipmentTypeId();
	}

	public void initialize(int daoNumber) {
		dao = entityManager.find(Equipmenttype.class, daoNumber);
		if (dao == null)
			throw new IllegalStateException("Dao number (" + daoNumber + ") not found");
	}

	public void update(Equipmenttype dao) {
		// just checking that the dao really has is
		if (dao.getEquipmentTypeId() > 0) {
			// get the actual entity from database to a dao-named attribute
			initialize(dao.getEquipmentTypeId());
			// start database transaction
			entityManager.getTransaction().begin();
			dao.setTypeName(dao.getTypeName());
			entityManager.merge(dao);
			entityManager.getTransaction().commit();
		}
	}

	public void delete() {
		entityManager.getTransaction().begin();
		entityManager.remove(dao);
		entityManager.getTransaction().commit();
	}

	public void destroy() {
		entityManager.close();
	}

	public int getEquipmentTypeByTypeCode(int typeCode) {
		List<Equipmenttype> equipmentTypeList = em
				.createNamedQuery("Equipmenttype.getEquipmentTypeByTypeCode", Equipmenttype.class)
				.setParameter(1, typeCode).getResultList();
		if (equipmentTypeList.size() > 0) {
			Equipmenttype e = equipmentTypeList.get(0);
			return e.getEquipmentTypeId();
		}

		else
			return 0;
	}
}
