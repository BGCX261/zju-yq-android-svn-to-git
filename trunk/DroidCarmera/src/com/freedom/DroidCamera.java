package com.freedom;

import java.io.IOException;

import com.google.android.maps.MapActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class DroidCamera extends Activity implements SurfaceHolder.Callback{
    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    Camera mCamera;
    
    private LocationManager lm01;
	private String strLocationProvider="";
	private Location l01=null;
	
	private TextView mTextView01;
	private TextView mTextView02;
	private TextView mTextView03;
	private TextView mTextView04;
	private TextView mTextView05;
	
	private double Lat;
	private double Lng;
	private boolean isGPS = false;
	
	private SensorManager sensorManager;
	private OrienGeter og;
	private double roll;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
      	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    		  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
      	setContentView(R.layout.main);
      	
      	sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
      	sensorManager.registerListener(mOriListener, 
      			SensorManager.SENSOR_ORIENTATION, SensorManager.SENSOR_DELAY_NORMAL);
      	og = new OrienGeter(sensorManager);
      		
      	mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
      	mSurfaceHolder = mSurfaceView.getHolder();
      	mSurfaceHolder.addCallback(this);
      	mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
      	
      	mTextView01 = (TextView)findViewById(R.id.TextView01);
      	mTextView02 = (TextView)findViewById(R.id.TextView02);
      	mTextView03 = (TextView)findViewById(R.id.TextView03);
      	mTextView04 = (TextView)findViewById(R.id.TextView04);
      	
        Log.i("Start Tag", "start");
        lm01 = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        l01 = getLocationProvider(lm01);
        lm01.requestLocationUpdates(strLocationProvider, 1000, 0, mLocationListener01);
        
        //l01 = lm01.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(l01!=null)
        {
        	processLocationUpdated(l01);
        }
        else
        {
        	Log.e("Location Null", "NULL");
        	mTextView01.setText("服务错误，不能获得当前位置");
        	mTextView02.setText("纬度: NA");
        	mTextView03.setText("经度：NA");
        	mTextView04.setText("朝向: NA");
        }
    }

    public final SensorListener mOriListener = new SensorListener()
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
				mTextView04.setText("朝向：" + roll);
			}
		}
    	
    };
    
    public final LocationListener mLocationListener01 = new LocationListener()
    {
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			processLocationUpdated(location);
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
    	
    };
    
	private void processLocationUpdated(Location location) {
		// TODO Auto-generated method stub
		Lat = location.getLatitude();
		Lng = location.getLongitude();
		
		mTextView02.setText("纬度：" + Lat);
		mTextView03.setText("经度：" + Lng);
		//mTextView04.setText("朝向：" + og.getRoll());
	}
    
	private Location getLocationProvider(LocationManager lm) {
		// TODO Auto-generated method stub
		Location retLocation = null;
		try
		{
			Criteria mCriteria01 = new Criteria();
			mCriteria01.setAccuracy(Criteria.ACCURACY_FINE);
			mCriteria01.setAltitudeRequired(false);
			mCriteria01.setBearingRequired(false);
			mCriteria01.setCostAllowed(true);
			mCriteria01.setPowerRequirement(Criteria.POWER_LOW);
			//strLocationProvider = lm.getBestProvider(mCriteria01, true);
			
			if(isGPS)
			{
				strLocationProvider = LocationManager.GPS_PROVIDER;
				mTextView01.setText("使用GPS获得的您所在位置的信息：");
			}
			else
			{
				strLocationProvider = LocationManager.NETWORK_PROVIDER;
				mTextView01.setText("使用网络获得的您所在位置的信息：");
			}
			retLocation = lm.getLastKnownLocation(strLocationProvider);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return retLocation;
	}
	
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int arg1, int w, int h) {
		// TODO Auto-generated method stub
		Camera.Parameters parameters = mCamera.getParameters();
		//parameters.setPreviewSize(w, h);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
        // to draw.
        mCamera = Camera.open();
        try {
           mCamera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
            // TODO: add more exception handling logic here
        }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
	}
	
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	menu.add(0,0,0, "关于");
    	menu.add(0,1,1, "退出");
    	menu.add(0,2,2, "卫星");
    	menu.add(0,3,3, "网络");
    	return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	super.onOptionsItemSelected(item);
    	switch(item.getItemId())
    	{
    	case 0:
    		openAboutDialog();
    		break;
    	case 1:
    		finish();
    		break;
    	case 2:
    		isGPS = true;
    		mTextView01.setText("使用GPS获得的您所在位置的信息：");
    		lm01.removeUpdates(mLocationListener01);   //更改监听器，使用GPS来获得位置信息
    		lm01.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener01);
    		break;
    	case 3:
    		isGPS = false;
    		mTextView01.setText("使用网络获得的您所在位置的信息：");
    		lm01.removeUpdates(mLocationListener01);   //更改监听器，使用网络来获得位置信息
    		lm01.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, mLocationListener01);
    		break;
    	default:
    		break;
    	}
    	return true;
    }
    
    private void openAboutDialog()
    {
    	new AlertDialog.Builder(this)
    	.setTitle("关于")
    	.setMessage("国创")
    	.setPositiveButton("确认", 
    			new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				}).show();
    }
}