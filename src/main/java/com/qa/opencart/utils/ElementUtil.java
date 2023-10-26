package com.qa.opencart.utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.qa.opencart.factory.DriverFactory;
import com.qa.opencart.frameworkexceptions.FrameException;

public class ElementUtil {
	private WebDriver driver;
	private JavaScriptUtil jsUtil;
	private final int DEFAULT_TIME_OUT = 5;

	public ElementUtil(WebDriver driver) {
		this.driver = driver;
		jsUtil = new JavaScriptUtil(this.driver);
	}

	public void doClear(By locator) {
		getElement(locator).clear();
	}

	public void doSendKeys(By locator, String value) {
		if (value == null) {
			System.out.println("null values are not allowed.");
			throw new FrameException("VALUECANNOTBENULL");
		}
		doClear(locator);
		getElement(locator).sendKeys(value);
	}

	public void doClick(By locator) {
		getElement(locator).click();
	}

	public void doClick(By locator, int timeout) {
		checkElementClickable(locator, timeout).click();
	}

	public WebElement getElement(By locator, int timeout) {
		WebElement element = waitForElementVisible(locator, timeout);
		if (Boolean.parseBoolean(DriverFactory.highlightElement)) {
			jsUtil.flash(element);
		}
		return element;
	}

	public WebElement getElement(By locator) {
		WebElement element = null;

		try {
			element = driver.findElement(locator);
			System.out.println("Element is found with locator.." + locator);
		} catch (NoSuchElementException e) {
			System.out.println("Element is not found using this locator.........." + locator);

			element = waitForElementVisible(locator, DEFAULT_TIME_OUT);
		}
		if (Boolean.parseBoolean(DriverFactory.highlightElement)) {
			jsUtil.flash(element);
		}
		return element;
	}

	public String doGetElementText(By locator) {
		return getElement(locator).getText();

	}

	public boolean checkelementIsDisplayed(By locator) {
		return getElement(locator).isDisplayed();

	}

	public String doGetAttributeValue(By locator, String attrName) {
		return getElement(locator).getAttribute(attrName);
	}

	public int getElementsCount(By locator) {
		return getElements(locator).size();
	}

	public List<String> getElementsAttributeValue(By locator, String attrName) {
		List<WebElement> eleList = getElements(locator);
		List<String> eleAttrList = new ArrayList<String>();
		for (WebElement e : eleList) {
			String attrValue = e.getAttribute(attrName);
			// System.out.println(attrValue);
			eleAttrList.add(attrValue);
		}
		return eleAttrList;
	}

	public List<String> getElementsTextList(By locator) {

		List<WebElement> elementsLinksList = getElements(locator);
		List<String> elesTextList = new ArrayList<String>();
		for (WebElement e : elementsLinksList) {
			String text = e.getText();
			elesTextList.add(text);
		}
		return elesTextList;
	}

	public List<WebElement> getElements(By locator) {
		return driver.findElements(locator);
	}

	public void clickElementFromPageSection(By locator, String eleText) {
		List<WebElement> eleList = getElements(locator);
		for (WebElement e : eleList) {
			String text = e.getText();
			System.out.println(text);
			if (text.equals(eleText)) {
				e.click();
				break;
			}
		}

	}

	public boolean checkElementIsDisplayed(By locator) {
		List<WebElement> eleList = getElements(locator);

		if (eleList.size() > 0) {
			System.out.println(locator + "element is present on the page");
			return true;

		}
		return false;

	}

	public void search(String searchKey, By searchLocator, String suggName, By suggestions)
			throws InterruptedException {
		doSendKeys(searchLocator, searchKey);
		Thread.sleep(3000);

		List<WebElement> suggList = getElements(suggestions);
		System.out.println("total suggestions: " + suggList.size());
		if (suggList.size() > 0) {
			for (WebElement e : suggList) {
				String text = e.getText();
				if (text.length() > 0) {
					System.out.println(text);
					if (text.contains(suggName)) {
						e.click();
						break;
					}
				} else {
					System.out.println("blank values --- no suggestions");
					break;
				}
			}
		} else {
			System.out.println("no search suggestions found");
		}
	}

