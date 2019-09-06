package za.co.technoris.iamfit;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.SessionReadResponse;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import za.co.technoris.iamfit.ble.HeartRate;
import za.co.technoris.iamfit.ble.SleepDataDay;
import za.co.technoris.iamfit.ble.SportDataItem;
import za.co.technoris.iamfit.common.logger.Log;
import za.co.technoris.iamfit.common.logger.LogView;
import za.co.technoris.iamfit.common.logger.LogWrapper;
import za.co.technoris.iamfit.common.logger.MessageOnlyLogFilter;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getDateTimeInstance;
import static java.util.concurrent.TimeUnit.MINUTES;
import static za.co.technoris.iamfit.helper.Helper.parseTime;

/**
 * This sample demonstrates how to use the Sessions API of the Google Fit platform to insert
 * sessions into the History API, query against existing data, and remove sessions. It also
 * demonstrates how to authenticate a user with Google Play Services and how to properly
 * represent data in a Session, as well as how to use ActivitySegments.
 */
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int BODY_SENSORS_PERMISSIONS_REQUEST_CODE = 55;
    private static final String SLEEP_SESSION_NAME = "Nightly Sleep";
    private static final UUID UniqueID = new UUID(154646, 354984);
    public static final Locale enZA = new Locale("en", "ZA");
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss", enZA);
    public static final String TAG = "IAMFit";
    String path = "/storage/self/primary/veryfit2.1/syn/";
    File directory = new File(path);
    File[] filesList = directory.listFiles();
    String selectedLog;
    static SportDataItem sportDataItem = new SportDataItem();
    SleepDataDay sleepDataDay = new SleepDataDay();
    static HeartRate heartRate = new HeartRate();
    static int hrLine = 0;
    int stepLine = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // This method sets up our custom logger, which will print all log messages to the device
        // screen, as well as to adb logcat.
        initializeLogging();

        ArrayAdapter<String> adapter = null;
        try {
            List<String> logs = new ArrayList<>();
            for (File file1 : filesList) {
                logs.add(file1.getName());
            }
            Collections.sort(logs, Collections.reverseOrder());
            adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, logs);
        }
        catch (NullPointerException ex) {
            Log.e("Files", ex.getMessage());
        }
        Spinner spinner = findViewById(R.id.logs_spinner);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                clearLogView();
                selectedLog = (String)((Spinner)findViewById(R.id.logs_spinner)).getSelectedItem();
                hrLine = 0;
                stepLine = 0;
                readFile(path + selectedLog);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedLog = (String)((Spinner)findViewById(R.id.logs_spinner)).getSelectedItem();
            }
        });
    }

    private void readFile(String filename) {
        BufferedReader bufferedReader;
        try {
            selectedLog = extractDate((String)((Spinner)findViewById(R.id.logs_spinner)).getSelectedItem());
            FileInputStream fileInputStream = new FileInputStream(filename);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String strLine;
            String[] splitStr;
            int line = 0;
            while ((strLine = bufferedReader.readLine()) != null) {
                if (strLine.contains(selectedLog.replace("-",""))) {
                    if (strLine.contains("SportDataItem")){
                        splitStr = strLine.split(", ");
                        sportDataItem.setDate(Long.valueOf(splitStr[1].split("=")[1]));
                        sportDataItem.setHour(Integer.valueOf(splitStr[2].split("=")[1]));
                        sportDataItem.setMinute(Integer.valueOf(splitStr[3].split("=")[1]));
                        sportDataItem.setStepCount(Integer.valueOf(splitStr[5].split("=")[1]));
                        // When permissions are revoked the app is restarted so here is sufficient to check for
                        // permissions core to the Activity's functionality
                        int stepCount = sportDataItem.getStepCount();
                        if (line > 0 && stepCount > 0) {
                            stepLine++;
                            Log.i(TAG, sportDataItem.toString());
                            if (hasRuntimePermissions()) {
                                insertAndVerifySteps();
                            } else {
                                requestRuntimePermissions();
                            }
                        }
                        line++;
                    } else if (strLine.contains("SleepDataDay")) {
                        splitStr = strLine.split(", ");
                        sleepDataDay.setDate(Long.valueOf(splitStr[0].split("=")[1]));
                        sleepDataDay.setEndTimeHour(Integer.valueOf(splitStr[1].split("=")[1]));
                        sleepDataDay.setEndTimeMinute(Integer.valueOf(splitStr[2].split("=")[1]));
                        sleepDataDay.setTotalSleepMinutes(Integer.valueOf(splitStr[3].split("=")[1]));
                        Log.i(TAG, sleepDataDay.toString());
                        if (hasRuntimePermissions()) {
                            insertAndVerifySessionWrapper();
                        } else {
                            requestRuntimePermissions();
                        }
                    } else if (strLine.contains("HeartRate{")) {
                        hrLine++;
                        splitStr = strLine.split(", ");
                        heartRate.setDate(Long.valueOf(splitStr[1].split("=")[1]));
                        heartRate.setMinute(Integer.valueOf(splitStr[2].split("=")[1]));
                        heartRate.setRate(Integer.valueOf(removeLastChar(splitStr[3].split("=")[1])));
                        Log.i(TAG, heartRate.toString());
                        if (hasRuntimeHRPermissions()) {
                            insertandVerifyHR();
                        } else {
                            requestRuntimeHRPermissions();
                        }
                    }
                }
            }
            Log.i(TAG, "------\n");
            bufferedReader.close();
        }
        catch (IOException ex)
        {
            Log.e(TAG, ex.getLocalizedMessage());
        }
        catch (NullPointerException ex)
        {
            Log.e(TAG, ex.getMessage());
        }
    }

    private void insertandVerifyHR() {
        insertHRData()
            .continueWithTask(
                    new Continuation<Void, Task<DataReadResponse>>() {
                        @Override
                        public Task<DataReadResponse> then(@NonNull Task<Void> task) throws Exception {
                            // Read history of HR data inserted
                            return readHistoryHRData();
                        }
                    });
        // At this point, the data has been inserted and can be read.
        if (hrLine == 1) {
            Log.i(TAG, "HR Data insert was successful!");
        }
    }

    private Task<Void> insertHRData() {
        // Create a new dataset and insertion request.
        DataSet dataSet = insertFitnessHRData();

        // Then, invoke the History API to insert the data.
        if (hrLine == 1) {
            Log.i(TAG, "Inserting the HR dataset in the History API.");
        }
        return Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .insertData(dataSet)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // At this point, the data has been inserted and can be read.
//                                    Log.i(TAG, "HR Data insert was successful!");
                                } else {
                                    Log.e(TAG, "There was a problem inserting the HR dataset.", task.getException());
                                }
                            }
                        });
    }

    /**
     * Asynchronous task to read the history data. When the task succeeds, it will print out the data.
     */
    private Task<DataReadResponse> readHistoryHRData() {
        // Begin by creating the query.
        DataReadRequest readRequest = queryFitnessHRData();

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                // For the sake of the sample, we'll print the data so we can see what we just
                                // added. In general, logging fitness information should be avoided for privacy
                                // reasons.
//                                printData(dataReadResponse);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "There was a problem reading the data.", e);
                            }
                        });
    }

    /**
     * Creates and returns a {@link DataSet} of step count data for insertion using the History API.
     */
    private DataSet insertFitnessHRData() {
        if (hrLine == 1) {
            Log.i(TAG, "Creating a new HR data insert request.");
        }
DataSet dataSet = null;
        // [START build_insert_data_request]
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        try {
            // [START build_insert_data_request]
            // Set a start and end time for our data, using a start time of 1 hour before this moment.
            String stepsDate = heartRate.getDate() + " " + parseTime(heartRate.getMinute());
//            String stepsDate1 = sportDataDay.getDate() + " 00:00:00";
            long endTime = formatter.parse(stepsDate).getTime();
//            long startTime = formatter.parse(stepsDate1).getTime();

        // Create a data source
        DataSource dataSource =
                new DataSource.Builder()
                        .setAppPackageName(this)
                        .setDataType(DataType.TYPE_HEART_RATE_BPM)
                        .setStreamName(TAG + " - hr count")
                        .setType(DataSource.TYPE_RAW)
                        .build();

        // Create a data set
        float hRate = (float)heartRate.getRate();
        dataSet = DataSet.create(dataSource);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPoint =
                dataSet.createDataPoint().setTimeInterval(0, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_BPM).setFloat(hRate);
        dataSet.add(dataPoint);
        // [END build_insert_data_request]
        }
        catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return dataSet;
    }

    /** Returns a {@link DataReadRequest} for all heart rate changes in the past day. */
    public static DataReadRequest queryFitnessHRData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 2 days before this moment.
        DataReadRequest readRequest = null;
        try {
            // [START build_insert_data_request]
            // Set a start and end time for our data, using a start time of 1 hour before this moment.
            String stepsDate = heartRate.getDate() + " " + parseTime(heartRate.getMinute());
//            String stepsDate1 = sportDataDay.getDate() + " 00:00:00";
            long endTime = formatter.parse(stepsDate).getTime();
//            long startTime = formatter.parse(stepsDate1).getTime();

        java.text.DateFormat dateFormat = getDateTimeInstance();
//        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
            if (hrLine == 1) {
                Log.i(TAG, "Range Minute: " + dateFormat.format(endTime));
            }

        readRequest =
                new DataReadRequest.Builder()
                        // The data request can specify multiple data types to return, effectively
                        // combining multiple data queries into one call.
                        // In this example, it's very unlikely that the request is for several hundred
                        // datapoints each consisting of a few steps and a timestamp.  The more likely
                        // scenario is wanting to see how many steps were walked per day, for 7 days.
                        .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                        .bucketByTime(1, TimeUnit.DAYS)
                        .setTimeRange(0, endTime, TimeUnit.MILLISECONDS)
                        .build();
        // [END build_read_data_request]
        }
        catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return readRequest;
    }

    /**
     * Inserts and reads data by chaining {@link Task} from {@link #insertData()} and {@link
     * #readHistoryData()}.
     */
    private void insertAndVerifySteps() {
        insertData()
                .continueWithTask(
                        new Continuation<Void, Task<DataReadResponse>>() {
                            @Override
                            public Task<DataReadResponse> then(@NonNull Task<Void> task) throws Exception {
                                return readHistoryData();
                            }
                        });
        if (stepLine == 2) {
            Log.i(TAG, "Steps data insert was successful!");
        }
    }

    /** Creates a {@link DataSet} and inserts it into user's Google Fit history. */
    private Task<Void> insertData() {
        // Create a new dataset and insertion request.
        DataSet dataSet = insertFitnessData();

        // Then, invoke the History API to insert the data.
        if (stepLine == 2) {
            Log.i(TAG, "Inserting the steps dataset in the History API.");
        }
        return Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .insertData(dataSet)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // At this point, the data has been inserted and can be read.
//                                    Log.i(TAG, "Steps data insert was successful!");
                                } else {
                                    Log.e(TAG, "There was a problem inserting the steps dataset.", task.getException());
                                }
                            }
                        });
    }

    /**
     * Asynchronous task to read the history data. When the task succeeds, it will print out the data.
     */
    private Task<DataReadResponse> readHistoryData() {
        // Begin by creating the query.
        DataReadRequest readRequest = queryFitnessData();

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                // For the sake of the sample, we'll print the data so we can see what we just
                                // added. In general, logging fitness information should be avoided for privacy
                                // reasons.
//                                printData(dataReadResponse);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "There was a problem reading the data.", e);
                            }
                        });
    }

    /**
     * Creates and returns a {@link DataSet} of step count data for insertion using the History API.
     */
    private DataSet insertFitnessData() {
        if (stepLine == 2) {
            Log.i(TAG, "Creating a new step data insert request.");
        }

        DataSet dataSet = null;
        try {
            // [START build_insert_data_request]
            // Set a start and end time for our data, using a start time of 1 hour before this moment.
            String stepsDate = sportDataItem.getDate() + " " + parseTime(sportDataItem.getHour(), sportDataItem.getMinute());
            long endTime = formatter.parse(stepsDate).getTime();
            long startTime = new Date(endTime - MINUTES.toMillis(14)).getTime();

            // Create a data source
            DataSource dataSource =
                new DataSource.Builder()
                        .setAppPackageName(this)
                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .setStreamName(TAG + " - step count")
                        .setType(DataSource.TYPE_RAW)
                        .build();

        // Create a data set
        int stepCountDelta = sportDataItem.getStepCount();
        dataSet = DataSet.create(dataSource);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPoint =
                dataSet.createDataPoint().setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(dataPoint);
        // [END build_insert_data_request]
        }
        catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
        }

        return dataSet;
    }

    /** Returns a {@link DataReadRequest} for all step count changes in the past day. */
    public static DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 day before this moment.
        DataReadRequest readRequest = null;
        try {
            // [START build_insert_data_request]
            // Set a start and end time for our data, using a start time of 1 hour before this moment.
            String stepsDate = sportDataItem.getDate() + " 23:00:00";
            String stepsDate1 = sportDataItem.getDate() + " 05:00:00";
            long endTime = formatter.parse(stepsDate).getTime();
            long startTime = formatter.parse(stepsDate1).getTime();

//        java.text.DateFormat dateFormat = getDateTimeInstance();
//        Log.i(TAG, "Step Data");
//        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
//        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        readRequest =
                new DataReadRequest.Builder()
                        // The data request can specify multiple data types to return, effectively
                        // combining multiple data queries into one call.
                        // In this example, it's very unlikely that the request is for several hundred
                        // datapoints each consisting of a few steps and a timestamp.  The more likely
                        // scenario is wanting to see how many steps were walked per day, for 7 days.
                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA
                        )
                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                        .bucketByTime(1, TimeUnit.DAYS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();
        // [END build_read_data_request]
        }
        catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return readRequest;
    }

    /**
     * Logs a record of the query result. It's possible to get more constrained data sets by
     * specifying a data source or data type, but for demonstrative purposes here's how one would dump
     * all the data. In this sample, logging also prints to the device screen, so we can see what the
     * query returns, but your app should not log fitness information as a privacy consideration. A
     * better option would be to dump the data you receive to a local data directory to avoid exposing
     * it to other applications.
     */
    public static void printData(DataReadResponse dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(
                    TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
        // [END parse_read_data_result]
    }
    /**
     * Deletes a {@link DataSet} from the History API. In this example, we delete all step count data
     * for the past 24 hours.
     */
    private void deleteData() {
        Log.i(TAG, "Deleting this day's step count data.");

        DataDeleteRequest request = null;
        try {
        // [START delete_dataset]
        // Set a start and end time for our data, using a start time of 1 day before this moment.

            String stepsDate = sportDataItem.getDate() + " 23:00:00";
            String stepsDate1 = sportDataItem.getDate() + " 05:00:00";
        long endTime = formatter.parse(stepsDate).getTime();
        long startTime = formatter.parse(stepsDate1).getTime();

        //  Create a delete request object, providing a data type and a time interval
        request =
                new DataDeleteRequest.Builder()
                        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .build();
        }
        catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
        }

        // Invoke the History API with the HistoryClient object and delete request, and then
        // specify a callback that will check the result.
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .deleteData(request)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully deleted this day's step count data.");
                                } else {
                                    Log.e(TAG, "Failed to delete this day's step count data.", task.getException());
                                }
                            }
                        });
    }

    /**
     * Updates and reads data by chaning {@link Task} from {@link #updateData()} and {@link
     * #readHistoryData()}.
     */
    private void updateAndReadData() {
        updateData()
                .continueWithTask(
                        new Continuation<Void, Task<DataReadResponse>>() {
                            @Override
                            public Task<DataReadResponse> then(@NonNull Task<Void> task) throws Exception {
                                return readHistoryData();
                            }
                        });
    }

    /**
     * Creates a {@link DataSet},then makes a {@link DataUpdateRequest} to update step data. Then
     * invokes the History API with the HistoryClient object and update request.
     */
    private Task<Void> updateData() {
        // Create a new dataset and update request.
        DataSet dataSet = updateFitnessData();
        long startTime = 0;
        long endTime = 0;

        // Get the start and end times from the dataset.
        for (DataPoint dataPoint : dataSet.getDataPoints()) {
            startTime = dataPoint.getStartTime(TimeUnit.MILLISECONDS);
            endTime = dataPoint.getEndTime(TimeUnit.MILLISECONDS);
        }

        // [START update_data_request]
        Log.i(TAG, "Updating the dataset in the History API.");

        DataUpdateRequest request =
                new DataUpdateRequest.Builder()
                        .setDataSet(dataSet)
                        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();

        // Invoke the History API to update data.
        return Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .updateData(request)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // At this point the data has been updated and can be read.
                                    Log.i(TAG, "Data update was successful.");
                                } else {
                                    Log.e(TAG, "There was a problem updating the dataset.", task.getException());
                                }
                            }
                        });
    }

    /** Creates and returns a {@link DataSet} of step count data to update. */
    private DataSet updateFitnessData() {
        Log.i(TAG, "Creating a new data update request.");
DataSet dataSet = null;
        try {
        // [START build_update_data_request]
        // Set a start and end time for the data that fits within the time range
        // of the original insertion.

            String stepsDate = sportDataItem.getDate() + " 23:00:00";
            String stepsDate1 = sportDataItem.getDate() + " 05:00:00";
            long endTime = formatter.parse(stepsDate).getTime();
            long startTime = formatter.parse(stepsDate1).getTime();

        // Create a data source
        DataSource dataSource =
                new DataSource.Builder()
                        .setAppPackageName(this)
                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .setStreamName(TAG + " - step count")
                        .setType(DataSource.TYPE_RAW)
                        .build();

        // Create a data set
        int stepCountDelta = sportDataItem.getStepCount();
        dataSet = DataSet.create(dataSource);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPoint =
                dataSet.createDataPoint().setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(dataPoint);
        // [END build_update_data_request]
    }
        catch (ParseException ex) {
        Log.e(TAG, ex.getMessage());
    }

        return dataSet;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_data) {
            deleteData();
            return true;
        } else if (id == R.id.action_update_data) {
            clearLogView();
            updateAndReadData();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A wrapper for {@Link #insertAndVerifySession}. If the user account has OAuth permission,
     * continue to {@Link #insertAndVerifySession}, else request OAuth permission for the account.
     */
    private void insertAndVerifySessionWrapper() {
        if (hasOAuthPermission()) {
            insertAndVerifySession();
        } else {
            requestOAuthPermission();
        }
    }

    /**
     * Checks if user's account has OAuth permission to Fitness API.
     */
    private boolean hasOAuthPermission() {
        FitnessOptions fitnessOptions = getFitnessSignInOptions();
        return GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions);
    }

    /** Launches the Google SignIn activity to request OAuth permission for the user. */
    private void requestOAuthPermission() {
        FitnessOptions fitnessOptions = getFitnessSignInOptions();
        GoogleSignIn.requestPermissions(
                this,
                REQUEST_OAUTH_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(this),
                fitnessOptions);
    }

    /** Gets {@Link FitnessOptions} in order to check or request OAuth permission for the user. */
    private FitnessOptions getFitnessSignInOptions() {
        return FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_WRITE)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                insertAndVerifySession();
            }
        }
    }

    /**
     * Creates and executes a {@Link SessionInsertRequest} using {@Link
     * com.google.android.gms.fitness.SessionsClient} to insert a session.
     */
    private Task<Void> insertSession() {
        // First, create a new session and an insertion request.
        SessionInsertRequest insertRequest = insertFitnessSession();

        // [START insert_session]
        // Then, invoke the Sessions API to insert the session and await the result,
        // which is possible here because of the AsyncTask. Always include a timeout when
        // calling await() to avoid hanging that can occur from the service being shutdown
        // because of low memory or other conditions.
        Log.i(TAG, "Inserting the sleep session in the Sessions API");
        return Fitness.getSessionsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .insertSession(insertRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // At this point the session has been inserted and can be read.
                        Log.i(TAG, "Sleep session insert was successful!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "There was a problem inserting the session: " +
                                e.getLocalizedMessage());
                    }
                });
        // [END insert_session]
    }

    /**
     * Creates and executes {@Link SessionReadRequest} using {@Link
     * com.google.android.gms.fitness.SessionsClient} to verify the insertion succeeded.
     */
    private Task<SessionReadResponse> verifySession() {
        // Begin by creating the query.
        SessionReadRequest readRequest = readFitnessSession();

        // [START read_session]
        // Invoke the Sessions API to fetch the session with the query and wait for the result
        // of the read request. Note: Fitness.SessionsApi.readSession() requires the
        // ACCESS_FINE_LOCATION permission.
        return Fitness.getSessionsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readSession(readRequest)
                .addOnSuccessListener(new OnSuccessListener<SessionReadResponse>() {
                    @Override
                    public void onSuccess(SessionReadResponse sessionReadResponse) {
                        // Get a list of sessions that match the criteria to check the result.
                        List<Session> sessions = sessionReadResponse.getSessions();
                        Log.i(TAG, "Session read was successful. Number of returned sessions is: "
                        + sessions.size());

                        for (Session session: sessions) {
                            // Process the session
                            dumpSession(session);

                            //Process the data sets for this session
                            List<DataSet> dataSets = sessionReadResponse.getDataSet(session);
                            for (DataSet dataSet : dataSets) {
                                dumpDataSet(dataSet);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to read session");
                    }
                });
        // [END read_session]
    }

    /**
     * Inserts and verifies a session by chaining {@Link Task} form {@Link #insertSession} and
     * {@Link #verifySession}.
     */
    private void insertAndVerifySession() {
        insertSession().continueWithTask(new Continuation<Void, Task<SessionReadResponse>>() {
            @Override
            public Task<SessionReadResponse> then(@NonNull Task<Void> task) {
                return verifySession();
            }
        });
    }

    /**
     *  Creates a {@link SessionInsertRequest} for a run that consists of 10 minutes running,
     *  10 minutes walking, and 10 minutes of running. The request contains two {@link DataSet}s:
     *  speed data and activity segments data.
     *
     *  {@link Session}s are time intervals that are associated with all Fit data that falls into
     *  that time interval. This data can be inserted when inserting a session or independently,
     *  without affecting the association between that data and the session. Future queries for
     *  that session will return all data relevant to the time interval created by the session.
     *
     *  Sessions may contain {@link DataSet}s, which are comprised of {@link DataPoint}s and a
     *  {@link DataSource}.
     *  A {@link DataPoint} is associated with a Fit {@link DataType}, which may be
     *  derived from the {@link DataSource}, as well as a time interval, and a value. A given
     *  {@link DataSet} may only contain data for a single data type, but a {@link Session} can
     *  contain multiple {@link DataSet}s.
     */
    private SessionInsertRequest insertFitnessSession() {
        Log.i(TAG, "Creating a new sleep session");
        String sleepDate = sleepDataDay.getDate() + " " + parseTime(sleepDataDay.getEndTimeHour(), sleepDataDay.getEndTimeMinute());

        SessionInsertRequest sessionInsertRequest = null;

        try {
            // Set a start and end time for our data
            long endTime = formatter.parse(sleepDate).getTime();
            long startTime = new Date(endTime - MINUTES.toMillis(sleepDataDay.getTotalSleepMinutes())).getTime();

            // [START build_insert_session_request]
            // Create a session with metadata about the activity.
            Session session = new Session.Builder()
                    .setName(SLEEP_SESSION_NAME)
                    .setDescription("Sleep recorded for " + sleepDataDay.getDate())
                    .setIdentifier(UniqueID.toString())
                    .setStartTime(startTime, TimeUnit.MILLISECONDS)
                    .setEndTime(endTime, TimeUnit.MILLISECONDS)
                    .setActivity(FitnessActivities.SLEEP)
                    .build();

            // Build a session insert request
            sessionInsertRequest = new SessionInsertRequest.Builder()
                    .setSession(session)
                    .build();
            // [END build_insert_session_request]
        }
        catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
        }

        return sessionInsertRequest;
    }

    /**
     * Returns a {@Link SessionReadRequest} for steps taken in the last day
     */
    private SessionReadRequest readFitnessSession() {
        Log.i(TAG, "Reading History API results for session: " + SLEEP_SESSION_NAME);
        // [START build_read_session_request]
        // Set a start and end time for the query, using a start time of 1 day before this moment.
        String sDate1 = sleepDataDay.getDate() + " " + parseTime(sleepDataDay.getEndTimeHour(), sleepDataDay.getEndTimeMinute());
        long startTime = 0;
        long endTime = 0;
        try {
            // Set a start and end time for our data, using a start time of 1 day before this moment.
            endTime = formatter.parse(sDate1).getTime();
            startTime = new Date(endTime - MINUTES.toMillis(sleepDataDay.getTotalSleepMinutes())).getTime();
        }
        catch (ParseException ex)
        {
            Log.e(TAG, ex.getLocalizedMessage());
        }

        // Build a session read request
        // [END build_read_session_request]

        return new SessionReadRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .setSessionName(SLEEP_SESSION_NAME)
                .build();
    }

    private static void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data Type: " + dataSet.getDataType().getName());
        for (DataPoint dataPoint : dataSet.getDataPoints()) {
            DateFormat dateFormat = getDateTimeInstance();
            Log.i(TAG, "Data Point:");
            Log.i(TAG, "\tType: " + dataPoint.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dataPoint.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dataPoint.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dataPoint.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName() +
                        " Value: " + dataPoint.getValue(field));
            }
        }
    }

    private void dumpSession(Session session) {
        DateFormat dateFormat = getDateTimeInstance();
        Log.i(TAG, "Data returned for Session: " + session.getName()
        + "\n\tDescription: " + session.getDescription()
        + "\n\tStart: " + dateFormat.format(session.getStartTime(TimeUnit.MILLISECONDS))
        + "\n\tEnd: " + dateFormat.format(session.getEndTime(TimeUnit.MILLISECONDS)));
    }


    /** Clears all the logging message in the LogView. */
    private void clearLogView() {
        LogView logView = findViewById(R.id.sample_logview);
        logView.setText("Ready");
    }

    /**
     *  Initializes a custom log class that outputs both to in-app targets and logcat.
     */
    private void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);
        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);
        // On screen logging via a customized TextView.
        LogView logView = findViewById(R.id.sample_logview);

        // Fixing this lint errors adds logic without benefit.
        //noinspection AndroidLintDeprecation
        logView.setTextAppearance(this, R.style.Log);

        logView.setBackgroundColor(Color.WHITE);
        msgFilter.setNext(logView);
        Log.i(TAG, "Ready");
    }

    /** Returns the current state of permissions needed. */
    private boolean hasRuntimePermissions() {
        int permissionState =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /** Returns the current state of permissions needed. */
    private boolean hasRuntimeHRPermissions() {
        int permissionState =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestRuntimePermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.container),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestRuntimeHRPermissions() {
        boolean shouldProvideRationale =
                false;
            shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.BODY_SENSORS);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.container),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.BODY_SENSORS},
                                    BODY_SENSORS_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting sensor permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.BODY_SENSORS},
                        BODY_SENSORS_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                insertAndVerifySessionWrapper();
            } else {
                // Permission denied.

                // In this Activity we've chosen to notify the user that they
                // have rejected a core permission for the app since it makes the Activity useless.
                // We're communicating this message in a Snackbar since this is a sample app, but
                // core permissions would typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                Snackbar.make(
                        findViewById(R.id.container),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        } else if (requestCode == BODY_SENSORS_PERMISSIONS_REQUEST_CODE) {
            insertandVerifyHR();
        }
    }

    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    private static String extractDate(String str) {
        return str.substring(str.indexOf('2'), str.length() - 4);
    }
}
