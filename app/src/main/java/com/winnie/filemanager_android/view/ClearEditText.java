package com.winnie.filemanager_android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;


import androidx.appcompat.widget.AppCompatEditText;

import com.winnie.filemanager_android.R;

/**
 *
 * @author winnie
 * @date 2018/11/22
 * 右边带删除图标的编辑框
 */
public class ClearEditText extends AppCompatEditText {

    /**
     * 编辑框宽度， 高度
     */
    private int mContentWidth;
    private int mContentHeight;

    /**
     * 编辑框右边距
     */
    private int mOldPaddingRight;
    private int mNewPaddingRight;

    /**
     * 是否绘制图标
     */
    private boolean mClearBitmapFlag;

    /**
     * 图标left，top位置
     */
    private float mClearIconLeft;
    private float mClearIconTop;

    /**
     * 图标宽度，高度
     */
    private int mClearIconWidth;
    private int mClearIconHeight;

    /**
     * 绘制的图标
     */
    private Drawable mClearDrawable;

    /**
     * 画笔
     */
    private Paint mClearIconPaint;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initView();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mClearIconWidth = dp2px(20);
        mClearIconHeight = dp2px(20);
        if(mClearDrawable == null){
            mClearDrawable = getResources().getDrawable(R.drawable.ic_input_clear);
        }
    }

    public int dp2px(float dipValue) {
        Resources res = Resources.getSystem();
        return (int) (dipValue * res.getDisplayMetrics().density);
    }

    private void initView(){
        mClearIconPaint = new Paint();
        mClearIconPaint.setAntiAlias(true);
        initDrawClearBitmap();
        mOldPaddingRight = getPaddingRight();
        mNewPaddingRight = mOldPaddingRight + mClearIconWidth + 15;
        //右边距包含删除图标
        setPadding(getPaddingLeft(), getPaddingTop(), mNewPaddingRight, getPaddingBottom());
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)){
                    mClearBitmapFlag = hasFocus();
                    invalidate();
                }else {
                    mClearBitmapFlag = false;
                    invalidate();
                }
            }
        });

        setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                mClearBitmapFlag = !TextUtils.isEmpty(getText());
            }else {
                mClearBitmapFlag = false;
            }
            invalidate();
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mContentWidth = w;
        mContentHeight = h;
        initDrawClearBitmap();
    }

    private void initDrawClearBitmap(){
        //保留右边距
        mClearIconLeft = mContentWidth - mClearIconWidth - mOldPaddingRight;
        //上下居中
        mClearIconTop = (mContentHeight - mClearIconHeight) / 2;

        mClearDrawable.setBounds((int) mClearIconLeft, (int) mClearIconTop,
                (int)(mClearIconLeft + mClearIconWidth), (int)(mClearIconTop + mClearIconHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        if(mClearBitmapFlag) {
            //防止因为输入过多导致控件被scroll
            canvas.translate(getScrollX(), getScrollY());
            mClearDrawable.draw(canvas);
        }
        canvas.restore();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(isClearClick(event.getX(), event.getY())) {
                    //点击在清除的位置，清除文案内容
                    if (mClearBitmapFlag) {
                        mClearBitmapFlag = false;
                        setText("");
                        if(onDelClickListener != null){
                            onDelClickListener.onDelClick();
                        }
                        return true;
                    }
                }
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 点击点在清除图标范围内
     */
    private boolean isClearClick(float x, float y){
        //扩大删除图标点击范围
        if(x > mClearIconLeft && x< mContentWidth && y >0 &&y< mContentHeight){
            return true;
        }
        return false;
    }


    private OnDelClickListener onDelClickListener;

    public void setOnDelClickListener(OnDelClickListener onDelClickListener) {
        this.onDelClickListener = onDelClickListener;
    }

    public interface OnDelClickListener{
        void onDelClick();
    }
}
