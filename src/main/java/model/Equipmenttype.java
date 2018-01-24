package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the equipmenttype database table.
 * 
 */
@Entity
@NamedNativeQueries({
	@NamedNativeQuery(
			name	=	"Equipmenttype.findAll", 
			query	=	"SELECT * FROM Equipmenttype",
						resultClass=Equipmenttype.class
	),
	@NamedNativeQuery(
			name	=	"Equipmenttype.getEquipmenttypeByTypeCode", 
			query	=	"SELECT * "+
						"FROM equipmenttype " +
						"WHERE equipmenttype.equipmentTypeCode = ?",
						resultClass=Equipmenttype.class
	)
	})

public class Equipmenttype implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int equipmentTypeId;

	private int equipmentTypeCode;

	private String typeName;

	//bi-directional many-to-one association to Equipment
	@OneToMany(mappedBy="equipmenttype")
	private List<Equipment> equipments;

	public Equipmenttype() {
	}

	public int getEquipmentTypeId() {
		return this.equipmentTypeId;
	}

	public void setEquipmentTypeId(int equipmentTypeId) {
		this.equipmentTypeId = equipmentTypeId;
	}

	public int getEquipmentTypeCode() {
		return this.equipmentTypeCode;
	}

	public void setEquipmentTypeCode(int equipmentTypeCode) {
		this.equipmentTypeCode = equipmentTypeCode;
	}

	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public List<Equipment> getEquipments() {
		return this.equipments;
	}

	public void setEquipments(List<Equipment> equipments) {
		this.equipments = equipments;
	}

	public Equipment addEquipment(Equipment equipment) {
		getEquipments().add(equipment);
		equipment.setEquipmenttype(this);

		return equipment;
	}

	public Equipment removeEquipment(Equipment equipment) {
		getEquipments().remove(equipment);
		equipment.setEquipmenttype(null);

		return equipment;
	}

}