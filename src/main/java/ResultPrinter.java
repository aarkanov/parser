import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;

public class ResultPrinter {

    public static void main(String[]args) throws Exception {
        Reader.loadTestSuiteFile();

        //Test suite name
        printAllTestSuiteNames();

        //Print out the total number of tests that passed and their details
        printAllTestDetails(Reader.Result.PASS);

        //Print out the total number of tests that failed and their details
        printAllTestDetails(Reader.Result.FAIL);

        //Print out the total number of tests that are blocked and their details
        printAllTestDetails(Reader.Result.BLOCKED);

        //Print out the total number of test that took more than 10 seconds to execute
        printAllTestsGreaterThanDuration(10.0);
    }


    private static void printAllTestSuiteNames() throws Exception {
        String[] suiteNames = Reader.getTestSuiteNames();

        if (suiteNames.length != 0) {
            Logger.logOutput("Test suite names are");
            for (String suite : suiteNames) {
                Logger.logComment(suite);
            }
        } else {
            Logger.logWarning(String.format("There are no test suite's in json file %s", Reader.getTestSuiteFilename()));
        }
    }

    private static void printAllTestDetails(Reader.Result result) throws Exception {
        HashMap<String, List<JSONObject>> testResults = Reader.getAllTests(result);

        String testName;
        String testExecutionTime;

        //Check if any suite with pass tests available
        if (!testResults.keySet().isEmpty()) {
            Logger.logOutput(String.format("%s List --> Total number of tests that %s and their details", result.toString().toUpperCase(), result.toString()));

            //for each suite
            for (String suite : testResults.keySet()) {
                Logger.logAction("Test Suite --> " + suite);

                //get pass results and time duration
                for (int i = 0; i < testResults.get(suite).size(); i++) {
                    testName = testResults.get(suite).get(i).get("test_name").toString();
                    testExecutionTime = testResults.get(suite).get(i).get("time").toString();
                    Logger.logAction(String.format("%d) %s      -        %s", i+1, testName, testExecutionTime));
                }
            }
            if (result.toString().toLowerCase().contains("blocked")) {
                Logger.logComment("Note - Blocked tests did not execute and hence should not have any duration");
            }
        } else {
            Logger.logWarning(String.format("There are no %s tests in json file %s", result.toString().toUpperCase(), Reader.getTestSuiteFilename()));
        }
    }

    private static void printAllTestsGreaterThanDuration(Double executionTime) throws Exception {
        HashMap<String, List<JSONObject>> timedTests = Reader.getAllTestsGreaterThanDuration(executionTime);

        String testName;
        String testExecutionTime;

        //Check if any suite available with duration greater than expected
        if (!timedTests.keySet().isEmpty()) {
            Logger.logOutput(String.format("Execution Time GREATER than %1$,.2f seconds",executionTime));

            //for each suite
            for (String suite : timedTests.keySet()) {
                Logger.logAction("Test Suite --> " + suite);

                ////get fail tests and time duration
                for (int i = 0; i < timedTests.get(suite).size(); i++) {
                    testName = timedTests.get(suite).get(i).get("test_name").toString();
                    testExecutionTime = timedTests.get(suite).get(i).get("time").toString();
                    Logger.logAction(String.format("%d) %s      -        %s", i+1, testName, testExecutionTime));
                }
            }
        } else {
            Logger.logWarning(String.format("There are no tests that ran greater than %1$,.2f in json file %s", executionTime, Reader.getTestSuiteFilename()));
        }
    }
}
