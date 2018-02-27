package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import db.EquipmentDataReader;
import db.PropertyUtils;



@RestController
public class ConfigurationController {
	Properties appProperties = PropertyUtils.loadProperties();

	@RequestMapping(value="/rest/uploadEquipmentFile", method=RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> uploadEquipmentFile(@RequestParam("file") MultipartFile file) throws IOException {		
		if (!verifyFileExtension(file.getOriginalFilename())) {
			return new ResponseEntity<>("File not uploaded: file extension should be \"xlsx\" (Excel spreadsheet)." , HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		}
		
		File convertFile = new File("DataFiles" + File.separator + file.getOriginalFilename());
		convertFile.createNewFile();
		FileOutputStream fout = new FileOutputStream(convertFile);
		fout.write(file.getBytes());
		fout.close();
		
		if (!verifyEquipmentFileHeaders(convertFile.getAbsolutePath())) {
			deleteFile(convertFile);
			return new ResponseEntity<>("Spreadsheet headers wrong." , HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		}
		
		EquipmentDataReader.readEquipmentFromFile(convertFile.getPath());
		return new ResponseEntity<>("Equipment data read succesfully!", HttpStatus.OK);	
	}
	
	@RequestMapping(value="/rest/uploadTypeFile", method=RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> uploadTypeFile(@RequestParam("file") MultipartFile file) throws IOException {		
		if (!verifyFileExtension(file.getOriginalFilename())) {
			return new ResponseEntity<>("File not uploaded: file extension should be \"xlsx\" (Excel spreadsheet)." , HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		}
		
		File convertFile = new File("DataFiles" + File.separator + file.getOriginalFilename());
		convertFile.createNewFile();
		FileOutputStream fout = new FileOutputStream(convertFile);
		fout.write(file.getBytes());
		fout.close();
		
		if (!verifyTypeFileHeaders(convertFile.getAbsolutePath())) {
			deleteFile(convertFile);
			return new ResponseEntity<>("Spreadsheet headers wrong." , HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		}
		
		EquipmentDataReader.readEquipmentTypesFromFile(convertFile.getPath());
		return new ResponseEntity<>("Equipment data read succesfully!", HttpStatus.OK);	
	}
	
	public boolean verifyFileExtension(String filePath) {	
		String extension = "";
		int i = filePath.lastIndexOf('.');
		if (i > 0) {
		    extension = filePath.substring(i+1);
		}
		
		if (!extension.equals("xlsx"))
			return false;
		else
			return true;
	}
	
	public boolean verifyEquipmentFileHeaders(String filePath) {
                
		FileInputStream inputStreamEquipment = null;
		try {
			inputStreamEquipment = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
//			return "Equipment file not found: " + e1.getMessage();
		}
		
		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(inputStreamEquipment);
		} catch (IOException e1) {
			e1.printStackTrace();
//			return "Equipment file could not be read: " + e1.getMessage();
		}
		
		Sheet firstSheet = workbook.getSheetAt(0);
		
		Iterator<Row> iterator = firstSheet.iterator();

		
		for (int i=0; i<Integer.parseInt(appProperties.getProperty("EquipmentFileRowsBeforeData")) - 1; i++) {
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
				return false;
			else if (column == serialColumn && !cell.getStringCellValue().equals(serialStr))
				return false;
			else if (column == typeCodeColumn && !cell.getStringCellValue().equals(typeCodeStr))
				return false;
		}
		return true;
	}
	
	public boolean verifyTypeFileHeaders(String filePath) {
		
		
		FileInputStream inputStreamEquipment = null;
		try {
			inputStreamEquipment = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
//			return "Equipment file not found: " + e1.getMessage();
		}
		
		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(inputStreamEquipment);
		} catch (IOException e1) {
			e1.printStackTrace();
//			return "Equipment file could not be read: " + e1.getMessage();
		}
		
		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();

		for (int i=0; i<Integer.parseInt(appProperties.getProperty("TypeFileRowsBeforeData")) - 1; i++) {
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
				return false;
			else if (column == typeCodeColumn && !cell.getStringCellValue().equals(typeCodeStr))
				return false;
		}
		return true;
	}
	
	public boolean deleteFile(File file) {
		boolean result = false;
		try {
			result = Files.deleteIfExists(file.toPath());
		} catch (NoSuchFileException x) {
			System.err.format("%s: no such" + " file or directory%n", file);
		} catch (DirectoryNotEmptyException x) {
			System.err.format("%s not empty%n", file);
		} catch (IOException x) {
			// File permission problems are caught here.
			System.err.println("Permission error: " + x);
		}
		return result;
	}
}