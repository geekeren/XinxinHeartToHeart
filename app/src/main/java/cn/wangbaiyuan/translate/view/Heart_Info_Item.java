package cn.wangbaiyuan.translate.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by BrainWang on 2016/1/31.
 */
public class Heart_Info_Item extends TextView {
    public Heart_Info_Item(Context context,String text) {
        super(context);
        setGravity(Gravity.CENTER);
        setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        setText(text);

    }
}
