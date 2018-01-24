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
	@SequenceGenerator(name="EQUIPMENT_EQUIPMENTID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EQUIPMENT_EQUIPMENTID_GENERATOR")
	private int equipmentId;

	private String name;

	private String serial;

	private int status;

	//bi-directional many-to-one association to Equipmenttype
	@ManyToOne
	@JoinColumn(name="equipmentTypeCode")
	private Equipmenttype equipmenttype;

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

}