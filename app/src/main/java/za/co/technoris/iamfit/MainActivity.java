package za.co.technoris.iamfit;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import za.co.technoris.iamfit.ble.HealthHeartRateItem;
import za.co.technoris.iamfit.ble.HealthSleep;
import za.co.technoris.iamfit.ble.HealthSport;
import za.co.technoris.iamfit.ble.HeartRate;
import za.co.technoris.iamfit.ble.SleepDataDay;
import za.co.technoris.iamfit.ble.SportDataDay;
import za.co.technoris.iamfit.common.logger.Log;
import za.co.technoris.iamfit.common.logger.LogView;
import za.co.technoris.iamfit.common.logger.LogWrapper;
import za.co.technoris.iamfit.common.logger.MessageOnlyLogFilter;

import static java.text.DateFormat.getDateTimeInstance;
import static java.text.DateFormat.getTimeInstance;

/**
 * This sample demonstrates how to use the Sessions API of the Google Fit platform to insert
 * sessions into the History API, query against existing data, and remove sessions. It also
 * demonstrates how to authenticate a user with Google Play Services and how to properly
 * represent data in a Session, as well as how to use ActivitySegments.
 */
public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    //
//    private static final String SLEEP_SESSION_NAME = "Rest";
    private TextView mTextMessage;
//    private static final UUID UniqueID = new UUID(415452,548775);
//
public static final String TAG = "IAMFit";
    public static final String FILES_TAG = "Files";
