package fi.danfoss.equipmentmanager.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fi.danfoss.equipmentmanager.model.Equipment;
import fi.danfoss.equipmentmanager.model.EquipmentDao;
import fi.danfoss.equipmentmanager.model.Equipmenttype;
import fi.danfoss.equipmentmanager.model.EquipmenttypeDao;
import fi.danfoss.equipmentmanager.utils.EquipmentDataReader;

@RestController
public class ConfigurationController {	
	private EquipmentDao edao;
	private EquipmenttypeDao etdao;
	final static Logger logger = Logger.getLogger(ChartController.class);
	
	/**
	 * REST method to upload Equipment file
	 * 
	 * @param file				Uploaded Equipment file
	 * @return					HTTP response
	 * @throws IOException
	 */
	@RequestMapping(value = "/rest/uploadEquipmentFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> uploadEquipmentFile(@RequestParam("file") MultipartFile file) throws IOException {
		EquipmentDataReader equipmentDataReader = new EquipmentDataReader();
		logger.info("Equipment file uploaded.");
		return equipmentDataReader.verifyEquipmentFile(file);
	}

	/**
	 * REST method to upload EquipmentType file
	 * 
	 * @param file				Uploaded EquipmentType file
	 * @return					HTTP response
	 * @throws IOException
	 */
	@RequestMapping(value = "/rest/uploadTypeFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> uploadTypeFile(@RequestParam("file") MultipartFile file) throws IOException {
		EquipmentDataReader equipmentDataReader = new EquipmentDataReader();
		logger.info("Equipment type file uploaded.");
		return equipmentDataReader.verifyEquipmentTypeFile(file);
	}

	/**
	 * REST method to return all Equipment
	 * 
	 * @return					Equipment in a List
	 */
	@RequestMapping("/rest/getEquipment")
	public List<Equipment> getEquipment() {
		edao = new EquipmentDao();
		edao.init();
		List<Equipment> result = edao.getAll();
		edao.destroy();
		return result;
	}
	
	/**
	 * REST method to return all EquipmentTypes
	 * 
	 * @return					EquipmentTypes in a List
	 */
	@RequestMapping("/rest/getEquipmentTypes")
	public List<Equipmenttype> getEquipmentTypes() {
		etdao = new EquipmenttypeDao();
		etdao.init();
		List<Equipmenttype> result = etdao.getAll();
		etdao.destroy();
		return result;
	}
	
	/**
	 * REST method to enable equipment (change status in Equipment to 1)
	 * 
	 * @param equipmentId		EquipmentId of Equipment to update 
	 * @return					Updated Equipment
	 */
	@RequestMapping("/rest/enableEquipment")
	public Equipment enableEquipment(@RequestParam(value = "equipmentId") String equipmentId) {
		edao = new EquipmentDao();
		edao.init();
		edao.initialize(Integer.parseInt(equipmentId));
		Equipment eq = edao.getDao();
		eq.setStatus(1);
		edao.persist(eq);
		edao.destroy();
		logger.info("Equipment " + eq.getName() + "/" + eq.getSerial() + " enabled.");
		return eq;
	}

	/**
	 * REST method to disable equipment (change status in Equipment to 0)
	 * 
	 * @param equipmentId		EquipmentId of Equipment to update 
	 * @return					Updated Equipment
	 */
	@RequestMapping("/rest/disableEquipment")
	public Equipment disableEquipment(@RequestParam(value = "equipmentId") String equipmentId) {
		edao = new EquipmentDao();
		edao.init();
		edao.initialize(Integer.parseInt(equipmentId));
		Equipment eq = edao.getDao();
		eq.setStatus(0);
		edao.persist(eq);
		edao.destroy();
		logger.info("Equipment " + eq.getName() + "/" + eq.getSerial() + " disabled.");
		return eq;
	}
	
