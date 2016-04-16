package cn.wangbaiyuan.translate.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by BrainWang on 2016/1/31.
 */
public class Notify_Item_Textview extends TextView {
    public Notify_Item_Textview(Context context,AttributeSet attr) {
        super(context,attr);
        setGravity(Gravity.CENTER);
        setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        Typeface face=Typeface.createFromAsset(context.getAssets(), "font/fontawesome-webfont.ttf");
        setTypeface(face);
    }
}
