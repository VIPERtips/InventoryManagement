package com.tips.InventoryManagement.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.catalina.connector.Response;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public class ReportController {
	@GetMapping("/generate-report")
	public ResponseEntity<byte[]> generateReport() throws IOException{
		String  logFilePath = "public/logs/report.txt";
		File logFile = new File(logFilePath);
		if(logFile.exists()) {
			byte[] bytes = Files.readAllBytes(logFile.toPath());
			return ResponseEntity.ok()
					.contentType(MediaType.TEXT_PLAIN)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.txt")
					.body(bytes);
		} else {
		return ResponseEntity.notFound().build();
		}
	}
}
