API Testing SetUp/Config
---------------------------------------------
	Used RunScope (API testing & monitoring tool)
	It supports different JavaScript library, mainly Underscore.js and Chai assertion library
	It's mainly web oriented. Install radar-agent to access web services deployed on local machine i.e. localhost.
	After installing Runscope, import test script file to project
	
UI Test Automation SetUp/Config
---------------------------------------------
	Using Eclipse IDE.
	Selenium WebDriver for Java
	Test project's set up requirement
		- Add JUnit 4 JAR
		- Add Homecrest.All JAR (homecrest.core as not sufficient enough)
		- Need to have 'test' folder as Source Folder for Eclipse to find test cases there.
		- Project folder structure should look like this. test (source folder) > selenium (package) > NssTodoUiTestDemo.java (attached file)
		- Right click on java file and select > Run As > JUnit Test
		- Workspace is added here with directory named 'Selenium-Workspace'
	