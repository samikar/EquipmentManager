package fi.danfoss.equipmentmanager.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;


/**
 * Persistence class for the employee database table.
 * 
 */

@NamedNativeQueries({
	@NamedNativeQuery(
			name	=	"Employee.findAll", 
			query	=	"SELECT * FROM employee",
						resultClass=Employee.class
	),
	@NamedNativeQuery(
			name	=	"Employee.findByEmployeeId", 
			query	=	"SELECT * "+
						"FROM employee " +
						"WHERE employeeId = ?",
						resultClass=Employee.class
	)
	})

@Entity(name="Employee")
@Table (name="employee")
public class Employee implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int employeeKey;

	private String employeeId;

	private String name;

	public Employee() {
	}
	
	public Employee(String employeeId, String name) {
		this.employeeId = employeeId;
		this.name = name;
	}

	public int getEmployeeKey() {
		return this.employeeKey;
	}

	public void setEmployeeKey(int employeeKey) {
		this.employeeKey = employeeKey;
	}

	public String getEmployeeId() {
		return this.employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}