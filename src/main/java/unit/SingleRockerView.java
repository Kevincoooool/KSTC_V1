
package unit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * 双摇杆view
 * from github sarlmoclen
 */
public class SingleRockerView extends SurfaceView implements Callback, Runnable {

    /**thread*/
    private Thread th;
    /**SurfaceHolder*/
    private SurfaceHolder sfh;
    /**画布*/
    private Canvas canvas;
    /**画笔*/
    private Paint paint;
    /**标记是否在描画*/
    private boolean isDraw = false;
    public static int Value_X = 1500;
    public static int Value_Y = 1500;

    /**摇杆1圆心x*/
    private int CIRCLE_X1 = 100;
    /**摇杆1圆心y*/
    private int CIRCLE_Y1 = 100;
    /**摇杆1圆半径*/
    private int CIRCLE_R1 = 50;
    /**摇杆1小圆半径*/
    private float CIRCLE_SMALL_R1 = 10;
    /**摇杆1背景圆形的X坐标*/
    private int RockerCircleX1 = CIRCLE_X1;
    /**摇杆1背景圆形的Y坐标*/
    private int RockerCircleY1 = CIRCLE_Y1;
    /**摇杆1背景圆形的半径*/
    private int RockerCircleR1 = CIRCLE_R1;
    /**摇杆1的X坐标*/
    private float SmallRockerCircleX1 = CIRCLE_X1;
    /**摇杆1的Y坐标*/
    private float SmallRockerCircleY1 = CIRCLE_Y1;
    /**摇杆1的半径*/
    private float SmallRockerCircleR1 = CIRCLE_SMALL_R1;

    /**摇杆1按下：true 没有按下 false*/
    private boolean flagIn = false;


