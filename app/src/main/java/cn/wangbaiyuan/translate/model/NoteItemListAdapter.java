package cn.wangbaiyuan.translate.model;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.wangbaiyuan.translate.R;
import cn.wangbaiyuan.translate.noteDetailActivity;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class NoteItemListAdapter extends RecyclerView.Adapter<NoteItemListAdapter.ViewHolder> {
    public int position;
    public List<SingleNote> mValues;
    private Context context;
    private Fragment fragment;
    private View view;

    public NoteItemListAdapter(Fragment fragment) {
        mValues = new ArrayList<SingleNote>();
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_content, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.mView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        String time = mValues.get(position).publish_time;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);

        } catch (ParseException e) {

        }
        String timeStr = DateUtils.toTodayString(date);

        int[] noteTypeColors = new int[]{
                context.getResources().getColor(R.color.color_type_all),
                context.getResources().getColor(R.color.color_type_spirit),
                context.getResources().getColor(R.color.color_type_note),
                context.getResources().getColor(R.color.color_type_notifition),
                context.getResources().getColor(R.color.color_type_idea),
                context.getResources().getColor(R.color.color_type_collection)
        };
        holder.mListTimeView.setText(timeStr);
//        if(holder.mItem.type==SingleNote.TypeCollectIndex){
//            holder.mListFromView.setText(SingleNote.noteTypes[SingleNote.TypeCollectIndex]);
//            holder.mListFromView.setVisibility(View.VISIBLE);
//        }else{
//            holder.mListFromView.setText("");
//            holder.mListFromView.setVisibility(View.GONE);
//        }

        if(holder.mItem.statusIndex== SingleNote.StatusDraftIndex){
            holder.mListStatusView.setTextColor( context.getResources().getColor(R.color.tx_status_draft));
                holder.mListStatusView.setText("("+ SingleNote.noteStatuss[holder.mItem.statusIndex]+")");
            holder.mListStatusView.setVisibility(View.VISIBLE);
        }else{
            holder.mListStatusView.setText("");
            holder.mListStatusView.setVisibility(View.GONE);
        }

        holder.mSummaryView.setText(holder.mItem.content);


        int type = holder.mItem.type;
        String displayType=(type==SingleNote.TypeallIndex)? "未归类":SingleNote.noteTypes[type];
        holder.mListTypeView.setText(displayType);
        holder.mListTypeView.setBackgroundColor(noteTypeColors[type]);
        //LinearLayout ll_noteList=(LinearLayout)mView.
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                NoteItemListAdapter.this.position = position;
                return false;
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //Toast.makeText(context,position+"",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, noteDetailActivity.class);
                Bundle bundle = new Bundle();
                SingleNote item = mValues.get(position);
                bundle.putLong(SingleNote.ITEM_ID, item.id);
                bundle.putString(SingleNote.ITEM_TIME, item.publish_time);
                bundle.putInt(SingleNote.ITEM_TYPE, item.type);
                bundle.putInt(SingleNote.ITEM_STATUS, item.statusIndex);
                bundle.putString(SingleNote.ITEM_CONTENT, item.content);
                bundle.putString(SingleNote.ITEM_TITLE, item.title);
                intent.putExtras(bundle);

                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void clear() {
        mValues.clear();
    }

    public void addItemLast(SingleNote item) {
        mValues.add(item);
    }

    public void addItemFirst(SingleNote item) {
        mValues.add(0, item);
    }

    public void deleteItem(int position) {
        mValues.remove(position);
        //ITEM_MAP.put(item.id+"", item);
    }
private Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case 100:
            notifyDataSetChanged();
                break;
        }

    }
};

    public void MyNotifyItemInserted(int iposition) {
        notifyItemInserted(iposition);
        Runnable notifyItemInsertedRun=new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(100);
            }
        };
        new Thread(notifyItemInsertedRun).start();
    }
    public void MyNotifyItemRemoved(final int iposition){
        notifyItemRemoved(iposition);
        Runnable notifyItemRemovedRun=new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(100);
            }
        };
        new Thread(notifyItemRemovedRun).start();
    }

    public void MYNotifyItemChanged(int position) {
        notifyItemChanged(position);
        Runnable notifyItemChangedRun=new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(100);
            }
        };
        new Thread(notifyItemChangedRun).start();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mListStatusView;
        public final TextView mListTimeView;
        public final TextView mListFromView;
        public final TextView mSummaryView;
        public final TextView mListTypeView;
        public SingleNote mItem;

        public ViewHolder(View view) {
            super(view);
            //view.setOnCreateContextMenuListener(this);

            mView = view;
            mListTimeView = (TextView) view.findViewById(R.id.tv_noteListTime);
            mListStatusView = (TextView) view.findViewById(R.id.tv_noteList_status);
            mListFromView = (TextView) view.findViewById(R.id.tv_noteList_from);
            mSummaryView = (TextView) view.findViewById(R.id.summary);
            mListTypeView = (TextView) view.findViewById(R.id.tv_noteListType);
        }


        @Override
        public String toString() {
            return super.toString() + " '" + mSummaryView.getText() + "'";
        }


    }
}
