package com.kstc.kstc_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class MainActivity extends Activity {

	//用于存储app参数
	private static final String SET_FILENAME = "ano_set_filename";

	public static String 	HostIP		= "192.168.4.1";
	public static int 	HostPort	= 9000;
	public static int 	LocalPort	= 9001;
	public static DatagramPacket UDP_SendPacket;
	public static boolean UDP_SendFlah = false;
	public static boolean UDP_ServerRunable = false;

	public static int VAL_ACC_X = 0;
	public static int VAL_ACC_Y = 0;
	public static int VAL_ACC_Z = 0;
	public static int VAL_GYR_X = 0;
	public static int VAL_GYR_Y = 0;
	public static int VAL_GYR_Z = 0;
	public static float VAL_ANG_X = 0;
	public static float VAL_ANG_Y = 0;
	public static float VAL_ANG_Z = 0;
	public static float VAL_HEIGHT = 0;
	public static int VAL_PID_ROL_P=0,VAL_PID_ROL_I=0,VAL_PID_ROL_D=0,VAL_PID_PIT_P=0,VAL_PID_PIT_I=0,VAL_PID_PIT_D=0, VAL_PID_YAW_P=0,VAL_PID_YAW_I=0,VAL_PID_YAW_D=0;
	public static int VAL_PID_PID1_P=0,VAL_PID_PID1_I=0,VAL_PID_PID1_D=0,VAL_PID_PID2_P=0,VAL_PID_PID2_I=0,VAL_PID_PID2_D=0, VAL_PID_PID3_P=0,VAL_PID_PID3_I=0,VAL_PID_PID3_D=0;
	public static int VAL_VOTAGE1 = 0;

	// Menu
 	private MenuItem mItemConnect;
 	private MenuItem mItemOptions;
 	private MenuItem mItemAbout;

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏模式

		// 得到SP对象   
		SharedPreferences sp = getSharedPreferences(SET_FILENAME, MODE_PRIVATE);
		// 从存储的XML文件中根据相应的键获取数据，没有数据就返回默认值    
		HostIP = sp.getString("Set_HostIP", "192.168.4.1");
		HostPort = sp.getInt("Set_HostPort", 9000);
		LocalPort = sp.getInt("Set_LocalPort", 9001);

		Button mbutton = (Button) findViewById(R.id.btn_col);
		mbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ActivityControl.class);
				startActivity(intent);
			}
		});

		mbutton = (Button) findViewById(R.id.btn_wifi_set);
		mbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, WifiSet.class);
				startActivity(intent);
			}
		});
		mbutton = (Button) findViewById(R.id.btn_carcol);
		mbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, CarControl.class);
				startActivity(intent);
			}
		});
		mbutton = (Button) findViewById(R.id.btn_ygcol);
		mbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, YGControl.class);
				startActivity(intent);
			}
		});
	}
	//创建菜单项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	mItemConnect = menu.add("连接");
    	mItemOptions = menu.add("设置");
    	mItemAbout = menu.add("关于");
    	return (super.onCreateOptionsMenu(menu));
    	
    }
    //菜单项动作处理
	@SuppressWarnings("deprecation")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if ( item == mItemConnect ) 
    	{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, WifiSet.class);
			startActivity(intent);
    	} 
    	else if ( item == mItemOptions ) 
    	{
			AlertDialog about = new AlertDialog.Builder(this).create();
			about.setCancelable(false);
			about.setMessage("对不起并没有设置功能");
			about.setButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			about.show();
    	} 
    	else if ( item == mItemAbout ) 
    	{
    		AlertDialog about = new AlertDialog.Builder(this).create();
    		about.setCancelable(false);
    		about.setMessage("欢迎使用酷世DIY上位机V1.0" +
					"  QQ97354734");
    		about.setButton("OK", new DialogInterface.OnClickListener() 
    		{
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
    		about.show();
    	}
    	return super.onOptionsItemSelected(item);
    }
	
	@Override
    public synchronized void onResume()
	{
		Save_Set();
    	super.onResume();
    }
    
    @Override
    public void onDestroy()
	{
		Save_Set();
		UDP_Stop();
        super.onDestroy();
    }
    //退出提醒
    @Override
    public void onBackPressed() {
    	new AlertDialog.Builder(this)
    	.setTitle("酷世DIY")
    	.setMessage("关闭程序?")
    	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();				
			}
		})
		.setNegativeButton("No", null)
		.show();
    }

	void Save_Set()
	{
		// 得到编辑器对象   
		Editor editor = getSharedPreferences(SET_FILENAME, MODE_PRIVATE).edit();
		// 存入键值对   
		editor.putString("Set_HostIP", HostIP);
		editor.putInt("Set_HostPort",HostPort);
		editor.putInt("Set_LocalPort",LocalPort);
		// 将内存中的数据写到XML文件中去   
		editor.commit();
	}

	static boolean UDP_Start()
	{
		byte[] data = new byte[0];

		while(UDP_SendPacket!=null)
			UDP_SendPacket = null;
		try
		{
			InetAddress I_HostIP = InetAddress.getByName(HostIP);
			UDP_SendPacket = new DatagramPacket(data,0,I_HostIP, HostPort);
            UDP_ServerRunable = true;
			new Thread(new UDP_Server()).start();
			return true;
		}
		catch(Exception e)
		{
			//return false;
		}
		return true;
	}
	static void UDP_Stop()
	{
        UDP_ServerRunable = false;
	}
	static class UDP_Server implements Runnable {
		byte[] buf = new byte[1024];
		DatagramSocket udp_socket = null;
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		@Override
		public void run() {
			try {
				udp_socket = new DatagramSocket(LocalPort);
			}
			catch(Exception e){}
			while (UDP_ServerRunable) {
				try {
					if (UDP_SendFlah) {
						UDP_SendFlah = false;
						udp_socket.send(UDP_SendPacket);
					}

					udp_socket.setSoTimeout(10);
					udp_socket.receive(packet);
					DataAnl(packet.getData(), packet.getLength());
				} catch (Exception e) {
					//run_enable = false;
				}
			}
			udp_socket.close();
			UDP_SendPacket = null;
			udp_socket = null;
		}
	}
	static void UDP_Send(byte[] data)
	{
		if(UDP_ServerRunable) {
			UDP_SendPacket.setData(data);
			UDP_SendPacket.setLength(data.length);
			UDP_SendFlah = true;
		}
	}
    static void SendData(String message)
    {

    }
	static void SendData_Byte(byte[] data)
    {
		UDP_Send(data);
    }
	static void Send_Command(byte data)
    {
    	byte[] bytes = new byte[6];
    	byte sum=0;

    	bytes[0] = (byte) 0xaa;
		bytes[1] = (byte) 0xaf;
		bytes[2] = (byte) 0x01;
		bytes[3] = (byte) 0x01;
		bytes[4] = data;
		for(int i=0;i<5;i++) sum += bytes[i];
		bytes[5] = sum;
		SendData_Byte(bytes);
    }
	static void Send_Command02(byte data)
    {
    	byte[] bytes = new byte[6];
    	byte sum=0;
    	bytes[0] = (byte) 0xaa;
		bytes[1] = (byte) 0xaf;
		bytes[2] = (byte) 0x02;
		bytes[3] = (byte) 0x01;
		bytes[4] = data;
		for(int i=0;i<5;i++) sum += bytes[i];
		bytes[5] = sum;
		SendData_Byte(bytes);
    }
	static void Send_PID1()
    {
    	byte[] bytes = new byte[23];
    	byte sum=0;
    	bytes[0] = (byte) 0xaa;
		bytes[1] = (byte) 0xaf;
		bytes[2] = (byte) 0x10;
		bytes[3] = (byte) 18;
		bytes[4] = (byte) (VAL_PID_ROL_P/256);
		bytes[5] = (byte) (VAL_PID_ROL_P%256);
		bytes[6] = (byte) (VAL_PID_ROL_I/256);
		bytes[7] = (byte) (VAL_PID_ROL_I%256);
		bytes[8] = (byte) (VAL_PID_ROL_D/256);
		bytes[9] = (byte) (VAL_PID_ROL_D%256);
		
		bytes[10] = (byte) (VAL_PID_PIT_P/256);
		bytes[11] = (byte) (VAL_PID_PIT_P%256);
		bytes[12] = (byte) (VAL_PID_PIT_I/256);
		bytes[13] = (byte) (VAL_PID_PIT_I%256);
		bytes[14] = (byte) (VAL_PID_PIT_D/256);
		bytes[15] = (byte) (VAL_PID_PIT_D%256);
		
		bytes[16] = (byte) (VAL_PID_YAW_P/256);
		bytes[17] = (byte) (VAL_PID_YAW_P%256);
		bytes[18] = (byte) (VAL_PID_YAW_I/256);
		bytes[19] = (byte) (VAL_PID_YAW_I%256);
		bytes[20] = (byte) (VAL_PID_YAW_D/256);
		bytes[21] = (byte) (VAL_PID_YAW_D%256);
		
		for(int i=0;i<22;i++) sum += bytes[i];
		bytes[22] = sum;
		
		SendData_Byte(bytes);
    }
	static void Send_PID2()
    {
    	byte[] bytes = new byte[23];
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
		
		for(int i=0;i<22;i++) sum += bytes[i];
		bytes[22] = sum;
		
		SendData_Byte(bytes);
    }
    static int COM_BUF_LEN = 1000;
    static byte[] RX_Data = new byte[COM_BUF_LEN];	//接收到的数据，AA开头
    static int rxstate = 0;
    static int rxlen = 0;//该帧已经接收到的长度
    static int rxcnt = 0;//该写入哪字节
    static void DataAnl(byte[] data, int len)
    {
    	for(int i=0;i<len;i++)
    	{
    		if(rxstate==0)//寻找开头AA
    		{
    			if(data[i]==(byte)0xaa)
    			{
    				rxstate = 1;
    				RX_Data[0] = (byte) 0xaa;
    			}
    		}
    		else if(rxstate==1)//寻找第二个AA
    		{
    			if(data[i]==(byte)0xaa)
    			{
    				rxstate = 2;
    				RX_Data[1] = (byte) 0xaa;
    			}
    			else
    				rxstate = 0;
    		}
    		else if(rxstate==2)//接收功能字
    		{
    			rxstate = 3;
    			RX_Data[2] = data[i];
    		}
    		else if(rxstate==3)//接收len
    		{
    			if(data[i]>45)
    				rxstate = 0;
    			else
    			{
    				rxstate = 4;
    				RX_Data[3] = data[i];
    				rxlen = RX_Data[3];
    				if(rxlen<0)
    					rxlen = -rxlen;
    				rxcnt = 4;
    			}
    		}
    		else if(rxstate==4)
    		{
    			rxlen--;
    			RX_Data[rxcnt] = data[i];
    			rxcnt++;
    			if(rxlen<=0)
    				rxstate = 5;
    		}
    		else if(rxstate==5)//接收sum
    		{
    			RX_Data[rxcnt] = data[i];
    			if(rxcnt<=(COM_BUF_LEN-1))
    				FrameAnl(rxcnt+1);
    			//Toast.makeText(getApplicationContext(), "DataAnl OK", Toast.LENGTH_SHORT).show();
    			rxstate = 0;
    		}
    	}
    }
    static void FrameAnl(int len)
    {
    	byte sum = 0;
    	for(int i=0;i<(len-1);i++)
    		sum += RX_Data[i];
    	if(sum==RX_Data[len-1])
    	{
    		//Toast.makeText(getApplicationContext(), "FrameAnl OK", Toast.LENGTH_SHORT).show();
    		if(RX_Data[2]==1)//status
    		{
    			VAL_ANG_X = ((float)(BytetoUint(4)))/100;
    			VAL_ANG_Y = ((float)(BytetoUint(6)))/100;
    			VAL_ANG_Z = ((float)(BytetoUint(8)))/100;
				VAL_HEIGHT = ((float)(BytetoUint(10)))/100;
    		}
    		if(RX_Data[2]==2)//senser
    		{
    			VAL_ACC_X = BytetoUint(4);
    			VAL_ACC_Y = BytetoUint(6);
    			VAL_ACC_Z = BytetoUint(8);
    			VAL_GYR_X = BytetoUint(10);
    			VAL_GYR_Y = BytetoUint(12);
    			VAL_GYR_Z = BytetoUint(14);
    		}
    		if(RX_Data[2]==5)//votage
    		{
    			VAL_VOTAGE1 = BytetoUint(4);
    		}
			if(RX_Data[2]==6)//pid
			{
				CarControl.VAL_PID_PID1_P = BytetoUint(4);
				CarControl.VAL_PID_PID1_I = BytetoUint(6);
				CarControl.VAL_PID_PID1_D = BytetoUint(8);
				CarControl.VAL_PID_PID2_P = BytetoUint(10);
				CarControl.VAL_PID_PID2_I = BytetoUint(12);
				CarControl.VAL_PID_PID2_D = BytetoUint(14);
				CarControl.VAL_PID_PID3_P = BytetoUint(16);
				CarControl.VAL_PID_PID3_I = BytetoUint(18);
				CarControl.VAL_PID_PID3_D = BytetoUint(20);


			}
    	}
    }
    static short BytetoUint(int cnt)
    {
		short r = 0;  
		r <<= 8;  
		r |= (RX_Data[cnt] & 0x00ff);  
		r <<= 8;  
		r |= (RX_Data[cnt+1] & 0x00ff);  
		return r;	
    }
}
