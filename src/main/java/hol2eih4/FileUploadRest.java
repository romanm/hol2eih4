package hol2eih4;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadRest {
	private static final Logger logger = LoggerFactory.getLogger(FileUploadRest.class);
	@Value("${config.applicationExcelFolderPfad}") private String applicationExcelFolderPfad;
	private String backupFileName(String fileName) {
		DateTime today = new DateTime();
		String timestampStr = AppConfig.yyyyMMddHHmmssDateFormat.format(today.toDate());
		String targetFile = fileName.substring(0,fileName.lastIndexOf("."))+"-"+timestampStr+fileName.substring(fileName.lastIndexOf("."));
		return targetFile;
	}
	@RequestMapping(value="/upload", method=RequestMethod.POST)
	public @ResponseBody String handleFileUpload(
			@RequestParam("file") MultipartFile file
			, @RequestParam("fileName") String fileName
			, Principal userPrincipal){

//		String dbFileViewDir = AppConfig.applicationExcelFolderPfad+ "";// propertiConfig.folderDb + propertiConfig.folderPublicFiles;
		String dbFileViewDir = applicationExcelFolderPfad+ "";// propertiConfig.folderDb + propertiConfig.folderPublicFiles;
		logger.debug(dbFileViewDir);
		File dm = openCreateFolder(dbFileViewDir);
		logger.debug(""+dm);
		logger.debug(""+file);
		logger.debug(""+fileName+" / "+AppConfig.getExcelfilename()+" / "+fileName.indexOf(AppConfig.getExcelfilename()));
		String backupFileName = backupFileName(AppConfig.getExcelfilename());
		logger.debug(backupFileName);

//		copyFile(AppConfig.applicationExcelFolderPfad + AppConfig.getExcelfilename()
//		, AppConfig.applicationExcelFolderPfad+"backup/"+ backupFileName);
		copyFile(applicationExcelFolderPfad + AppConfig.getExcelfilename()
				, applicationExcelFolderPfad+"backup/"+ backupFileName);

		if (fileName.indexOf(AppConfig.getExcelfilename()) >= 0) {
			if (!file.isEmpty()) {
				try {
					byte[] bytes = file.getBytes();
					BufferedOutputStream stream = 
							new BufferedOutputStream(new FileOutputStream(
									new File(applicationExcelFolderPfad + AppConfig.getExcelfilename())));
//					new File(AppConfig.applicationExcelFolderPfad + AppConfig.getExcelfilename())));
					stream.write(bytes);
					stream.close();
//					copyFile(AppConfig.applicationExcelFolderPfad + AppConfig.getExcelfilename()
					copyFile(applicationExcelFolderPfad + AppConfig.getExcelfilename()
							, innerExcelFolderPfad + AppConfig.getExcelfilename());
//					, AppConfig.innerExcelFolderPfad + AppConfig.getExcelfilename());
					return "You successfully uploaded " + fileName + "!";
				} catch (Exception e) {
					return "You failed to upload " + fileName + " => " + e.getMessage();
				}
			} else {
				return "You failed to upload " + fileName + " because the file was empty.";
			}
		}else{
			return "Ім’я файла не відповідає дійсності";
		}
	}

	@Value("${config.innerExcelFolderPfad}") private String innerExcelFolderPfad;

	public void copyFile(String sourceFileName,String targetFileName) {
		logger.debug("cp "+sourceFileName +" "+targetFileName);
		try {
			Path sourceFile = new File(sourceFileName).toPath();
			Path targetFile = new File(targetFileName).toPath();
			Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("OK");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private File openCreateFolder(String dir) {
		logger.debug(dir);
		File file = new File(dir);
		if(!file.exists())
		{
			logger.debug(dir);
			file.mkdirs();
		}
		return file;
	}
	
}
