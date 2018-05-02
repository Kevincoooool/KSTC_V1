package com.kstc.kstc_v1;

import java.util.Timer;
import java.util.TimerTask;
import unit.thr_bar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityControl extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();  
	private SensorManager mSensorManager;  
    private Sensor mSensor;  
    
    buttonListener btnliListener1 = new buttonListener();
    
    private int VAL_THR,VAL_YAW=1500,VAL_ROL,VAL_PIT;
    private int DeadAngle = 1;
    private int MAXAngle = 50;
    private int YAW_D = 1200;
    private int YAW_A = 1800;
    public static int FLAG_DG = 0;
    private Switch switch_dg=null;
    Timer send_timer = new Timer( );
    
    private float acc_x,acc_y,acc_z;
    
    TextView text_thr;
    TextView text_yaw;
    TextView text_rol;
    TextView text_pit;
    TextView tv_ang_rol;
    TextView tv_ang_pit;
    TextView tv_ang_yaw;
    TextView tv_height;
    TextView tv_acc_x;
    TextView tv_acc_y;
    TextView tv_acc_z;
    TextView tv_gyr_x;
    TextView tv_gyr_y;
    TextView tv_gyr_z;
    TextView tv_votage1;
    
    private final Handler ui_handler = new Handler();  
    
    private final Runnable ui_task = new Runnable() {  
  
        @Override  
        public void run() {  
            // TODO Auto-generated method stub   
        	ui_handler.postDelayed(this, 30); 
        	
        	VAL_THR = thr_bar.Value * 1000 / thr_bar.Value_max + 1000;
        	double temp_rol = Math.atan2(acc_y,acc_z)*180/Math.PI;
        	double temp_pit = -Math.atan2(acc_x,acc_z)*180/Math.PI;
        	
        	if (temp_rol > MAXAngle)
        		temp_rol = MAXAngle;
        	else if (temp_rol < -MAXAngle)
        		temp_rol = -MAXAngle;
        	if (temp_rol < DeadAngle && temp_rol > -DeadAngle)
        		temp_rol = 0;
        	
        	if (temp_pit > MAXAngle)
        		temp_pit = MAXAngle;
        	else if (temp_pit < -MAXAngle)
        		temp_pit = -MAXAngle;
        	if (temp_pit < DeadAngle && temp_pit > -DeadAngle)
        		temp_pit = 0;

        	VAL_ROL = (int)(temp_rol * 500 / MAXAngle + 1500);
        	VAL_PIT = (int)(temp_pit * 500 / MAXAngle + 1500);
            text_thr.setText(""+VAL_THR);
            text_yaw.setText(""+VAL_YAW);
            text_rol.setText(""+VAL_ROL);
            text_pit.setText(""+VAL_PIT);
            tv_ang_rol.setText(""+MainActivity.VAL_ANG_X);
            tv_ang_pit.setText(""+MainActivity.VAL_ANG_Y);
            tv_ang_yaw.setText(""+MainActivity.VAL_ANG_Z);

            tv_height.setText(""+MainActivity.VAL_HEIGHT);
            tv_acc_x.setText(""+MainActivity.VAL_ACC_X);
            tv_acc_y.setText(""+MainActivity.VAL_ACC_Y);
            tv_acc_z.setText(""+MainActivity.VAL_ACC_Z);
            tv_gyr_x.setText(""+MainActivity.VAL_GYR_X);
            tv_gyr_y.setText(""+MainActivity.VAL_GYR_Y);
            tv_gyr_z.setText(""+MainActivity.VAL_GYR_Z);
            tv_votage1.setText(""+MainActivity.VAL_VOTAGE1);

        }  
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏模式
	getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕变暗
	setContentView(R.layout.activity_control);

	 mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);  
     mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_GRAVITY  
     if (null == mSensorManager) {  
         Log.d(TAG, "deveice not support SensorManager");  
     }  
     // 参数三，检测的精准度  
     mSensorManager.registerListener(myAccelerometerListener, mSensor,  SensorManager.SENSOR_DELAY_NORMAL);// SENSOR_DELAY_GAME  
     
     text_thr = (TextView)findViewById(R.id.text_col_thr);
     text_yaw = (TextView)findViewById(R.id.text_col_yaw);
     text_rol = (TextView)findViewById(R.id.text_col_rol);
     text_pit = (TextView)findViewById(R.id.text_col_pit);
     tv_ang_rol = (TextView)findViewById(R.id.text_val_rol);
     tv_ang_pit = (TextView)findViewById(R.id.text_val_pit);
     tv_ang_yaw = (TextView)findViewById(R.id.text_val_yaw);
     tv_height = (TextView)findViewById(R.id.text_height);
     tv_acc_x = (TextView)findViewById(R.id.text_val_acc_x);
     tv_acc_y = (TextView)findViewById(R.id.text_val_acc_y);
     tv_acc_z = (TextView)findViewById(R.id.text_val_acc_z);
     tv_gyr_x = (TextView)findViewById(R.id.text_val_gyr_x);
     tv_gyr_y = (TextView)findViewById(R.id.text_val_gyr_y);
     tv_gyr_z = (TextView)findViewById(R.id.text_val_gyr_z);
     tv_votage1 = (TextView)findViewById(R.id.text_val_votage1);


     Button mbutton = (Button) findViewById(R.id.btn_yaw_d);
     mbutton.setOnTouchListener(btnliListener1);
     mbutton = (Button) findViewById(R.id.btn_yaw_a);
     mbutton.setOnTouchListener(btnliListener1);
     mbutton = (Button) findViewById(R.id.button_cal_acc);
     mbutton.setOnTouchListener(btnliListener1);

     mbutton = (Button) findViewById(R.id.btn_stop);
     mbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.Send_Command((byte)0xa0);
			}
		});
     mbutton = (Button) findViewById(R.id.button_cal_acc);
     mbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.Send_Command((byte)0xa3);
            }
        });
     mbutton = (Button) findViewById(R.id.btn_suoding);
     mbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.Send_Command((byte)0xa0);
			}
		});
     mbutton = (Button) findViewById(R.id.btn_jias);
     mbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.Send_Command((byte)0xa1);
			}
		});
     mbutton = (Button) findViewById(R.id.button_yjqf);
        mbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.Send_Command((byte)0xb1);
            }
        });
        mbutton = (Button) findViewById(R.id.button_yjjl);
        mbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.Send_Command((byte)0xb2);
            }
        });

        switch_dg=(Switch)super.findViewById(R.id.switch_dg);
        switch_dg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Toast.makeText(getApplicationContext(), "打开定高", Toast.LENGTH_SHORT).show();
                    MainActivity.Send_Command((byte)0xc9);
                    FLAG_DG=1;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "关闭定高", Toast.LENGTH_SHORT).show();
                    MainActivity.Send_Command((byte)0xc8);
                    FLAG_DG=0;
                }
            }
        });
     send_timer.schedule(send_task,500,50);
     ui_handler.postDelayed(ui_task, 100);  
	}
	class buttonListener implements OnTouchListener
    {
		@SuppressLint("ClickableViewAccessibility")
		public boolean onTouch(View v, MotionEvent event)
        {
			if(v.getId() == R.id.btn_yaw_d)
            {
            	if(event.getAction() == MotionEvent.ACTION_UP){  
                	VAL_YAW = 1500;

                }   
            	if(event.getAction() == MotionEvent.ACTION_CANCEL){  
                	VAL_YAW = 1500;
                }   
                if(event.getAction() == MotionEvent.ACTION_DOWN){  
                	VAL_YAW = YAW_D;
                }  
            }  
			if(v.getId() == R.id.btn_yaw_a)
            {
            	if(event.getAction() == MotionEvent.ACTION_UP){  
                	VAL_YAW = 1500;
                }   
            	if(event.getAction() == MotionEvent.ACTION_CANCEL){  
                	VAL_YAW = 1500;
                }   
                if(event.getAction() == MotionEvent.ACTION_DOWN){  
                	VAL_YAW = YAW_A;
                }
            }  
            return false;  
        } 
	}
	  /* 
     * SensorEventListener接口的实现，需要实现两个方法 
     * 方法1 onSensorChanged 当数据变化的时候被触发调用 
     * 方法2 onAccuracyChanged 当获得数据的精度发生变化的时候被调用，比如突然无法获得数据时 
     * */  
    final SensorEventListener myAccelerometerListener = new SensorEventListener(){  
          
        //复写onSensorChanged方法  
        public void onSensorChanged(SensorEvent sensorEvent){  
            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){  
                //Log.i(TAG,"onSensorChanged");
                  
                //图解中已经解释三个值的含义  
                acc_x = sensorEvent.values[0];  
                acc_y = sensorEvent.values[1];  
                acc_z = sensorEvent.values[2];
                Log.i(TAG,"\n heading "+acc_x);
                // Log.i(TAG,"\n pitch "+acc_y);
                //Log.i(TAG,"\n roll "+acc_z);
            }  
        }  
        //复写onAccuracyChanged方法  
        public void onAccuracyChanged(Sensor sensor , int accuracy){  
            Log.i(TAG, "onAccuracyChanged");  
        }  
    };  
      
    public void onPause(){  
        /* 
         * 很关键的部分：注意，说明文档中提到，即使activity不可见的时候，感应器依然会继续的工作，测试的时候可以发现，没有正常的刷新频率 
         * 也会非常高，所以一定要在onPause方法中关闭触发器，否则讲耗费用户大量电量，很不负责。 
         * */  
    	mSensorManager.unregisterListener(myAccelerometerListener);  
        super.onPause();  
    }  
    
    TimerTask send_task = new TimerTask( ) {
    	byte[] bytes = new byte[25];
    	public void run ( ) 
    	{
    		byte sum=0;
    		
    		bytes[0] = (byte) 0xaa;
    		bytes[1] = (byte) 0xaf;
    		bytes[2] = (byte) 0x03;
    		bytes[3] = (byte) 20;
    		bytes[4] = (byte) (VAL_THR/0xff);
    		bytes[5] = (byte) (VAL_THR%0xff);
    		bytes[6] = (byte) (VAL_YAW/0xff);
    		bytes[7] = (byte) (VAL_YAW%0xff);
    		bytes[8] = (byte) (VAL_ROL/0xff);
    		bytes[9] = (byte) (VAL_ROL%0xff);
    		bytes[10] = (byte) (VAL_PIT/0xff);
    		bytes[11] = (byte) (VAL_PIT%0xff);
    		bytes[12] = 1;
    		bytes[13] = 2;
    		bytes[14] = 3;
    		bytes[15] = 4;
    		bytes[16] = 0;
    		bytes[17] = 0;
    		bytes[18] = 0;
    		bytes[19] = 0;
    		bytes[20] = 0;
    		bytes[21] = 0;
    		bytes[22] = 0;
    		bytes[23] = 0;
    		for(int i=0;i<24;i++) sum += bytes[i];
    		bytes[24] = sum;
    		
    		MainActivity.SendData_Byte(bytes);
    	}
    	};
    	
    protected void onDestroy ( ) {
		if (send_timer != null) 
		{
			send_timer.cancel( );
			send_timer = null;
		}
		super.onDestroy( );
		}

}  
