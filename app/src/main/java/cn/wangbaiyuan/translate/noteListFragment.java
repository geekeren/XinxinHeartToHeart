package cn.wangbaiyuan.translate;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cn.wangbaiyuan.translate.model.DbAdapter;
import cn.wangbaiyuan.translate.model.SingleNote;
import cn.wangbaiyuan.translate.model.NoteItemListAdapter;
import cn.wangbaiyuan.translate.model.Translate;

/**
 * An activity representing a list of notes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link noteDetailActivity} representing
 * translate_item details. On tablets, the activity presents the list of items and
 * translate_item details side-by-side using two vertical panes.
 */
public class noteListFragment extends Fragment {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    // TODO: Rename and change types of parameters


    private static NoteItemListAdapter adapter = null;
    private Spinner spinner;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static Context context;
    private RecyclerView recyclerView;
    private DbAdapter dbnote;
    private final int MENU_ITEM_SAVE_DRAFT=0101;
    private View rootView;

    // TODO: Rename and change types and number of parameters
    public static noteListFragment newInstance(Context icontext, String param1, String param2) {
        context = icontext;
        noteListFragment fragment = new noteListFragment();
        adapter = new NoteItemListAdapter(fragment);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public noteListFragment() {
        super();
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbnote = new DbAdapter(getContext());
        setHasOptionsMenu(true);
        spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        //spinner.se
        spinner.setAdapter(new MyAdapter(
                getContext(),
                SingleNote.noteTypes));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.clear();
                loadNoteAdapterFromLocalDB();
                adapter.notifyDataSetChanged();
                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        menuInflater.inflate(R.menu.menu_note, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_note) {
            Intent intent = new Intent(getContext(), EditNoteActivity.class);
            intent.putExtra("isNew", true);
            intent.putExtra("TypeIndex", spinner.getSelectedItemPosition());
            startActivityForResult(intent, EditNoteActivity.REQUST_NEW);
        }else if(id==R.id.action_creatShortCut){
            Intent intent=new Intent();
            intent.setClass(context, MainActivity.class);
            Translate.creatShortcut(getContext(),getString(R.string.TAB_NOTE),intent);
        }

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View source, ContextMenu.ContextMenuInfo menuInfo) {

        getActivity().getMenuInflater().inflate(R.menu.menu_list_pop, menu);
        if(adapter.mValues.get(adapter.position).statusIndex== SingleNote.StatusDraftIndex){
            menu.add(0, MENU_ITEM_SAVE_DRAFT, 0, getString(R.string.save));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position=adapter.position;
        switch (item.getItemId()) {
            case R.id.action_notelist_delete:
                dbnote.deleteNote(adapter.mValues.get(adapter.position).id);
                adapter.deleteItem(adapter.position);
                adapter.MyNotifyItemRemoved(adapter.position);
                Toast.makeText(getActivity(), "删除成功！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_notelist_share:

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);

                SingleNote noteitem = adapter.mValues.get(position);
                String shareText = noteitem.content;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                shareIntent.setType("text/plain");
                //设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(shareIntent, "分享记事到"));
                break;
            case MENU_ITEM_SAVE_DRAFT:
                dbnote.markDraftSaved(adapter.mValues.get(position).id);
                Toast.makeText(getActivity(), getString(R.string.toast_save_success), Toast.LENGTH_SHORT).show();
                refreshNoteList();
                break;
            case R.id.action_notelist_menu_copy:
                String copycontent=adapter.mValues.get(position).content;
                ClipboardManager clip=(ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipdata=ClipData.newPlainText("text",copycontent);
                if(clipdata!=null){
                    clip.setPrimaryClip(clipdata);
                    Toast.makeText(getActivity(), "复制成功！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_note_list, container, false);
            swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_notelist_container);
            //设置刷新时动画的颜色，可以设置4个
            swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    refreshNoteList();
                }
            });

            recyclerView = (RecyclerView) rootView.findViewById(R.id.note_list);
            assert recyclerView != null;
            setupRecyclerView(recyclerView);

            //  recyclerView.re
            registerForContextMenu(recyclerView);
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        SingleNote note;
        switch (requestCode) {

            case EditNoteActivity.REQUST_NEW:
                switch (resultCode) {
                    case EditNoteActivity.RESULT_SAVE_NEW:
                        recyclerView.scrollToPosition(0);
                        note = new SingleNote(data.getBundleExtra("newNote"));
                        adapter.addItemFirst(note);
                        adapter.MyNotifyItemInserted(0);
                        break;
                    case EditNoteActivity.RESULT_SAVE_MODIFY:
//                        note = new SingleNote(data.getBundleExtra("modifyNote"));
//                        adapter.addItemFirst(note);
                        adapter.notifyDataSetChanged();
                        break;
                }
                break;
        }
    }

    private void loadNoteAdapterFromLocalDB() {
        int selecttype = (spinner == null ? 0 : spinner.getSelectedItemPosition());

        Cursor cursor = dbnote.getAllNote(selecttype);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String time = cursor.getString(cursor.getColumnIndex("publish_time"));
                    int status = cursor.getInt(cursor.getColumnIndex("status"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    int type = cursor.getInt(cursor.getColumnIndex("type"));
                    String content = cursor.getString(cursor.getColumnIndex("content"));
                    adapter.addItemLast(new SingleNote(id, time, status, title, type, content));
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    public void refreshNoteList() {

        adapter.clear();
        loadNoteAdapterFromLocalDB();
        loadNoteAdapterFromCloud();
        adapter.notifyDataSetChanged();
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }

    }

    private void loadNoteAdapterFromCloud() {

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(adapter);
        refreshNoteList();
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }


    //自定义下拉菜单
    public static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(R.layout.notelist_spinner, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));
            return view;
        }

        @Override
        public Resources.Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Resources.Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }

    @Override
    public void onResume(){
        refreshNoteList();
        super.onResume();
    }

    @Override
    public void onDestroy(){
        dbnote.close();
        super.onDestroy();
    }
}
