package com.freedom;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class OrienGeter {
	private float roll;
	
	public OrienGeter(SensorManager sm)
	{
		sm.registerListener(oriListener, SensorManager.SENSOR_ORIENTATION, 
				SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	public final SensorListener oriListener = new SensorListener()
	{
		@Override
		public void onAccuracyChanged(int sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(int sensor, float[] values) {
			// TODO Auto-generated method stub
			if(sensor==SensorManager.SENSOR_ORIENTATION)
			{
				roll = values[0];
			}
		}
	
	};
	
	public float getRoll()
	{
		return roll;
	}

}
