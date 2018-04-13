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
	private static Properties properties = PropertyUtils.loadProperties();
	private final static String dataFilePath = "DataFiles" + File.separator;
	
	EquipmentDao edao;
	EquipmenttypeDao etdao;
		
	public ResponseEntity<Object> verifyEquipmentFile(MultipartFile file) {
		
		if (!verifyFileExtension(file.getOriginalFilename()))
			return new ResponseEntity<>("File extension should be \"xlsx\" (Excel spreadsheet).",
					HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		else { 
			File convertFile = writeFile(file, dataFilePath);

			readEquipmentFromFile(convertFile.getPath());
			deleteFile(convertFile);
		}
		return new ResponseEntity<>("Equipment data read succesfully!", HttpStatus.OK);
	}
	
	public ResponseEntity<Object> verifyEquipmentTypeFile(MultipartFile file) {
		
		if (!verifyFileExtension(file.getOriginalFilename()))
			return new ResponseEntity<>("File extension should be \"xlsx\" (Excel spreadsheet).",
					HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		else { 
			File convertFile = writeFile(file, dataFilePath);			

			readEquipmentTypesFromFile(convertFile.getPath());
			deleteFile(convertFile);
		}
		return new ResponseEntity<>("Equipment data read succesfully!", HttpStatus.OK);
	}
	
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
//			System.out.println("i: " + i + " firstRow: " + firstRow + " lastRow: " + lastRow);
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
			} else
				etdao.persist(eqType);
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
	
	public boolean deleteFile(File file) {
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