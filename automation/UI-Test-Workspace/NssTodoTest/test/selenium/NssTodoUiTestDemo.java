package selenium;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.Select;

public class NssTodoUiTestDemo {

	private static WebDriver driver;
	
	
	// Declare UI elements
	WebElement txtTaskName;
	WebElement btnAddTask;
	WebElement categoryDropDown;
	Select categorySelect;
	
	WebElement dueDay;
	Select dueDaySelect;
	
	WebElement dueMonth;
	Select dueMonthSelect;
	
	WebElement dueYear;
	Select dueYearSelect;

	WebElement controlDiv;
	List<WebElement> categorySpanElemList;
	
	Map<String, String> categoryMap;
	
	@BeforeClass
	public static void Initialize() {
		
		// Environment Variable PATH refers to this directory. No need to add it everytime
		System.setProperty("webdriver.chrome.driver", "C://Selenium//chromedriver.exe");
		driver = new ChromeDriver();
		
		// Open home page of NSS-TODO-Automation
		driver.get("http://localhost/nss-todo-automation/index.php");
		
		// Maximize browser 
		driver.manage().window().maximize();
	}
	
	@AfterClass
	public static void CleanUp() {
		driver.close();
	}
	
	
	@Before
	public void SetUp() {
		
		txtTaskName = driver.findElement(By.xpath("//input[@name='data']"));
		btnAddTask = driver.findElement(By.xpath("//input[@value='Add']"));
		
		categoryDropDown = driver.findElement(By.name("category"));
		categorySelect = new Select(categoryDropDown);
		
		dueDay = driver.findElement(By.name("due_day"));
		dueDaySelect = new Select(dueDay);
		
		dueMonth = driver.findElement(By.name("due_month"));
		dueMonthSelect = new Select(dueMonth);
		
		dueYear = driver.findElement(By.name("due_year"));
		dueYearSelect = new Select(dueYear);
		
		controlDiv = driver.findElement(By.className("controls"));
		categorySpanElemList = controlDiv.findElements(By.xpath(".//span[contains(@style,'color:')]"));
		categoryMap = new HashMap<String, String>();
		
		for (WebElement span : categorySpanElemList) {
			String attrValue = span.getAttribute("style");
			String colorCode = attrValue.split(";")[0].split(":")[1].trim();
			String categoryName = span.getText().trim();
			categoryMap.put(categoryName, colorCode);
		}
	}
	
	@After
	public void TearDown() {
		
	}
	
	
	@Test
	// Create task without name shouldn't add any task
	public void TestCreateEmptyTask() throws InterruptedException {
				
		List<WebElement> taskListBefore = driver.findElements(By.tagName("li"));
		txtTaskName.clear();
		btnAddTask.click();
		List<WebElement> taskListAfter = driver.findElements(By.tagName("li"));
		assertEquals("Empty task was created. It shouldn't be allowed", taskListBefore.size(), taskListAfter.size());
	}
	
	@Test
	public void TestCreateTaskWithoutCategory() {
		
		String taskName = "Test create task without category";
		txtTaskName.sendKeys(taskName);
		btnAddTask.click();
		assertTrue("Task without category not created successfully", !driver.findElements(By.xpath("//*[text() [contains(.,'" + taskName + "')]]")).isEmpty());
	}
	
	@Test
	public void TestCreateTaskWithCategory() {
		String taskName = "Test create task with category";
		String categoryName = "Leisure";
		txtTaskName.sendKeys(taskName);
		categorySelect.selectByVisibleText(categoryName);
		btnAddTask.click();
		List<WebElement> foundTasks = driver.findElements(By.xpath("//*[text() [contains(.,'" + taskName + "')]]"));
		assertTrue("Task with category not created successfully", !foundTasks.isEmpty());
		
		// Created task is wrapped in Span because it's created with category
		WebElement createdTaskSpan = foundTasks.get(0);
		String attrValue = createdTaskSpan.getAttribute("style");
		String colorCode = attrValue.split(";")[0].split(":")[1].trim();
		if(categoryMap.containsKey(categoryName)) {
			assertEquals("Category is not matching", categoryMap.get(categoryName), colorCode);			
		}
		else {
			System.out.println("Unexpected: Category " + categoryName + " is not found in CategoryMap");
		}
	}
	
	@Test
	public void TestCreateTaskWithFutureDueDate() {
		
		String taskName = "Test create task with future due date and no category";
		String expectedDueDate = "22/08/19";
		
		txtTaskName.sendKeys(taskName);
		dueDaySelect.selectByValue("22");
		dueMonthSelect.selectByValue("8");
		dueYearSelect.selectByValue("2019");
		btnAddTask.click();		
		
		List<WebElement> foundTasks = driver.findElements(By.xpath("//*[text() [contains(.,'" + taskName + "')]]"));
		assertTrue("Task with category and due date not created successfully", !foundTasks.isEmpty());
		
		// Verify DueDate
		WebElement createdTaskLi = foundTasks.get(0);
		String taskNameWithDueDate = createdTaskLi.getText().trim();
		assertTrue("Due Date not matching in newly created task", taskNameWithDueDate.contains(expectedDueDate));
	}
	
	@Test
	public void TestCreateTaskWithPastDueDateWithCategory() {
		// Past Due date task are Overdue. Hence, red colored text. 
		// Assigned Category is written in adjacent span element with correct category color and its name inside parenthesis 
		
		String taskName = "Test create task with past due date and no category";
		String expectedDueDate = "17/01/18";
		String categoryName = "Personal";
		String overDueCategoryName = "Overdue";
		
		txtTaskName.sendKeys(taskName);
		categorySelect.selectByVisibleText(categoryName);
		dueDaySelect.selectByValue("17");
		dueMonthSelect.selectByValue("1");
		dueYearSelect.selectByValue("2018");
		btnAddTask.click();		
		
		List<WebElement> foundTasks = driver.findElements(By.xpath("//*[text() [contains(.,'" + taskName + "')]]"));
		assertTrue("Task with category and due date not created successfully", !foundTasks.isEmpty());
		
		// Verify DueDate
		WebElement createdTaskSpan = foundTasks.get(0);
		String taskNameWithDueDate = createdTaskSpan.getText().trim();
		assertTrue("Due Date not matching in newly created task", taskNameWithDueDate.contains(expectedDueDate));
		
		// Verify category color of Overdue task
		String taskSpanStyle = createdTaskSpan.getAttribute("style");
		String colorCode = taskSpanStyle.split(";")[0].split(":")[1].trim();
		if(categoryMap.containsKey(overDueCategoryName)) {
			assertEquals("Category is not matching", categoryMap.get(overDueCategoryName), colorCode);			
		}
		else {
			System.out.println("Unexpected: Category " + overDueCategoryName + " is not found in CategoryMap");
		}
		
		// Verify assigned category. Find next following span with text containing assigned category. Verify its color  
		WebElement categorySpan = createdTaskSpan.findElement(By.xpath("./following-sibling::span[text() [contains(.,'" + categoryName + "')]][1]"));
		if(categoryMap.containsKey(categoryName)) {
			assertEquals("Category is not matching", categoryMap.get(categoryName), categorySpan.getAttribute("style").split(";")[0].split(":")[1].trim());			
		}
		else {
			System.out.println("Unexpected: Category " + categoryName + " is not found in CategoryMap");
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		
	}

}
