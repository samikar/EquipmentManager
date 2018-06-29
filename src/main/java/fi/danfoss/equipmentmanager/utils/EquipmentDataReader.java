package fi.danfoss.equipmentmanager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import fi.danfoss.equipmentmanager.controller.ChartController;
import fi.danfoss.equipmentmanager.model.Equipment;
import fi.danfoss.equipmentmanager.model.EquipmentDao;
import fi.danfoss.equipmentmanager.model.Equipmenttype;
import fi.danfoss.equipmentmanager.model.EquipmenttypeDao;

public class EquipmentDataReader {
	private static Properties properties = PropertyUtils.loadProperties();
	private final static String tempFilePath = Paths.get(properties.getProperty("TempFilePath")).toString();
	final static Logger logger = Logger.getLogger(ChartController.class);
	
	EquipmentDao edao;
	EquipmenttypeDao etdao;
		
	/**
	 * Verify that EquipmentFile has proper file extension (xlsx)
	 * 
	 * @param file				EquipmentFile as MultipartFile
	 * @return					HTTP-response
	 */
	public ResponseEntity<Object> verifyEquipmentFile(MultipartFile file) {
		if (!verifyFileExtension(file.getOriginalFilename()))
			return new ResponseEntity<>("File extension should be \"xlsx\" (Excel spreadsheet).",
					HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		else { 
			File convertFile = writeFile(file, tempFilePath);

			readEquipmentFromFile(convertFile.getPath());
			deleteFile(convertFile);
		}
		return new ResponseEntity<>("Equipment data read succesfully!", HttpStatus.OK);
	}
	
	/**
	 * Verify that EquipmentTypeFile has proper file extension (xslx)
	 * 
	 * @param file				EquipmentTypeFile as MultipartFile
	 * @return					HTTP-response
	 */
	public ResponseEntity<Object> verifyEquipmentTypeFile(MultipartFile file) {
		if (!verifyFileExtension(file.getOriginalFilename()))
			return new ResponseEntity<>("File extension should be \"xlsx\" (Excel spreadsheet).",
					HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		else {
			File convertFile = writeFile(file, tempFilePath);			
			readEquipmentTypesFromFile(convertFile.getPath());
			deleteFile(convertFile);
		}
		return new ResponseEntity<>("Type data read succesfully!", HttpStatus.OK);
	}
	
	/**
	 * Reads data from EquipmentFile and writes it to DB
	 * 
	 * @param filePath			Path to file to read
	 * @return					Response message as a String
	 */
	public String readEquipmentFromFile(String filePath) {
		int equipmentNameColumn = Integer.parseInt(properties.getProperty("EquipmentFileNameColumn")) - 1;
		int serialColumn = Integer.parseInt(properties.getProperty("EquipmentFileSerialColumn")) - 1;
		int typeColumn = Integer.parseInt(properties.getProperty("EquipmentFileTypeColumn")) - 1;
		int firstRow = Integer.parseInt(properties.getProperty("EquipmentFileFirstDataRow"));
		int lastRow = Integer.parseInt(properties.getProperty("EquipmentFileLastDataRow"));
		
		edao = new EquipmentDao();
		etdao = new EquipmenttypeDao();
		edao.init();
		etdao.init();
		
		FileInputStream inputStreamEquipment = null;
		
		File f = new File(filePath);
		if(!f.exists() && !f.isDirectory()) { 
			return "Equipment file not found!";
		}
		
		try {
			inputStreamEquipment = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return "Equipment file not found: " + e1.getMessage();
		}
		

		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(inputStreamEquipment);
		} catch (IOException e1) {
			e1.printStackTrace();
			return "Equipment file could not be read: " + e1.getMessage();
		}

		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();


		for (int i=0; i<firstRow - 1; i++) {
			iterator.next();	
		}		
		
		for (int i = firstRow; i <= lastRow; i++) {
			Equipment e = new Equipment();
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			String typeCode = "";
			e.setName("");
			e.setSerial(Integer.toString(i));
			e.setEquipmenttype(null);

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				int column = cell.getColumnIndex();
				if (column == equipmentNameColumn)
					e.setName(cell.getStringCellValue());
				else if (column == serialColumn)
					e.setSerial(cell.getStringCellValue());
				else if (column == typeColumn) {
					typeCode = cell.getStringCellValue();
					
					if (etdao.typeCodeExists(Integer.parseInt(typeCode))) {
						etdao.initialize(etdao.getEquipmentTypeIdByTypeCode(Integer.parseInt(typeCode)));
						Equipmenttype eqType = new Equipmenttype();
						eqType = etdao.getDao();
						e.setEquipmenttype(eqType);
					}
				}
				e.setStatus(1);
			}
			
			// Check if equipment with serial is already in database, update if found,
			// insert if not
			if (edao.serialExists(e.getSerial())) {
				Equipment equipmentInDB = edao.getBySerial(e.getSerial());
				equipmentInDB.setName(e.getName());
				equipmentInDB.setEquipmenttype(e.getEquipmenttype());
				equipmentInDB.setStatus(e.getStatus());
				edao.update(equipmentInDB);
			} 
			else
				edao.persist(e);
		}
		
		// workbook.close();
		edao.destroy();
		etdao.destroy();
		
		try {
			inputStreamEquipment.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "Error closing inputstream: " + e.getMessage();
		}
		return "Equipment file read complete.";
	}
	
