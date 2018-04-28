package com.fengjr.simpledatepicker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fengjr.simpledatepicker.R;

import java.util.List;
/**
 * Created by zengyong on 2018/1/18.
 */
public class CalendarView extends LinearLayout implements View.OnClickListener {

    private MonthView monthView;
    private TextView tv_date;
    private ImageView iv_leftArrow , iv_rightArrow;

    private OnDateChangeListener onDateChangeListener;
    private OnDatePickedListener onDatePickedListener;

    public CalendarView(Context context) {
        this(context , null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setListener();
    }

    private void init(Context context){
        // 设置排列方向为竖向
        setOrientation(VERTICAL);

        View contentView = View.inflate(context , R.layout.layout_calendar_title, null);
        this.monthView = (MonthView) contentView.findViewById(R.id.monthView);
        this.tv_date = (TextView) contentView.findViewById(R.id.tv_date);
        this.iv_leftArrow = (ImageView) contentView.findViewById(R.id.iv_leftArrow);
        this.iv_rightArrow = (ImageView) contentView.findViewById(R.id.iv_rightArrow);

        contentView.findViewById(R.id.tv_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                monthView.showOriginYear();
            }
        });
        this.addView(contentView);
    }

    private void setListener(){
        this.iv_leftArrow.setOnClickListener(this);
        this.iv_rightArrow.setOnClickListener(this);
        this.monthView.setOnDateChangeListener(new MonthView.OnDateChangeListener() {
            @Override
            public void onDateChange(int year, int month) {
                showTitleDate(year , month);
                if(onDateChangeListener != null){
                    onDateChangeListener.onDateChange(year , month);
                }
            }
        });
        this.monthView.setOnDatePickedListener(new MonthView.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                if(onDatePickedListener != null){
                    onDatePickedListener.onDatePicked(date);
                }
            }
        });
    }

    public void setDate(int year , int month){
        monthView.setDate(year , month);
        showTitleDate(year , month);
    }


    private void showTitleDate(int year , int month){
        this.tv_date.setText(year + "年" + month + "月");
    }

    public void setSelectedDate(List<String> date){
        this.monthView.setSelectedDate(date);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_leftArrow){
            setPreMonth();
        }else if(v.getId() == R.id.iv_rightArrow){
            setNextMonth();
        }
    }


    private void setPreMonth(){
        this.monthView.smoothScrollToPreMonth();
    }

    private void setNextMonth(){
        this.monthView.smoothScrollToNextMonth();
    }

    public void setOnDateChangeListener(OnDateChangeListener listener){
        this.onDateChangeListener = listener;
    }

    public void setOnDatePickedListener(OnDatePickedListener listener){
        this.onDatePickedListener = listener;
    }

    public interface OnDateChangeListener{
        void onDateChange(int year , int month);
    }

    public interface OnDatePickedListener{
        void onDatePicked(String date);
    }

}
