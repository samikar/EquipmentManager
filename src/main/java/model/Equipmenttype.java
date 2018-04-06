package model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;


/**
 * Persistence class for the equipmenttype database table.
 * 
 */
@Entity
@NamedNativeQueries({
	@NamedNativeQuery(
			name	=	"Equipmenttype.findAll", 
			query	=	"SELECT * FROM Equipmenttype " +
						"ORDER BY TypeName ASC",
						resultClass=Equipmenttype.class
	),
	@NamedNativeQuery(
			name	=	"Equipmenttype.findByTypeCode", 
			query	=	"SELECT * "+
						"FROM equipmenttype " +
						"WHERE equipmenttype.TypeCode = ?",
						resultClass=Equipmenttype.class
	),
	@NamedNativeQuery(
			name	=	"Equipmenttype.findByEquipmentTypeId", 
			query	=	"SELECT * "+
						"FROM equipmenttype " +
						"WHERE equipmenttype.equipmentTypeId = ?",
						resultClass=Equipmenttype.class					
	),@NamedNativeQuery(
			name	=	"Equipmenttype.findEquipmenttypesWithEquipment", 
			query	=	"SELECT DISTINCT equipmenttype.equipmentTypeId, equipmenttype.typeCode, equipmenttype.typeName " +
						"FROM equipmenttype " +
						"LEFT JOIN equipment ON equipmenttype.equipmentTypeId = equipment.equipmentTypeId " +
						"WHERE equipmenttype.equipmentTypeId = equipment.equipmentTypeId AND " +
						"equipment.status = 1 " +
						"ORDER BY equipmenttype.typeName",
						resultClass=Equipmenttype.class
	)
	})

public class Equipmenttype implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int equipmentTypeId;

	private int typeCode;

	private String typeName;
	/*
	//bi-directional many-to-one association to Equipment
	@OneToMany(mappedBy="equipmenttype")
	private List<Equipment> equipments;
	 */
	public Equipmenttype() {
	}
	
	public Equipmenttype(int typeCode, String typeName) {
		this.typeCode = typeCode;
		this.typeName = typeName;
	}

	public int getEquipmentTypeId() {
		return this.equipmentTypeId;
	}

	public void setEquipmentTypeId(int equipmentTypeId) {
		this.equipmentTypeId = equipmentTypeId;
	}

	public int getTypeCode() {
		return this.typeCode;
	}

	public void setTypeCode(int typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}