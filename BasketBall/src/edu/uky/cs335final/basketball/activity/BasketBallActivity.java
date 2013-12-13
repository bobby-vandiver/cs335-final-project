package edu.uky.cs335final.basketball.activity;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import edu.uky.cs335final.basketball.R;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.model.Camera;
import edu.uky.cs335final.basketball.view.BasketBallView;

public class BasketBallActivity extends Activity implements SensorEventListener {

    private static final String TAG = BasketBallActivity.class.getCanonicalName();

    private static final float ACCELERATION_THRESHOLD = 9.0f;
    private static final float FILTER_ALPHA = 0.8f;

    private static final float CAMERA_ANGLE_DISPLACEMENT = 10.0f;

    private Vector gravity;
    private Vector previousAcceleration;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private Camera camera;

    private BasketBallView basketBallView;
    private View hudView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initAccelerometer();
    }

    private void initView() {
        camera = createCamera();
        basketBallView = new BasketBallView(this, camera);
        setContentView(basketBallView);
        addHud();
    }

    private Camera createCamera() {
        Vector eye = new Vector(0f, 0f, 12f);
        Vector center = new Vector(0f, 5f, 0f);
        Vector up = new Vector(0f, 1f, 0f);

        return new Camera(eye, center, up);
    }

    private void addHud() {
        loadHudLayout();
        addCameraControls();
    }

    private void loadHudLayout() {

        LayoutInflater inflater = getLayoutInflater();
        hudView = inflater.inflate(R.layout.hud, null);

        ViewGroup.LayoutParams params = getLayoutParams();
        addContentView(hudView, params);
    }

    private ViewGroup.LayoutParams getLayoutParams() {
        return new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void addCameraControls() {

        // TODO: Place holders to verify functionality of controls

        addListenerToButton("look up", R.id.look_up_button);
        addListenerToButton("look down", R.id.look_down_button);
        addListenerToButton("look left", R.id.look_left_button);
        addListenerToButton("look right", R.id.look_right_button);

        addListenerToButton("move left", R.id.move_left_button);
        addListenerToButton("move right", R.id.move_right_button);
    }

    private void addListenerToButton(String message, int viewId) {
        OnClickListener listener = createClickListener(message);
        ImageButton button = (ImageButton) findViewById(viewId);
        button.setOnClickListener(listener);
    }

    private OnClickListener createClickListener(final String message) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayToast(message);
            }
        };
    }

    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initAccelerometer() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        gravity = new Vector();
        previousAcceleration = new Vector();
    }

    @Override
    protected void onPause() {
        super.onPause();
        basketBallView.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        basketBallView.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.v(TAG, "Sensor changed");

        Vector sensorEvent = new Vector(event.values[0], event.values[1], event.values[2]);
        Vector filteredSensorEvent = filterSensorEvent(sensorEvent);

        gravity.multiply(FILTER_ALPHA).add(filteredSensorEvent);

        Vector currentAcceleration = new Vector(sensorEvent).subtract(gravity);
        Log.v(TAG, "currentAcceleration [" + currentAcceleration + "]");

        float delta = changeInAcceleration(currentAcceleration);
        Log.v(TAG, "Delta [" + delta + "]");

        if(playerCanShootBall(delta)) {
            Log.d(TAG, "Shot detected");
            basketBallView.shootBall(delta);
        }
    }

    private Vector filterSensorEvent(Vector sensorEvent) {
        float scalar = 1.0f - FILTER_ALPHA;
        return new Vector(sensorEvent).multiply(scalar);
    }

    private float changeInAcceleration(Vector currentAcceleration) {
        return currentAcceleration.magnitude() - previousAcceleration.magnitude();
    }

    private boolean playerCanShootBall(float delta) {
        return delta > ACCELERATION_THRESHOLD;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
