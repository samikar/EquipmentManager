package db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
			String headerVerify;
			
			headerVerify = verifyEquipmentFileHeaders(convertFile.getAbsolutePath());
			if (!headerVerify.equals("OK")) {
				return new ResponseEntity<>(
						"Spreadsheet headers wrong: " + headerVerify + ". \nFix configuration file or spreadsheet headers.",
						HttpStatus.UNSUPPORTED_MEDIA_TYPE);
			}
			else {
				readEquipmentFromFile(convertFile.getPath());
			}
			EquipmentDataReader.deleteFile(convertFile);
		}
		return new ResponseEntity<>("Equipment data read succesfully!", HttpStatus.OK);
	}
	
	public static ResponseEntity<Object> verifyEquipmentTypeFile(MultipartFile file) {
		
		if (!verifyFileExtension(file.getOriginalFilename()))
			return new ResponseEntity<>("File extension should be \"xlsx\" (Excel spreadsheet).",
					HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		else { 
			File convertFile = EquipmentDataReader.writeFile(file, dataFilePath);
			String headerVerify;
			
			headerVerify = verifyTypeFileHeaders(convertFile.getAbsolutePath());
			if (!headerVerify.equals("OK")) {
				return new ResponseEntity<>(
						"Spreadsheet headers wrong: " + headerVerify + ". \nFix configuration file or spreadsheet headers.",
						HttpStatus.UNSUPPORTED_MEDIA_TYPE);
			}
			else {
				readEquipmentTypesFromFile(convertFile.getPath());
			}
			EquipmentDataReader.deleteFile(convertFile);
		}
		return new ResponseEntity<>("Equipment data read succesfully!", HttpStatus.OK);
	}
	
	public static String readEquipmentFromFile(String filePath) {
		FileInputStream inputStreamEquipment = null;
		
		File f = new File(filePath);
		if(!f.exists() && !f.isDirectory()) { 
			return "Equipment file not found!";
		}
		/*
		try {
			inputStreamEquipment = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return "Equipment file not found: " + e1.getMessage();
		}
		*/

		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(inputStreamEquipment);
		} catch (IOException e1) {
			e1.printStackTrace();
			return "Equipment file could not be read: " + e1.getMessage();
		}

		Sheet firstSheet = workbook.getSheetAt(0);
		int rows = firstSheet.getPhysicalNumberOfRows();
		EquipmentDao edao = new EquipmentDao();
		EquipmenttypeDao eqTypeDao = new EquipmenttypeDao();
		edao.init();
		eqTypeDao.init();
		
		Iterator<Row> iterator = firstSheet.iterator();

		for (int i=0; i<Integer.parseInt(appProperties.getProperty("EquipmentFileRowsBeforeData")); i++) {
			iterator.next();	
		}

		for (int i = 3; i < rows - Integer.parseInt(appProperties.getProperty("EquipmentFileRowsAfterData")); i++) {
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			Equipment e = new Equipment();
			String typeCode = "";

			while (cellIterator.hasNext()) {
				int descriptionColumn = Integer.parseInt(appProperties.getProperty("EquipmentFileDescriptionColumn"));
				int serialColumn = Integer.parseInt(appProperties.getProperty("EquipmentFileSerialColumn"));
				int typeCodeColumn = Integer.parseInt(appProperties.getProperty("EquipmentFileTypeCodeColumn"));
				
				
				Cell cell = cellIterator.next();
				int column = cell.getColumnIndex();

				if (column == descriptionColumn)
					e.setName(cell.getStringCellValue());
				else if (column == serialColumn)
					e.setSerial(cell.getStringCellValue());
				else if (column == typeCodeColumn)
					typeCode = cell.getStringCellValue();
				
				if (typeCode.length() >= 4) {
					Equipmenttype eqType = new Equipmenttype();
					// int typeCode = Integer.parseInt(cell.getStringCellValue());
					eqTypeDao.initialize(eqTypeDao.getEquipmentTypeIdByTypeCode(Integer.parseInt(typeCode)));
					eqType = eqTypeDao.getDao();
					e.setEquipmenttype(eqType);
				}
				e.setStatus(1);
			}

			// Check if equipment with serial is already in database, update if found,
			// insert if not
			int eId = edao.getEquipmentIdBySerial(e.getSerial());
			if (eId > 0) {
				Equipment equipmentInDB = edao.getBySerial(e.getSerial());
				equipmentInDB.setName(e.getName());
				equipmentInDB.setEquipmenttype(e.getEquipmenttype());
				edao.update(equipmentInDB);
			} else
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
		FileInputStream inputStreamType = null;
		Workbook workbook = null;
		
		File f = new File(filePath);
		if(!f.exists() && !f.isDirectory()) { 
			return "Equipment type file not found!";
		}

		/*
		try {
			inputStreamType = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.out.println("Equipment type file could not be read: " + e1.getMessage());
			return "Equipment type file could not be read: " + e1.getMessage();
		}
		*/

		try {
			workbook = new XSSFWorkbook(inputStreamType);
		} catch (IOException e1) {
			e1.printStackTrace();
			return "Equipment type file could not be read: " + e1.getMessage();
		}

		Sheet firstSheet = workbook.getSheetAt(0);
		int rows = firstSheet.getPhysicalNumberOfRows();
		Iterator<Row> iterator = firstSheet.iterator();
		EquipmenttypeDao eqTypeDao = new EquipmenttypeDao();
		eqTypeDao.init();

		for (int i=0; i<Integer.parseInt(appProperties.getProperty("TypeFileRowsBeforeData")); i++) {
			iterator.next();	
		}

		for (int i = 3; i < rows - Integer.parseInt(appProperties.getProperty("TypeFileRowsAfterData")); i++) {
			Equipmenttype eqType = new Equipmenttype();
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();

			while (cellIterator.hasNext()) {
				int typeNameColumn = Integer.parseInt(appProperties.getProperty("TypeFileTypeNameColumn"));
				int typeCodeColumn = Integer.parseInt(appProperties.getProperty("TypeFileTypeCodeColumn"));
				
				Cell cell = cellIterator.next();
				int column = cell.getColumnIndex();
				
				if (column == typeNameColumn)
					eqType.setTypeName(cell.getStringCellValue());
				else if (column == typeCodeColumn)
					eqType.setTypeCode(Integer.parseInt(cell.getStringCellValue()));
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
	
	public static String verifyEquipmentFileHeaders(String filePath) {

		FileInputStream inputStreamEquipment = null;
		try {
			inputStreamEquipment = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			// return "Equipment file not found: " + e1.getMessage();
		}

		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(inputStreamEquipment);
		} catch (IOException e1) {
			e1.printStackTrace();
			// return "Equipment file could not be read: " + e1.getMessage();
		}

		Sheet firstSheet = workbook.getSheetAt(0);

		Iterator<Row> iterator = firstSheet.iterator();

		for (int i = 0; i < Integer.parseInt(appProperties.getProperty("EquipmentFileRowsBeforeData")) - 1; i++) {
			iterator.next();
		}

		Row headerRow = iterator.next();

		Iterator<Cell> cellIterator = headerRow.cellIterator();
		while (cellIterator.hasNext()) {
			int descriptionColumn = Integer.parseInt(appProperties.getProperty("EquipmentFileDescriptionColumn"));
			int serialColumn = Integer.parseInt(appProperties.getProperty("EquipmentFileSerialColumn"));
			int typeCodeColumn = Integer.parseInt(appProperties.getProperty("EquipmentFileTypeCodeColumn"));
			String descriptionStr = appProperties.getProperty("EquipmentFileDescriptionString");
			String serialStr = appProperties.getProperty("EquipmentFileSerialString");
			String typeCodeStr = appProperties.getProperty("EquipmentFileTypeCodeString");

			Cell cell = cellIterator.next();
			int column = cell.getColumnIndex();
			if (column == descriptionColumn && !cell.getStringCellValue().equals(descriptionStr))
				return "is \"" + cell.getStringCellValue() + "\", should be: \"" + descriptionStr + "\"";
			else if (column == serialColumn && !cell.getStringCellValue().equals(serialStr))
				return "is \"" + cell.getStringCellValue() + "\", should be: \"" + serialStr + "\"";
			else if (column == typeCodeColumn && !cell.getStringCellValue().equals(typeCodeStr))
				return "is \"" + cell.getStringCellValue() + "\", should be: \"" + typeCodeStr + "\"";
		}
		return "OK";
	}
	
	public static String verifyTypeFileHeaders(String filePath) {
		FileInputStream inputStreamEquipment = null;
		try {
			inputStreamEquipment = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			// return "Equipment file not found: " + e1.getMessage();
		}

		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(inputStreamEquipment);
		} catch (IOException e1) {
			e1.printStackTrace();
			// return "Equipment file could not be read: " + e1.getMessage();
		}

		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();

		for (int i = 0; i < Integer.parseInt(appProperties.getProperty("TypeFileRowsBeforeData")) - 1; i++) {
			iterator.next();
		}
		Row headerRow = iterator.next();

		Iterator<Cell> cellIterator = headerRow.cellIterator();
		while (cellIterator.hasNext()) {
			int typeNameColumn = Integer.parseInt(appProperties.getProperty("TypeFileTypeNameColumn"));
			int typeCodeColumn = Integer.parseInt(appProperties.getProperty("TypeFileTypeCodeColumn"));
			String typeNameStr = appProperties.getProperty("TypeFileTypeNameStr");
			String typeCodeStr = appProperties.getProperty("TypeFileTypeCodeStr");

			Cell cell = cellIterator.next();
			int column = cell.getColumnIndex();

			if (column == typeNameColumn && !cell.getStringCellValue().equals(typeNameStr))
				return "is \"" + cell.getStringCellValue() + "\", should be: \"" + typeNameStr + "\"";
			else if (column == typeCodeColumn && !cell.getStringCellValue().equals(typeCodeStr))
				return "is \"" + cell.getStringCellValue() + "\", should be: \"" + typeCodeStr + "\"";
		}
		return "OK";
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