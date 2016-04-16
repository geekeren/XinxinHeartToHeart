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
public class translateItemView {
    private View convertView;
    private TextView title;
    private TextView content;

    public translateItemView(final Context context, String titleStr, String contentStr){
        convertView = LayoutInflater.from(context).inflate(
                R.layout.translate_item, null);

        title = (TextView) convertView.findViewById(R.id.title);
        title.setText(titleStr);
        title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

        //title.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG |Paint.ANTI_ALIAS_FLAG);
        content = (TextView) convertView.findViewById(R.id.summary);
        content.setText(contentStr);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,content.getText(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public TextView getTitleView(){
        return title;
    }
public View getView(){
    return convertView;
}


}