public static final Locale enZA = new Locale("en", "ZA");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", enZA);
    public static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", enZA);
    File file = new File("sync_2019-08-13.txt");
    String path = "/storage/self/primary/veryfit2.1/syn/";
    File directory = new File(path);
    File[] filesList = directory.listFiles();
    String selectedLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextMessage = (TextView) findViewById(R.id.message);
        // This method sets up our custom logger, which will print all log messages to the device
        // screen, as well as to adb logcat.
        initializeLogging();

        BottomNavigationView navView = findViewById(R.id.nav_view);

        mTextMessage = findViewById(R.id.message);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ArrayAdapter<String> adapter = null;
        try {
            List<String> logs = new ArrayList<String>();
            for (File file1 : filesList) {
                logs.add(file1.getName());
            }
            Collections.sort(logs);
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
                selectedLog = (String)((Spinner)findViewById(R.id.logs_spinner)).getSelectedItem();
                readFile(path + selectedLog);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedLog = (String)((Spinner)findViewById(R.id.logs_spinner)).getSelectedItem();
            }
        });

        if (checkPermission()) {
            listFiles();
            readFile(path + file.getName());
        }
        else {
            requestPermission();
        }
    }

    private void listFiles() {
        Log.i(FILES_TAG, "Path: " + path);
        Log.i(FILES_TAG, "Size: " + filesList.length);
        for (File file1 : filesList) {
            Log.i(FILES_TAG, "Filename: " + file1.getName());
        }
        Log.i(FILES_TAG, "------\n");
    }

    private boolean checkPermission() {
        int result = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to read files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        else
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void readFile(String filename) {
        BufferedReader bufferedReader = null;
        Date date;
        try {
            selectedLog = extractDate((String)((Spinner)findViewById(R.id.logs_spinner)).getSelectedItem());
            FileInputStream fileInputStream = new FileInputStream(filename);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String strLine;
            int line = 1;
            String[] splitStr;
            SportDataDay sportDataDay = new SportDataDay();
            SleepDataDay sleepDataDay = new SleepDataDay();
            HeartRate heartRate = new HeartRate();
            long endTime;
            long startTime;
            DataSource dataSource;
            int stepCountDelta;
            DataSet dataSet;
            DataPoint dataPoint;
            while ((strLine = bufferedReader.readLine()) != null) {
                if (strLine.contains(selectedLog.replace("-",""))) {
                    if (strLine.contains("SportDataDay")) {
                        splitStr = strLine.split(", ");
                        sportDataDay.setDate(Long.valueOf(splitStr[0].split("=")[1]));
                        sportDataDay.setTotalStepCount(Integer.valueOf(splitStr[1].split("=")[1]));
                        Log.i(TAG, sportDataDay.toString());
//                        startTime = LOG_DATE_FORMAT.parse(sportDataDay.getDate() + " 00:00:00").getTime();
//                        endTime = LOG_DATE_FORMAT.parse(sportDataDay.getDate() + " 23:00:00").getTime();
                    } else if (strLine.contains("SleepDataDay")) {
                        splitStr = strLine.split(", ");
                        sleepDataDay.setDate(Long.valueOf(splitStr[0].split("=")[1]));
                        sleepDataDay.setEndTimeHour(Integer.valueOf(splitStr[1].split("=")[1]));
                        sleepDataDay.setEndTimeMinute(Integer.valueOf(splitStr[2].split("=")[1]));
                        sleepDataDay.setTotalSleepMinutes(Integer.valueOf(splitStr[3].split("=")[1]));
                        Log.i(TAG, sleepDataDay.toString());
                    } else if (strLine.contains("HeartRate{")) {
                        splitStr = strLine.split(", ");
                        heartRate.setDate(Long.valueOf(splitStr[1].split("=")[1]));
                        heartRate.setMinute(Integer.valueOf(splitStr[2].split("=")[1]));
                        heartRate.setRate(Integer.valueOf(removeLastChar(splitStr[3].split("=")[1])));
                        Log.i(TAG, heartRate.toString());

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
//        catch (ParseException ex) {
//            Log.e(TAG, ex.getMessage());
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                readFile(path + file.getName());
            }
        }
    }

//    /**
//     *  Creates a {@link SessionInsertRequest} for a run that consists of 10 minutes running,
//     *  10 minutes walking, and 10 minutes of running. The request contains two {@link DataSet}s:
//     *  speed data and activity segments data.
//     *
//     *  {@link Session}s are time intervals that are associated with all Fit data that falls into
//     *  that time interval. This data can be inserted when inserting a session or independently,
//     *  without affecting the association between that data and the session. Future queries for
//     *  that session will return all data relevant to the time interval created by the session.
//     *
//     *  Sessions may contain {@link DataSet}s, which are comprised of {@link DataPoint}s and a
//     *  {@link DataSource}.
//     *  A {@link DataPoint} is associated with a Fit {@link DataType}, which may be
//     *  derived from the {@link DataSource}, as well as a time interval, and a value. A given
//     *  {@link DataSet} may only contain data for a single data type, but a {@link Session} can
//     *  contain multiple {@link DataSet}s.
//     */
//    private SessionInsertRequest insertFitnessSession() {
//        Log.i(TAG, "Creating a new sleep session");
//        // Create the sleep session
//        long startTime1 = 0;
//        long endTime1 = 0;
//        try {
//            startTime1 = new SimpleDateFormat(DATE_FORMAT, new Locale("en", "ZA")).parse("12/08/2019 00:23:00")
//                    .getTime();
//            endTime1 = new SimpleDateFormat(DATE_FORMAT, new Locale("en", "ZA")).parse("12/08/2019 05:52:00")
//                    .getTime();
//        }
//        catch (ParseException ex)
//        {
//            Log.i("Error formatting the date: ", ex.getMessage());
//        }
//        Session session = new Session.Builder()
//                .setName(SLEEP_SESSION_NAME)
//                .setIdentifier(UniqueID.toString())
//                .setDescription("Some much needed rest")
//                .setStartTime(startTime1, MILLISECONDS)
//                .setEndTime(endTime1, MILLISECONDS)
//                .setActivity(FitnessActivities.SLEEP)
//                .build();
//
//// Build the request to insert the session.
//        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
//                .setSession(session)
//                .build();
//
//       /* // Creating a new session for an afternoon run
//        Log.i(TAG, "Creating a new session for an afternoon run");
//        // Setting start and end times for our run.
//        Calendar cal = Calendar.getInstance();
//        Date now = new Date();
//        cal.setTime(now);
//        // Set a range of the run, using a start time of 30 minutes before this moment,
//        // with a 10-minute walk in the middle.
//        long endTime = cal.getTimeInMillis();
//        cal.add(Calendar.MINUTE, -10);
//        long endWalkTime = cal.getTimeInMillis();
//        cal.add(Calendar.MINUTE, -10);
//        long startWalkTime = cal.getTimeInMillis();
//        cal.add(Calendar.MINUTE, -10);
//        long startTime = cal.getTimeInMillis();
//
//        // Create a data source
//        DataSource speedDataSource = new DataSource.Builder()
//                .setAppPackageName(this.getPackageName())
//                .setDataType(DataType.TYPE_SPEED)
//                .setName(SAMPLE_SESSION_NAME + "- speed")
//                .setType(DataSource.TYPE_RAW)
//                .build();
//
//        float runSpeedMps = 10;
//        float walkSpeedMps = 3;
//        // Create a data set of the run speeds to include in the session.
//        DataSet speedDataSet = DataSet.create(speedDataSource);
//
//        DataPoint firstRunSpeed = speedDataSet.createDataPoint()
//                .setTimeInterval(startTime, startWalkTime, TimeUnit.MILLISECONDS);
//        firstRunSpeed.getValue(Field.FIELD_SPEED).setFloat(runSpeedMps);
//        speedDataSet.add(firstRunSpeed);
//
//        DataPoint walkSpeed = speedDataSet.createDataPoint()
//                .setTimeInterval(startWalkTime, endWalkTime, TimeUnit.MILLISECONDS);
//        walkSpeed.getValue(Field.FIELD_SPEED).setFloat(walkSpeedMps);
//        speedDataSet.add(walkSpeed);
//
//        DataPoint secondRunSpeed = speedDataSet.createDataPoint()
//                .setTimeInterval(endWalkTime, endTime, TimeUnit.MILLISECONDS);
//        secondRunSpeed.getValue(Field.FIELD_SPEED).setFloat(runSpeedMps);
//        speedDataSet.add(secondRunSpeed);
//
//        // [START build_insert_session_request_with_activity_segments]
//        // Create a second DataSet of ActivitySegments to indicate the runner took a 10-minute walk
//        // in the middle of the run.
//        DataSource activitySegmentDataSource = new DataSource.Builder()
//                .setAppPackageName(this.getPackageName())
//                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
//                .setName(SAMPLE_SESSION_NAME + "-activity segments")
//                .setType(DataSource.TYPE_RAW)
//                .build();
//        DataSet activitySegments = DataSet.create(activitySegmentDataSource);
//
//        DataPoint firstRunningDp = activitySegments.createDataPoint()
//                .setTimeInterval(startTime, startWalkTime, TimeUnit.MILLISECONDS);
//        firstRunningDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.RUNNING);
//        activitySegments.add(firstRunningDp);
//
//        DataPoint walkingDp = activitySegments.createDataPoint()
//                .setTimeInterval(startWalkTime, endWalkTime, TimeUnit.MILLISECONDS);
//        walkingDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.WALKING);
//        activitySegments.add(walkingDp);
//
//        DataPoint secondRunningDp = activitySegments.createDataPoint()
//                .setTimeInterval(endWalkTime, endTime, TimeUnit.MILLISECONDS);
//        secondRunningDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.RUNNING);
//        activitySegments.add(secondRunningDp);
//
//        // [START build_insert_session_request]
//        // Create a session with metadata about the activity.
//        Session session = new Session.Builder()
//                .setName(SAMPLE_SESSION_NAME)
//                .setDescription("Long run around Joburg Park")
//                .setIdentifier("UniqueIdentifierHere")
//                .setActivity(FitnessActivities.RUNNING)
//                .setStartTime(startTime, TimeUnit.MILLISECONDS)
//                .setEndTime(endTime, TimeUnit.MILLISECONDS)
//                .build();
//
//        // Build a session insert request
//        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
//                .setSession(session)
//                .addDataSet(speedDataSet)
//                .addDataSet(activitySegments)
//                .build();
//        // [END build_insert_session_request]
//        // [END build_insert_session_request_with_activity_segments] */
//
//        return insertRequest;
//    }

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
        LogView logView = (LogView) findViewById(R.id.sample_logview);

        // Fixing this lint errors adds logic without benefit.
        //noinspection AndroidLintDeprecation
        logView.setTextAppearance(this, R.style.Log);

        logView.setBackgroundColor(Color.WHITE);
        msgFilter.setNext(logView);
        Log.i(TAG, "Ready");
    }

    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    private static String extractDate(String str) {
        return str.substring(str.indexOf('2'), str.length() - 4);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.nav_details:
                    mTextMessage.setText(R.string.title_details);
                    return true;
                case R.id.nav_device:
                    mTextMessage.setText(R.string.title_device);
                    return true;
                case R.id.nav_user:
                    mTextMessage.setText(R.string.title_user);
                    return true;
            }
            return false;
        }
    };
}
