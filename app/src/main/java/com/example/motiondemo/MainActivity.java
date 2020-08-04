package com.example.motiondemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    //imports DBHelper Class
    DBHelper mydb;

    // System sensor manager instance.
    private SensorManager mSensorManager;

    // Accelerometer and magnetometer sensors, as retrieved from the
    // sensor manager.
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;

    // Current data from accelerometer & magnetometer.  The arrays hold values
    // for X, Y, and Z.
    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];

    // Very small values for the accelerometer (on all three axes) should
    // be interpreted as 0. This value is the amount of acceptable
    // non-zero drift.
    private static final float VALUE_DRIFT = 0.05f;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydb = new DBHelper(this);
        //ArrayList array_list = mydb.getAllData();

        //probably don't need the adapter since we're not displaying yet
        //ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1, array_list);

        mSensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD);


    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        super.onStart();

        // Listeners for the sensors are registered in this callback and
        // can be unregistered in onStop().
        //
        // Check to ensure sensors are available before registering listeners.
        // Both listeners are registered with a "normal" amount of delay
        // (SENSOR_DELAY_NORMAL).

/*        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }*/

        //if for some reason you just wanna write a file
/*        String filename = "myfile";
        String fileContents = "Hello world!";
        try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    @Override
    protected void onStop() {
        super.onStop();

        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        mSensorManager.unregisterListener(this);
        //Log.d("app", "app closed");
        Log.d("db", "Number of rows logged on closed: " + String.valueOf(mydb.numberOfRows()));

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Date currentTime = Calendar.getInstance().getTime(); //seems to only be precise up to second may need sensorEvent timestamp
        int sensorType = sensorEvent.sensor.getType();

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            mAccelerometerData = sensorEvent.values.clone();
        } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            mMagnetometerData = sensorEvent.values.clone();
        }
        long Time = sensorEvent.timestamp; //in nanoseconds
        mydb.insertData(Time, mAccelerometerData[0], mAccelerometerData[1], mAccelerometerData[2]);
        Log.d("db", "Number of rows logged : " + String.valueOf(mydb.numberOfRows()));


       /*switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = sensorEvent.values.clone();
                //long Time = sensorEvent.timestamp;
                Log.d("db", "Number of rows logged : " + String.valueOf(mydb.numberOfRows()));
                mydb.insertData(Time, mAccelerometerData[0], mAccelerometerData[1], mAccelerometerData[2]);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = sensorEvent.values.clone();
                break;
            default:
                return;
       }*/

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void DeleteData(View view) {
        mSensorManager.unregisterListener(this);
        //mSensorManager = null;
        mydb.deleteTable();
    }

    public void RecordData(View view) {
        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void StopRecord(View view) {
        mSensorManager.unregisterListener(this);
        //mSensorManager = null;
        Log.d("db", "Number of rows logged on stop: " + String.valueOf(mydb.numberOfRows()));
    }
}