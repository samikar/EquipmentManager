package model;

import java.io.Serializable;
import java.util.List;

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
			name	=	"Equipment.findBySerial", 
			query	=	"SELECT * "+
						"FROM equipment " +
						"WHERE equipment.serial = ?",
						resultClass=Equipment.class
	),
	@NamedNativeQuery(
			name	=	"Equipment.findByType", 
			query	=	"SELECT * "+
						"FROM equipment " +
						"INNER JOIN equipmentType ON equipment.equipmentTypeId = equipmentType.equipmentTypeId " +
						"WHERE equipmentType.typeCode = ?",
						resultClass=Equipment.class
	),
	@NamedNativeQuery(
			name	=	"Equipment.findAllOrderedByType", 
			query	=	"SELECT * "+
						"FROM equipment " +
						"ORDER BY equipmentTypeId",
						resultClass=Equipment.class
	),@NamedNativeQuery(
			name	=	"Equipment.findRandomAvailable", 
			query	=	"SELECT * "+
						"FROM equipment " +
						"LEFT JOIN reservation ON reservation.equipmentId = equipment.equipmentId " +
						"WHERE reservation.date_take IS NULL AND reservation.date_return IS NULL " +
						"ORDER BY RAND() LIMIT 1",
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

	//bi-directional many-to-one association to Equipmenttype
	@ManyToOne
	@JoinColumn(name="equipmentTypeId")
	private Equipmenttype equipmenttype;

	/*
	//bi-directional one-to-one association to Reservation
	@OneToMany(mappedBy="equipment")
	private List<Reservation> reservations;
	 */
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

	public Equipmenttype getEquipmenttype() {
		return this.equipmenttype;
	}

	public void setEquipmenttype(Equipmenttype equipmenttype) {
		this.equipmenttype = equipmenttype;
	}

	/*
	public List<Reservation> getReservations() {
		return this.reservations;
	}
	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}
	*/
}