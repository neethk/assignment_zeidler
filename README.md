Setup :
-Import pom.xml to any of the IDE preferably IntelliJ
-Post import , click on "Run>Edit configuration"
-Add new Maven configuration 
-Select the cloned repo to "Working directoy" and uncheck "use project settings"
-Commandline: clean test -Denvironment=conf -Dbrowser=chrome -DskipTests=false -Drun=local -DsuiteXmlFile=src/test/resources/homepagetests.xml
-click on "runner"  and enter VM options as -DforkCount=0
-JRE should be 1.8 


Alternate way:(from Terminal)
1)clone the project 
2)change directory to the cloned repo
3)execute the command : mvn clean test -Denvironment=conf -Dbrowser=chrome -DskipTests=false -Drun=local -DsuiteXmlFile=src/test/resources/homepagetests.xml

This will start running all 7 testcases for Add new computer

Test case document:https://docs.google.com/spreadsheets/d/1oJ_5ZARiKcjIxcefR_IQ8jCcYO3601IUbh61O5EX_Nw/edit?usp=sharing
The testcases marked as done in this above document are the ones automated here.


Note:
-Have used Allure reporting for reporting 
- There is a "allure-results" folder which can be rendered using jenkins for detailed report
- The TestNG results can be found at  "/zeidler/target/surefire-reports/index.html" when opened in a browser
-screenshots of the failure can be found at "screenshot "folder in the repo


