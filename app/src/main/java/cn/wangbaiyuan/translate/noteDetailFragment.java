package cn.wangbaiyuan.translate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.wangbaiyuan.translate.model.SingleNote;

/**
 * A fragment representing a single note detail screen.
 * This fragment is either contained in a {@link }
 * in two-pane mode (on tablets) or a {@link noteDetailActivity}
 * on handsets.
 */
public class noteDetailFragment extends Fragment {


    private TextView txcontent;
    private SingleNote note;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public noteDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.note_detail, container, false);
        Bundle bundle=getActivity().getIntent().getExtras();
        txcontent=(TextView)rootView;
        note=new SingleNote(bundle);
        loadNote(note);
        setHasOptionsMenu(true);
        return rootView;
    }

    private void loadNote(SingleNote note){

        String content=note.content;
        String title=note.title;
        if (!content.equals("")||!title.equals("")) {
            AppCompatActivity activity=(AppCompatActivity)getActivity();
            activity.getSupportActionBar().setTitle(title);
            txcontent.setText(content);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        menuInflater.inflate(R.menu.menu_note_detail, menu);

    }
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View source, ContextMenu.ContextMenuInfo menuInfo) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getActivity().getMenuInflater().inflate(R.menu.menu_note_detail, menu);
//
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_note_edit:

                Intent intent = new Intent(getContext(), EditNoteActivity.class);
                intent.putExtra("isNew", false);
                intent.putExtras(note.convertToBundle());
                startActivityForResult(intent, EditNoteActivity.REQUST_MODIFY);
                break;
            case R.id.action_note_share:

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String shareText = note.content;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                shareIntent.setType("text/plain");
                //设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(shareIntent, "分享记事到"));
                break;
        }
        return true;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EditNoteActivity.REQUST_MODIFY:
                switch (resultCode) {
                    case EditNoteActivity.RESULT_SAVE_MODIFY:
                        SingleNote inote = new SingleNote(data.getBundleExtra("modifyNote"));
                        loadNote(inote);
                        break;
                }
                break;
        }
    }

    public void onBackPressed() {

        getActivity().onBackPressed();
    }
}
