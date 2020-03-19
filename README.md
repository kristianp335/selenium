Download ChromeDriver and configure the path in the JUnit test.  
You can find ChromeDrivers here: https://chromedriver.chromium.org/home

This is a maven project with Selenium and JUnit.

Automates my demo environment https://tiercontent.liferayuk.com environment and loops 10 times.

There is a trick in the code which calls the **JavaScript executor** to run some JS which scrolls the page down. This is important for Liferay AB testing since the element is not clickable until you scroll to it.

