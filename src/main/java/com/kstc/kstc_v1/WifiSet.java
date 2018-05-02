package com.kstc.kstc_v1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class WifiSet extends Activity {

    /*Java 验证Ip是否合法*/
    public static boolean isIPAddress(String ipaddr) {
        boolean flag = false;
        Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
        Matcher m = pattern.matcher(ipaddr);
        flag = m.matches();
        return flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_set);

        Button mbutton = (Button) findViewById(R.id.btn_wifilink);
        if(MainActivity.UDP_ServerRunable)
            mbutton.setText("断开连接");
        else
            mbutton.setText("连接");
        final TextView host_ip = (TextView) findViewById(R.id.edit_hostip);
        final TextView host_port = (TextView) findViewById(R.id.edit_hostport);
        final TextView local_port = (TextView) findViewById(R.id.edit_localport);

        host_ip.setText(MainActivity.HostIP);
        host_port.setText(String.valueOf(MainActivity.HostPort));
        local_port.setText(String.valueOf(MainActivity.LocalPort));

        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.HostIP = host_ip.getText().toString();
                MainActivity.HostPort = Integer.valueOf(host_port.getText().toString()).intValue();
                MainActivity.LocalPort = Integer.valueOf(local_port.getText().toString()).intValue();

                Button mbutton = (Button) findViewById(R.id.btn_wifilink);
                if(mbutton.getText()=="连接")
                {
                    if (isIPAddress(MainActivity.HostIP))
                    {
                        //开启UDP
                        MainActivity.UDP_Start();
                        if (MainActivity.UDP_ServerRunable)
                        {
                            //开启成功
                            WifiSet.this.finish();
                        }
                        else {
                            Toast toast = Toast.makeText(WifiSet.this, "UDP连接创建失败,请检查参数设置", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    else
                    {
                        Toast toast = Toast.makeText(WifiSet.this, "非法IP,请检查IP设置", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else
                {
                    MainActivity.UDP_Stop();
                    mbutton.setText("连接");
                }
            }
        });
    }
}
