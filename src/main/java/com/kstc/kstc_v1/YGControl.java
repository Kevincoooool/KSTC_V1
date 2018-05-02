package com.kstc.kstc_v1;

/**
 * Created by Administrator on 2017/12/20.
 */

import unit.DoubleRockerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


/**
 * @author winder
 *
 */
public class YGControl extends Activity {

    private int VAL_THR,VAL_YAW,VAL_ROL,VAL_PIT;
    private static final String TAG = MainActivity.class.getSimpleName();
    buttonListener btnliListener2 = new buttonListener();
    Timer send_timer = new Timer( );
    TextView text_thr1;
    TextView text_yaw1;
    TextView text_rol1;
    TextView text_pit1;
    TextView tv_ang_rol1;
    TextView tv_ang_pit1;
    TextView tv_ang_yaw1;
    TextView tv_height1;

    private final Handler ui_handler1 = new Handler();
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏模式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕变暗
        setContentView(R.layout.activity_double_rocker);

        text_thr1 = (TextView)findViewById(R.id.text_con_thr1);
        text_pit1 = (TextView)findViewById(R.id.text_con_pit1);
        text_rol1 = (TextView)findViewById(R.id.text_con_rol1);
        text_yaw1 = (TextView)findViewById(R.id.text_con_yaw1);
        tv_ang_rol1 = (TextView)findViewById(R.id.text_val_rol1);
        tv_ang_pit1 = (TextView)findViewById(R.id.text_val_pit1);
        tv_ang_yaw1 = (TextView)findViewById(R.id.text_val_yaw1);
        tv_height1 = (TextView)findViewById(R.id.text_val_height1);

        Button mbutton = (Button) findViewById(R.id.btn_jt);
        mbutton.setOnTouchListener(btnliListener2);
        mbutton = (Button) findViewById(R.id.btn_jias);
        mbutton.setOnTouchListener(btnliListener2);
        mbutton = (Button) findViewById(R.id.btn_jies);
        mbutton.setOnTouchListener(btnliListener2);
        mbutton = (Button) findViewById(R.id.btn_jz);
        mbutton.setOnTouchListener(btnliListener2);

        mbutton = (Button) findViewById(R.id.btn_jt);
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.Send_Command((byte)0xa0);
            }
        });
        mbutton = (Button) findViewById(R.id.btn_jz);
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.Send_Command((byte)0xa3);
            }
        });
        mbutton = (Button) findViewById(R.id.btn_jias);
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.Send_Command((byte)0xa0);
            }
        });
        mbutton = (Button) findViewById(R.id.btn_jies);
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.Send_Command((byte)0xa1);
            }
        });

        send_timer.schedule(send_task,500,50);
        ui_handler1.postDelayed(ui_task, 100);
    }
    class buttonListener implements View.OnTouchListener
    {
        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouch(View v, MotionEvent event)
        {
            return false;
        }
    }
    private final Runnable ui_task = new Runnable()
    {
        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            ui_handler1.postDelayed(this, 30);
            VAL_THR = DoubleRockerView.Value_Y1;
            VAL_ROL = DoubleRockerView.Value_X2;
            VAL_PIT = DoubleRockerView.Value_Y2;
            VAL_YAW = DoubleRockerView.Value_X1;
            text_thr1.setText(""+VAL_THR);
            text_yaw1.setText(""+VAL_YAW);
            text_rol1.setText(""+VAL_ROL);
            text_pit1.setText(""+VAL_PIT);
            tv_ang_rol1.setText(""+MainActivity.VAL_ANG_X);
            tv_ang_pit1.setText(""+MainActivity.VAL_ANG_Y);
            tv_ang_yaw1.setText(""+MainActivity.VAL_ANG_Z);
            tv_height1.setText(""+MainActivity.VAL_HEIGHT);
        }
    };
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