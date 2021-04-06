package com.winnie.filemanager_android.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.winnie.filemanager_android.R;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : winnie
 * @date : 2019/1/10
 * @desc
 */
public class DatePickerDialog extends Dialog {
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.calendar_view)
    CalendarView calendarView;

    private SelectListener mSelectListener;
    private Long mCurrentTime;

    public DatePickerDialog(@NonNull Context context, Long currentTime) {
        super(context);
        mCurrentTime = currentTime;
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_date_picker);
        ButterKnife.bind(this);
        initDate();
    }

    private void initDate() {
        calendarView.setDate(mCurrentTime);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mCurrentTime = calendar.getTimeInMillis();
            if(mSelectListener!= null){
                mSelectListener.onSelectTime(mCurrentTime);
            }
            dismiss();
        });
    }


    public void setCurrentTime(long currentTime) {
        mCurrentTime = currentTime;
        initDate();
    }

    public Long getCurrentTime() {
        return mCurrentTime;
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setSelectListener(SelectListener listener) {
        mSelectListener = listener;
    }

    public interface SelectListener {
        void onSelectTime(long time);
    }
}
