package com.dhananjayanidhi.utils.loader;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dhananjayanidhi.R;


/**
 * Created by prathamesh on 16/01/16.
 */
public class SimpleArcDialog extends Dialog {
    // =============================================================================================
    // CONSTANTS
    // =============================================================================================


    // =============================================================================================
    // FIELDS
    // =============================================================================================

    private ArcConfiguration mConfiguration;
    private LinearLayout mLayout;
    private TextView mLoadingText;
    private boolean showWindow = true;

    // =============================================================================================
    // CONSTRUCTOR
    // =============================================================================================

    public SimpleArcDialog(Context context) {
        super(context);
    }

    public SimpleArcDialog(Context context, ArcConfiguration configuration) {
        super(context);
        mConfiguration = configuration;
    }

    public SimpleArcDialog(Context context, int themeResId,ArcConfiguration configuration) {
        super(context, themeResId);
        mConfiguration = configuration;
    }

    public SimpleArcDialog(Context context, boolean cancelable, OnCancelListener cancelListener,ArcConfiguration configuration) {
        super(context, cancelable, cancelListener);
        mConfiguration = configuration;
    }

    // =============================================================================================
    // METHODS
    // =============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.loader_layout);

        SimpleArcLoader mLoaderView = findViewById(R.id.loader);
        mLoadingText = findViewById(R.id.loadertext);
        mLayout = findViewById(R.id.window);
        mLoadingText.setTextColor(Color.BLACK);

        if (mConfiguration != null) {
            mLoaderView.refreshArcLoaderDrawable(mConfiguration);
            updateLoadingText(mConfiguration);
        }

        if(showWindow)
        {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(ContextCompat.getColor(getContext(),R.color.white));
            gd.setCornerRadius(10);
            gd.setStroke(2, ContextCompat.getColor(getContext(),R.color.white));
            mLayout.setBackground(gd);

            if(mConfiguration != null && mConfiguration.getTextColor() == ContextCompat.getColor(getContext(),R.color.Color1D1B20))
                mLoadingText.setTextColor(ContextCompat.getColor(getContext(),R.color.ColorEBEBEB));
        }
    }

    private void updateLoadingText(ArcConfiguration configuration) {
        String text = configuration.getText();

        if (text.trim().length() == 0) {
            mLoadingText.setVisibility(View.GONE);
        } else {
            mLoadingText.setText(configuration.getText());
        }


        Typeface typeface = configuration.getTypeFace();
        if (typeface != null)
            mLoadingText.setTypeface(typeface);

        int textSize = configuration.getTextSize();

        if (textSize > 0)
            mLoadingText.setTextSize(textSize);


        mLoadingText.setTextColor(mConfiguration.getTextColor());
    }

    public LinearLayout getLayout()
    {
        return mLayout;
    }

    public TextView getLoadingTextView()
    {
        return mLoadingText;
    }

    public void setConfiguration(ArcConfiguration configuration) {
        mConfiguration = configuration;
    }

    public void showWindow(boolean state)
    {
        showWindow = true;
    }
}
