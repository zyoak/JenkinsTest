package com.hqyxjy.ldf.supercalendar;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.fengjr.simpledatepicker.view.MyMonthView;
import com.fengjr.simpledatepicker.view.SimpleMonthView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zengyong on 2018/3/29
 */

public class TestActivity extends Activity {

    private ViewPager viewpager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        viewpager = (ViewPager)findViewById(R.id.viewpager);
        final CalendarViewAdapter adapter = new CalendarViewAdapter();
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(CalendarViewAdapter.INDEX_POSITION);
        final int year = 2018;
        final int month = 3;
        adapter.setDate(year , month);
        Log.e("TestActivity" , "startPosition: " + viewpager.getCurrentItem());
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                int offPosition = position - CalendarViewAdapter.INDEX_POSITION;
                int newMonth = (month + offPosition - 1)%12 + 1;
                int newYear = year + (month + offPosition -1)/12;
                if(newMonth <= 0){
                    newMonth = 12 + newMonth;
                    newYear = newYear -1;
                }
                Log.e("DemoActivity" , newYear + "   " + newMonth + "   " + position);
                adapter.setDate(newYear , newMonth);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });



        MyMonthView myMonthView = (MyMonthView) findViewById(R.id.myMonthView);
        myMonthView.setDate(2018,3);

        Set<String> paymented = new HashSet<>();
        paymented.add("2018-3-4");
        paymented.add("2018-3-15");
        paymented.add("2018-3-28");

        Set<String> needPayment = new HashSet<>();
        needPayment.add("2018-3-8");
        needPayment.add("2018-3-10");
        needPayment.add("2018-3-19");

        myMonthView.setPaymentDate(needPayment , paymented);
    }


    private class CalendarViewAdapter extends PagerAdapter {

        public static final int INDEX_POSITION = 1000;


        private ArrayList<SimpleMonthView> calendars = new ArrayList<>();
        private SimpleMonthView currentSimpleMonthView;

        public CalendarViewAdapter(){
            for(int i=0;i<3;i++){
                SimpleMonthView monthView = new SimpleMonthView(TestActivity.this);
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
