package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the reservation database table.
 * 
 */
@Entity

@NamedNativeQueries({
	@NamedNativeQuery(
			name	=	"Reservation.findAll", 
			query	=	"SELECT * FROM reservation",
            			resultClass=Equipmentreservation.class
	),
    @NamedNativeQuery(
            name    =   "Reservation.getReservationsByType",
            query   =   "SELECT * " +
                        "FROM reservation " +
                        "WHERE reservation.reservationType = ?",
                        resultClass=Equipmentreservation.class
    ),
    @NamedNativeQuery(
            name    =   "Reservation.getReservationsByEquipmentId",
            query   =   "SELECT * " +
                        "FROM reservation " +
                        "WHERE reservation.equipmentId = ?",
                        resultClass=Equipmentreservation.class
    )
    
    })

public class Equipmentreservation implements Serializable {
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

	private String employeeId_return;

	private String employeeId_take;

	private String equipmentId;

	private int reservationType;

	public Equipmentreservation() {
	}

	public int getreservationId() {
		return this.reservationId;
	}

	public void setreservationId(int reservationId) {
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

	public String getEmployeeId_return() {
		return this.employeeId_return;
	}

	public void setEmployeeId_return(String employeeId_return) {
		this.employeeId_return = employeeId_return;
	}

	public String getEmployeeId_take() {
		return this.employeeId_take;
	}

	public void setEmployeeId_take(String employeeId_take) {
		this.employeeId_take = employeeId_take;
	}

	public String getEquipmentId() {
		return this.equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public int getReservationType() {
		return this.reservationType;
	}

	public void setReservationType(int reservationType) {
		this.reservationType = reservationType;
	}

}