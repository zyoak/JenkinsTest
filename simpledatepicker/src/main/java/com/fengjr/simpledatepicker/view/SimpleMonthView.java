package com.fengjr.simpledatepicker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.fengjr.simpledatepicker.bean.DPInfo;
import com.fengjr.simpledatepicker.calendar.DPCManager;
/**
 * 日历单月
 * Created by zengyong on 2018/3/27
 */
public class SimpleMonthView extends View {

    private static final String TAG = "SimpleMonthView";

    private int bg_color = Color.WHITE;
    private int lastPointX, lastPointY;
    private int width;   //控件总宽度
    private int height;  //控件总高度
    private int cellWidth; //日历cell宽度
    private int cellHeight; //日历cell高度

    private int year;
    private int month;

    private DPCManager mCManager = DPCManager.getInstance(); //日期管理器
    private final Region[][] MONTH_REGIONS_6 = new Region[6][7];
    private DPInfo[][] info;
    private Paint mPaint;
    private int colNum = 7;  // 月份的列数
    private int currentRow; // 当前月份总共行数
    private float ratio;  // 宽高比例
    private onDatePickedListener onDatePickedListener;
    private Context context;

    public SimpleMonthView(Context context) {
        this(context , null);
    }

    public SimpleMonthView(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public SimpleMonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    private void init(Context context){
        ratio = 1.2f;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG , "onMeasure "+getMeasuredWidth() + "   " + getMeasuredHeight());
        width = getMeasuredWidth();
        cellWidth = width / 7;
        cellHeight = (int) (cellWidth * ratio);
        height = (int) (cellWidth * currentRow * ratio);
        setMeasuredDimension(width , height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        if(info == null)
            return;

        //region区域
        for (int i = 0; i < currentRow; i++) {
            for (int j = 0; j < colNum; j++) {
                Region region = new Region();
                region.set(j * cellWidth , i * cellHeight,
                        (j + 1) * cellWidth, (i + 1) * cellHeight);
                MONTH_REGIONS_6[i][j] = region;
            }
        }

        //画分割线 竖线
        for(int i=0;i< colNum;i++){
            canvas.drawLine(cellWidth * i , 0 , cellWidth * i , height , mPaint);
        }
        //画分割线 横线
        for(int i=0;i<=currentRow;i++){
            canvas.drawLine(0 , cellHeight * i , width , cellHeight * i , mPaint);
        }
        //画日期
        for(int i=0;i<currentRow;i++){
            for(int j=0;j<colNum;j++){
                Rect rect = MONTH_REGIONS_6[i][j].getBounds();
                mPaint.setTextSize(dip2px(context , 18));
                canvas.drawText(info[i][j].strG, rect.centerX(), rect.centerY(), mPaint);
            }
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setDate(int year , int month){
        this.year = year;
        this.month = month;
        info = mCManager.obtainDPInfo(year, month);
        if (TextUtils.isEmpty(info[4][0].strG)) {
           currentRow = 4;
        } else if (TextUtils.isEmpty(info[5][0].strG)) {
            currentRow = 5;
        } else {
            currentRow = 6;
        }

        invalidate();
        requestLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastPointX = (int) event.getX();
                lastPointY = (int) event.getY();
                Log.e("MonthView" , "action_down: " + lastPointX);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                Log.e("MonthView" , "action_up: ");
                if(Math.abs(event.getX() - lastPointX) < 50 && Math.abs(event.getY() - lastPointY) < 50){
                    defineRegion((int) event.getX(), (int) event.getY());
                }
                break;
        }
        return true;
    }


    private void defineRegion(int x, int y) {
        for (int i = 0; i < MONTH_REGIONS_6.length; i++) {
            for (int j = 0; j < MONTH_REGIONS_6[i].length; j++) {
                Region region = MONTH_REGIONS_6[i][j];
                if (TextUtils.isEmpty(info[i][j].strG)) {
                    continue;
                }
                if (region.contains(x, y)) {
                    String date = year + "-" + month + "-" + info[i][j].strG;
                    invalidate();
                    if (null != onDatePickedListener) {
                        onDatePickedListener.onDatePicked(date);
                    }
                }
            }
        }
    }

    public void setOnDatePickedListener(onDatePickedListener listener){
        this.onDatePickedListener = listener;
    }

    public interface onDatePickedListener{
        void onDatePicked(String date);
    }

}
