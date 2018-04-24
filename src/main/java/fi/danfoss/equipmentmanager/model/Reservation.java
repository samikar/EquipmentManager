package fi.danfoss.equipmentmanager.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * Persistence class for the reservation database table.
 * 
 */
@Entity(name="Reservation")
@Table (name="reservation")
@NamedNativeQueries({
	@NamedNativeQuery(
			name	=	"Reservation.findAll", 
			query	=	"SELECT * FROM reservation",
            			resultClass=Reservation.class
	),
	@NamedNativeQuery(
			name    =   "Reservation.findBySerial",
	        query   =   "SELECT * " +
	                    "FROM reservation " +
	                    "INNER JOIN equipment ON reservation.equipmentId = equipment.equipmentId " +
	                    "WHERE equipment.serial = ?",
	                    resultClass=Reservation.class
	    ),
    @NamedNativeQuery(
            name    =   "Reservation.findByType",
            query   =   "SELECT * " +
                        "FROM reservation " +
                        "WHERE reservation.reservationType = ?",
                        resultClass=Reservation.class
    ),
    @NamedNativeQuery(
            name    =   "Reservation.findByEquipmentId",
            query   =   "SELECT * " +
                        "FROM reservation " +
                        "WHERE reservation.equipmentId = ?",
                        resultClass=Reservation.class
    ),
    @NamedNativeQuery(
            name    =   "Reservation.findOpenByEmployeeId",
            query   =   "SELECT * " +
                        "FROM reservation " +
                        "INNER JOIN employee ON reservation.employeeKey = employee.employeeKey " + 
                        "WHERE employee.employeeId = ? AND reservation.date_return IS NULL",
                        resultClass=Reservation.class
    ),
    @NamedNativeQuery(
            name    =   "Reservation.findOpen",
            query   =   "SELECT * " +
                        "FROM reservation " +
                        "WHERE date_return IS NULL",
                        resultClass=Reservation.class
    ),
    @NamedNativeQuery(
            name    =   "Reservation.findOpenBySerial",
            query   =   "SELECT * " +
                        "FROM reservation " +
                        "INNER JOIN equipment ON reservation.equipmentId = equipment.equipmentId " + 
                        "WHERE reservation.date_return IS null AND " +
                        "equipment.serial = ?",
                        resultClass=Reservation.class                        
    ),
    @NamedNativeQuery(
            name    =   "Reservation.findBySerialAndDate",
            query   =   "SELECT * " +
                        "FROM reservation " +
                        "INNER JOIN equipment ON reservation.equipmentId = equipment.equipmentId " + 
                        "WHERE equipment.serial = ? AND " +			// serial
                        "reservation.date_take < ? AND " + 			// end 
                        "(reservation.date_return > ? OR " +		// begin
                        "reservation.date_return IS NULL) AND " +	// no return date
                        "equipment.status = 1",						// only look for active equipment
                        resultClass=Reservation.class                        
    )
    
    })

public class Reservation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int reservationId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_return")
	private Date dateReturn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_take")
	private Date dateTake;

	//private String employeeKey;

	private int reservationType;

	//bi-directional one-to-one association to Equipment
	@ManyToOne
	@JoinColumn(name="equipmentId")
	private Equipment equipment;
	
	//bi-directional many-to-one association to Employee
	@ManyToOne
	@JoinColumn(name="employeeKey")
	private Employee employee;

	public Reservation() {
	}

	public int getReservationId() {
		return this.reservationId;
	}

	public void setReservationId(int reservationId) {
		this.reservationId = reservationId;
	}

	public Date getDateReturn() {
		return this.dateReturn;
	}

	public void setDateReturn(Date dateReturn) {
		this.dateReturn = dateReturn;
	}

	public Date getDateTake() {
		return this.dateTake;
	}

	public void setDateTake(Date dateTake) {
		this.dateTake = dateTake;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public int getReservationType() {
		return this.reservationType;
	}

	public void setReservationType(int reservationType) {
		this.reservationType = reservationType;
	}

	public Equipment getEquipment() {
		return this.equipment;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}
}