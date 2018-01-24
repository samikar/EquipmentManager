package model;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
            			resultClass=Reservation.class
	),
    @NamedNativeQuery(
            name    =   "Reservation.getReservationsByType",
            query   =   "SELECT * " +
                        "FROM reservation " +
                        "WHERE reservation.reservationType = ?",
                        resultClass=Reservation.class
    ),
    @NamedNativeQuery(
            name    =   "Reservation.getReservationsByEquipmentId",
            query   =   "SELECT * " +
                        "FROM reservation " +
                        "WHERE reservation.equipmentId = ?",
                        resultClass=Reservation.class
    ),
    @NamedNativeQuery(
            name    =   "Reservation.getReservationsByEmployeeId",
            query   =   "SELECT * " +
                        "FROM reservation " +
                        "WHERE reservation.employeeId = ? AND reservation.date_return IS NULL",
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

	private String employeeId;

	private int reservationType;

	//bi-directional one-to-one association to Equipment
	@ManyToOne
	@JoinColumn(name="equipmentId")
	private Equipment equipment;

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

	public String getEmployeeId() {
		return this.employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public int getReservationType() {
		return this.reservationType;
	}

	public void setReservationType(int reservationType) {
		this.reservationType = reservationType;
	}

	@JsonIgnore
	public Equipment getEquipment() {
		return this.equipment;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

}