	/**
	 * REST method to insert a new piece of equipment
	 * 
	 * @param name				Name of Equipment
	 * @param serial			Serial number of Equipment
	 * @param equipmentTypeId	EquipmentType's equipmentTypeId
	 * @return					Inserted equipment
	 */
	@RequestMapping("/rest/insertEquipment")
	public Equipment insertEquipment(@RequestParam(value = "name") String name,						
									 @RequestParam(value = "serial") String serial,
									 @RequestParam(value = "equipmentTypeId") String equipmentTypeId) {
		edao = new EquipmentDao();
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
					
			logger.info("Equipment " + newEquipment.getName() + "/" + newEquipment.getSerial() + " inserted.");
			
			return newEquipment;
		}
	}
	
	/**
	 * REST method to insert a new EquipmentType
	 * 
	 * @param typeName			Name of EquipmentType
	 * @param typeCode			TypeCode of EquipmentType
	 * @return					Inserted EquipmentType
	 */
	@RequestMapping("/rest/insertType")
	public Equipmenttype insertType(@RequestParam(value = "typeName") String typeName,						
									@RequestParam(value = "typeCode") String typeCode) {
		etdao = new EquipmenttypeDao();
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

			logger.info("Equipment type " + newType.getTypeName() + "/" + newType.getTypeCode() + " inserted.");
			
			return newType;
		}
	}

	/**
	 * REST method to update Equipment
	 * 
	 * @param equipmentId		EquipmentId of Equipment to update
	 * @param name				New name
	 * @param serial			New serial number
	 * @param equipmentTypeId	EquipmentType's equipmentTypeId
	 * @return					Updated Equipment
	 */
	@RequestMapping("/rest/updateEquipment")
	public Equipment updateEquipment(@RequestParam(value = "equipmentId") String equipmentId,
									 @RequestParam(value = "name") String name, 
									 @RequestParam(value = "serial") String serial,
									 @RequestParam(value = "equipmentTypeId") String equipmentTypeId) {
		edao = new EquipmentDao();
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
				logger.error("Attempted to change equipment's serial to existing equipment's serial.");
				throw new IllegalArgumentException("Equipment with serial number " + serial + " already exists!");
		} else {
			edao.initialize(Integer.parseInt(equipmentId));
			Equipment eq = edao.getDao();

			eq.setName(name);
			eq.setSerial(serial);
			if (Integer.parseInt(equipmentTypeId) > 0) {
				etdao = new EquipmenttypeDao();
				etdao.init();
				etdao.initialize(Integer.parseInt(equipmentTypeId));
				Equipmenttype etype = etdao.getDao();
				eq.setEquipmenttype(etype);
				etdao.destroy();
			}
			edao.update(eq);
			edao.destroy();

			logger.info("Equipment " + eq.getName() + "/" + eq.getSerial() + " updated.");
			
			return eq;
		}
	}
	
	/**
	 * REST method to update EquipmentType
	 * @param equipmentTypeId	EquipmentTypeId of EquipmentType to update
	 * @param typeName			New typeName
	 * @param typeCode			new typeCode
	 * @return					Updated EquipmentType
	 */
	@RequestMapping("/rest/updateType")
	public Equipmenttype updateType(@RequestParam(value = "equipmentTypeId") String equipmentTypeId, 
 									@RequestParam(value = "typeName") String typeName,						
									@RequestParam(value = "typeCode") String typeCode) {
		
		etdao = new EquipmenttypeDao();
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
			logger.error("Attempted to change equipment type's typeCode to existing equipment type's typeCode.");
			throw new IllegalArgumentException("Equipment type with Type Code " + typeCode + " already exists!");
		}
		else {
			etdao.initialize(Integer.parseInt(equipmentTypeId));
			Equipmenttype etype = etdao.getDao();
			
			etype.setTypeName(typeName);
			etype.setTypeCode(Integer.parseInt(typeCode));
			
			etdao.update(etype);
			etdao.destroy();
			
			logger.info("Equipment type " + etype.getTypeName() + "/" + etype.getTypeCode() + " updated.");
			
			return etype;
		}
	}
	
	/**
	 * REST method to delete Equipment
	 * 
	 * @param equipmentId		EquipmentId of Equipment to delete
	 * @return					Deleted Equipment
	 */
	@RequestMapping("/rest/deleteEquipment")
	public Equipment deleteEquipment(@RequestParam(value = "equipmentId") String equipmentId) {
		if (!equipmentId.matches("\\d+")) {
			throw new IllegalArgumentException("EquipmentId must be an integer value!");	
		}
		else if (equipmentId.length() == 0) {
			throw new IllegalArgumentException("Field \"EquipmentId\" must not be empty!");
		}
		
		edao = new EquipmentDao();
		edao.init();
		if (!edao.equipmentIdExists(equipmentId)) {
			edao.destroy();
			throw new IllegalArgumentException("Equipment with \"EquipmentId\" not found!");
		}
		edao.initialize(Integer.parseInt(equipmentId));
		Equipment eq = edao.getDao();
		edao.delete();
		edao.destroy();
		
		logger.info("Equipment " + eq.getName() + "/" + eq.getSerial() + " deleted.");
		
		return eq;
	}
	
	/**
	 * REST method to delete EquipmentType
	 * 
	 * @param equipmentTypeId	EquipmentTypeId of EquipmenType to delete
	 * @return					Deleted EquipmentType
	 */
	@RequestMapping("/rest/deleteType")
	public Equipmenttype deleteType(@RequestParam(value = "equipmentTypeId") String equipmentTypeId) {
		if (!equipmentTypeId.matches("\\d+")) {
			throw new IllegalArgumentException("EquipmentTypeId must be an integer value!");	
		}
		else if (equipmentTypeId.length() == 0) {
			throw new IllegalArgumentException("Field \"EquipmentTypeId\" must not be empty!");
		}
		
		etdao = new EquipmenttypeDao();
		etdao.init();
		if (!etdao.equipmentTypeIdExists(Integer.parseInt(equipmentTypeId))) {
			etdao.destroy();
			throw new IllegalArgumentException("Equipment Type with EquipmentTypeId " + equipmentTypeId + " not found!");
		}
		
		etdao.initialize(Integer.parseInt(equipmentTypeId));
		Equipmenttype etype = etdao.getDao();
		
		edao = new EquipmentDao();
		edao.init();
		List<Equipment> equipmentList = edao.getByTypeCode(etype.getTypeCode());
		if (equipmentList.size() > 0) {
			edao.destroy();
			etdao.destroy();
			logger.error("Attempted to delete equipment type with equipment attached to it.");
			throw new IllegalArgumentException("Cannot delete Type while it has Equipment attached to it!");
		}
		else {
			etdao.delete();
			etdao.destroy();
			logger.info("Equipment type " + etype.getTypeName() + "/" + etype.getTypeCode() + " deleted.");
			return etype;
		}
	}
	
	@ExceptionHandler
	void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}
}