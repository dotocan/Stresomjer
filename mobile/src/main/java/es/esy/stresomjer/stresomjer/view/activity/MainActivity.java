package es.esy.stresomjer.stresomjer.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import es.esy.stresomjer.stresomjer.helper.Constants;
import es.esy.stresomjer.stresomjer.R;
import es.esy.stresomjer.stresomjer.view.fragment.ListContentFragment;
import es.esy.stresomjer.stresomjer.view.fragment.GraphContentFragment;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private TextView tvNavheaderUser;
    private SharedPreferences sharedPreferences;
    private String userFirstName, userLastName, userName;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting SharedPreferences data
        sharedPreferences = getSharedPreferences("Login", 0);
        userFirstName = sharedPreferences.getString(Constants.FIRST_NAME, "");
        userLastName = sharedPreferences.getString(Constants.LAST_NAME, "");
        userName = userFirstName + " " + userLastName;

        // Initializing the views
        initViews();
    }

    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(Constants.IS_LOGGED_IN, false);
        editor.putString(Constants.UNIQUE_ID, "");
        editor.putString(Constants.FIRST_NAME, "");
        editor.putString(Constants.LAST_NAME, "");
        editor.putString(Constants.EMAIL, "");

        editor.apply();

        goToLoginRegistration();
    }

    private void goToLoginRegistration() {
        Intent iToLoginRegistration = new Intent(MainActivity.this, LoginRegistrationActivity.class);
        startActivity(iToLoginRegistration);
    }

    private void initViews() {

        initToolbar();
        initDrawer();
        initWidgets();
    }

    private void initToolbar() {
        // Adding Toolbar to Main screen
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting ViewPager for each Tabs
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initDrawer() {
        // Create Navigation drawer and inlfate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Get the navigation drawer header
        View header = navigationView.getHeaderView(0);

        // Set the text in navigation drawer header
        tvNavheaderUser = (TextView) header.findViewById(R.id.tv_navheader_username);
        tvNavheaderUser.setText(userName);

        // Set behavior of Navigation drawer
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        // This method will trigger on item Click of navigation menu
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            // Set item in checked state
                            menuItem.setChecked(true);

                            // Closing drawer on item click
                            mDrawerLayout.closeDrawers();
                            return true;
                        }
                    });
        }
    }

    private void initWidgets() {
        // Adding Floating Action Button to bottom right of main view
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Starting MeasureActivity
                    Intent iToMeasureActivity = new Intent(MainActivity.this, MeasureActivity.class);
                    startActivity(iToMeasureActivity);
                }
            });
        }
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new ListContentFragment(), getString(R.string.card_content_fragment_title));
        adapter.addFragment(new GraphContentFragment(), getString(R.string.graph_content_fragment_title));
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                return true;

            case R.id.action_logout:
                logoutUser();
                break;

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }
}
