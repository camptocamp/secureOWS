CP := lib/commons-codec-1.3.jar:lib/commons-httpclient-3.0.1.jar:lib/commons-logging-1.1.jar:lib/commons-logging-adapters-1.1.jar:lib/commons-logging-api-1.1.jar:lib/junit.jar

compile:
	javac -d bin -classpath $(CP) src/com/camptocamp/owsproxy/TestWMS.java

run_tests: compile
	java -classpath $(CP):bin org.junit.runner.JUnitCore com.camptocamp.owsproxy.TestWMS


