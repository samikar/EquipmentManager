package model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the equipment database table.
 * 
 */
@Entity

@NamedNativeQueries({
	@NamedNativeQuery(
			name	=	"Equipment.findAll", 
			query	=	"SELECT * FROM Equipment",
						resultClass=Equipment.class
	),
	@NamedNativeQuery(
			name	=	"Equipment.getEquipmentBySerial", 
			query	=	"SELECT * "+
						"FROM equipment " +
						"WHERE equipment.serial = ?",
						resultClass=Equipment.class
	)
	})

public class Equipment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int equipmentId;

	private String name;
	
	private String serial;

	private int status;

	private String type;

	//bi-directional many-to-one association to Reservation
	//@OneToMany(mappedBy="equipment")
	//private List<Reservation> reservations;

	public Equipment() {
	}

	public int getEquipmentId() {
		return this.equipmentId;
	}

	public void setEquipmentId(int equipmentId) {
		this.equipmentId = equipmentId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getSerial() {
		return this.serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	/*
	public List<Reservation> getReservations() {
		return this.reservations;
	}

	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}
	 
	public Reservation addReservation(Reservation reservation) {
		getReservations().add(reservation);
		reservation.setEquipment(this);

		return reservation;
	}

	public Reservation removeReservation(Reservation reservation) {
		getReservations().remove(reservation);
		reservation.setEquipment(null);

		return reservation;
	}
	*/

}