package apkDemo.appium;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.MobileElement;

public class mobileDemo {
	UtilsMobile ut;

	
	public mobileDemo(String fecha) {
	
        ut=new UtilsMobile(fecha);
	}
	
	
	public void teardown() {
		ut.driver.quit();
	}
	

	 
	
    public void Login() throws InterruptedException {
    	  ut.driver.findElement(By.xpath("//*[@text='Skandia']")).click();
          Thread.sleep(5000);
          //ut.driver.findElement(By.xpath("//*[@class='UIAButton' and (./preceding-sibling::* | ./following-sibling::*)[@text='CertificacionesOff']]")).click();
       	  ut.revisarPaginaMobile("Primero", "Segundo");
      	  ut.screenshot("Primer Formulario");
    }
    
    public void accept(){
    	try {
		Thread.sleep(5000);
    	MobileElement validac1=ut.driver.findElementByXPath("//*[@resource-id='com.android.packageinstaller:id/permission_allow_button']");
    	validac1.click();
    	Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void CContacto() throws InterruptedException {
        ut.driver.findElement(By.xpath("//*[@class='UIAButton' and (./preceding-sibling::* | ./following-sibling::*)[@text='CertificacionesOff']]")).click();
     	  Thread.sleep(3000);
          ut.revisarSegundoDOM("Tercer", "Cuarto");
    	  ut.screenshot("Segundo Formulario");
          ut.driver.findElement(By.xpath("//*[@text='Back']")).click();
          Thread.sleep(2000);
    }
    
    public void Certificados() throws InterruptedException {
        ut.driver.findElement(By.xpath("//*[@class='UIAButton' and (./preceding-sibling::* | ./following-sibling::*)[@text='GuiaContacto']]")).click();
        Thread.sleep(2000);
        ut.revisarTercerDOM("Quinto", "Sexto");
  	    ut.screenshot("Tercer Formulario");
        ut.driver.findElement(By.xpath("//*[@text='icon back']")).click();
        Thread.sleep(2000);
    }
    
    public void ResetPassword() throws InterruptedException {
        ut.driver.findElement(By.xpath("//*[@text='¿Olvidó su contraseña?']")).click();
        Thread.sleep(2000);
        ut.revisarCuartoDOM("Septimo", "Octavo");
  	    ut.screenshot("Cuarto Formulario");
        ut.driver.findElement(By.xpath("//*[@text='icon back']")).click();
        Thread.sleep(2000);
    }
    
    public void Salida() throws InterruptedException {
        ut.driver.findElement(By.xpath("//*[@class='UIATextField']")).sendKeys("-112233000");
        Thread.sleep(2000);
        //new WebDriverWait(driver, 30).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@text='icpasslogin']")));
        ut.driver.findElement(By.xpath("//*[@text='icpasslogin']")).sendKeys("12");
        Thread.sleep(2000);
        ut.driver.findElement(By.xpath("//*[@text='INICIAR SESIÓN']")).click();
        Thread.sleep(2000);
        ut.screenshot("Finalizar");
	}
}

