package com.qa.opencart.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.log4testng.Logger;

import com.qa.opencart.frameworkexceptions.FrameException;

public class DriverFactory {
	WebDriver driver;
	OptionsManager optionsManager;
	public Properties prop;
	public static String highlightElement;

	public static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<WebDriver>();

	public static final Logger log = Logger.getLogger(DriverFactory.class);
	// info,warn,error,fatal

	public WebDriver initDriver(Properties prop) {

		String browserName = prop.getProperty("browser").trim();
		System.out.println("Browser name is: " + browserName);
		log.info("Browser name is :" + browserName);

		highlightElement = prop.getProperty("highlight");
		optionsManager = new OptionsManager(prop);

		switch (browserName.toLowerCase()) {
		case "chrome":
			if (Boolean.parseBoolean(prop.getProperty("remote"))) {
				// run on grid/remote:
				init_remoteDriver("chrome");

			} else {
				// run on local
				System.out.println("Running tests on local");
				log.info("Running tests on local");
				tlDriver.set(new ChromeDriver(optionsManager.getChromeOptions()));
			}

			break;
		case "edge":
			if (Boolean.parseBoolean(prop.getProperty("remote"))) {
				// run on grid/remote:
				init_remoteDriver("edge");
			} else {
				// run on local
				System.out.println("Running tests on local");
				log.info("Running tests on local");
				tlDriver.set(new EdgeDriver(optionsManager.getEdgeOptions()));
			}
			break;
		case "firefox":
			if (Boolean.parseBoolean(prop.getProperty("remote"))) {
				// run on grid/remote:
				init_remoteDriver("firefox");
			} else {
				// run on local
				System.out.println("Running tests on local");
				log.info("Running tests on local");
				tlDriver.set(new FirefoxDriver(optionsManager.getFirefoxOptions()));
			}

			break;
		default:
			System.out.println("please pass the right browser....." + browserName);
			log.error("please pass the right browser....." + browserName);
			throw new FrameException("NOBROWSERFOUNDEXCEPTION");
		}
		getDriver().manage().deleteAllCookies();
		getDriver().manage().window().maximize();
		getDriver().get(prop.getProperty("url"));
		return getDriver();
	}

	private void init_remoteDriver(String browserName) {
		System.out.println("Running tests on grid with browser: " + browserName);
		try {
			switch (browserName.toLowerCase()) {
			case "chrome":

				tlDriver.set(
						new RemoteWebDriver(new URL(prop.getProperty("huburl")), optionsManager.getChromeOptions()));
				break;
			case "edge":
				tlDriver.set(new RemoteWebDriver(new URL(prop.getProperty("huburl")), optionsManager.getEdgeOptions()));
				break;
			case "firefox":
				tlDriver.set(
						new RemoteWebDriver(new URL(prop.getProperty("huburl")), optionsManager.getFirefoxOptions()));
				break;

			default:
				break;

			}
		}

		catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	// return the thread local copy of the driver
	public synchronized static WebDriver getDriver() {
		return tlDriver.get();
	}

	public Properties initProp() {

		// mvn clean install -Denv="qa"
		prop = new Properties();
		FileInputStream ip = null;

		String envName = System.getProperty("env");
		System.out.println("Running test cases on env:  " + envName);
		try {
			if (envName == null) {
				System.out.println("no env is given .....hence running it on QA env.....");
				ip = new FileInputStream("./src/main/resources/config/qa.config.properties");
			} else {
				switch (envName.toLowerCase().trim()) {
				case "qa":
					ip = new FileInputStream("./src/main/resources/config/qa.config.properties");
					break;
				case "dev":
					ip = new FileInputStream("./src/main/resources/config/dev.config.properties");
					break;
				case "stage":
					ip = new FileInputStream("./src/main/resources/config/stage.config.properties");
					break;
				case "uat":
					ip = new FileInputStream("./src/main/resources/config/uat.config.properties");
					break;
				case "prod":
					ip = new FileInputStream("./src/main/resources/config/config.properties");
					break;

				default:
					System.out.println("plz pass the right env name....." + envName);
					throw new FrameException("NOVALIDENVGIVEN");

				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			prop.load(ip);
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("Properties are ====>" + prop);
		return prop;

	}

	/*
	 * take screenshot
	 */

	public static String getScreenshot() {
		File srcFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
		String path = System.getProperty("user.dir") + "/screenshot/" + System.currentTimeMillis() + ".png";
		File destination = new File(path);

		try {
			FileUtils.copyFile(srcFile, destination);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;

	}

}
