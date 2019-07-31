package apkDemo.appium;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;


import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.By;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.logging.Level;

public class UtilsMobile {
	PrintWriter out;
	PrintWriter out2;
	
	public WebDriverWait wait;

	public int waitingTime=60;
	public final Logger LOGGER = Logger.getLogger(UtilsMobile.class.getName());
	public String screenshotFolder="logs/";
	private String configFile;
	public String fecha="";
	
	 public String reportDirectory = "reports";
	 public String reportFormat = "xml";
	 public String testName = "Untitled";
	 public IOSDriver<IOSElement> driver = null;
	
	public UtilsMobile(String fecha) {
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

	    DesiredCapabilities dc = new DesiredCapabilities();
	        dc.setCapability("reportDirectory", reportDirectory);
	        dc.setCapability("reportFormat", reportFormat);
	        dc.setCapability("testName", testName);
	        dc.setCapability(MobileCapabilityType.UDID, "44E81F56-C26F-4231-8254-9C974B84485C");
    	try {
            driver = new IOSDriver<IOSElement>(new URL("http://localhost:4723/wd/hub"), dc);
            driver.setLogLevel(Level.INFO);
		} catch (MalformedURLException e) {
			System.err.println("Error inicializando driver en server appium "+e.getMessage());
		}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
		this.fecha=fecha;
		
	}

	
	public ResourceBundle cargarPropiedades(String archivo) {
		ResourceBundle rb = ResourceBundle.getBundle(archivo);
		return rb;
	}
	
	public void screenshot(String descripcion) {
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File(screenshotFolder+"/"+fecha+"/screenshots/"+descripcion+".png"));
		} catch (IOException e) {
			System.err.println("No se pudo tomar el pantallazo "+e.getMessage());
		}
	}
	
	 public WebElement expandRootElement(WebElement element) {
			WebElement ele = (WebElement) ((JavascriptExecutor) driver)
	.executeScript("return arguments[0].shadowRoot",element);
			return ele;
		}
	 
	 public String prettyprintxml(String dom) {
		 org.w3c.dom.Element node=null;
			try {
				node =  DocumentBuilderFactory
			    .newInstance()
			    .newDocumentBuilder()
			    .parse(new ByteArrayInputStream(dom.getBytes()))
			    .getDocumentElement();
			} catch (Exception e) {
				System.err.println("Error parseando dom "+e.getMessage());
			} 
		 
		 Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			 transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			 //initialize StreamResult with File object to save to file
			 StreamResult result = new StreamResult(new StringWriter());
			 DOMSource source = new DOMSource(node);
			 try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				System.err.println("error "+e.getMessage());
			}
			 String xmlString = result.getWriter().toString();
			 return xmlString;
		} catch (Exception e1) {
			return null;
		}
		 
	 }
	 
	 