	// ********************* Drop Down Utils ******************************//

	public void doSelectByDropDownByIndex(By locator, int index) {
		Select select = new Select(getElement(locator));
		select.selectByIndex(index);
	}

	public void doSelectByDropDownByVisibleText(By locator, String text) {
		Select select = new Select(getElement(locator));
		select.selectByVisibleText(text);
	}

	public void doSelectByDropDownByValueAttribute(By locator, String value) {
		Select select = new Select(getElement(locator));
		select.selectByValue(value);
	}

	public int getDropDownvalaueCount(By locator) {
		return getAllDropDownOptions(locator).size();
	}

	public List<String> getAllDropDownOptions(By locator) {
		Select select = new Select(getElement(locator));
		List<WebElement> optionsList = select.getOptions();
		List<String> optionsValueList = new ArrayList<String>();
		System.out.println("total values :" + optionsList.size());

		for (WebElement e : optionsList) {
			String text = e.getText();
			System.out.println(text);
			optionsValueList.add(text);
		}
		return optionsValueList;
	}

	public boolean doSelectDropDownValue(By locator, String dropDownValue) {
		boolean flag = false;
		Select select = new Select(getElement(locator));
		List<WebElement> optionsList = select.getOptions();
		System.out.println("total values :" + optionsList.size());

		for (WebElement e : optionsList) {
			String text = e.getText();

			if (text.equals(dropDownValue)) {
				flag = true;
				e.click();
				break;
			}

		}
		if (flag == false) {
			System.out.println(dropDownValue + "is not present in the drop down " + locator);
		}

		return flag;

	}

	public boolean doSelectValueFromDropDownWithoutSelect(By locator, String value) {
		boolean flag = false;
		List<WebElement> optionsList = getElements(locator);

		for (WebElement e : optionsList) {
			String text = e.getText();

			if (text.equals(value)) {
				flag = true;
				e.click();
				break;
			}
		}
		if (flag == false) {
			System.out.println(value + "is not present in the drop down " + locator);
		}

		return flag;
	}

	// *************************Actions class
	// Utils*************************************

	public void doActionsSendKeys(By locator, String value) {
		Actions act = new Actions(driver);
		act.sendKeys(getElement(locator), value).build().perform();
	}

	public void doActionsClick(By locator) {
		Actions act = new Actions(driver);
		act.click(getElement(locator)).build().perform();

	}

	public void doActionsClick(By locator, int timeout) {
		Actions act = new Actions(driver);
		act.click(checkElementClickable(locator, timeout)).build().perform();
	}

	public void doActions(By locator) {
		Actions act = new Actions(driver);
		act.click(getElement(locator)).build().perform();
	}

	public void doDragAndDrop(By sourceLocator, By targetLocator) {
		Actions act = new Actions(driver);
		act.dragAndDrop(getElement(sourceLocator), getElement(targetLocator)).build().perform();

	}

	public void doContextClick(By locator) {
		Actions act = new Actions(driver);
		act.contextClick(getElement(locator)).build().perform();

	}

	public void doMoveToElement(By locator) {
		Actions act = new Actions(driver);
		act.moveToElement(getElement(locator)).build().perform();
	}

	public void handleTwoLevelMenu(By parentMenu, By childMenu) throws InterruptedException {
		doMoveToElement(parentMenu);
		Thread.sleep(2000);
		doClick(childMenu);

	}

	public void handleTwoLevelMenu(By parentMenu, String childMenuLinkText) throws InterruptedException {
		doMoveToElement(parentMenu);
		Thread.sleep(2000);
		doClick(By.linkText(childMenuLinkText));

	}

