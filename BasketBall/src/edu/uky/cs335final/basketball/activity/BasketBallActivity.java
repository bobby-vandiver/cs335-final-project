package edu.uky.cs335final.basketball.activity;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import edu.uky.cs335final.basketball.R;
import edu.uky.cs335final.basketball.geometry.Vector;
import edu.uky.cs335final.basketball.camera.Camera;
import edu.uky.cs335final.basketball.render.BasketBallRenderer;
import edu.uky.cs335final.basketball.render.BasketBallRenderer.ReplayListener;
import edu.uky.cs335final.basketball.render.BasketBallRenderer.ShotListener;
import edu.uky.cs335final.basketball.view.BasketBallView;

public class BasketBallActivity extends Activity implements SensorEventListener {

    private static final String TAG = BasketBallActivity.class.getCanonicalName();

    private static final float ACCELERATION_THRESHOLD = 9.0f;
    private static final float FILTER_ALPHA = 0.8f;

    private Vector gravity;
    private Vector previousAcceleration;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private static final float CAMERA_ROTATION_ANGLE = 10.0f;
    private static final float CAMERA_DISPLACEMENT = 1.0f;

    private Camera camera;

    private BasketBallView basketBallView;
    private BasketBallRenderer basketBallRenderer;

    private View hudView;

    private Handler handler;

    private static final float[] speedFactors = { 1.0f, 0.25f, 0.1f };
    private int speedFactorSelector = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain handle for inter-thread communication
        handler = new Handler(Looper.getMainLooper());

        initView();
        initAccelerometer();
    }

    private void initView() {
        camera = createCamera();
        basketBallRenderer = createRenderer();

        basketBallView = new BasketBallView(this, basketBallRenderer);
        setContentView(basketBallView);

        addHud();
    }

    private Camera createCamera() {
        Vector position = new Vector(0f, 5f, 12f);
        Vector direction = new Vector(0f, 0f, -1f);
        Vector up = new Vector(0f, 1f, 0f);

        return new Camera(position, direction, up);
    }

    private BasketBallRenderer createRenderer() {
        BasketBallRenderer renderer = new BasketBallRenderer(this, camera);

        ShotListener shotListener = createShotListener();
        renderer.setShotListener(shotListener);

        ReplayListener replayListener = createReplayListener();
        renderer.setReplayListener(replayListener);

        return renderer;
    }

    private ShotListener createShotListener() {
        return new ShotListener() {

            @Override
            public void onStart() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Shot initiated!");
                    }
                });
            }

            @Override
            public void onComplete() {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        enableReplayButton();
                        enableSpeedButton();
                    }
                });
            }

            private void enableReplayButton() {
                Log.d(TAG, "Enabling replay button");

                Button replayButton = (Button) findViewById(R.id.replay_button);
                replayButton.setVisibility(View.VISIBLE);

                replayButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Cannot restart a replay once it has started
                        if (basketBallRenderer.isReplayInProgress())
                            return;

                        basketBallRenderer.startReplay();
                    }
                });
            }

            private void enableSpeedButton() {
                Log.d(TAG, "Enabling speed control button");

                Button speedButton = (Button) findViewById(R.id.speed_button);
                speedButton.setVisibility(View.VISIBLE);

                // Display default speed
                setSpeedButtonText(speedButton);
                setUpdateSpeed();

                OnClickListener listener = createSpeedButtonListener();
                speedButton.setOnClickListener(listener);
            }

            private OnClickListener createSpeedButtonListener() {
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Don't allow the button to change after the
                        // game is over for consistency with replay functionality
                        if(basketBallRenderer.isGameOver())
                            return;

                        // Select the next speed
                        speedFactorSelector++;

                        // Ensure we always have a valid index
                        speedFactorSelector %= speedFactors.length;

                        setUpdateSpeed();

                        // Change the text on the button
                        Button speedButton = (Button) v;
                        setSpeedButtonText(speedButton);
                    }
                };
            }

            private void setUpdateSpeed() {
                float factor = speedFactors[speedFactorSelector];
                basketBallRenderer.changeBallUpdateSpeed(factor);
            }

            private void setSpeedButtonText(Button speedButton) {
                float factor = speedFactors[speedFactorSelector];
                String text = Float.toString(factor);
                speedButton.setText(text);
            }

            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure() {
            }
        };
    }

    private ReplayListener createReplayListener() {
        return new ReplayListener() {
            @Override
            public void onStart() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        enableMoveableCamera();
                    }
                });
            }

            private void enableMoveableCamera() {
                Log.d(TAG, "Enabling moveable camera");
                basketBallView.setUpScaleGestureDetector(BasketBallActivity.this, camera);
            }

            @Override
            public void onStop() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Disabling replay button");
                        disableButton(R.id.replay_button);

                        Log.d(TAG, "Disabling speed button");
                        disableButton(R.id.speed_button);

                        Log.d(TAG, "Disabling moveable camera");
                        basketBallView.disableScaleGestureDetector();
                    }
                });
            }

            private void disableButton(int viewId) {
                Button button = (Button) findViewById(viewId);
                button.setOnClickListener(null);
                button.setEnabled(false);
            }
        };
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

        addPitchListener(CAMERA_ROTATION_ANGLE, R.id.look_up_button);
        addPitchListener(-CAMERA_ROTATION_ANGLE, R.id.look_down_button);

        addYawListener(CAMERA_ROTATION_ANGLE, R.id.look_left_button);
        addYawListener(-CAMERA_ROTATION_ANGLE, R.id.look_right_button);

        addMoveListener(-CAMERA_DISPLACEMENT, R.id.move_left_button);
        addMoveListener(CAMERA_DISPLACEMENT, R.id.move_right_button);
    }

    private void addPitchListener(float angle, int viewId) {
        OnClickListener listener = createPitchListener(angle);
        addListenerToButton(listener, viewId);
    }

    private void addYawListener(float angle, int viewId) {
        OnClickListener listener = createYawListener(angle);
        addListenerToButton(listener, viewId);
    }

    private void addMoveListener(float displacement, int viewId) {
        OnClickListener listener = createMoveListener(displacement);
        addListenerToButton(listener, viewId);
    }

    private void addListenerToButton(OnClickListener listener, int viewId) {
        ImageButton button = (ImageButton) findViewById(viewId);
        button.setOnClickListener(listener);
    }

    private OnClickListener createYawListener(final float angle) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.updateYaw(angle);
            }
        };
    }

    private OnClickListener createPitchListener(final float angle) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.updatePitch(angle);
            }
        };
    }

    private OnClickListener createMoveListener(final float offset) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Vector displacement = new Vector(offset, 0f, 0f);
                camera.updatePosition(displacement);
            }
        };
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
