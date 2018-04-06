package controller;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import model.Equipment;
import model.EquipmentDao;
import model.Equipmenttype;
import model.EquipmenttypeDao;
import utils.EquipmentDataReader;
import utils.PropertyUtils;

@RestController
public class ConfigurationController {
	// Database properties
	private static Properties properties = PropertyUtils.loadProperties();
	private String DBurl = properties.getProperty("DBurl");
	private String DBuser = properties.getProperty("DBuser");
	private String DBpassword = properties.getProperty("DBpassword");
	private String DBdriver = properties.getProperty("DBdriver");
	
	private EquipmentDao edao;
	private EquipmenttypeDao etdao;
	
	@RequestMapping(value = "/rest/uploadEquipmentFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> uploadEquipmentFile(@RequestParam("file") MultipartFile file) throws IOException {
		EquipmentDataReader equipmentDataReader = new EquipmentDataReader();
		equipmentDataReader.setProperties(DBurl, DBuser, DBpassword, DBdriver);
		return equipmentDataReader.verifyEquipmentFile(file);
	}

	@RequestMapping(value = "/rest/uploadTypeFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> uploadTypeFile(@RequestParam("file") MultipartFile file) throws IOException {
		EquipmentDataReader equipmentDataReader = new EquipmentDataReader();
		equipmentDataReader.setProperties(DBurl, DBuser, DBpassword, DBdriver);
		return equipmentDataReader.verifyEquipmentTypeFile(file);
	}

	@RequestMapping("/rest/getEquipment")
	public List<Equipment> getEquipment() {
		edao = new EquipmentDao();
		edao.setProperties(DBurl, DBuser, DBpassword, DBdriver);
		edao.init();
		List<Equipment> result = edao.getAll();
		edao.destroy();
		return result;
	}
	
	@RequestMapping("/rest/getEquipmentTypes")
	public List<Equipmenttype> getEquipmentTypes() {
		etdao = new EquipmenttypeDao();
		etdao.setProperties(DBurl, DBuser, DBpassword, DBdriver);
		etdao.init();
		List<Equipmenttype> result = etdao.getAll();
		etdao.destroy();
		return result;
	}
	
	@RequestMapping("/rest/enableEquipment")
	public Equipment enableEquipment(@RequestParam(value = "equipmentId") String equipmentId) {
		edao = new EquipmentDao();
		edao.setProperties(DBurl, DBuser, DBpassword, DBdriver);
		edao.init();
		edao.initialize(Integer.parseInt(equipmentId));
		Equipment eq = edao.getDao();
		eq.setStatus(1);
		edao.persist(eq);
		edao.destroy();
		return eq;
	}

	@RequestMapping("/rest/disableEquipment")
	public Equipment disableEquipment(@RequestParam(value = "equipmentId") String equipmentId) {
		edao = new EquipmentDao();
		edao.setProperties(DBurl, DBuser, DBpassword, DBdriver);
		edao.init();
		edao.initialize(Integer.parseInt(equipmentId));
		Equipment eq = edao.getDao();
		eq.setStatus(0);
		edao.persist(eq);
		edao.destroy();
		return eq;
	}
	
	@RequestMapping("/rest/insertEquipment")
	public Equipment insertEquipment(@RequestParam(value = "name") String name,						
									 @RequestParam(value = "serial") String serial,
									 @RequestParam(value = "equipmentTypeId") String equipmentTypeId) {
		edao = new EquipmentDao();
		edao.setProperties(DBurl, DBuser, DBpassword, DBdriver);
		edao.init();
		
		if (name.length() == 0) {
			edao.destroy();
			throw new IllegalArgumentException("Field \"name\" must not be empty!");
		}
		else if (serial.length() == 0) {
			edao.destroy();
			throw new IllegalArgumentException("Field \"serial\" must not be empty!");
		}
		else if (equipmentTypeId.length() == 0) {
			edao.destroy();
			throw new IllegalArgumentException("Field \"Equipment type\" must not be empty!");
		}
		else if (edao.serialExists(serial)) {
			edao.destroy();
			throw new IllegalArgumentException("Equipment with serial number " + serial + " already exists!");
		}
		else {
			etdao = new EquipmenttypeDao();
			etdao.setProperties(DBurl, DBuser, DBpassword, DBdriver);
			etdao.init();
			etdao.initialize(Integer.parseInt(equipmentTypeId));
			Equipment newEquipment = new Equipment();
			Equipmenttype etype = etdao.getDao();
			
			newEquipment.setName(name);
			newEquipment.setSerial(serial);
			newEquipment.setEquipmenttype(etype);
			newEquipment.setStatus(1);
			edao.persist(newEquipment);
			
			edao.destroy();
			etdao.destroy();
					
			return newEquipment;
		}
	}
	
