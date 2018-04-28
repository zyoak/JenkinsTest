package com.fengjr.simpledatepicker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.fengjr.simpledatepicker.R;
import com.fengjr.simpledatepicker.bean.DPInfo;
import com.fengjr.simpledatepicker.calendar.DPCManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 日历
 * Created by zengyong on 2018/1/17.
 */
public class MonthView extends View{

    private final Region[][] MONTH_REGIONS_4 = new Region[4][7];    //日期对应的region区域
    private final Region[][] MONTH_REGIONS_5 = new Region[5][7];
    private final Region[][] MONTH_REGIONS_6 = new Region[6][7];

    private final DPInfo[][] INFO_4 = new DPInfo[4][7];    // 日期信息
    private final DPInfo[][] INFO_5 = new DPInfo[5][7];
    private final DPInfo[][] INFO_6 = new DPInfo[6][7];

    private Paint mPaint;
    private Scroller mScroller;
    private int width , height;
    private int cellWidth;
    private int criticalWidth;
    private int lastMoveX;
    private int lastPointX, lastPointY;
    private float sizeTextGregorian;

    private int indexYear , indexMonth;
    private int centerYear, centerMonth;
    private int originYear , originMonth;
    private int leftYear, leftMonth;
    private int rightYear, rightMonth;
    private int bgColor = 0xffffffff;  //背景

    private DPCManager mCManager = DPCManager.getInstance();
    private final Map<String, List<Region>> REGION_SELECTED = new HashMap<>();
    private OnDateChangeListener onDateChangeListener;
    private OnDatePickedListener onDatePickedListener;
    private List<String> selectedDate = new ArrayList<>();

