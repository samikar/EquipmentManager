package controller;

import java.io.IOException;
import java.util.List;

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

@RestController
public class ConfigurationController {
	@RequestMapping(value = "/rest/uploadEquipmentFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> uploadEquipmentFile(@RequestParam("file") MultipartFile file) throws IOException {
		return EquipmentDataReader.verifyEquipmentFile(file);
	}

	@RequestMapping(value = "/rest/uploadTypeFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> uploadTypeFile(@RequestParam("file") MultipartFile file) throws IOException {
		return EquipmentDataReader.verifyEquipmentTypeFile(file);
	}

	@RequestMapping("/rest/getEquipment")
	public List<Equipment> getEquipment() {
		EquipmentDao edao = new EquipmentDao();
		edao.init();
		List<Equipment> result = edao.getAll();
		edao.destroy();
		return result;
	}
	
	@RequestMapping("/rest/getEquipmentTypes")
	public List<Equipmenttype> getEquipmentTypes() {
		EquipmenttypeDao etdao = new EquipmenttypeDao();
		etdao.init();
		List<Equipmenttype> result = etdao.getAll();
		etdao.destroy();
		return result;
	}
	
	@RequestMapping("/rest/enableEquipment")
	public Equipment enableEquipment(@RequestParam(value = "equipmentId") String equipmentId) {
		EquipmentDao edao = new EquipmentDao();
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
		EquipmentDao edao = new EquipmentDao();
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
		EquipmentDao edao = new EquipmentDao();
		EquipmenttypeDao etdao = new EquipmenttypeDao();
		Equipment newEquipment = new Equipment();
		edao.init();
		etdao.init();
		
		if (edao.serialExists(serial)) {
			edao.destroy();
			etdao.destroy();
			throw new IllegalArgumentException("Equipment with serial number " + serial + " already exists.");
		}
		else {
			etdao.initialize(Integer.parseInt(equipmentTypeId));
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
		EquipmenttypeDao etdao = new EquipmenttypeDao();
		etdao.init();
		
		if (!typeCode.matches("\\d+")) {
			etdao.destroy();
			throw new IllegalArgumentException("Typecode must be an integer value.");	
		}
		else if (etdao.typeCodeExists(Integer.parseInt(typeCode))) {
			etdao.destroy();
			throw new IllegalArgumentException("Equipment type with Typecode " + typeCode + " already exists.");
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
		EquipmentDao edao = new EquipmentDao();
		edao.init();
		edao.initialize(Integer.parseInt(equipmentId));
		Equipment eq = edao.getDao();
		
		eq.setName(name);
		eq.setSerial(serial);
		if (Integer.parseInt(equipmentTypeId) > 0) {
			EquipmenttypeDao etdao = new EquipmenttypeDao();
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
	
	@RequestMapping("/rest/updateType")
	public Equipmenttype updateType(@RequestParam(value = "equipmentTypeId") String equipmentTypeId, 
 									 @RequestParam(value = "typeName") String typeName,						
									 @RequestParam(value = "typeCode") String typeCode) {
		
		EquipmenttypeDao etdao = new EquipmenttypeDao();
		etdao.init();
		etdao.initialize(Integer.parseInt(equipmentTypeId));
		Equipmenttype etype = etdao.getDao();
		
		etype.setTypeName(typeName);
		etype.setTypeCode(Integer.parseInt(typeCode));
		
		etdao.update(etype);
		etdao.destroy();
		
		return etype;
	}
	
	@RequestMapping("/rest/deleteEquipment")
	public Equipment deleteEquipment(@RequestParam(value = "equipmentId") String equipmentId) {
		EquipmentDao edao = new EquipmentDao();
		edao.init();
		edao.initialize(Integer.parseInt(equipmentId));
		Equipment eq = edao.getDao();
		edao.delete();
		edao.destroy();
		return eq;
	}
	
	@RequestMapping("/rest/deleteType")
	public Equipmenttype deleteType(@RequestParam(value = "equipmentTypeId") String equipmentTypeId) {
		EquipmentDao edao = new EquipmentDao();
		EquipmenttypeDao etdao = new EquipmenttypeDao();
		edao.init();
		etdao.init();
		etdao.initialize(Integer.parseInt(equipmentTypeId));
		Equipmenttype etype = etdao.getDao();
		List<Equipment> equipmentList = edao.getByTypeCode(etype.getTypeCode());
		if (equipmentList.size() > 0) {
			edao.destroy();
			etdao.destroy();
			throw new IllegalArgumentException("Cannot delete Type while it has Equipment attached to it.");
		}
		else {
			etdao.delete();
			etdao.destroy();
			return etype;
		}
	}

	@ExceptionHandler
	void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}
}