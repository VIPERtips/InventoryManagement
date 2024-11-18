package com.tips.InventoryManagement.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportLogger {
	private static final String LOG_FILE = "public/logs/report.txt";
	private ReportLogger() {}
	//set product
	public static void logReport(String  reportType,String productName,String brand,Integer quantity,double unitcost,double costPrice,String category,String username) {
		try(PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE,true)))
		{
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String timeStamp = LocalDateTime.now().format(formatter);
			writer.println(timeStamp + " - "+ reportType +" - "+productName+ " - "+brand + " - " +"Quantity "+ " - "+ quantity+ "  - "+ "Category "+ "  - "+ category + "  - " + "UnitPrice $" +unitcost+ "  - " +"CostPrice $"+costPrice + "  - " + "Initiated By "+ "  - " +username);
			System.out.println("log written");
		} catch (IOException e) {
			System.out.println("error writing to file");
		}
		
		
	}
	
	public static void logReport(String  reportType,String productName,double amount) {
		logReport(reportType, productName, amount);
		System.out.println("log written");
	}
}
