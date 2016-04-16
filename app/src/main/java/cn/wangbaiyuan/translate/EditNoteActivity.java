package cn.wangbaiyuan.translate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import cn.wangbaiyuan.translate.model.DateUtils;
import cn.wangbaiyuan.translate.model.DbAdapter;
import cn.wangbaiyuan.translate.model.SingleNote;

public class EditNoteActivity extends AppCompatActivity {
    private Boolean isNewNote = false;
    public final static int REQUST_NEW = 1;
    public final static int REQUST_MODIFY = 2;

    public final static int RESULT_SAVE_NEW = 1;
    public final static int RESULT_SAVE_MODIFY = 2;

    public final static int RESULT_CANCEL = 2;
    public static int RESPONSE_SAVE_DRAFT;
    private EditText editText_notetitle;
    private EditText editText_notecontent;
    private DbAdapter dbnote;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        isNewNote =isNewNote();

        spinner = (Spinner) findViewById(R.id.spinner2);
        //spinner.se
        spinner.setAdapter(new noteListFragment.MyAdapter(
                this, SingleNote.noteTypes));

        editText_notetitle = (EditText) findViewById(R.id.editText_noteTitletxt);
        editText_notecontent = (EditText) findViewById(R.id.editText_notecontent);
        loadNote();
        dbnote = new DbAdapter(getBaseContext());
        Button btn_newnote_cancel = (Button) findViewById(R.id.btn_newnote_cancel);
        Button btn_newnote_save = (Button) findViewById(R.id.btn_newnote_save);
        btn_newnote_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_newnote_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEditEmpty()){

                    Intent intent = new Intent();
                    if (isNewNote) {//新建
                        SingleNote note = makeNoteItem(SingleNote.StatusDbIndex);
                        note.id = dbnote.createNewNote(note);
                        intent.putExtra("newNote", note.convertToBundle());
                        Toast.makeText(EditNoteActivity.this, getString(R.string.toast_save_success), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_SAVE_NEW, intent);
                    } else {//修改
                        SingleNote note = makeNoteItem(SingleNote.StatusDbIndex);
                        //note.id = dbnote.createNewNote(note);
                        dbnote.modifySingleNote(note);
                        intent.putExtra("modifyNote", note.convertToBundle());
                        setResult(RESULT_SAVE_MODIFY, intent);
                    }


                    finish();
                }else{
                    Toast.makeText(getBaseContext(), getString(R.string.edit_note_empty), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private Boolean isNewNote() {
        //
        return getIntent().getBooleanExtra("isNew", true);
    }
    private void loadNote(){
        if(!isNewNote){
            Bundle bundle=getIntent().getExtras();
            SingleNote note=new SingleNote(bundle);
            String title=note.title;
            getSupportActionBar().setTitle("编辑"+(title.equals("")? "记事":"《"+title+"》"));
            spinner.setSelection(note.type);
            editText_notetitle.setText(title);
            editText_notecontent.setText(note.content);
        }else{
            Intent intent=getIntent();
            if(intent.getAction()==null){
                spinner.setSelection(getIntent().getIntExtra("TypeIndex", SingleNote.TypeallIndex));
            }else if(intent.getAction()==Intent.ACTION_SEND){
                spinner.setSelection(SingleNote.TypeCollectIndex);
                editText_notetitle.setText("系统分享："+ DateUtils.getDateAndMinute());
                editText_notecontent.requestFocus();

                editText_notecontent.setText(getIntent().getClipData().getItemAt(0).getText().toString());
            }

        }



    }
    private boolean isEditEmpty(){
        return editText_notetitle.getText().toString().isEmpty()&& editText_notecontent.getText().toString().isEmpty();
    }

    private SingleNote makeNoteItem(int statusDbIndex) {
        SingleNote note;
        if(!isNewNote){
            Bundle bundle=getIntent().getExtras();
            note = new SingleNote(bundle.getLong(SingleNote.ITEM_ID)
            ,bundle.getString(SingleNote.ITEM_TIME)
            ,statusDbIndex,editText_notetitle.getText().toString()
                    ,spinner.getSelectedItemPosition(), editText_notecontent.getText().toString());
            }else{
            note = new SingleNote(this, spinner.getSelectedItemPosition(), statusDbIndex
                    , editText_notetitle.getText().toString()
                    , editText_notecontent.getText().toString());
        }
         return note;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(!isEditEmpty()&&isNewNote){
            Intent intent = new Intent();
            SingleNote note = makeNoteItem(SingleNote.StatusDraftIndex);
            note.id = dbnote.createNewNote(note);
            note.uploadToCloud();
            intent.putExtra("newNote", note.convertToBundle());
            Toast.makeText(EditNoteActivity.this, getString(R.string.toast_save_as_draft), Toast.LENGTH_SHORT).show();
            setResult(RESULT_SAVE_NEW, intent);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy(){
        dbnote.close();
        super.onDestroy();
    }
}
