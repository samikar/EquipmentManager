package db;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.Equipment;
import model.EquipmentDao;
import model.Equipmenttype;
import model.EquipmenttypeDao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class EquipmentDataReader {
	static Properties appProperties = PropertyUtils.loadProperties();
	
	public static String readEquipmentFromFile(String filePath) {

		FileInputStream inputStreamEquipment = null;
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
		int rows = firstSheet.getPhysicalNumberOfRows();
		EquipmentDao edao = new EquipmentDao();
		EquipmenttypeDao eqTypeDao = new EquipmenttypeDao();
		edao.init();
		eqTypeDao.init();

		System.out.println("Reading equipment file...");
		
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
				e.setEquipmentId(eId);
				edao.update(e);
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
			System.out.println("Workbook could not be read: " + e1.getMessage());
			return "Workbook could not be read: " + e1.getMessage();
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
}