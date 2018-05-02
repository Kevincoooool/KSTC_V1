package com.kstc.kstc_v1;


/**
 * Created by Administrator on 2017/12/16.
 */
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import unit.SingleRockerView;

public class CarControl extends Activity {

        private Switch switch_on=null;
        private int VAL_X,VAL_Y;
        Timer send_timer = new Timer( );
        TextView text_val_x;
        TextView text_val_y;
        public static int VAL_PID_PID1_P=0,VAL_PID_PID1_I=0,VAL_PID_PID1_D=0,VAL_PID_PID2_P=0,VAL_PID_PID2_I=0,VAL_PID_PID2_D=0, VAL_PID_PID3_P=0,VAL_PID_PID3_I=0,VAL_PID_PID3_D=0, VAL_PID_PID4_P=0,VAL_PID_PID4_I=0,VAL_PID_PID4_D=0;
        private final Handler ui_handler = new Handler();
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
                super.onCreate(savedInstanceState);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏模式
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//禁止屏幕变暗
                setContentView(R.layout.activity_carcontrol);

                switch_on=(Switch)super.findViewById(R.id.switch_on);
                text_val_x = (TextView)findViewById(R.id.val_x);
                text_val_y = (TextView)findViewById(R.id.val_y);


                switch_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked)
                                {
                                        Toast.makeText(getApplicationContext(), "开", Toast.LENGTH_SHORT).show();
                                        MainActivity.Send_Command((byte)0xc9);
                                }
                                else
                                {
                                        Toast.makeText(getApplicationContext(), "关", Toast.LENGTH_SHORT).show();
                                        MainActivity.Send_Command((byte)0xc8);
                                }
                        }
                });
                Button mbutton = (Button) findViewById(R.id.btn_rpid);
                mbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                MainActivity.Send_Command((byte)0x09);
                        }
                });
                mbutton = (Button) findViewById(R.id.btn_wpid);
                mbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Send_PID();
                        }
                });
                send_timer.schedule(send_task,1000,50);
                ui_handler.postDelayed(ui_task, 100);

        }

        private final Runnable ui_task = new Runnable()
        {
                @Override
                public void run() {

                        // TODO Auto-generated method stub
                        ui_handler.postDelayed(this, 30);
                        VAL_X = SingleRockerView.Value_X;
                        VAL_Y = SingleRockerView.Value_Y;
                        text_val_x.setText(""+VAL_X);
                        text_val_y.setText(""+VAL_Y);
                }
        };
        TimerTask send_task = new TimerTask( ) {
                byte[] bytes = new byte[5];
                public void run ( )
                {
                        byte sum=0;
                        bytes[0] = (byte) 0xaa;
                        bytes[1] = (byte) (VAL_X/0xff);
                        bytes[2] = (byte) (VAL_Y%0xff);
                        bytes[3] = (byte) 0xa0;
                        for(int i=0;i<4;i++) sum += bytes[i];
                        bytes[4] = sum;
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
        static void Send_PID()
        {
                byte[] bytes = new byte[29];
                byte sum=0;
                bytes[0] = (byte) 0xaa;
                bytes[1] = (byte) 0xaf;
                bytes[2] = (byte) 0x11;
                bytes[3] = (byte) 18;
                bytes[4] = (byte) (VAL_PID_PID1_P/256);
                bytes[5] = (byte) (VAL_PID_PID1_P%256);
                bytes[6] = (byte) (VAL_PID_PID1_I/256);
                bytes[7] = (byte) (VAL_PID_PID1_I%256);
                bytes[8] = (byte) (VAL_PID_PID1_D/256);
                bytes[9] = (byte) (VAL_PID_PID1_D%256);

                bytes[10] = (byte) (VAL_PID_PID2_P/256);
                bytes[11] = (byte) (VAL_PID_PID2_P%256);
                bytes[12] = (byte) (VAL_PID_PID2_I/256);
                bytes[13] = (byte) (VAL_PID_PID2_I%256);
                bytes[14] = (byte) (VAL_PID_PID2_D/256);
                bytes[15] = (byte) (VAL_PID_PID2_D%256);

                bytes[16] = (byte) (VAL_PID_PID3_P/256);
                bytes[17] = (byte) (VAL_PID_PID3_P%256);
                bytes[18] = (byte) (VAL_PID_PID3_I/256);
                bytes[19] = (byte) (VAL_PID_PID3_I%256);
                bytes[20] = (byte) (VAL_PID_PID3_D/256);
                bytes[21] = (byte) (VAL_PID_PID3_D%256);

                bytes[22] = (byte) (VAL_PID_PID4_P/256);
                bytes[23] = (byte) (VAL_PID_PID4_P%256);
                bytes[24] = (byte) (VAL_PID_PID4_I/256);
                bytes[25] = (byte) (VAL_PID_PID4_I%256);
                bytes[26] = (byte) (VAL_PID_PID4_D/256);
                bytes[27] = (byte) (VAL_PID_PID4_D%256);

                for(int i=0;i<28;i++) sum += bytes[i];
                bytes[28] = sum;

                MainActivity.SendData_Byte(bytes);
        }
}
