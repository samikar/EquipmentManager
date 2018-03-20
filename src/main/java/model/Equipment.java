package model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;


/**
 * Persistence class for the equipment database table.
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
						"WHERE equipmentType.typeCode = ?" ,
						resultClass=Equipment.class
	),
	@NamedNativeQuery(
			name	=	"Equipment.findEnabledByType", 
			query	=	"SELECT * "+
						"FROM equipment " +
						"INNER JOIN equipmentType ON equipment.equipmentTypeId = equipmentType.equipmentTypeId " +
						"WHERE equipmentType.typeCode = ? AND " +
						"equipment.status = 1",
						resultClass=Equipment.class
	),
	@NamedNativeQuery(
			name	=	"Equipment.findAllOrderedByTypeName", 
			query	=	"SELECT * " +
						"FROM equipment " +
						"INNER JOIN equipmentType ON equipment.equipmentTypeId = equipmentType.equipmentTypeId " +
						"WHERE equipment.status = 1 " +
						"ORDER BY equipmenttype.typeName",
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
	
	public Equipment(String name, String serial, int status, Equipmenttype equipmenttype) {
		this.name = name;
		this.serial = serial;
		this.status = status;
		this.equipmenttype = equipmenttype;
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