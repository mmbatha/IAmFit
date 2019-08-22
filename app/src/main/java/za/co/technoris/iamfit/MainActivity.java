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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    File file = new File("/storage/self/primary/veryfit2.1/syn/sync_2019-08-20.txt");
    String path = "/storage/self/primary/veryfit2.1/syn/";
    File directory = new File(path);
    File[] filesList = directory.listFiles();
    File log = new File("/storage/self/primary/VeryFitPro/sync/20190821.log");
//    public static final String SAMPLE_SESSION_NAME = "Joggy jog";
//    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
//
//    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;
//    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextMessage = (TextView) findViewById(R.id.message);
        // This method sets up our custom logger, which will print all log messages to the device
        // screen, as well as to adb logcat.
        initializeLogging();
//
//        // When permissions are revoked the app is restarted so onCreate is sufficient to check for
//        // permissions core to the Activity's functionality.
//        if (hasRuntimePermissions()) {
//            insertAndVerifySessionWrapper();
//        } else {
//            requestRuntimePermissions();
//        }
       BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//
        List<String> logs = new ArrayList<String>();
        for (File file1 : filesList) {
            logs.add(file1.getName());
        }
        Collections.sort(logs);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, logs);
        Spinner spinner = findViewById(R.id.logs_spinner);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedLog = (String)((Spinner)findViewById(R.id.logs_spinner)).getSelectedItem();
                readFile(path + selectedLog);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            }
//        });
//
//        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//
//        spinner.setAdapter(adapter);
//
//        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            }
//        });

        if (checkPermission()) {
            listFiles();
            readFile(path + file.getName());
        }
        else {
            requestPermission();
        }
    }

    private void listFiles() {
        Log.d("Files", "Path: " + path);
        Log.d("Files", "Size: " + filesList.length);
        for (File file1 : filesList) {
            Log.d("Files", "Filename: " + file1.getName());
        }
        Log.d("Files", "\n------\n");
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
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
        try {
            FileInputStream fileInputStream = new FileInputStream(filename);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String strLine;
            int line = 1;
            String[] splitStr;
            SportDataDay sportDataDay = new SportDataDay();
            SleepDataDay sleepDataDay = new SleepDataDay();
            HeartRate heartRate = new HeartRate();
            while ((strLine = bufferedReader.readLine()) != null) {
                    if (strLine.contains("SportDataDay")) {
                        splitStr = strLine.split(", ");
                        sportDataDay.setDate(Long.valueOf(splitStr[0].split("=")[1]));
                        sportDataDay.setTotalStepCount(Integer.valueOf(splitStr[1].split("=")[1]));
                        Log.i(TAG, sportDataDay.toString());
                    } else if (strLine.contains("SleepDataDay")) {
                        splitStr = strLine.split(", ");
                        sleepDataDay.setDate(Long.valueOf(splitStr[0].split("=")[1]));
                        sleepDataDay.setEndTimeHour(Integer.valueOf(splitStr[1].split("=")[1]));
                        sleepDataDay.setEndTimeMinute(Integer.valueOf(splitStr[2].split("=")[1]));
                        sleepDataDay.setTotalSleepMinutes(Integer.valueOf(splitStr[3].split("=")[1]));
                        Log.i(TAG, sleepDataDay.toString());
                    } else if (strLine.contains("HeartRate")) {
                        splitStr = strLine.split(", ");
                        heartRate.setDate(Long.valueOf(splitStr[1].split("=")[1]));
                        heartRate.setMinute(Integer.valueOf(splitStr[2].split("=")[1]));
                        heartRate.setRate(Integer.valueOf(removeLastChar(splitStr[3].split("=")[1])));
                        Log.i(TAG, heartRate.toString());
                    }
            }
            Log.i(TAG, "\n------\n");
            bufferedReader.close();
//            FileInputStream logInputStream = new FileInputStream(log);
//            bufferedReader = new BufferedReader(new InputStreamReader(logInputStream));
//            while ((strLine = bufferedReader.readLine()) != null) {
//                if (strLine.contains("HealthSport")) {
//                    strLine = stripData(strLine);
//                    splitStr = strLine.split(", ");
//
//                }
//            }
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

//    /**
//     * A wrapper for {@link #insertAndVerifySession}. If the user account has OAuth permission,
//     * continue to {@link #insertAndVerifySession}, else request OAuth permission for the account.
//     */
//    private void insertAndVerifySessionWrapper() {
//        if (hasOAuthPermission()) {
//            insertAndVerifySession();
//        } else {
//            requestOAuthPermission();
//        }
//    }
//
//    /**
//     * Checks if user's account has OAuth permission to Fitness API.
//     */
//    private boolean hasOAuthPermission() {
//        FitnessOptions fitnessOptions = getFitnessSignInOptions();
//        return GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions);
//    }
//
//    /** Launches the Google SignIn activity to request OAuth permission for the user. */
//    private void requestOAuthPermission() {
//        FitnessOptions fitnessOptions = getFitnessSignInOptions();
//        GoogleSignIn.requestPermissions(
//                this,
//                REQUEST_OAUTH_REQUEST_CODE,
//                GoogleSignIn.getLastSignedInAccount(this),
//                fitnessOptions);
//    }
//
//    /** Gets {@link FitnessOptions} in order to check or request OAuth permission for the user. */
//    private FitnessOptions getFitnessSignInOptions() {
//        return FitnessOptions.builder()
//                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
//                .addDataType(DataType.TYPE_SPEED, FitnessOptions.ACCESS_WRITE)
//                .build();
//    }
//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                readFile(path + file.getName());
            }
        }
    }
