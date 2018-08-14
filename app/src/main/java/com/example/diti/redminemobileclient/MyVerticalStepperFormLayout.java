package com.example.diti.redminemobileclient;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;

public class MyVerticalStepperFormLayout extends VerticalStepperFormLayout {
    private Context mContext;

    public MyVerticalStepperFormLayout(Context context) {
        super(context);
        mContext = context;
    }

    public MyVerticalStepperFormLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public MyVerticalStepperFormLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }


    @Override
    protected void setUpStepLayoutAsConfirmationStepLayout(LinearLayout stepLayout) {
        super.setUpStepLayoutAsConfirmationStepLayout(stepLayout);
        RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
        View v = verticalStepperFormImplementation.createStepContentView(100);
        stepContent.addView(v);
    }
}
