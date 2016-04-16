package cn.wangbaiyuan.translate.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.wangbaiyuan.translate.R;

/**
 * Created by 王柏元 on 2015-08-21.
 */
public class HeartTableRowView {
    private View TableRowView;
    private TextView title;
    private TextView content;

    public HeartTableRowView(final Context context, String titleStr, String contentStr) {
        TableRowView = LayoutInflater.from(context).inflate(
                R.layout.heart_tablerow, null);

        title = (TextView) TableRowView.findViewById(R.id.rowtitle);
        title.setText(titleStr);
        content = (TextView) TableRowView.findViewById(R.id.rowcontent);
        content.setText(contentStr);

    }



    public View getView() {
        return TableRowView;
    }


}