	@RequestMapping("/rest/insertType")
	public Equipmenttype insertType(@RequestParam(value = "typeName") String typeName,						
									@RequestParam(value = "typeCode") String typeCode) {
		etdao = new EquipmenttypeDao();
		etdao.setProperties(DBurl, DBuser, DBpassword, DBdriver);
		etdao.init();
		
		if (!typeCode.matches("\\d+")) {
			etdao.destroy();
			throw new IllegalArgumentException("Typecode must be an integer value!");	
		}
		else if (typeName.length() == 0) {
			etdao.destroy();
			throw new IllegalArgumentException("Field \"Type Name\" must not be empty!");
		}
		else if (etdao.typeCodeExists(Integer.parseInt(typeCode))) {
			etdao.destroy();
			throw new IllegalArgumentException("Equipment type with Typecode " + typeCode + " already exists!");
		}
		else {

			Equipmenttype newType = new Equipmenttype();	
			newType.setTypeName(typeName);
			newType.setTypeCode(Integer.parseInt(typeCode));
			etdao.persist(newType);
			etdao.destroy();
					
			return newType;
		}
	}

	@RequestMapping("/rest/updateEquipment")
	public Equipment updateEquipment(@RequestParam(value = "equipmentId") String equipmentId,
									 @RequestParam(value = "name") String name, 
									 @RequestParam(value = "serial") String serial,
									 @RequestParam(value = "equipmentTypeId") String equipmentTypeId) {
		edao = new EquipmentDao();
		edao.setProperties(DBurl, DBuser, DBpassword, DBdriver);
		edao.init();

		if (name.length() == 0) {
			edao.destroy();
			throw new IllegalArgumentException("Field \"name\" must not be empty!");
		}
		else if (serial.length() == 0) {
			edao.destroy();
			throw new IllegalArgumentException("Field \"serial\" must not be empty!");
		}
		else if (equipmentTypeId.length() == 0) {
			edao.destroy();
			throw new IllegalArgumentException("Field \"Equipment type\" must not be empty!");
		}
		else if (edao.serialExists(serial) && Integer.parseInt(equipmentId) != (edao.getBySerial(serial).getEquipmentId())) {
				edao.destroy();
				throw new IllegalArgumentException("Equipment with serial number " + serial + " already exists!");
		} else {
			edao.initialize(Integer.parseInt(equipmentId));
			Equipment eq = edao.getDao();

			eq.setName(name);
			eq.setSerial(serial);
			if (Integer.parseInt(equipmentTypeId) > 0) {
				etdao = new EquipmenttypeDao();
				etdao.setProperties(DBurl, DBuser, DBpassword, DBdriver);
				etdao.init();
				etdao.initialize(Integer.parseInt(equipmentTypeId));
				Equipmenttype etype = etdao.getDao();
				eq.setEquipmenttype(etype);
				etdao.destroy();
			}
			edao.update(eq);
			edao.destroy();

			return eq;
		}
	}
	
