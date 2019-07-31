package apkDemo.appium;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class webDemo {
	UtilsWeb utw;
	PrintWriter out;
	PrintWriter out2;

	public webDemo(String fecha) {
		utw=new UtilsWeb(fecha);
	}
	
	public void inicializarDriver() {
		String configFile="";
		configFile="./recursos.properties";
		Properties mainProperties = new Properties();

	    FileInputStream file;
	    try {
			file = new FileInputStream(configFile);
			mainProperties.load(file);
		    file.close();
		} catch (Exception e1) {
			System.err.println("No pude cargar archivo de propiedades "+e1.getMessage());
		}
		utw.startURL(mainProperties.getProperty("url"));
		utw.screenshot("Abrir pagina web");
		System.out.println("Generando aplicativo");
		webbalizer(mainProperties.getProperty("url"));
	}
	
	public void realizarAcciones() {
		
	}
	
	public void revisarPaginaWeb() {
    	
   	 try {
			out = new PrintWriter("recursos/domTempWeb.txt","UTF-8");
			String dom=utw.driver.getPageSource();
			out.println(dom);
			File f = new File("recursos/domWeb.txt");
			if(!f.exists()) { 
			    out2 = new PrintWriter("recursos/domWeb.txt");
				out2.println(dom);
				out2.close();
			}
    	 } catch (Exception e) {
			System.err.println("No se pudo crear archivo "+e.getMessage());
		}finally{
			out.close();
			//out2.close();
		}
   	 	List<String> lista =compareDoms("recursos/domWeb.txt","recursos/domTempWeb.txt");
		for(int i=0;i<lista.size();i++) {
			
			System.out.println(lista.get(i));
		}
		revisarLookAndFeel();
		compareImages(utw.screenshotFolder+"/"+utw.fecha+"/screenshots/DOMLookweb.png", utw.screenshotFolder+"/"+utw.fecha+"/screenshots/DOMLookTempweb.png");

   }
	
	public List<String> compareDoms(String ruta1, String ruta2) {
    	BufferedReader br=null;
    	BufferedReader br2=null;
    	List<String> lista=null;
    	try  {
    		lista=new LinkedList<String>();
    		br = new BufferedReader(new FileReader(ruta1));
    		br2 = new BufferedReader(new FileReader(ruta2));
    	    String line;
    	    String line2;
    	    int contador=0;

    	    while ((line = br.readLine()) != null && (line2=br2.readLine()) !=null) {
    	    	contador++;
    	    	//System.out.println("ORIGINAL "+line);
    	    	//System.out.println("ACTUAL "+line2);
    	    	if(!line.equals(line2)) {
    	    		int index=StringUtils.indexOfDifference(line, line2);
    	    		int i=0;
    	    		String subtringDiferenciaOri="";
    	    		String subtringDiferenciaDest="";
    	    		if(index<5) {
    	    			i=0;
    	    		}else {
    	    			i=5;
    	    		}
    	    		//System.out.println(line.length() +" ||||||||||||| "+index);

    	    		if(line.length()<=index+5||line2.length()<=index+5) {
    	    			i=0;
    	    		}
    	    		
    	    		subtringDiferenciaOri=line.substring(index-i,index+i);
    	    		subtringDiferenciaDest=line2.substring(index-i,index+i);
    	    		lista.add("Se encontró una diferencia en la linea "+contador+"\nDom original: " +line+"\nDom actual: "+line2+"\nDiferencia: ("+subtringDiferenciaOri+") --- ("+subtringDiferenciaDest+")\n---------------------------------");
    	    		//lista.add("Se encontró una diferencia en la linea "+contador+"\nDom original: " +line+"\nDom actual: "+line2+"\n---------------------------------");
    	    	}
    	    }
    	    
    	}catch(Exception e) {
    		System.err.println("Error leyendo archivos "+e.getMessage());
    	}finally {
    		try {
				br.close();
			} catch (IOException e) {
				System.err.println("No pude cerrar el primer archivo "+e.getMessage());
			}
    		try {
				br2.close();
			} catch (IOException e) {
				System.err.println("No pude cerrar el segundo archivo "+e.getMessage());
			}
    	}
    	return lista;
    }
	
	public void compareImages(String file1,String file2) {
		System.out.println("Comparando imagenes de look and feel");
		BufferedImage imgA = null; 
        BufferedImage imgB = null; 
  
        try
        { 
            File fileA = new File(file1); 
            File fileB = new File(file2); 
  
            imgA = ImageIO.read(fileA); 
            imgB = ImageIO.read(fileB); 
        } 
        catch (IOException e) 
        { 
            System.out.println(e); 
        } 
        int width1 = imgA.getWidth(); 
        int width2 = imgB.getWidth(); 
        int height1 = imgA.getHeight(); 
        int height2 = imgB.getHeight(); 
  
        if ((width1 != width2) || (height1 != height2)) 
            System.out.println("Error: Las dimensiones de la imagen son distintas"); 
        else
        { 
            long difference = 0; 
            for (int y = 0; y < height1; y++) 
            { 
                for (int x = 0; x < width1; x++) 
                { 
                    int rgbA = imgA.getRGB(x, y); 
                    int rgbB = imgB.getRGB(x, y); 
                    int redA = (rgbA >> 16) & 0xff; 
                    int greenA = (rgbA >> 8) & 0xff; 
                    int blueA = (rgbA) & 0xff; 
                    int redB = (rgbB >> 16) & 0xff; 
                    int greenB = (rgbB >> 8) & 0xff; 
                    int blueB = (rgbB) & 0xff; 
                    difference += Math.abs(redA - redB); 
                    difference += Math.abs(greenA - greenB); 
                    difference += Math.abs(blueA - blueB); 
                } 
            } 
            double total_pixels = width1 * height1 * 3; 
            double avg_different_pixels = difference / total_pixels; 
            double percentage = (avg_different_pixels/255)*100; 
  
            System.out.println("Porcentaje de diferencia en look and feel:"+ percentage); 
        } 
	}
	
	public void revisarLookAndFeel(){
		utw.screenshot("DOMLookTempweb");
		File f = new File("recursos/DOMLookweb.png");
		if(!f.exists()) { 
			utw.screenshot("DOMLookweb");
			File scrFile = ((TakesScreenshot)utw.driver).getScreenshotAs(OutputType.FILE);
			try {
				FileUtils.copyFile(scrFile, new File("recursos/DOMLookweb.png"));
			} catch (IOException e) {
				System.err.println("No se pudo tomar el pantallazo "+e.getMessage());
			}
		}else {
			try {
				Files.copy(Paths.get("recursos/DOMLookweb.png"), Paths.get(utw.screenshotFolder+"/"+utw.fecha+"/screenshots/DOMLookweb.png"), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				System.err.println("Error copiando archivo dom look en la carpeta de fecha "+e.getMessage());
			}

		}
		
	}
	
	public ResourceBundle cargarPropiedades(String archivo) {
		ResourceBundle rb = ResourceBundle.getBundle(archivo);
		return rb;
	}
	
	public void webbalizer(String url) {
		try {
		String[] comandos= {"cd "+System.getProperty("user.dir")+"\\phantomjs",
				"\""+System.getProperty("user.dir")+"/phantomjs/bin/phantomjs.exe\" phantalyzer.js "+url+""};
		ProcessBuilder builder = new ProcessBuilder(
	            "cmd.exe", "/c", comandos[0]+"&&"+comandos[1]);
	        builder.redirectErrorStream(true);
	        Process p;
			
				p = builder.start();
			
	        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String line;
	        while (true) {
	            line = r.readLine();
	            if (line == null) { break; }
	            if(line.contains("detectedApps:")) {
	            	System.out.println(line);
	            }
	           
	        }
			} catch (Exception e) {
				System.out.println("Error obteniendo inputline "+e.getMessage());
			}
	}
	
	public void teardown() {
		utw.driver.quit();
	}
}
