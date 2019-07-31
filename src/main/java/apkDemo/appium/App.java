package apkDemo.appium;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class App {
	static String fecha;
	static String logfile;

	/*public static void main(String[] args) {
		
		if(args[0].equalsIgnoreCase("web")) {
			ejecutarWeb();
			App.excel();
		}else if(args[0].equalsIgnoreCase("movil")) {
			ejecutarMobile();
		}else {
			System.out.println("Ingrese si desea ejecutar \"web\" o \"movil\" como parametro");
		}
	}*/
	
	public static void ejecutarWeb() {
		App aplicacion = new App("web");
		webDemo mbd = new webDemo(fecha);
		mbd.inicializarDriver();
		mbd.revisarPaginaWeb();
		mbd.teardown();
		aplicacion.finalizarAplicacion();
		
		
	}
	
	public static void main (String[] args) throws InterruptedException /*ejecutarWeb()*/ {
		App aplicacion=new App("web"); 
		mobileDemo md=new mobileDemo(fecha);
		//md.Login();
		//md.Certificados();
		//md.Canales();
		//md.Validacion();
		//md.revisarPaginaMobile(); //<------ cambio importante en el DOM
		md.Login();
		//md.accept();
		md.CContacto();
		md.Certificados();
		md.ResetPassword();
		md.Salida();
		md.teardown();
		aplicacion.finalizarAplicacion();
	}

	public App(String carpeta) {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-hhmm");
		Date date = new Date();
		fecha = dateFormat.format(date);
		new File(System.getProperty("user.dir")+"/logs/" + fecha + "").mkdirs();
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream(System.getProperty("user.dir")+"/logs/" + fecha + "/logon.txt"));
		} catch (FileNotFoundException e) {
			System.err.println("No pude generar archivo de logs " + e.getMessage());
		}
		// ENVIA AL ARCHIVO DE TEXTO
		System.setOut(out);
		System.setErr(out);
		logfile=System.getProperty("user.dir")+"/logs/" + fecha + "/logon.txt";
	}

	public void finalizarAplicacion() {
	
		DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd-hhmmss");
		Date date2 = new Date();
		System.out.println("Finalizando ejecución a las " + dateFormat2.format(date2));
	}
	
	public static void excel() {
		String filename=System.getProperty("user.dir")+"/logs/" + fecha + "/reporte"+fecha+".xls";
		HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Diferencias");  

        HSSFRow rowhead = sheet.createRow((short)0);
        rowhead.createCell(0).setCellValue("No. linea");
        rowhead.createCell(1).setCellValue("Diferencia");
        rowhead.createCell(2).setCellValue("DOM original");
        rowhead.createCell(3).setCellValue("DOM actual");
        List<String> diferencias=retrieveLog("diferencias");
        int contadorDiferencias=0;
        int contadorApps=0;
        for(int i=0;i<diferencias.size();i++) {
        	contadorDiferencias++;
        	//System.out.println(diferencias.get(i)+"\n");
        	HSSFRow rowhead2 = sheet.createRow((short)i+1);
        	String[] linea=diferencias.get(i).split("\\|");
        	
            rowhead2.createCell(0).setCellValue(linea[0]);
            rowhead2.createCell(1).setCellValue(linea[1]);
            rowhead2.createCell(2).setCellValue(linea[2]);
            rowhead2.createCell(3).setCellValue(linea[3]);
        }
        
        

        HSSFSheet sheet2 = workbook.createSheet("APP");
        HSSFRow row = sheet2.createRow((short)0);
        row.createCell(0).setCellValue("Apps detectadas");
        diferencias=retrieveLog("apps");
        for(int i=0;i<diferencias.size();i++) {
        	int damn=i;
        	damn++;
        	String[] linea=diferencias.get(i).split("\\|");
        	
        	for(int c=0;c<linea.length;c++) {
        		contadorApps++;
        		row = sheet2.createRow((short)damn++);
            	row.createCell(0).setCellValue(linea[c]);
            
        	}
        	
        }
        
        
        
        diferencias=retrieveLog("ejecucion");
        
        HSSFSheet sheet3 = workbook.createSheet("Look and feel");
        HSSFRow row2 = sheet3.createRow((short)0);
        row2.createCell(0).setCellValue("porcentaje de diferencia look and feel");
        row2.createCell(1).setCellValue(diferencias.get(0));
        HSSFRow row3 = sheet3.createRow((short)1);
        row3.createCell(0).setCellValue("hora de ejecución");
        row3.createCell(1).setCellValue(diferencias.get(1));
        HSSFRow row4 = sheet3.createRow((short)2);
        row4.createCell(0).setCellValue("cantidad de diferencias");
        row4.createCell(1).setCellValue(contadorDiferencias);
        HSSFRow row5 = sheet3.createRow((short)3);
        row5.createCell(0).setCellValue("cantidad de apps usadas");
        row5.createCell(1).setCellValue(contadorApps);

        FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(filename);
		
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
		} catch (Exception e) {
			System.err.println("Error generando archivo de excel "+e.getMessage());
		}
        System.out.println("Se genera archivo de excel");
	}

	
	public static List<String> retrieveLog(String log) {
		
		BufferedReader br=null;
		List<String> lista=null;
		List<String> resultado=new LinkedList<String>();
		try {
		
		lista=new LinkedList<String>();
		br = new BufferedReader(new FileReader(logfile));
		String line;
		while ((line = br.readLine()) != null) {
			lista.add(line);
		}
		br.close();
		}catch(Exception e) {
			
			System.err.println("Error leyendo logs creacion de excel "+e.getMessage());
		}
		
		
		
		if(log.equalsIgnoreCase("diferencias")) {
			
			for(int i=0;i<lista.size();i++) {
				if(lista.get(i).contains("una diferencia en la linea")) {
					String numDiferencia = lista.get(i).substring(Math.max(lista.get(i).length() - 2, 0));
					i++;
					String domOriginal=lista.get(i).length() < 13 ? lista.get(i) : lista.get(i).substring(13, lista.get(i).length()-1);
					i++;
					String domActual=lista.get(i).length() < 11 ? lista.get(i) : lista.get(i).substring(11, lista.get(i).length()-1);
					i++;
					String diferencias=lista.get(i);
					resultado.add(numDiferencia+"|"+domOriginal+"|"+domActual+"|"+diferencias);;
				}
			}
		}else if(log.equalsIgnoreCase("apps")) {
			for(int i=0;i<lista.size();i++) {
				if(lista.get(i).contains("detectedApps:")) {
					String apps = lista.get(i);
					resultado.add(apps);
				    
				}
			}
			
		}else if(log.equalsIgnoreCase("ejecucion")) {
			for(int i=0;i<lista.size();i++) {
				if(lista.get(i).contains("Porcentaje de diferencia en look and feel:")) {
					String apps = lista.get(i);
					resultado.add(apps);
					i++;
					String rest=lista.get(i);
					resultado.add(rest);
				}
			}
		}
		
		return resultado;
	}
}