	@RequestMapping("/rest/updateType")
	public Equipmenttype updateType(@RequestParam(value = "equipmentTypeId") String equipmentTypeId, 
 									@RequestParam(value = "typeName") String typeName,						
									@RequestParam(value = "typeCode") String typeCode) {
		
		etdao = new EquipmenttypeDao();
		etdao.setProperties(DBurl, DBuser, DBpassword, DBdriver);
		etdao.init();
		
		if (!typeCode.matches("\\d+")) {
			etdao.destroy();
			throw new IllegalArgumentException("Typecode must be an integer value!");	
		}
		else if (typeName.length() == 0) {
			etdao.destroy();
			throw new IllegalArgumentException("Field \"Type Name\" must not be empty!");
		}
		else if (etdao.typeCodeExists(Integer.parseInt(typeCode)) && Integer.parseInt(equipmentTypeId) != etdao.getByTypeCode(Integer.parseInt(typeCode)).getEquipmentTypeId()) {
			etdao.destroy();
			throw new IllegalArgumentException("Equipment type with Type Code " + typeCode + " already exists!");
		}
		else {
			etdao.initialize(Integer.parseInt(equipmentTypeId));
			Equipmenttype etype = etdao.getDao();
			
			etype.setTypeName(typeName);
			etype.setTypeCode(Integer.parseInt(typeCode));
			
			etdao.update(etype);
			etdao.destroy();
			
			return etype;
		}
	}
	
	@RequestMapping("/rest/deleteEquipment")
	public Equipment deleteEquipment(@RequestParam(value = "equipmentId") String equipmentId) {
		if (!equipmentId.matches("\\d+")) {
			throw new IllegalArgumentException("EquipmentId must be an integer value!");	
		}
		else if (equipmentId.length() == 0) {
			throw new IllegalArgumentException("Field \"EquipmentId\" must not be empty!");
		}
		
		edao = new EquipmentDao();
		edao.setProperties(DBurl, DBuser, DBpassword, DBdriver);
		edao.init();
		if (!edao.equipmentIdExists(equipmentId)) {
			edao.destroy();
			throw new IllegalArgumentException("Equipment with \"EquipmentId\" not found!");
		}
		edao.initialize(Integer.parseInt(equipmentId));
		Equipment eq = edao.getDao();
		edao.delete();
		edao.destroy();
		return eq;
	}
	
	@RequestMapping("/rest/deleteType")
	public Equipmenttype deleteType(@RequestParam(value = "equipmentTypeId") String equipmentTypeId) {
		if (!equipmentTypeId.matches("\\d+")) {
			throw new IllegalArgumentException("EquipmentTypeId must be an integer value!");	
		}
		else if (equipmentTypeId.length() == 0) {
			throw new IllegalArgumentException("Field \"EquipmentTypeId\" must not be empty!");
		}
		
		etdao = new EquipmenttypeDao();
		etdao.setProperties(DBurl, DBuser, DBpassword, DBdriver);
		etdao.init();
		if (!etdao.equipmentTypeIdExists(Integer.parseInt(equipmentTypeId))) {
			etdao.destroy();
			throw new IllegalArgumentException("Equipment Type with EquipmentTypeId " + equipmentTypeId + " not found!");
		}
		
		etdao.initialize(Integer.parseInt(equipmentTypeId));
		Equipmenttype etype = etdao.getDao();
		
		edao = new EquipmentDao();
		edao.setProperties(DBurl, DBuser, DBpassword, DBdriver);
		edao.init();
		List<Equipment> equipmentList = edao.getByTypeCode(etype.getTypeCode());
		if (equipmentList.size() > 0) {
			edao.destroy();
			etdao.destroy();
			throw new IllegalArgumentException("Cannot delete Type while it has Equipment attached to it!");
		}
		else {
			etdao.delete();
			etdao.destroy();
			return etype;
		}
	}

	public void setProperties(String DBurl, String DBuser, String DBpassword, String DBdriver) {
		this.DBurl = DBurl;
		this.DBuser = DBuser;
		this.DBpassword = DBpassword;
		this.DBdriver = DBdriver;
	}
	
	@ExceptionHandler
	void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}
}