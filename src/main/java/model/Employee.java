package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * Persistence class for the employee database table.
 * 
 */
@Entity
@NamedNativeQueries({
	@NamedNativeQuery(
			name	=	"Employee.findAll", 
			query	=	"SELECT e FROM employee e",
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

public class Employee implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int employeeKey;

	private String employeeId;

	private String name;

	public Employee() {
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