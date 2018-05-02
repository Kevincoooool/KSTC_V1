package unit;

import com.kstc.kstc_v1.ActivityControl;
import com.kstc.kstc_v1.MainActivity;
import com.kstc.kstc_v1.R.color;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class thr_bar extends View {

	private Context mcontext;

	public static int Value = 0;
	public static int Value_height = 0;
	public static int Value_max = 255;
	
	public int top = 10;
	public int bottom = 10;
	public int left = 10;
	public int width = 10;
	public int height = 100;
	public int left_margin = 10;
	public int top_margin = 10;
	
	Paint p_line = new Paint();
	Paint p_background = new Paint();
	Paint p_active = new Paint();
	Paint p_text = new Paint();
	
	String youmen_string = "油门";
	float youmen_text_width = 0;
	
	int color_line = Color.GRAY;
	int color_background = Color.GRAY;
	int color_active = Color.BLACK;
	int color_text = color.springgreen;
	
	static boolean isinrect(float x1, float y1, float top, float height, float left, float width)
	{//x2 y2为中心点
		if(x1>left&&x1<left+width)
			if(y1>top&&y1<top+height)
				return true;
			else
				return false;
		else
			return false;
	}	
	
	public thr_bar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public thr_bar(Context context,AttributeSet set) {
		super(context,set);
		mcontext = context;
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowMgr = (WindowManager)mcontext.getSystemService(Context.WINDOW_SERVICE);
		if(! this.isInEditMode())
		{
			windowMgr.getDefaultDisplay().getMetrics(dm);	//获取屏幕分辨率
		}

		top = dm.heightPixels / 30;
		height = top * 28;
		bottom = top + height;
		left = dm.widthPixels / 50;
		width = left * 6;
		
		p_line.setAntiAlias(true);
		p_line.setColor(color_line);
		
		p_background.setAntiAlias(true);
		p_background.setColor(color_background);
		//p_background.setAlpha(150);
		
		p_active.setAntiAlias(true);
		p_active.setColor(color_active);
		//p_active.setAlpha(150);
		
		p_text.setAntiAlias(true);
		p_text.setColor(color_text);
		//p_text.setAlpha(255);
		p_text.setTextSize(30);
		youmen_text_width = p_text.measureText(youmen_string);
		
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawRect(left, top, left+width, top+height, p_line);
		canvas.drawRect(left, bottom - Value_height, left+width, bottom, p_active);
		canvas.drawText(youmen_string, left+width/2-youmen_text_width/2, (top+bottom)/2, p_text);
	}
	
	int act_num;//有效触点是第几个
	void changevalue()
	{
		if(Value_height>height)Value_height = height;
		if(Value_height<0)Value_height = 0;
		
		Value = Value_height * Value_max / height;
	}
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float zx,zy;
		int znum;
		
		
		znum = event.getActionIndex();
		zx = event.getX(znum);
		zy = event.getY(znum);
		
		switch(event.getActionMasked())
		{
		case MotionEvent.ACTION_DOWN:		//第一个点按下
			if(isinrect(zx,zy,top,height,left,width))
			{
				act_num = znum;
				Value_height = (int) (bottom - zy);
				changevalue();
				invalidate();
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN:	//第二个点按下
			if(isinrect(zx,zy,top,height,left,width))
			{
				act_num = znum;
				Value_height = (int) (bottom - zy);
				changevalue();
				invalidate();
			}
			break;
		case MotionEvent.ACTION_MOVE:		//移动
			if(znum==act_num)
			{
				Value_height = (int) (bottom - zy);
				changevalue();
				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:		//全部手指抬起
			if(ActivityControl.FLAG_DG==0)
				act_num = 255;
			else
			{
				Value_height = (int)(bottom/2.05);
				changevalue();
				invalidate();
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:		//两个手指时，第一个手指抬起
			if(znum==act_num&&ActivityControl.FLAG_DG==0)
			{
				act_num = 255;
			}
			else
			{
				Value_height = (int)(bottom/2.05);
				changevalue();
				invalidate();
			}
			break;
		default:
			break;
		}
		
		
		return true;  
	}
}
