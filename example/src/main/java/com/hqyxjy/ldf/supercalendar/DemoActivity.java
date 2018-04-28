package com.hqyxjy.ldf.supercalendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.fengjr.simpledatepicker.util.DPUtil;
import com.fengjr.simpledatepicker.view.MyCalendarView;
import com.fengjr.simpledatepicker.view.MyMonthView;
import com.fengjr.simpledatepicker.view.SimpleMonthView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zengyong on 2018/3/27
 */

public class DemoActivity extends FragmentActivity {

    private SimpleMonthView simpleMonthView;

    private int i=1;

    private ViewPager viewpager;

    private MyMonthView myMonthView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        simpleMonthView = (SimpleMonthView) findViewById(R.id.simpleMonthView);

        viewpager = (ViewPager)findViewById(R.id.viewpager);

//        simpleMonthView.setDate(2018,3);
//
//        findViewById(R.id.tvTest).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                simpleMonthView.setDate(2018 , i++);
//            }
//        });
//
//
//        simpleMonthView.setOnDatePickedListener(new SimpleMonthView.onDatePickedListener() {
//            @Override
//            public void onDatePicked(String date) {
//                Log.e("DemoActivity" , "date: " + date);
//            }
//        });
//
//
//        myMonthView = (MyMonthView) findViewById(R.id.myMonthView);
//        myMonthView.setDate(2018,3);
//
//        Set<String> paymented = new HashSet<>();
//        paymented.add("2018-3-4");
//        paymented.add("2018-3-15");
//        paymented.add("2018-3-28");
//
//        Set<String> needPayment = new HashSet<>();
//        needPayment.add("2018-3-8");
//        needPayment.add("2018-3-10");
//        needPayment.add("2018-3-19");

       // myMonthView.setPaymentDate(needPayment , paymented);

//        final CalendarViewAdapter adapter = new CalendarViewAdapter();
//        viewpager.setAdapter(adapter);
//        viewpager.setCurrentItem(CalendarViewAdapter.INDEX_POSITION);
//        final int year = 2018;
//        final int month = 3;
//        adapter.setDate(year , month);
//        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
//
//            @Override
//            public void onPageSelected(int position) {
//                int offPosition = position - CalendarViewAdapter.INDEX_POSITION;
//
//                int offMonth = year * 12 + month + offPosition;
//                int newYear = offMonth / 12;
//                int newMonth = offMonth % 12;
//                if(newMonth == 0 && offPosition < 0){
//                    newYear = newYear-1;
//                    newMonth = 12;
//                }else if(newMonth == 0 && offPosition > 0){
//                    newYear = newYear + 1;
//                    newMonth = 1;
//                }
//                Log.e("DemoActivity" , newYear + "   " + newMonth + "   " + position);
//                adapter.setDate(newYear , newMonth);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {}
//        });


        init();


        test();
    }


    private void init(){
        ListView lv = (ListView) findViewById(R.id.lv);

        LinearLayout linearLayout = new LinearLayout(this);
        View headView = View.inflate(this , R.layout.item_header, null);
        MyCalendarView myCalendarView = (MyCalendarView) headView.findViewById(R.id.myCalendarView);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        LinearLayout.LayoutParams calParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
               dm.widthPixels + DPUtil.dip2px(this , 60));

        linearLayout.addView(headView , calParams);
        lv.addHeaderView(linearLayout);


        lv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 40;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = new TextView(parent.getContext());
                tv.setText(String.valueOf(position));
                return tv;
            }
        });
    }



    private void test(){

    }



    private class CalendarViewAdapter extends PagerAdapter{

        public static final int INDEX_POSITION = 1000;


        private ArrayList<SimpleMonthView> calendars = new ArrayList<>();
        private SimpleMonthView currentSimpleMonthView;

        public CalendarViewAdapter(){
            for(int i=0;i<3;i++){
                SimpleMonthView monthView = new SimpleMonthView(DemoActivity.this);
                monthView.setOnDatePickedListener(new SimpleMonthView.onDatePickedListener() {
                    @Override
                    public void onDatePicked(String date) {
                        Log.e("DemoActivity" , "date: " + date);
                    }
                });
                monthView.setDate(2018,3);
                calendars.add(monthView);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SimpleMonthView calendar = calendars.get(position % calendars.size());
            currentSimpleMonthView = calendar;
            ViewGroup parent = (ViewGroup) calendar.getParent();
            if(parent != null){
                parent.removeView(calendar);
            }
            container.addView(calendar);
            return calendar;
        }

        public void setDate(int year , int month){
            if(currentSimpleMonthView != null)
                currentSimpleMonthView.setDate(year , month);
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



}