    public MonthView(Context context) {
        this(context , null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mScroller = new Scroller(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG |
                Paint.LINEAR_TEXT_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        criticalWidth = (int) (1F / 5F * width);
        cellWidth = (int) (width / 7F);
        int cellH4 = (int) (h / 4F);
        int cellH5 = (int) (h / 5F);
        int cellH6 = (int) (h / 6F);

        sizeTextGregorian = width / 20F;

        for (int i = 0; i < MONTH_REGIONS_4.length; i++) {
            for (int j = 0; j < MONTH_REGIONS_4[i].length; j++) {
                Region region = new Region();
                region.set((j * cellWidth), (i * cellH4), cellWidth + (j * cellWidth),
                        cellWidth + (i * cellH4));
                MONTH_REGIONS_4[i][j] = region;
            }
        }
        for (int i = 0; i < MONTH_REGIONS_5.length; i++) {
            for (int j = 0; j < MONTH_REGIONS_5[i].length; j++) {
                Region region = new Region();
                region.set((j * cellWidth), (i * cellH5), cellWidth + (j * cellWidth),
                        cellWidth + (i * cellH5));
                MONTH_REGIONS_5[i][j] = region;
            }
        }
        for (int i = 0; i < MONTH_REGIONS_6.length; i++) {
            for (int j = 0; j < MONTH_REGIONS_6[i].length; j++) {
                Region region = new Region();
                region.set((j * cellWidth), (i * cellH6), cellWidth + (j * cellWidth),
                        cellWidth + (i * cellH6));
                MONTH_REGIONS_6[i][j] = region;
            }
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(bgColor);

        draw(canvas, width * (indexMonth - 1), 0 , leftYear, leftMonth);
        draw(canvas, width * indexMonth, 0 , centerYear, centerMonth);
        draw(canvas, width * (indexMonth + 1), 0 , rightYear, rightMonth);

       // Log.e("MonthView" , "OnDraw......");
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX() , mScroller.getCurrY());
            invalidate();
        }else{
//            requestLayout();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);
                lastPointX = (int) event.getX();
                lastPointY = (int) event.getY();
                Log.e("MonthView" , "action_down: " + lastPointX);
                break;
            case MotionEvent.ACTION_MOVE:
                    int totalMoveX = (int) (lastPointX - event.getX()) + lastMoveX;
                    smoothScrollTo(totalMoveX);
                    Log.e("MonthView" , "action_move: " + totalMoveX);
                break;
            case MotionEvent.ACTION_UP:
                Log.e("MonthView" , "action_up: ");
                if (lastPointX > event.getX() && Math.abs(lastPointX - event.getX()) >= criticalWidth) {
                    indexMonth++;
                    centerMonth = (centerMonth + 1) % 13;
                    if (centerMonth == 0) {
                        centerMonth = 1;
                        centerYear++;
                    }
                } else if (lastPointX < event.getX() && Math.abs(lastPointX - event.getX()) >= criticalWidth) {
                    indexMonth--;
                    centerMonth = (centerMonth - 1) % 12;
                    if (centerMonth == 0) {
                        centerMonth = 12;
                        centerYear--;
                    }
                }
                buildRegion();
                computeDate();
                mScroller.forceFinished(true);
                smoothScrollTo(width * indexMonth);
                lastMoveX = width * indexMonth;
                if(Math.abs(event.getX() - lastPointX) < 50 && Math.abs(event.getY() - lastPointY) < 50){
                    defineRegion((int) event.getX(), (int) event.getY());
                }
                break;
        }
        return true;
    }

   public void smoothScrollToPreMonth(){
       indexMonth--;
       centerMonth = (centerMonth - 1) % 12;
       if (centerMonth == 0) {
           centerMonth = 12;
           centerYear--;
       }
       buildRegion();
       computeDate();
       mScroller.forceFinished(true);
       smoothScrollTo(width * indexMonth);
       lastMoveX = width * indexMonth;
   }

    public void smoothScrollToNextMonth(){
        indexMonth++;
        centerMonth = (centerMonth + 1) % 13;
        if (centerMonth == 0) {
            centerMonth = 1;
            centerYear++;
        }
        buildRegion();
        computeDate();
        mScroller.forceFinished(true);
        smoothScrollTo(width * indexMonth);
        lastMoveX = width * indexMonth;
   }

    public void showOriginYear(){
        if(centerYear == originYear && centerMonth == originMonth){
            return;
        }
        indexMonth = (originYear * 12 + originMonth) - (centerYear * 12 + centerMonth);
        centerYear = originYear;
        centerMonth = originMonth;
        buildRegion();
        computeDate();
        mScroller.forceFinished(true);
        smoothScrollTo(width * indexMonth);
        lastMoveX = width * indexMonth;
    }

    private void smoothScrollTo(int fx) {
        int dx = fx - mScroller.getFinalX();
        smoothScrollBy(dx);
    }

    private void smoothScrollBy(int dx) {
        mScroller.startScroll(mScroller.getFinalX(), 0 , dx, 0 , 500);
        invalidate();
    }

    private void defineRegion(int x, int y) {
        DPInfo[][] info = mCManager.obtainDPInfo(centerYear, centerMonth);
        Region[][] tmp;
        if (TextUtils.isEmpty(info[4][0].strG)) {
            tmp = MONTH_REGIONS_4;
        } else if (TextUtils.isEmpty(info[5][0].strG)) {
            tmp = MONTH_REGIONS_5;
        } else {
            tmp = MONTH_REGIONS_6;
        }
        for (int i = 0; i < tmp.length; i++) {
            for (int j = 0; j < tmp[i].length; j++) {
                Region region = tmp[i][j];
                if (TextUtils.isEmpty(mCManager.obtainDPInfo(centerYear, centerMonth)[i][j].strG)) {
                    continue;
                }
                if (region.contains(x, y)) {
                    List<Region> regions = REGION_SELECTED.get(indexYear + ":" + indexMonth);
                    regions.add(region);
                    String date = centerYear + "-" + centerMonth + "-" +
                            mCManager.obtainDPInfo(centerYear, centerMonth)[i][j].strG;
                    invalidate();
                    if (null != onDatePickedListener) {
                        onDatePickedListener.onDatePicked(date);
                    }
                }
            }
        }
    }


    private void draw(Canvas canvas, int x, int y, int year, int month) {
        canvas.save();
        canvas.translate(x, y);
        DPInfo[][] info = mCManager.obtainDPInfo(year, month);
        DPInfo[][] result;
        Region[][] tmp;
        int row = 4;
        if (TextUtils.isEmpty(info[4][0].strG)) {
            tmp = MONTH_REGIONS_4;
            arrayClear(INFO_4);
            result = arrayCopy(info, INFO_4);
            row = 4;
        } else if (TextUtils.isEmpty(info[5][0].strG)) {
            tmp = MONTH_REGIONS_5;
            arrayClear(INFO_5);
            result = arrayCopy(info, INFO_5);
            row = 5;
        } else {
            tmp = MONTH_REGIONS_6;
            arrayClear(INFO_6);
            result = arrayCopy(info, INFO_6);
            row = 6;
        }
        int cellHeight = height / row;
        //画分割线 竖线
        for(int i=0;i<7;i++){
            canvas.drawLine(cellWidth * i , 0 , cellWidth * i , height , mPaint);
        }
        //画分割线 横线
        for(int i=0;i<=row;i++){
            canvas.drawLine(0 , cellHeight * i , width , cellHeight * i , mPaint);
        }
        //画日期
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                Rect rect = tmp[i][j].getBounds();
                draw(canvas, rect, info[i][j]);
                drawCircle(canvas , rect , info[i][j]);
            }
        }
        canvas.restore();
    }

    private void drawCircle(Canvas canvas , Rect rect , DPInfo info){
        String date = centerYear + "-" + centerMonth + "-" + info.strG;
        if(selectedDate.contains(date)){
            canvas.drawCircle(rect.centerX() , rect.centerY() + (rect.bottom - rect.top)/6 , (rect.bottom - rect.top)/12 ,mPaint);
        }
    }

    private void draw(Canvas canvas, Rect rect, DPInfo info) {
        drawGregorian(canvas, rect, info.isToday ? getResources().getString(R.string.today) : info.strG);
    }

    private void drawGregorian(Canvas canvas, Rect rect, String str) {
        mPaint.setTextSize(sizeTextGregorian);
        canvas.drawText(str, rect.centerX(), rect.centerY(), mPaint);
    }


    private void arrayClear(DPInfo[][] info) {
        for (DPInfo[] anInfo : info) {
            Arrays.fill(anInfo, null);
        }
    }

    private DPInfo[][] arrayCopy(DPInfo[][] src, DPInfo[][] dst) {
        for (int i = 0; i < dst.length; i++) {
            System.arraycopy(src[i], 0, dst[i], 0, dst[i].length);
        }
        return dst;
    }

    private void buildRegion() {
        String key = indexYear + ":" + indexMonth;
        if (!REGION_SELECTED.containsKey(key)) {
            REGION_SELECTED.put(key, new ArrayList<Region>());
        }
    }

    private void computeDate() {
        rightYear = leftYear = centerYear;
        rightMonth = centerMonth + 1;
        leftMonth = centerMonth - 1;
        if (centerMonth == 12) {
            rightYear++;
            rightMonth = 1;
        }
        if (centerMonth == 1) {
            leftYear--;
            leftMonth = 12;
        }
        if (null != onDateChangeListener) {
            onDateChangeListener.onDateChange(centerYear , centerMonth);
        }
    }

    public void setDate(int year, int month) {
        centerYear = year;
        centerMonth = month;
        originYear = year;
        originMonth = month;
        indexYear = 0;
        indexMonth = 0;
        buildRegion();
        computeDate();
        requestLayout();
        invalidate();
    }

    public void setSelectedDate(List<String> selectedDate){
        this.selectedDate = selectedDate;
        invalidate();
    }

    public void setOnDateChangeListener(OnDateChangeListener listener){
        this.onDateChangeListener = listener;
    }

    public void setOnDatePickedListener(OnDatePickedListener listener){
        this.onDatePickedListener = listener;
    }

    public interface OnDateChangeListener {
        void onDateChange(int year , int month);
    }

    public interface OnDatePickedListener {
        void onDatePicked(String date);
    }


}
