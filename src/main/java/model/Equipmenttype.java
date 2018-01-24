package model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the equipmenttype database table.
 * 
 */
@Entity
@NamedQuery(name="Equipmenttype.findAll", query="SELECT e FROM Equipmenttype e")
public class Equipmenttype implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int equipmentTypeId;

	private int equipmentTypeCode;

	private String typeName;

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

}