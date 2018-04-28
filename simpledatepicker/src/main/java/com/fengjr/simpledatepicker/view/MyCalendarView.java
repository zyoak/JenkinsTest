package com.fengjr.simpledatepicker.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengjr.simpledatepicker.R;
import com.fengjr.simpledatepicker.bean.DPInfo;
import com.fengjr.simpledatepicker.calendar.DPCManager;
import com.fengjr.simpledatepicker.util.DPUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

/**
 * Created by zengyong on 2018/3/29
 */
public class MyCalendarView extends LinearLayout implements View.OnClickListener {

    private static final int INDEX_POSITION = 1000;  //当前设置日期对应viewpager对位置

    private ViewPager viewpager;
    private TextView tv_date;    // 年月指示标题
    private DPCManager dpcManager;   //日期管理器
    private CalendarViewAdapter adapter;
    private int positionYear , positionMonth;  //viewpager指定位置对应对年月，作为标点
    private OnDateChangeAndPickedListener onDateChangeAndPickListener;
    private int calenderHeight;

    public MyCalendarView(@NonNull Context context) {
        this(context , null);
    }

    public MyCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public MyCalendarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        setListener();
    }

    private void init(){
        dpcManager = DPCManager.getInstance();
        View contentView = View.inflate(getContext() , R.layout.layout_calendar_view , null);
        tv_date = (TextView) contentView.findViewById(R.id.tv_date);
        contentView.findViewById(R.id.iv_leftArrow).setOnClickListener(this);
        contentView.findViewById(R.id.iv_rightArrow).setOnClickListener(this);
        contentView.findViewById(R.id.tv_currentDate).setOnClickListener(this);
        viewpager = (ViewPager) contentView.findViewById(R.id.viewpager);
        //获取当天年月
        Calendar calendar = Calendar.getInstance();
        positionYear = calendar.get(Calendar.YEAR);
        positionMonth = calendar.get(Calendar.MONTH) + 1;

        calenderHeight = getCalendarViewHeight(positionYear , positionMonth);

        adapter = new CalendarViewAdapter(positionYear , positionMonth);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(INDEX_POSITION);

        addView(contentView);
    }

    private void setListener(){
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int offPosition = position - INDEX_POSITION;
                int newMonth = (positionMonth + offPosition - 1) % 12 + 1;
                int newYear = positionYear + (positionMonth + offPosition -1) / 12;
                if(newMonth <= 0){
                    newMonth = 12 + newMonth;
                    newYear = newYear -1;
                }
                adapter.setDate(newYear, newMonth);
                if(onDateChangeAndPickListener != null){
                    calenderHeight = getCalendarViewHeight(newYear , newMonth);
                    onDateChangeAndPickListener.onViewHeightChange(calenderHeight);
                    onDateChangeAndPickListener.onDateChanged(newYear , newMonth);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public int getCalendarViewHeight(){
        return calenderHeight;
    }

    /**
     * 根据年月获取控件的高度
     * @param year
     * @param month
     * @return
     */
    private int getCalendarViewHeight(int year , int month){
        DPInfo[][] info = dpcManager.obtainDPInfo(year, month);
        int calendarRow = 0;
        if (TextUtils.isEmpty(info[4][0].strG)) {
            calendarRow = 4;
        } else if (TextUtils.isEmpty(info[5][0].strG)) {
            calendarRow = 5;
        } else {
            calendarRow = 6;
        }
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.widthPixels / MyMonthView.MONTH_COLUMN * calendarRow + DPUtil.dip2px(getContext() , 60);
        return height;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_rightArrow){
            viewpager.setCurrentItem(viewpager.getCurrentItem() + 1);
        }else if(v.getId() == R.id.iv_leftArrow){
            viewpager.setCurrentItem(viewpager.getCurrentItem() - 1);
        }else if(v.getId() == R.id.tv_currentDate){
            viewpager.setCurrentItem(INDEX_POSITION);
            if(onDateChangeAndPickListener != null){

            }
        }
    }


    /**
     * 设置待回款和已回款日期
     * @param needPayment   日期格式 2018-3-29
     * @param paymented
     */
    public void setPaymentDate(@NonNull Set<String> needPayment , @NonNull Set<String> paymented){
        adapter.setPaymentDate(needPayment , paymented);
    }


    private class CalendarViewAdapter extends PagerAdapter {

        private ArrayList<MyMonthView> calendars = new ArrayList<>();
        private MyMonthView currentMonthView;

        /**
         * @param currentMonth  1-12
         */
        public CalendarViewAdapter(int currentYear , int currentMonth){
            for(int i=0;i<5;i++){
                MyMonthView monthView = new MyMonthView(getContext());
                monthView.setDate(currentYear,currentMonth);
                monthView.setOnDatePickedListener(new MyMonthView.OnDatePickedListener() {
                    @Override
                    public void onDatePicked(int year, int month, int day) {
                        if(onDateChangeAndPickListener != null){
                            onDateChangeAndPickListener.onDatePicked(year , month , day);
                        }
                    }
                });
                calendars.add(monthView);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            MyMonthView calendar = calendars.get(position % calendars.size());
            currentMonthView = calendar;
            ViewGroup parent = (ViewGroup) calendar.getParent();
            if(parent != null){
                parent.removeView(calendar);
            }

            container.addView(calendar);
            return calendar;
        }

        /**
         * 要显示的设置年月
         * @param year
         * @param month
         */
        public void setDate(int year , int month){
            if(currentMonthView != null)
                currentMonthView.setDate(year , month);
        }

        /**
         * @param needPayment 待回款，
         * @param paymented  未回款
         */
        public void setPaymentDate(@NonNull Set<String> needPayment , @NonNull Set<String> paymented){
            if(currentMonthView != null)
                currentMonthView.setPaymentDate(needPayment , paymented);
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(container);
        }

    }


    public void setOnDateChangeAndPickedListener(OnDateChangeAndPickedListener listener){
        this.onDateChangeAndPickListener = listener;
    }

    public interface OnDateChangeAndPickedListener{

        void onDateChanged(int year , int month);

        void onDatePicked(int year , int month , int day);

        /**
         * 控件自身高端
         * @param heightPix
         */
        void onViewHeightChange(int heightPix);
    }



}
