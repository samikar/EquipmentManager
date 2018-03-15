package utils;

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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import model.Equipment;
import model.EquipmentDao;
import model.Equipmenttype;
import model.EquipmenttypeDao;

public class EquipmentDataReader {
	private final static String dataFilePath = "DataFiles" + File.separator;
	static Properties appProperties = PropertyUtils.loadProperties();
	
	public static ResponseEntity<Object> verifyEquipmentFile(MultipartFile file) {
		
		if (!verifyFileExtension(file.getOriginalFilename()))
			return new ResponseEntity<>("File extension should be \"xlsx\" (Excel spreadsheet).",
					HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		else { 
			File convertFile = EquipmentDataReader.writeFile(file, dataFilePath);

			readEquipmentFromFile(convertFile.getPath());
			deleteFile(convertFile);
		}
		return new ResponseEntity<>("Equipment data read succesfully!", HttpStatus.OK);
	}
	
	public static ResponseEntity<Object> verifyEquipmentTypeFile(MultipartFile file) {
		
		if (!verifyFileExtension(file.getOriginalFilename()))
			return new ResponseEntity<>("File extension should be \"xlsx\" (Excel spreadsheet).",
					HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		else { 
			File convertFile = EquipmentDataReader.writeFile(file, dataFilePath);			

			readEquipmentTypesFromFile(convertFile.getPath());
			deleteFile(convertFile);
		}
		return new ResponseEntity<>("Equipment data read succesfully!", HttpStatus.OK);
	}
	
	public static String readEquipmentFromFile(String filePath) {
		int equipmentNameColumn = Integer.parseInt(appProperties.getProperty("EquipmentFileNameColumn")) - 1;
		int serialColumn = Integer.parseInt(appProperties.getProperty("EquipmentFileSerialColumn")) - 1;
		int typeColumn = Integer.parseInt(appProperties.getProperty("EquipmentFileTypeColumn")) - 1;
		int firstRow = Integer.parseInt(appProperties.getProperty("EquipmentFileFirstDataRow"));
		int lastRow = Integer.parseInt(appProperties.getProperty("EquipmentFileLastDataRow"));
		
		EquipmentDao edao = new EquipmentDao();
		EquipmenttypeDao eqTypeDao = new EquipmenttypeDao();
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
		edao.init();
		eqTypeDao.init();

		for (int i=0; i<firstRow - 1; i++) {
			iterator.next();	
		}		
		
		for (int i = firstRow; i <= lastRow; i++) {
			System.out.println("i: " + i + " firstRow: " + firstRow + " lastRow: " + lastRow);
			Equipment e = new Equipment();
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			String typeCode = "";
			e.setName("foo");
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
					
					if (eqTypeDao.typeCodeExists(Integer.parseInt(typeCode))) {
						eqTypeDao.initialize(eqTypeDao.getEquipmentTypeIdByTypeCode(Integer.parseInt(typeCode)));
						Equipmenttype eqType = new Equipmenttype();
						eqType = eqTypeDao.getDao();
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
		eqTypeDao.destroy();
		
		try {
			inputStreamEquipment.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "Error closing inputstream: " + e.getMessage();
		}
		return "Equipment file read complete.";
	}

	public static String readEquipmentTypesFromFile(String filePath) {
		int typeNameColumn = Integer.parseInt(appProperties.getProperty("TypeFileTypeNameColumn"));
		int typeCodeColumn = Integer.parseInt(appProperties.getProperty("TypeFileTypeCodeColumn"));
		int firstRow = Integer.parseInt(appProperties.getProperty("TypeFileFirstDataRow"));
		int lastRow = Integer.parseInt(appProperties.getProperty("TypeFileLastDataRow"));
		
		EquipmenttypeDao eqTypeDao = new EquipmenttypeDao();
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

		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();
		eqTypeDao.init();
		
		for (int i=0; i<firstRow - 1; i++) {
			iterator.next();	
		}
		
		for (int i = firstRow; i <= lastRow; i++) {
			Equipmenttype eqType = new Equipmenttype();
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();

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
			int eTypeId = eqTypeDao.getEquipmentTypeIdByTypeCode(eqType.getTypeCode());
			if (eTypeId > 0) {
				eqType.setEquipmentTypeId(eTypeId);
				eqTypeDao.update(eqType);
			} else
				eqTypeDao.persist(eqType);
		}
		// workbook.close();
		
		eqTypeDao.destroy();
		try {
			inputStreamType.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Inputstream for Equipment type file could not be closed.");
			return e.getMessage();
		}
		return "Equipment type file read complete.";
	}
	
	public static boolean verifyFileExtension(String filePath) {
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
	
	public static File writeFile(MultipartFile file, String path) {
		Path convertedFile = null;
		try {
			convertedFile = Paths.get(path + file.getOriginalFilename());
			Files.write(convertedFile, file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return convertedFile.toFile();
	}
	
	public static boolean deleteFile(File file) {
		boolean result = false;
		if (file != null) {
			try {
				result = true;
				Files.delete(file.toPath());
			} catch (NoSuchFileException x) {
				System.err.format("%s: no such" + " file or directory%n", file);
			} catch (DirectoryNotEmptyException x) {
				System.err.format("%s not empty%n", file);
			} catch (IOException x) {
				// File permission problems are caught here.
				System.err.println("Permission error: " + x);
			}
		}
		return result;
	}
}