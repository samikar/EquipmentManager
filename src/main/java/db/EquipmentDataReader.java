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

public class EquipmentDataReader {
	public static void main(String[] args) throws IOException {
		readEquipmentTypesFromFile("C:\\EquipmentManager\\Eclipse_workspace\\EquipmentManager\\test_files\\luokat.xlsx");
		readEquipmentFromFile("C:\\EquipmentManager\\Eclipse_workspace\\EquipmentManager\\test_files\\laitteet.xlsx");
	}
	
	private static String readEquipmentFromFile(String filePath) {
				
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
		String typeName = "";
		EquipmentDao edao = new EquipmentDao();
		EquipmenttypeDao eqTypeDao = new EquipmenttypeDao();
		edao.init();
		eqTypeDao.init();
		
		Iterator<Row> iterator = firstSheet.iterator();
		
		iterator.next();
		iterator.next();
		iterator.next();
		
		for (int i=3; i<rows-5;i++) {
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			Equipment e = new Equipment();
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				int column = cell.getColumnIndex();

				switch (column) {
				case 1:
					e.setName(cell.getStringCellValue());
					break;
				case 4:
					e.setSerial(cell.getStringCellValue());	
					break;
				case 9:
					String typeCode = cell.getStringCellValue();
					
					if (typeCode.length() < 4)
						typeName = "N/A";
					else {
						Equipmenttype eqType = new Equipmenttype();
						//int typeCode = Integer.parseInt(cell.getStringCellValue());
						eqTypeDao.initialize(eqTypeDao.getEquipmentTypeIdByTypeCode(Integer.parseInt(typeCode)));
						eqType = eqTypeDao.getDao();
						e.setEquipmenttype(eqType);
					}
					e.setStatus(1);
					break;
				}	
			}
			
			// Check if equipment with serial is already in database, update if found, insert if not
			int eId = edao.getEquipmentIdBySerial(e.getSerial()); 
			if (eId > 0) {
				e.setEquipmentId(eId);
				edao.update(e);
			}
			else
				edao.persist(e);
		}
		// workbook.close();
		try {
			inputStreamEquipment.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "Error closing inputstream: " + e.getMessage();
		}
		return "Equipment file read complete.";
	}
	
	/*
	private static String getEquipmentType(int typeCode) {
		String typeFilePath = "C:\\EquipmentManager\\Eclipse_workspace\\EquipmentManager\\test_files\\luokat.xlsx";
		
		FileInputStream inputStreamType = null;
		Workbook workbook = null;
		
		
		try {
			inputStreamType= new FileInputStream(new File(typeFilePath));
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
		iterator.next();
		iterator.next();
		iterator.next();
				
		for (int i=3; i<rows-5;i++) {
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			String typeName = "";
						
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				int column = cell.getColumnIndex();

				switch (column) {
				case 0:
					typeName = cell.getStringCellValue();
					break;
				case 4:
					//e.setEquipmentId(Integer.parseInt(cell.getStringCellValue()));
					if (typeCode == Integer.parseInt(cell.getStringCellValue()))
						return typeName;
					break;
				}
			}
		}
		// workbook.close();
		try {
			inputStreamType.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Inputstream for Equipment type file could not be closed.");
			return e.getMessage();
		}
		return "Equipment type for code " + typeCode + " not found!";
	}
	*/
	
	private static String readEquipmentTypesFromFile(String filePath) {
	//String typeFilePath = "C:\\EquipmentManager\\Eclipse_workspace\\EquipmentManager\\test_files\\luokat.xlsx";
		
		FileInputStream inputStreamType = null;
		Workbook workbook = null;
		
		try {
			inputStreamType= new FileInputStream(new File(filePath));
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
		
		iterator.next();
		iterator.next();
		iterator.next();
				
		for (int i=3; i<rows-5;i++) {
			Equipmenttype eqType = new Equipmenttype();
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			
						
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				int column = cell.getColumnIndex();
				switch (column) {
				case 0:
					eqType.setTypeName(cell.getStringCellValue());
					break;
				case 4:
					eqType.setTypeCode(Integer.parseInt(cell.getStringCellValue()));
					break;
				}
			}
			// Check if equipment with serial is already in database, update if found,
			// insert if not
			int eTypeId = eqTypeDao.getEquipmentTypeIdByTypeCode(eqType.getTypeCode());
			if (eTypeId > 0) {
				eqType.setEquipmentTypeId(eTypeId);
				eqTypeDao.update(eqType);
			} else
				eqTypeDao.persist(eqType);
		}
	// workbook.close();
		try
		{
			inputStreamType.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Inputstream for Equipment type file could not be closed.");
			return e.getMessage();
		}
		return "Equipment type file read complete.";
	}
}