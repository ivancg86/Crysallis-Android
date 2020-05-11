package com.example.chrysallis.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class WrapGrid extends GridView {

    public WrapGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapGrid(Context context) {
        super(context);
    }

    public WrapGrid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
