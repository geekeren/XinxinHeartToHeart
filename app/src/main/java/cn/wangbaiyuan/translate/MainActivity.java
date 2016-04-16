package cn.wangbaiyuan.translate;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import cn.wangbaiyuan.translate.services.MainService;

public class MainActivity extends AppCompatActivity
        implements  TranslateFragment.OnFragmentInteractionListener,UserFragment.OnFragmentInteractionListener
        ,HeartToHeartFragment.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static  String[] titles = {"昕翻译", "昕记事","昕心相印" ,"个人中心"};
    private Menu menu;
    private static CharSequence[] TabTitles= {"昕翻译", "昕记事","昕相印", "我"};
    private  CharSequence[] TabicoTitles;
    private ActionBar actionBar;
    private noteListFragment notelistfragment;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabicoTitles=new CharSequence[] {getString(R.string.fa_home)
                , getString(R.string.fa_sticky_note)
                ,getString(R.string.fa_heartbeat)
                ,getString(R.string.fa_user)};
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final Spinner spinner=(Spinner)findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                menu.clear();
                spinner.setVisibility(View.GONE);
                actionBar.setTitle(titles[mViewPager.getCurrentItem()]);
                switch (position) {
                    case 0:

                        break;
                    case 1:
                        notelistfragment.refreshNoteList();
                        spinner.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        getMenuInflater().inflate(R.menu.menu_note, menu);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabTextColors(Color.LTGRAY, Color.WHITE);
        for (int i=0;i<tabLayout.getTabCount();i++){
            TabLayout.Tab tab=tabLayout.getTabAt(i);
            if(tab!=null){
                tab.setCustomView(mSectionsPagerAdapter.getTabView(i));
            }
        }



        if (getSupportActionBar() != null) {
            actionBar = getSupportActionBar();
            //ActionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            //ActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.hehua));
            actionBar.setIcon(R.mipmap.ic_launcher);
            actionBar.setTitle(titles[mViewPager.getCurrentItem()]);
        }
        notelistfragment=noteListFragment.newInstance(this,"","");

        Intent intent=new Intent(this, MainService.class);
        intent.setAction(MainService.ACTION);
        startService(intent);
    }


@Override
public void onBackPressed(){
    moveTaskToBack(true);
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu=menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar translate_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position){
                case 0:
                    return TranslateFragment.newInstance("","");
                case 1:

                    return notelistfragment;
                case 2:
                    return HeartToHeartFragment.newInstance("", "");
                case 3:
                    return UserFragment.newInstance("","");
            }
              return TranslateFragment.newInstance("","");
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return TabTitles[position];
        }

        public View getTabView(int position) {
            Typeface face=Typeface.createFromAsset(getAssets(), "font/fontawesome-webfont.ttf");
            View v= LayoutInflater.from(getBaseContext()).inflate(R.layout.main_tab_view, null);
            TextView tab_ico_title=(TextView)v.findViewById(R.id.tab_ico_title);
            TextView tab_text_title=(TextView)v.findViewById(R.id.tab_text_title);
            tab_ico_title.setTypeface(face);
            tab_ico_title.setText(TabicoTitles[position]);
            tab_text_title.setTypeface(face);
            tab_text_title.setText(getPageTitle(position));
            ColorStateList colors=tabLayout.getTabTextColors();
            tab_ico_title.setTextColor(colors);
            tab_text_title.setTextColor(colors);
            return v;
        }
    }
}
