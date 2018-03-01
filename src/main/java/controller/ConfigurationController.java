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
import java.util.List;
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
import model.Equipment;
import model.EquipmentDao;
import model.Equipmenttype;
import model.EquipmenttypeDao;
import model.Reservation;
import model.ReservationDao;

@RestController
public class ConfigurationController {
	Properties appProperties = PropertyUtils.loadProperties();

	@RequestMapping(value = "/rest/uploadEquipmentFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> uploadEquipmentFile(@RequestParam("file") MultipartFile file) throws IOException {
		if (!verifyFileExtension(file.getOriginalFilename())) {
			return new ResponseEntity<>("File extension should be \"xlsx\" (Excel spreadsheet).",
					HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		}

		File convertFile = new File("DataFiles" + File.separator + file.getOriginalFilename());
		convertFile.createNewFile();
		FileOutputStream fout = new FileOutputStream(convertFile);
		fout.write(file.getBytes());
		fout.close();

		String headerVerify;
		headerVerify = verifyEquipmentFileHeaders(convertFile.getAbsolutePath());
		if (!headerVerify.equals("OK")) {
			deleteFile(convertFile);
			return new ResponseEntity<>(
					"Spreadsheet headers wrong: " + headerVerify + ". \nFix configuration file or spreadsheet headers.",
					HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		}

		EquipmentDataReader.readEquipmentFromFile(convertFile.getPath());
		return new ResponseEntity<>("Equipment data read succesfully!", HttpStatus.OK);
	}

	@RequestMapping(value = "/rest/uploadTypeFile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> uploadTypeFile(@RequestParam("file") MultipartFile file) throws IOException {
		if (!verifyFileExtension(file.getOriginalFilename())) {
			return new ResponseEntity<>("File extension should be \"xlsx\" (Excel spreadsheet).",
					HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		}

		File convertFile = new File("DataFiles" + File.separator + file.getOriginalFilename());
		convertFile.createNewFile();
		FileOutputStream fout = new FileOutputStream(convertFile);
		fout.write(file.getBytes());
		fout.close();

		String headerVerify;
		headerVerify = verifyTypeFileHeaders(convertFile.getAbsolutePath());

		if (!headerVerify.equals("OK")) {
			deleteFile(convertFile);
			return new ResponseEntity<>(
					"Spreadsheet headers wrong: " + headerVerify + ". \nFix configuration file or spreadsheet headers.",
					HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		}

		EquipmentDataReader.readEquipmentTypesFromFile(convertFile.getPath());
		return new ResponseEntity<>("Equipment type data read succesfully!", HttpStatus.OK);
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

	public String verifyEquipmentFileHeaders(String filePath) {

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

	public String verifyTypeFileHeaders(String filePath) {
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