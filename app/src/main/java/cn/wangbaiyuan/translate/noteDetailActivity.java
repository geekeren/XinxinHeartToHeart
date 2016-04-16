package cn.wangbaiyuan.translate;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;

/**
 * An activity representing a single note detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * translate_item details are presented side-by-side with a list of items
 * in a {@link MainActivity}.
 */
public class noteDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            noteDetailFragment fragment = new noteDetailFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.note_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed(){

        super.onBackPressed();
    }
}
