package com.example.stefan.androidwebsockets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Stefan on 23.11.2015.
 */
public class MultiCursorEditText extends EditText {

    public MultiCursorEditText(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void setSelections(int index1, int index2, int index3) {
        super.setSelection(1);
        super.setSelection(2);
        super.setSelection(3);
    }
}