	public void multiLevelMenuChildMenuHandle(By parentMenuLocator, String level2LinkText, String level3LinkText,
			String level4LinkText) throws InterruptedException {
		WebElement level1 = getElement(parentMenuLocator);

		Actions act = new Actions(driver);
		act.moveToElement(level1).build().perform();
		level1.click();
		Thread.sleep(1000);

		WebElement level2 = getElement(By.linkText(level2LinkText));
		act.moveToElement(level2).build().perform();
		Thread.sleep(1000);

		WebElement level3 = getElement(By.linkText(level3LinkText));
		act.moveToElement(level3).build().perform();
		Thread.sleep(1000);

		doClick(By.linkText(level4LinkText));

	}

	// ***********************Wait Utils********************************
	public Alert waitForAlertJsPopUpWithFluentWait(int timeout, int pollingTime) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout))
				.ignoring(NoAlertPresentException.class).pollingEvery(Duration.ofSeconds(pollingTime))
				.withMessage("----timeout is done-----element is not found.........");
		return wait.until(ExpectedConditions.alertIsPresent());

	}

	public Alert waitForAlertJsPopUp(int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		return wait.until(ExpectedConditions.alertIsPresent());
	}

	public String alertJSGetText(int timeout) {
		return waitForAlertJsPopUp(timeout).getText();
	}

	public void alertAccept(int timeout) {
		waitForAlertJsPopUp(timeout).accept();
	}

	public void alertDismiss(int timeout) {
		waitForAlertJsPopUp(timeout).dismiss();
	}

	public void EnterAlertValue(int timeout, String value) {
		waitForAlertJsPopUp(timeout).sendKeys(value);
	}

	public String waitForTitleIsAndCpature(String titleFragment, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		if (wait.until(ExpectedConditions.titleContains(titleFragment))) {
			String title = driver.getTitle();
			return title;
		} else {
			System.out.println("title is not present in the given timeout : " + timeout);
			return null;
		}

	}

	public String waitForFullTitleAndCpature(String titleVal, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		if (wait.until(ExpectedConditions.titleIs(titleVal))) {
			String title = driver.getTitle();
			return title;
		} else {
			System.out.println("title is not present in the given timeout : " + timeout);
			return null;
		}

	}

	public String waitForURLContainsAndCpature(String urlFragment, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		if (wait.until(ExpectedConditions.urlContains(urlFragment))) {
			String url = driver.getCurrentUrl();
			return url;
		} else {
			System.out.println("url is not present in the given timeout : " + timeout);
			return null;
		}

	}

	public String waitForURLAndCpature(String urlValue, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		if (wait.until(ExpectedConditions.urlToBe(urlValue))) {
			String url = driver.getCurrentUrl();
			return url;
		} else {
			System.out.println("url is not present in the given timeout : " + timeout);
			return null;
		}

	}

	public boolean waitForTotalWindows(int totalWindowsToBe, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		return wait.until(ExpectedConditions.numberOfWindowsToBe(totalWindowsToBe));
	}

	/*
	 * An expectation for checking that an element is present on the DOM of a page.
	 * This does not necessarily mean that the element is visible
	 */
	public WebElement waitForElementPresenece(By locator, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		return wait.until(ExpectedConditions.presenceOfElementLocated(locator));

	}

	/*
	 * An expectation for checking that an element is present on the DOM of a page
	 * and visible. Visibility means that the element is not only displayed but also
	 * has a height and width that is greater than 0. default timeout =500 ms;
	 * 
	 */

	public WebElement waitForElementVisible(By locator, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		if (Boolean.parseBoolean(DriverFactory.highlightElement)) {
			jsUtil.flash(element);
		}
		return element;

	}

	// default timeout =intervalTime
	public WebElement waitForElementVisible(By locator, int timeout, int intervalTime) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout), Duration.ofSeconds(intervalTime));
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

	}
	/*
	 * An expectation for checking that all elements present on the web page that
	 * match the locator are visible. Visibility means that the elements are not
	 * only displayed but also have a height and width that is greater than 0.
	 */

	public List<WebElement> waitForElementsVisible(By locator, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));

	}

	/*
	 * An expectation for checking that there is at least one element present on a
	 * web page.
	 * 
	 */

	public List<WebElement> waitForElementsPresence(By locator, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));

	}
	/*
	 * An expectation for checking an element is visible and enabled such that you
	 * can click it. Parameters:locator used to find the elementReturns:the
	 * WebElement once it is located and clickable (visible and enabled)
	 */

	public void clickElementWhenReady(By locator, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
		;
	}

	public WebElement checkElementClickable(By locator, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		return wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	// frames with wait:

	public void waitForFrameAndSwitchToItWithFluentWait(int timeout, int pollingTime, String idOrName) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout))
				.ignoring(NoSuchFrameException.class).pollingEvery(Duration.ofSeconds(pollingTime))
				.withMessage("----timeout is done-----frame is not found.........");
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(idOrName));

	}

	public void waitForFrameAndSwitchToItByIDorName(int timeout, String idOrName) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(idOrName));
	}

	public void waitForFrameAndSwitchToItByIndex(int timeout, int frameIndex) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameIndex));
	}

	public void waitForFrameAndSwitchToItByFrameElement(int timeout, WebElement frameElement) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
	}

	public void waitForFrameAndSwitchToItByFrameLocator(int timeout, By frameLocator) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
	}

	public WebElement waitForElementVisibleWithFluentWait(By locator, int timeout, int pollingTime) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
				.ignoring(ElementNotInteractableException.class).pollingEvery(Duration.ofSeconds(pollingTime))
				.withMessage("----timeout is done-----element is not found.........");
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

	}

	public WebElement waitForElementPresenceWithFluentWait(By locator, int timeout, int pollingTime) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeout))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class)
				.ignoring(ElementNotInteractableException.class).pollingEvery(Duration.ofSeconds(pollingTime))
				.withMessage("----timeout is done-----element is not found.........");
		return wait.until(ExpectedConditions.presenceOfElementLocated(locator));

	}

	public WebElement retryingElement(By locator, int timeout) {

		WebElement element = null;
		int attempts = 0;

		while (attempts < timeout) {
			try {
				element = getElement(locator);
				System.out.println("Element is found...." + locator + " in attempts: " + attempts);
				break;
			} catch (NoSuchElementException e) {
				System.out.println("Element is not found...." + locator + " in attempts: " + attempts);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			attempts++;
		}

		if (element == null) {
			System.out.println("element is not found...tried for " + timeout + "secs"
					+ " within the interval of 500 milliseconds");
		}
		return element;
	}

	public WebElement retryingElement(By locator, int timeout, int pollingTime) {

		WebElement element = null;
		int attempts = 0;

		while (attempts < timeout) {
			try {
				element = getElement(locator);
				System.out.println("Element is found...." + locator + " in attempts: " + attempts);
				break;
			} catch (NoSuchElementException e) {
				System.out.println("Element is not found...." + locator + " in attempts: " + attempts);
				try {
					Thread.sleep(pollingTime);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			attempts++;
		}

		if (element == null) {
			System.out.println("element is not found...tried for " + timeout + " secs "
					+ " within the interval of 500 milliseconds");
		}
		return element;
	}

	public boolean isPageLoaded() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		String flag = wait.until(ExpectedConditions.jsReturnsValue("return document.readyState == \'complete\'"))
				.toString();
		return Boolean.parseBoolean(flag);
	}

	public void waitForPageLoad(int timeout) {
		long endTime = System.currentTimeMillis() + timeout;
		while (System.currentTimeMillis() < endTime) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			String pageState = js.executeScript("return document.readyState").toString();
			if (pageState.equals("complete")) {
				System.out.println("PAGE DOM is fully loaded now.... ");
				break;
			} else {
				System.out.println("PAGE is not loaded....." + pageState);
			}
		}

	}

}
