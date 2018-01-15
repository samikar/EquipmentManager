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
	/*
	 * private static final String FILE_NAME =
	 * "C:\\EquipmentManager\\Eclipse_workspace\\EquipmentManager\\test_files\\Omaisuusluettelo.xlsx";
	 * 
	 * public static void main(String[] args) {
	 * 
	 * try {
	 * 
	 * FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
	 * Workbook workbook = new XSSFWorkbook(excelFile); Sheet datatypeSheet =
	 * workbook.getSheetAt(0); Iterator<Row> iterator = datatypeSheet.iterator();
	 * 
	 * while (iterator.hasNext()) {
	 * 
	 * Row currentRow = iterator.next(); Iterator<Cell> cellIterator =
	 * currentRow.iterator();
	 * 
	 * while (cellIterator.hasNext()) {
	 * 
	 * Cell currentCell = cellIterator.next(); // getCellTypeEnum shown as
	 * deprecated for version 3.15 // getCellTypeEnum ill be renamed to getCellType
	 * starting from version 4.0 if (currentCell.getCellTypeEnum() ==
	 * CellType.STRING) { System.out.print(currentCell.getStringCellValue() + "--");
	 * } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
	 * System.out.print(currentCell.getNumericCellValue() + "--"); }
	 * 
	 * } System.out.println();
	 * 
	 * } } catch (FileNotFoundException e) { e.printStackTrace(); } catch
	 * (IOException e) { e.printStackTrace(); }
	 * 
	 * }
	 */
	public static void main(String[] args) throws IOException {
		String excelFilePath = "C:\\EquipmentManager\\Eclipse_workspace\\EquipmentManager\\test_files\\Omaisuusluettelo.xlsx";
		FileInputStream inputStream = new FileInputStream(new File(excelFilePath));

		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet firstSheet = workbook.getSheetAt(0);

		EquipmentDao dao = new EquipmentDao();
		dao.init();
		
		Iterator<Row> iterator = firstSheet.iterator();
		iterator.next();
				
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			Equipment e = new Equipment();
			
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				int column = cell.getColumnIndex();

				switch (column) {
				case 0:
					e.setEquipmentId(Integer.parseInt(cell.getStringCellValue()));
					
					break;
				case 1:
					e.setName(cell.getStringCellValue());
					// TODO: Placeholder values
					e.setType(cell.getStringCellValue().substring(0, 4));
					e.setStatus(1);
					break;
				}
				
			}
			System.out.println("Id:" + e.getEquipmentId() + " Name:" + e.getName() + " Type:" + e.getType() + " Status:" + e.getStatus());
			dao.persist(e);
		}

		// workbook.close();
		inputStream.close();
	}
}