//
//    /**
//     *  Creates and executes a {@link SessionInsertRequest} using {@link
//     *  com.google.android.gms.fitness.SessionsClient} to insert a session.
//     */
//    private Task<Void> insertSession() {
//        //First, create a new session and an insertion request.
//        SessionInsertRequest insertRequest = insertFitnessSession();
//
//        // [START insert_session]
//        // Then, invoke the Sessions API to insert the session and await the result,
//        // which is possible here because of the AsyncTask. Always include a timeout when
//        // calling await() to avoid hanging that can occur from the service being shutdown
//        // because of low memory or other conditions.
//        Log.i(TAG, "Inserting the session in the Sessions API");
//        return Fitness.getSessionsClient(this, GoogleSignIn.getLastSignedInAccount(this))
//                .insertSession(insertRequest)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        // At this point, the session has been inserted and can be read.
//                        Log.i(TAG, "Session insert was successful!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.i(TAG, "There was a problem inserting the session: " +
//                                e.getLocalizedMessage());
//                    }
//                });
//        // [END insert_session]
//    }
//
//    /**
//     *  Creates and executes a {@link SessionReadRequest} using {@link
//     *  com.google.android.gms.fitness.SessionsClient} to verify the insertion succeeded .
//     */
//    private Task<SessionReadResponse> verifySession() {
//        // Begin by creating the query.
//        SessionReadRequest readRequest = readFitnessSession();
//
//        // [START read_session]
//        // Invoke the Sessions API to fetch the session with the query and wait for the result
//        // of the read request. Note: Fitness.SessionsApi.readSession() requires the
//        // ACCESS_FINE_LOCATION permission.
//        return Fitness.getSessionsClient(this, GoogleSignIn.getLastSignedInAccount(this))
//                .readSession(readRequest)
//                .addOnSuccessListener(new OnSuccessListener<SessionReadResponse>() {
//                    @Override
//                    public void onSuccess(SessionReadResponse sessionReadResponse) {
//                        // Get a list of the sessions that match the criteria to check the result.
//                        List<Session> sessions = sessionReadResponse.getSessions();
//                        Log.i(TAG, "Session read was successful. Number of returned sessions is: "
//                                + sessions.size());
//
//                        for (Session session : sessions) {
//                            // Process the session
//                            dumpSession(session);
//
//                            // Process the data sets for this session
//                            List<DataSet> dataSets = sessionReadResponse.getDataSet(session);
//                            for (DataSet dataSet : dataSets) {
//                                dumpDataSet(dataSet);
//                            }
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.i(TAG, "Failed to read session");
//                    }
//                });
//        // [END read_session]
//    }
//
//    /**
//     *  Inserts and verifies a session by chaining {@link Task} form {@link #insertSession} and
//     *  {@link #verifySession}.
//     */
//    private void insertAndVerifySession() {
//
//        insertSession().continueWithTask(new Continuation<Void, Task<SessionReadResponse>>() {
//            @Override
//            public Task<SessionReadResponse> then(@NonNull Task<Void> task) throws Exception {
//                return verifySession();
//            }
//        });
//    }
//
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
//
//    /**
//     * Returns a {@link SessionReadRequest} for all speed data in the past week.
//     */
//    private SessionReadRequest readFitnessSession() {
//        Log.i(TAG, "Reading History API results for session: " + SLEEP_SESSION_NAME);
//        // [START build_read_session_request]
//        // Set a start and end time for our query, using a start time of 1 week before this moment.
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
//
//        // Build a session read request
//        SessionReadRequest readRequest = new SessionReadRequest.Builder()
//                .setTimeInterval(startTime1, endTime1, MILLISECONDS)
//                //                .addDataType(DataType.TYPE_SPEED)
//                .setSessionName(SLEEP_SESSION_NAME)
//                .build();
//        // [END build_read_session_request]
//
//        return readRequest;
//    }
//
//    private void dumpDataSet(DataSet dataSet) {
//        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
//        for (DataPoint dp : dataSet.getDataPoints()) {
//            DateFormat dateFormat = getTimeInstance();
//            Log.i(TAG, "Data point:");
//            Log.i(TAG, "\tType: " + dp.getDataType().getName());
//            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(MILLISECONDS)));
//            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(MILLISECONDS)));
//            for(Field field : dp.getDataType().getFields()) {
//                Log.i(TAG, "\tField: " + field.getName() +
//                        " Value: " + dp.getValue(field));
//            }
//        }
//    }
//
//    private void dumpSession(Session session) {
//        DateFormat dateFormat = getDateTimeInstance();
//        Log.i(TAG, "Data returned for Session: " + session.getName()
//                + "\n\tDescription: " + session.getDescription()
//                + "\n\tStart: " + dateFormat.format(session.getStartTime(MILLISECONDS))
//                + "\n\tEnd: " + dateFormat.format(session.getEndTime(MILLISECONDS)));
//    }
//
//    /**
//     * Deletes the {@link DataSet} we inserted with our {@link Session} from the History API.
//     * In this example, we delete all step count data for the past 24 hours. Note that this
//     * deletion uses the History API, and not the Sessions API, since sessions are truly just time
//     * intervals over a set of data, and the data is what we are interested in removing.
//     */
//    private void deleteSession() {
//        Log.i(TAG, "Deleting today's session data for speed");
//
//        // Set a start and end time for our data, using a start time of 1 day before this moment.
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
//
//        // Create a delete request object, providing a data type and a time interval
//        DataDeleteRequest request = new DataDeleteRequest.Builder()
//                .setTimeInterval(startTime1, endTime1, MILLISECONDS)
////                .addDataType(DataType.TYPE_SPEED)
//                .deleteAllSessions() // Or specify a particular session here
//                .build();
//
//        // Delete request using HistoryClient and specify listeners that will check the result.
//        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
//                .deleteData(request)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.i(TAG, "Successfully deleted today's sessions");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // The deletion will fail if the requesting app tries to delete data
//                        // that it did not insert.
//                        Log.i(TAG, "Failed to delete today's sessions");
//                    }
//                });
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_delete_session) {
//            deleteSession();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
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

//    /** Returns the current state of the permissions needed. */
//    private boolean hasRuntimePermissions() {
//        int permissionState =
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
//        return permissionState == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void requestRuntimePermissions() {
//        boolean shouldProvideRationale =
//                ActivityCompat.shouldShowRequestPermissionRationale(this,
//                        Manifest.permission.ACCESS_FINE_LOCATION);
//
//        // Provide an additional rationale to the user. This would happen if the user denied the
//        // request previously, but didn't check the "Don't ask again" checkbox.
//        if (shouldProvideRationale) {
//            Log.i(TAG, "Displaying permission rationale to provide additional context.");
//            Snackbar.make(
//                    findViewById(R.id.main_activity_view),
//                    R.string.permission_rationale,
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Request permission
//                            ActivityCompat.requestPermissions(MainActivity.this,
//                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                    REQUEST_PERMISSIONS_REQUEST_CODE);
//                        }
//                    })
//                    .show();
//        } else {
//            Log.i(TAG, "Requesting permission");
//            // Request permission. It's possible this can be auto answered if device policy
//            // sets the permission in a given state or the user denied the permission
//            // previously and checked "Never ask again".
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    REQUEST_PERMISSIONS_REQUEST_CODE);
//        }
//    }
//
//    /**
//     * Callback received when a permissions request has been completed.
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        Log.i(TAG, "onRequestPermissionResult");
//        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
//            if (grantResults.length <= 0) {
//                // If user interaction was interrupted, the permission request is cancelled and you
//                // receive empty arrays.
//                Log.i(TAG, "User interaction was cancelled.");
//            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission was granted.
//                insertAndVerifySessionWrapper();
//
//            } else {
//                // Permission denied.
//
//                // In this Activity we've chosen to notify the user that they
//                // have rejected a core permission for the app since it makes the Activity useless.
//                // We're communicating this message in a Snackbar since this is a sample app, but
//                // core permissions would typically be best requested during a welcome-screen flow.
//
//                // Additionally, it is important to remember that a permission might have been
//                // rejected without asking the user for permission (device policy or "Never ask
//                // again" prompts). Therefore, a user interface affordance is typically implemented
//                // when permissions are denied. Otherwise, your app could appear unresponsive to
//                // touches or interactions which have required permissions.
//                Snackbar.make(
//                        findViewById(R.id.main_activity_view),
//                        R.string.permission_denied_explanation,
//                        Snackbar.LENGTH_INDEFINITE)
//                        .setAction(R.string.settings, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                // Build intent that displays the App settings screen.
//                                Intent intent = new Intent();
//                                intent.setAction(
//                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package",
//                                        BuildConfig.APPLICATION_ID, null);
//                                intent.setData(uri);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                            }
//                        })
//                        .show();
//            }
//        }
//    }

    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    private static String stripData(String str) {
        return str.substring('H', '}');
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    showStuff();
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.nav_details:
                    hideStuff();
                    mTextMessage.setText(R.string.title_details);
                    return true;
                case R.id.nav_device:
                    hideStuff();
                    mTextMessage.setText(R.string.title_device);
                    return true;
                case R.id.nav_user:
                    hideStuff();
                    mTextMessage.setText(R.string.title_user);
                    return true;
            }
            return false;
        }
    };

    private void hideStuff() {
    }

    private void showStuff() {
    }
}
