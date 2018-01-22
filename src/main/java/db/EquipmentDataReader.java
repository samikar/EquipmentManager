package db;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.Equipment;
import model.EquipmentDao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

public class EquipmentDataReader {
	public static void main(String[] args) throws IOException {
		String equipmentFilePath = "C:\\EquipmentManager\\Eclipse_workspace\\EquipmentManager\\test_files\\laitteet.xlsx";
		FileInputStream inputStreamEquipment = new FileInputStream(new File(equipmentFilePath));
			
		Workbook workbook = new XSSFWorkbook(inputStreamEquipment);
		Sheet firstSheet = workbook.getSheetAt(0);
		int rows = firstSheet.getPhysicalNumberOfRows();
		String typeName = "";
		EquipmentDao dao = new EquipmentDao();
		dao.init();
		
		Iterator<Row> iterator = firstSheet.iterator();
		
		iterator.next();
		iterator.next();
		iterator.next();
		
		for (int i=3; i<rows-5;i++) {
		//while (iterator.hasNext()) {
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
					String typeId = cell.getStringCellValue();
					
					if (typeId.length() < 4)
						typeName = "N/A";
					else {
						int typeCode = Integer.parseInt(cell.getStringCellValue());
						typeName = getEquipmentType(typeCode);
					}
					e.setType(typeName);
					e.setStatus(1);
					break;
				}	
			}
			
			// Check if equipment with serial is already in database, update if found, insert if not
			int eId = dao.getEquipmentIdBySerial(e.getSerial()); 
			if (eId > 0) {
				e.setEquipmentId(eId);
				dao.update(e);
			}
			else
				dao.persist(e);
		}
		// workbook.close();
		inputStreamEquipment.close();
	}
	
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
	
	private static String getEquipmentTypes() {
		
		
		return "";
	}
}