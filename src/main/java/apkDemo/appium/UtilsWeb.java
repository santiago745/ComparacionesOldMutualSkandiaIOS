package apkDemo.appium;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.xml.sax.InputSource;



public class UtilsWeb {
	public WebDriver driver;
	public WebDriverWait wait;

	public int waitingTime=60;
	public String screenshotFolder="logs/";
	public String logFolder="logs/screenshots/";
	public final Logger LOGGER = Logger.getLogger(UtilsMobile.class.getName());

	public String fecha="";
	
	public UtilsWeb(String fecha) {

		System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("start-maximized");
		driver=new ChromeDriver(options);
		
		this.fecha=fecha;
	}
	
	public void startURL(String url) {
		driver.get(url);
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
	
	public void waitObject(String wb,int waitTime) {
        wait = new WebDriverWait(driver, waitTime);
        try {
        	wait.until(ExpectedConditions.elementToBeClickable(By.xpath(wb)));
        }catch(Exception e) {
        	System.err.println("No se pudo cargar el objeto "+e.getMessage());
        }
	}
	
	public WebElement objeto(String xpath) {
		WebElement elemento=driver.findElement(By.xpath(xpath));
		return elemento;
	}
	
	public void waitUntilGone(String xpath,int waitTime) {
		wait = new WebDriverWait(driver, waitTime);
        try {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
        }catch(Exception e) {
        	System.err.println("No se pudo cargar el objeto "+e.getMessage());
        }
	}
	
	public void scroll() {
		JavascriptExecutor jsx = (JavascriptExecutor)driver;
		jsx.executeScript("window.scrollBy(0,250)", "");
	}
	
	 public WebElement expandRootElement(WebElement element) {
			WebElement ele = (WebElement) ((JavascriptExecutor) driver)
	.executeScript("return arguments[0].shadowRoot",element);
			return ele;
		}
	 
	 public static String getPrettyString(String xmlData, int indent) throws Exception {
		 System.out.println("jueputa");
		  TransformerFactory transformerFactory = TransformerFactory.newInstance();
		  transformerFactory.setAttribute("indent-number", indent);

		  Transformer transformer = transformerFactory.newTransformer();
		  transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		  StringWriter stringWriter = new StringWriter();
		  StreamResult xmlOutput = new StreamResult(stringWriter);

		  Source xmlInput = new StreamSource(new StringReader(xmlData));
		  System.out.println("mierda");
		  transformer.transform(xmlInput, xmlOutput);
		  
		  return xmlOutput.getWriter().toString();
		 }
	 
	 public String prettyprintxml(String dom) {
		 org.w3c.dom.Element node=null;
		 
			try {
				node =  DocumentBuilderFactory
			    .newInstance()
			    .newDocumentBuilder()
			    .parse(new InputSource(new StringReader(dom)))
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
	 
/*****************************************/
	 
	 
	 
}