/****************************/
	 
		
		
	 public void revisarPaginaMobile(String domTemp, String domEstable) {
	    	
    	 try {
    		 //src/main/java/
 			out = new PrintWriter("recursos/"+domTemp+".txt");
 			String dom=driver.getPageSource();
 			dom=prettyprintxml(dom);
 			out.println(dom);
 			File f = new File("recursos/"+domEstable+".txt");
 			if(!f.exists()) { 
 			    out2 = new PrintWriter("recursos/"+domEstable+".txt");
 				out2.println(dom);
 				out2.close();
 				//screenshotFolder+"/"+fecha+"/screenshots/DOMLook.png", screenshotFolder+"/"+fecha+"/screenshots/DOMLookTemp.png"
 			}
     	 } catch (FileNotFoundException e) {
 			System.err.println("No se pudo crear archivo "+e.getMessage());
 		}finally{
 			out.close();
 			//out2.close();
 		}
    	 List<String> lista =compareDoms("recursos/"+domEstable+".txt","recursos/"+domTemp+".txt");
		for(int i=0;i<lista.size();i++) {
			
			System.out.println(lista.get(i));
		}
		revisarLookAndFeel();
		compareImages(screenshotFolder+"/"+fecha+"/screenshots/DOMLook.png", screenshotFolder+"/"+fecha+"/screenshots/DOMLookTemp.png");
    }
	 public void revisarLookAndFeel(){
			screenshot("DOMLookTemp");
			File f = new File("recursos/DOMLook.png");
			if(!f.exists()) { 
				screenshot("DOMLook");
				File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
				try {
					FileUtils.copyFile(scrFile, new File("recursos/DOMLook.png"));
				} catch (IOException e) {
					System.err.println("No se pudo tomar el pantallazo "+e.getMessage());
				}
			}else {
				try {
					Files.copy(Paths.get("recursos/DOMLook.png"), Paths.get(screenshotFolder+"/"+fecha+"/screenshots/DOMLook.png"), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					System.err.println("Error copiando archivo dom look en la carpeta de fecha "+e.getMessage());
				}
				
			}
			
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
		    	    
		    	    	if(!line.equals(line2)) {
		    	    		int index=StringUtils.indexOfDifference(line, line2);
		    	    		int i=0;
		    	    		if(index<5) {
		    	    			i=0;
		    	    		}else {
		    	    			i=5;
		    	    		}
		    	    		if(line.length()<=index+5||line2.length()<=index+5) {
		    	    			i=0;
		    	    		}
		    	    		String subtringDiferenciaOri=line.substring(index-i,index+i);
		    	    		String subtringDiferenciaDest=line2.substring(index-i,index+i);
		    	    		lista.add("Se encontró una diferencia en la linea "+contador+"\nDom original: " +line+"\nDom actual: "+line2+"\nDiferencia: ("+subtringDiferenciaOri+") --- ("+subtringDiferenciaDest+")\n---------------------------------");
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
		  /*---------------------2-----------------------*/
			public void revisarSegundoDOM(String domTemp2, String domEstable2) {
			    	
			    	 try {
			    		 //src/main/java/
			 			out = new PrintWriter("recursos/"+domTemp2+".txt");
			 			String dom=driver.getPageSource();
			 			dom=prettyprintxml(dom);
			 			out.println(dom);
			 			File f = new File("recursos/"+domEstable2+".txt");
			 			if(!f.exists()) { 
			 			    out2 = new PrintWriter("recursos/"+domEstable2+".txt");
			 				out2.println(dom);
			 				out2.close();
			 				//screenshotFolder+"/"+fecha+"/screenshots/DOMLook.png", screenshotFolder+"/"+fecha+"/screenshots/DOMLookTemp.png"
			 			}
			     	 } catch (FileNotFoundException e) {
			 			System.err.println("No se pudo crear archivo "+e.getMessage());
			 		}finally{
			 			out.close();
			 			//out2.close();
			 		}
			    	 List<String> lista =compareDomsTwo("recursos/"+domEstable2+".txt","recursos/"+domTemp2+".txt");//<----Llamado comparacion DOMS
					for(int i=0;i<lista.size();i++) {
						
						System.out.println(lista.get(i));
					}
					revisarLookAndFeelTwo();//<-------------------------Lamado metodo tomar images
					compareImagesTwo(screenshotFolder+"/"+fecha+"/screenshots/DOMLook2.png", screenshotFolder+"/"+fecha+"/screenshots/DOMLookTemp2.png");//<-------Llamado metodo comparacion imagenes
			    }
			  
			  public void revisarLookAndFeelTwo(){
					screenshot("DOMLookTemp2");
					File f = new File("recursos/DOMLook2.png");
					if(!f.exists()) { 
						screenshot("DOMLook2");
						File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
						try {
							FileUtils.copyFile(scrFile, new File("recursos/DOMLook2.png"));
						} catch (IOException e) {
							System.err.println("No se pudo tomar el pantallazo "+e.getMessage());
						}
					}else {
						try {
							Files.copy(Paths.get("recursos/DOMLook2.png"), Paths.get(screenshotFolder+"/"+fecha+"/screenshots/DOMLook2.png"), StandardCopyOption.REPLACE_EXISTING);
						} catch (IOException e) {
							System.err.println("Error copiando archivo dom look en la carpeta de fecha "+e.getMessage());
						}
						
					}
					
				}
			  
				public void compareImagesTwo(String file3,String file4) {
					System.out.println("Comparando imagenes de look and feel");
					BufferedImage imgA = null; 
			        BufferedImage imgB = null; 
			  
			        try
			        { 
			            File fileA = new File(file3); 
			            File fileB = new File(file4); 
			  
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
				
				  public List<String> compareDomsTwo(String ruta3, String ruta4) {
				    	BufferedReader br=null;
				    	BufferedReader br2=null;
				    	List<String> lista=null;
				    	try  {
				    		lista=new LinkedList<String>();
				    		br = new BufferedReader(new FileReader(ruta3));
				    		br2 = new BufferedReader(new FileReader(ruta4));
				    	    String line;
				    	    String line2;
				    	    int contador=0;
				    	    while ((line = br.readLine()) != null && (line2=br2.readLine()) !=null) {
				    	    	contador++;
				    	    
				    	    	if(!line.equals(line2)) {
				    	    		int index=StringUtils.indexOfDifference(line, line2);
				    	    		int i=0;
				    	    		if(index<5) {
				    	    			i=0;
				    	    		}else {
				    	    			i=5;
				    	    		}
				    	    		if(line.length()<=index+5||line2.length()<=index+5) {
				    	    			i=0;
				    	    		}
				    	    		String subtringDiferenciaOri=line.substring(index-i,index+i);
				    	    		String subtringDiferenciaDest=line2.substring(index-i,index+i);
				    	    		lista.add("Se encontró una diferencia en la linea "+contador+"\nDom original: " +line+"\nDom actual: "+line2+"\nDiferencia: ("+subtringDiferenciaOri+") --- ("+subtringDiferenciaDest+")\n---------------------------------");
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
/*---------------------3-----------------------*/
				  
	public void revisarTercerDOM(String domTemp3, String domEstable3) {
		
		 try {
			 //src/main/java/
			out = new PrintWriter("recursos/"+domTemp3+".txt");
			String dom=driver.getPageSource();
			dom=prettyprintxml(dom);
			out.println(dom);
			File f = new File("recursos/"+domEstable3+".txt");
			if(!f.exists()) { 
			    out2 = new PrintWriter("recursos/"+domEstable3+".txt");
				out2.println(dom);
				out2.close();
				//screenshotFolder+"/"+fecha+"/screenshots/DOMLook.png", screenshotFolder+"/"+fecha+"/screenshots/DOMLookTemp.png"
			}
	 	 } catch (FileNotFoundException e) {
			System.err.println("No se pudo crear archivo "+e.getMessage());
		}finally{
			out.close();
			//out2.close();
		}
		 List<String> lista =compareDomsThree("recursos/"+domEstable3+".txt","recursos/"+domTemp3+".txt");//<----Llamado comparacion DOMS
		for(int i=0;i<lista.size();i++) {
			
			System.out.println(lista.get(i));
		}
		revisarLookAndFeelThree();//<-------------------------Lamado metodo tomar images
		compareImagesThree(screenshotFolder+"/"+fecha+"/screenshots/DOMLook3.png", screenshotFolder+"/"+fecha+"/screenshots/DOMLookTemp3.png");//<-------Llamado metodo comparacion imagenes
	    }
	  
	  public void revisarLookAndFeelThree(){
			screenshot("DOMLookTemp3");
		File f = new File("recursos/DOMLook3.png");
		if(!f.exists()) { 
			screenshot("DOMLook3");
			File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			try {
				FileUtils.copyFile(scrFile, new File("recursos/DOMLook3.png"));
			} catch (IOException e) {
				System.err.println("No se pudo tomar el pantallazo "+e.getMessage());
			}
		}else {
			try {
				Files.copy(Paths.get("recursos/DOMLook3.png"), Paths.get(screenshotFolder+"/"+fecha+"/screenshots/DOMLook3.png"), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				System.err.println("Error copiando archivo dom look en la carpeta de fecha "+e.getMessage());
				}
				
			}
			
		}
	  
		public void compareImagesThree(String file5,String file6) {
			System.out.println("Comparando imagenes de look and feel");
			BufferedImage imgA = null; 
	        BufferedImage imgB = null; 
	  
	        try
	        { 
	            File fileA = new File(file5); 
	            File fileB = new File(file6); 
	  
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
	
	  public List<String> compareDomsThree(String ruta5, String ruta6) {
	    	BufferedReader br=null;
	    	BufferedReader br2=null;
	    	List<String> lista=null;
	    	try  {
	    		lista=new LinkedList<String>();
	    		br = new BufferedReader(new FileReader(ruta5));
	    		br2 = new BufferedReader(new FileReader(ruta6));
	    	    String line;
	    	    String line2;
	    	    int contador=0;
	    	    while ((line = br.readLine()) != null && (line2=br2.readLine()) !=null) {
	    	    	contador++;
	    	    
	    	    	if(!line.equals(line2)) {
	    	    		int index=StringUtils.indexOfDifference(line, line2);
	    	    		int i=0;
	    	    		if(index<5) {
	    	    			i=0;
	    	    		}else {
	    	    			i=5;
	    	    		}
	    	    		if(line.length()<=index+5||line2.length()<=index+5) {
	    	    			i=0;
	    	    		}
	    	    		String subtringDiferenciaOri=line.substring(index-i,index+i);
	    	    		String subtringDiferenciaDest=line2.substring(index-i,index+i);
	    	    		lista.add("Se encontró una diferencia en la linea "+contador+"\nDom original: " +line+"\nDom actual: "+line2+"\nDiferencia: ("+subtringDiferenciaOri+") --- ("+subtringDiferenciaDest+")\n---------------------------------");
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
	  /*---------------------4-----------------------*/
	  
		public void revisarCuartoDOM(String domTemp4, String domEstable4) {
			
			 try {
				 //src/main/java/
				out = new PrintWriter("recursos/"+domTemp4+".txt");
				String dom=driver.getPageSource();
				dom=prettyprintxml(dom);
				out.println(dom);
				File f = new File("recursos/"+domEstable4+".txt");
				if(!f.exists()) { 
				    out2 = new PrintWriter("recursos/"+domEstable4+".txt");
					out2.println(dom);
					out2.close();
					//screenshotFolder+"/"+fecha+"/screenshots/DOMLook.png", screenshotFolder+"/"+fecha+"/screenshots/DOMLookTemp.png"
				}
		 	 } catch (FileNotFoundException e) {
				System.err.println("No se pudo crear archivo "+e.getMessage());
			}finally{
				out.close();
				//out2.close();
			}
			 List<String> lista =compareDomsQuarter("recursos/"+domEstable4+".txt","recursos/"+domTemp4+".txt");//<----Llamado comparacion DOMS
			for(int i=0;i<lista.size();i++) {
				
				System.out.println(lista.get(i));
			}
			revisarLookAndFeelQuarter();//<-------------------------Lamado metodo tomar images
			compareImagesQuarter(screenshotFolder+"/"+fecha+"/screenshots/DOMLook3.png", screenshotFolder+"/"+fecha+"/screenshots/DOMLookTemp3.png");//<-------Llamado metodo comparacion imagenes
		    }
		  
		  public void revisarLookAndFeelQuarter(){
				screenshot("DOMLookTemp4");
			File f = new File("recursos/DOMLook4.png");
			if(!f.exists()) { 
				screenshot("DOMLook4");
				File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
				try {
					FileUtils.copyFile(scrFile, new File("recursos/DOMLook4.png"));
				} catch (IOException e) {
					System.err.println("No se pudo tomar el pantallazo "+e.getMessage());
				}
			}else {
				try {
					Files.copy(Paths.get("recursos/DOMLook4.png"), Paths.get(screenshotFolder+"/"+fecha+"/screenshots/DOMLook4.png"), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					System.err.println("Error copiando archivo dom look en la carpeta de fecha "+e.getMessage());
					}
					
				}
				
			}
		  
			public void compareImagesQuarter(String file7,String file8) {
				System.out.println("Comparando imagenes de look and feel");
				BufferedImage imgA = null; 
		        BufferedImage imgB = null; 
		  
		        try
		        { 
		            File fileA = new File(file7); 
		            File fileB = new File(file8); 
		  
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
		
		  public List<String> compareDomsQuarter(String ruta7, String ruta8) {
		    	BufferedReader br=null;
		    	BufferedReader br2=null;
		    	List<String> lista=null;
		    	try  {
		    		lista=new LinkedList<String>();
		    		br = new BufferedReader(new FileReader(ruta7));
		    		br2 = new BufferedReader(new FileReader(ruta8));
		    	    String line;
		    	    String line2;
		    	    int contador=0;
		    	    while ((line = br.readLine()) != null && (line2=br2.readLine()) !=null) {
		    	    	contador++;
		    	    
		    	    	if(!line.equals(line2)) {
		    	    		int index=StringUtils.indexOfDifference(line, line2);
		    	    		int i=0;
		    	    		if(index<5) {
		    	    			i=0;
		    	    		}else {
		    	    			i=5;
		    	    		}
		    	    		if(line.length()<=index+5||line2.length()<=index+5) {
		    	    			i=0;
		    	    		}
		    	    		String subtringDiferenciaOri=line.substring(index-i,index+i);
		    	    		String subtringDiferenciaDest=line2.substring(index-i,index+i);
		    	    		lista.add("Se encontró una diferencia en la linea "+contador+"\nDom original: " +line+"\nDom actual: "+line2+"\nDiferencia: ("+subtringDiferenciaOri+") --- ("+subtringDiferenciaDest+")\n---------------------------------");
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

}