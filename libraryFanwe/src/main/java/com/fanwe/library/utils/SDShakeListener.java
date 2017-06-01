package com.fanwe.library.utils;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 摇一摇监听
 */
public class SDShakeListener implements SensorEventListener
{
    private static final int ACC_SHAKE = 19;
    /**
     * 触发计算的间隔
     */
    private static final int DURATION_CALCULATE = 100;
    /**
     * 触发通知的间隔
     */
    private static final int DURATION_NOTIFY = 500;

    private SensorManager sensorManager;
    private Sensor sensor;

    private float lastX;
    private float lastY;
    private float lastZ;
    private long lastTime;

    private long lastNotifyTime;

    private ShakeListener shakeListener;

    public SDShakeListener(Context context)
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public SDShakeListener setShakeListener(ShakeListener shakeListener)
    {
        this.shakeListener = shakeListener;
        return this;
    }

    public void start()
    {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop()
    {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastTime;
        if (deltaTime < DURATION_CALCULATE)
        {
            return;
        }
        lastTime = currentTime;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;

        lastX = x;
        lastY = y;
        lastZ = z;

        if (Math.abs(deltaX) >= ACC_SHAKE || Math.abs(deltaY) >= ACC_SHAKE || Math.abs(deltaZ) >= ACC_SHAKE)
        {
            notifyListener();
        }
    }

    private void notifyListener()
    {
        long current = System.currentTimeMillis();
        if (current - lastNotifyTime < DURATION_NOTIFY)
        {
            return;
        }
        lastNotifyTime = current;

        if (shakeListener != null)
        {
            shakeListener.onShake();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }

    public interface ShakeListener
    {
        void onShake();
    }
}