	/**
	 * Reads data from EquipmentTypeFile and writes it to DB
	 * 
	 * @param filePath			Path to file to read
	 * @return					Response message as a String
	 */
	public String readEquipmentTypesFromFile(String filePath) {
		int typeNameColumn = Integer.parseInt(properties.getProperty("TypeFileTypeNameColumn"));
		int typeCodeColumn = Integer.parseInt(properties.getProperty("TypeFileTypeCodeColumn"));
		int firstRow = Integer.parseInt(properties.getProperty("TypeFileFirstDataRow"));
		int lastRow = Integer.parseInt(properties.getProperty("TypeFileLastDataRow"));
		
		EquipmenttypeDao etdao = new EquipmenttypeDao();
		FileInputStream inputStreamType = null;
		Workbook workbook = null;
		
		File f = new File(filePath);
		if(!f.exists() && !f.isDirectory()) { 
			return "Equipment type file not found!";
		}

		try {
			inputStreamType = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.out.println("Equipment type file could not be read: " + e1.getMessage());
			return "Equipment type file could not be read: " + e1.getMessage();
		}

		try {
			workbook = new XSSFWorkbook(inputStreamType);
		} catch (IOException e1) {
			e1.printStackTrace();
			return "Equipment type file could not be read: " + e1.getMessage();
		}

//		logger.debug("Reading function start");
		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();
		etdao.init();
		
		for (int i=0; i<firstRow - 1; i++) {
			iterator.next();	
		}
		
		for (int i = firstRow; i <= lastRow; i++) {
			Equipmenttype eqType = new Equipmenttype();
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();

//			logger.debug("Reading cells");
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				int column = cell.getColumnIndex();
				
				if (column == typeNameColumn) {
					eqType.setTypeName(cell.getStringCellValue());
				}
				else if (column == typeCodeColumn) {
					eqType.setTypeCode(Integer.parseInt(cell.getStringCellValue()));
				}
			}
			
			// Check if equipment type with typeCode is already in database, update if found,
			// insert if not
			int eTypeId = etdao.getEquipmentTypeIdByTypeCode(eqType.getTypeCode());
			if (eTypeId > 0) {
				eqType.setEquipmentTypeId(eTypeId);
				etdao.update(eqType);
			} else {
//				logger.debug("Writing data");
				etdao.persist(eqType);
			}
		}
		// workbook.close();
		
		etdao.destroy();
		try {
			inputStreamType.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Inputstream for Equipment type file could not be closed.");
			return e.getMessage();
		}
		return "Equipment type file read complete.";
	}
	
	/**
	 * Verify that file extension is (xlsx) 
	 * @param filePath			Path to file to read
	 * @return					True if file extension is correct, false if not
	 */
	public boolean verifyFileExtension(String filePath) {
		String extension = "";
		int i = filePath.lastIndexOf('.');
		if (i > 0) {
			extension = filePath.substring(i + 1);
		}

		if (!extension.equals("xlsx"))
			return false;
		else
			return true;
	}
	
	/**
	 * Write file temporarily to server  
	 * 
	 * @param file				File to write
	 * @param path				Path to write file
	 * @return					File converted from MultipartFile to File-class
	 */
	public File writeFile(MultipartFile file, String path) {
		Path convertedFile = null;
		try {
			convertedFile = Paths.get(path + file.getOriginalFilename());
			Files.write(convertedFile, file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return convertedFile.toFile();
	}
	
	/**
	 * Delete file from server
	 * 
	 * @param file				File to delete
	 * @return					Return true if file deleted successfully, false if not
	 */
	public boolean deleteFile(File file) {
		boolean result = false;
		if (file != null) {
			try {
				result = true;
				Files.delete(file.toPath());
			} catch (NoSuchFileException x) {
				logger.error("No such file exception: " + x);
				System.err.format("%s: no such " + " file or directory%n", file);
			} catch (DirectoryNotEmptyException x) {
				logger.error("Directory not empty exception: " + x);
				System.err.format("%s not empty%n", file);
			} catch (IOException x) {
				// File permission problems are caught here.
				logger.error("Permission error: " + x);
				System.err.println("Permission error: " + x);
			}
		}
		return result;
	}
}