    public SingleRockerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initNeedClass();
    }

    public SingleRockerView(Context context) {
        super(context);
        initNeedClass();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        int newwidth =  this.getWidth();
        int newheight = this.getHeight();

        setPosition1(
                newwidth/2
                , newheight/2
                , newheight/3
                , newwidth/2
                , newheight/2
                , newheight/15);


        isDraw = true;
        th = new Thread(this);
        th.start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    public void surfaceDestroyed(SurfaceHolder holder) {
        isDraw = false;
    }

    public void run() {
        // TODO Auto-generated method stub
        while (isDraw) {
            draw();
            try {
                Thread.sleep(40);
            } catch (Exception ex) {
            }
        }
    }

    /**
     * 初始化
     * from github sarlmoclen
     */
    public void initNeedClass(){
        this.setKeepScreenOn(true);
        sfh = this.getHolder();
        sfh.addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @SuppressLint("NewApi")
    public void draw() {
        try {
            canvas = sfh.lockCanvas();
            canvas.drawColor(0x00000000, Mode.CLEAR);//Mode.CLEAR这个比较重要，防止绘制有影子，可以去掉看下效果
            paint.setColor(0xaaffffff);
            //绘制摇杆背景
            canvas.drawCircle(RockerCircleX1, RockerCircleY1, RockerCircleR1+20, paint);
            paint.setColor(0x70ffffff);
            //绘制摇杆背景
            canvas.drawCircle(RockerCircleX1, RockerCircleY1, RockerCircleR1, paint);
            paint.setColor(0xffffffff);
            //绘制摇杆
            canvas.drawCircle(SmallRockerCircleX1, SmallRockerCircleY1, SmallRockerCircleR1, paint);

        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            try {
                if (canvas != null)
                    sfh.unlockCanvasAndPost(canvas);
            } catch (Exception e2) {

            }
        }
    }

    /**
     * ACTION_DOWN:第一个按下
     * ACTION_UP：最后一个抬起
     * ACTION_POINTER_1_UP：两个手都按下情况下，这两个手中第一个按下的手抬起时触发
     * ACTION_POINTER_2_UP：两个手都按下情况下，这两个手中第二个按下的手抬起时触发
     * ACTION_POINTER_1_DOWN:ACTION_POINTER_1_UP抬起的那个手又按下时触发
     * ACTION_MOVE：只要有按下就一直触发
     * from github sarlmoclen
     */
    @SuppressLint("NewApi")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int newwidth =  this.getWidth();
        int newheight = this.getHeight();
        if (event.getAction() == MotionEvent.ACTION_DOWN )
        {
            //在这里判断先使用了左边的还是右边的
            if (Math.sqrt(Math.pow((RockerCircleX1 - (int) event.getX()), 2) + Math.pow((RockerCircleY1 - (int) event.getY()), 2)) <= RockerCircleR1) {
                SmallRockerCircleX1 = (int) event.getX();
                SmallRockerCircleY1 = (int) event.getY();
                Value_Y = 1500-(int)((SmallRockerCircleY1-newheight/2)/(newheight/1500.0));
                Value_X = (int)((SmallRockerCircleX1-newwidth/2)/(newheight/1500.0))+1500;
                flagIn = true;
                return true;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            //当释放最后一个摇杆时全部初始化
            SmallRockerCircleX1 = CIRCLE_X1;
            SmallRockerCircleY1 = CIRCLE_Y1;
            Value_Y = 1500-(int)((SmallRockerCircleY1-newheight/2)/(newheight/1500.0));
            Value_X = (int)((SmallRockerCircleX1-newwidth/2)/(newheight/1500.0))+1500;
            flagIn = false;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
                if(flagIn)
                {
                    // 当触屏区域不在活动范围内
                    if (Math.sqrt(Math.pow((RockerCircleX1 - (int) event.getX()), 2) + Math.pow((RockerCircleY1 - (int) event.getY()), 2)) >= RockerCircleR1)
                    {
                        //得到摇杆与触屏点所形成的角度
                        double tempRad = getRad(RockerCircleX1, RockerCircleY1, event.getX(), event.getY());
                        //保证内部小圆运动的长度限制
                        getXY(RockerCircleX1, RockerCircleY1, RockerCircleR1, tempRad);
                    } else {//如果小球中心点小于活动区域则随着用户触屏点移动即可
                        SmallRockerCircleX1 = (int) event.getX();
                        SmallRockerCircleY1 = (int) event.getY();
                        Value_Y = 1500-(int)((SmallRockerCircleY1-newheight/2)/(newheight/1500.0));
                        Value_X = (int)((SmallRockerCircleX1-newwidth/2)/(newheight/1500.0))+1500;
                    }
                }

        }


        return true;
    }

    /**
     * 获取圆周运动的X坐标，获取圆周运动的Y坐标
     * @param R 圆周运动的旋转点
     * @param centerX 旋转点X
     * @param centerY 旋转点Y
     * @param rad 旋转的弧度
     * from github sarlmoclen
     */
    public void getXY(float centerX, float centerY, float R, double rad) {
        //获取圆周运动的X坐标
        SmallRockerCircleX1 = (float) (R * Math.cos(rad)) + centerX;
        //获取圆周运动的Y坐标
        SmallRockerCircleY1 = (float) (R * Math.sin(rad)) + centerY;
    }

    /**
     * 获取圆周运动的X坐标，获取圆周运动的Y坐标
     * @param R 圆周运动的旋转点
     * @param centerX 旋转点X
     * @param centerY 旋转点Y
     * @param rad 旋转的弧度
     * from github sarlmoclen
     */


    /**
     * 得到两点之间的弧度
     * from github sarlmoclen
     */
    public double getRad(float px1, float py1, float px2, float py2) {
        //得到两点X的距离
        float x = px2 - px1;
        //得到两点Y的距离
        float y = py1 - py2;
        //算出斜边长
        float xie = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        //得到这个角度的余弦值（通过三角函数中的定理 ：邻边/斜边=角度余弦值）
        float cosAngle = x / xie;
        //通过反余弦定理获取到其角度的弧度
        float rad = (float) Math.acos(cosAngle);
        //注意：当触屏的位置Y坐标<摇杆的Y坐标我们要取反值-0~-180
        if (py2 < py1) {
            rad = -rad;
        }
        return rad;
    }

    /**
     * 设置摇杆1在view中的位置
     * from github sarlmoclen
     */
    public void setPosition1(int RockerCircleX1,
                             int RockerCircleY1,
                             int RockerCircleR1,
                             float SmallRockerCircleX1,
                             float SmallRockerCircleY1,
                             float SmallRockerCircleR1){
        this.RockerCircleX1 = RockerCircleX1;
        this.RockerCircleY1 = RockerCircleY1;
        this.RockerCircleR1 = RockerCircleR1;
        this.SmallRockerCircleX1 = SmallRockerCircleX1;
        this.SmallRockerCircleY1 = SmallRockerCircleY1;
        this.SmallRockerCircleR1 = SmallRockerCircleR1;
        CIRCLE_X1 = RockerCircleX1;
        CIRCLE_Y1 = RockerCircleY1;
        CIRCLE_R1 = RockerCircleR1;
        CIRCLE_SMALL_R1 = SmallRockerCircleR1;
    }



}