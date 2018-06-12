package fi.danfoss.equipmentmanager.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fi.danfoss.equipmentmanager.db.ADHandler;
import fi.danfoss.equipmentmanager.db.DatabaseUtil;

@Repository
@Transactional
public class EmployeeDao {

	final static Logger logger = Logger.getLogger(EmployeeDao.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	private Employee dao;

	public EmployeeDao() {
	}

	public void setEntityManager(EntityManager em) {
		entityManager = em;
	}

	public Employee getDao() {
		return dao;
	}

	public void setDao(Employee dao) {
		this.dao = dao;
	}

	public void init() {
		entityManager = DatabaseUtil.getEntityManager();
	}

	public void refresh() {
		this.destroy();
		this.init();
	}

	public List<Employee> getDaos() {
		entityManager.getTransaction().begin();
		Query query = entityManager.createNamedQuery("Employee.findAll");
		// Query query = entityManager.createQuery("from Product c", Product.class);
		List<Employee> result = query.getResultList();
		entityManager.getTransaction().commit();
		return result;
	}

	public int persist(Employee dao) {
		entityManager.getTransaction().begin();
		entityManager.persist(dao);
		entityManager.getTransaction().commit();
		return dao.getEmployeeKey();
	}

	public void initialize(int daoNumber) {
		dao = entityManager.find(Employee.class, daoNumber);
		if (dao == null)
			throw new IllegalStateException("Dao number (" + daoNumber + ") not found");
	}

	public void update(Employee dao) {
		// just checking that the dao really has is
		if (dao.getEmployeeKey() > 0) {
			// get the actual entity from database to a dao-named attribute
			initialize(dao.getEmployeeKey());
			// start database transaction
			entityManager.getTransaction().begin();
			dao.setEmployeeId(dao.getEmployeeId());
			dao.setName(dao.getName());
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
	/**
	 * Returns all Employees in DB
	 * 
	 * @return					Employees in a List
	 */

	public List<Employee> getAll() {
		List<Employee> reservations = entityManager.createNamedQuery("Employee.findAll", Employee.class)
				.getResultList();

		return reservations;
	}

	/**
	 * Returns Employee with employeeId
	 *
	 * @param employeeId		EmployeeId to search
	 * @return					Found Employee, null if not found
	 */
	public Employee getEmployeeByEmployeeId(String employeeId) {
		List<Employee> employeeList = entityManager.createNamedQuery("Employee.findByEmployeeId", Employee.class)
				.setParameter(1, employeeId).getResultList();

		if (employeeList.size() > 0) {
			return employeeList.get(0);
		} else
			return null;
	}

	/**
	 * Returns employeeKey (Primary Key) from DB with given employeeId 
	 * 
	 * @param employeeId		EmployeeId to search
	 * @return					EmployeeKey as an int
	 */
	public int getEmployeeKeyByEmployeeId(String employeeId) {
		List<Employee> employeeList = entityManager.createNamedQuery("Employee.findByEmployeeId", Employee.class)
				.setParameter(1, employeeId).getResultList();
		if (employeeList.size() > 0) {
			Employee e = employeeList.get(0);
			return e.getEmployeeKey();
		} else
			return 0;
	}

	/**
	 * Checks if Employee already exists in DB or AD
	 * 
	 * @param employeeId		EmployeeId to search
	 * @return					True if found, false if not
	 */
	public boolean employeeExists(String employeeId) {
		if (employeeInDB(employeeId))
			return true;
		else if (employeeInAD(employeeId))
			return true;
		else
			return false;
	}

	/**
	 * Checks if Employee exists in DB 
	 * 
	 * @param employeeId		EmployeeId to search
	 * @return					True if found, false if not
	 */
	public boolean employeeInDB(String employeeId) {
		List<Employee> employeeList = entityManager.createNamedQuery("Employee.findByEmployeeId", Employee.class)
				.setParameter(1, employeeId).getResultList();
		if (employeeList.size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * Checks if Employee exists in AD
	 * 
	 * @param employeeId		EmployeeId to search
	 * @return					True if found, false if not
	 */
	public boolean employeeInAD(String employeeId) {
		ADHandler handler = new ADHandler();
		handler.init();
		String name = handler.findEmployeeName(employeeId);
		handler.close();
		dao = new Employee();
		if (name.length() > 0) {
			// Adds found employee to DB
			dao.setEmployeeId(employeeId);
			dao.setName(name);
			persist(dao);
			System.out.println("Employee added to DB");
			return true;
		} else
			return false;
	}
}