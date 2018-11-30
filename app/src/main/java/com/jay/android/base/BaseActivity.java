package com.jay.android.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jay.android.BuildConfig;
import com.jay.android.R;
import com.jay.android.pages.share.ShareActivity;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout contentLayout;
    private FloatingActionButton fab;

    public String TAG = getClass().getSimpleName();
    public boolean debug = BuildConfig.DEBUG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(getPageTitle());
        getSupportActionBar().setTitle("");
        addContentView();

        if (isShowBackArrow()) {
            // add back arrow to toolbar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }
        fab = findViewById(R.id.fab);
        if (isShowFab()) {
            fab.show();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fabClickCallback(view);
                }
            });
        } else {
            fab.hide();
        }

        initView();
    }

    public void addContentView() {
        View v = View.inflate(this, getContentViewId(), null);
        contentLayout = findViewById(R.id.container);
        contentLayout.addView(v, 0);
//        ConstraintSet set = new ConstraintSet();
//        set.clone(contentLayout);
//        set.connect(v.getId(), ConstraintSet.TOP, contentLayout.getId(), ConstraintSet.TOP, 0);
//        set.applyTo(contentLayout);
    }

    abstract public int getContentViewId();

    abstract public String getPageTitle();

    abstract public void initView();

    public void log(String l) {
        if (debug) {
            Log.i(TAG, l);
        }
    }

    public void fabClickCallback(View view) {
        showSnackBar(view, "fab is clicked!");
    }

    public void showSnackBar(View view, String str) {
        if (view == null || TextUtils.isEmpty(str)) {
            return;
        }
        Snackbar.make(view, str, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void showSnackBarWithAction(View view, String str, View.OnClickListener listener) {
        if (view == null || TextUtils.isEmpty(str) || listener == null) {
            return;
        }
        Snackbar.make(view, str, Snackbar.LENGTH_LONG)
                .setAction("Action", listener).show();
    }


    public boolean isShowFab() {
        return true;
    }

    public boolean isShowBackArrow() {
        return true;
    }
    public boolean isShowMenu() {
        return true;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public FloatingActionButton getFab() {
        return fab;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isShowMenu()) {
            getMenuInflater().inflate(R.menu.home_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // handle arrow click here
            case R.id.share_menu:
                share();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void share(){
        String googleUrl = "https://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID;
        String yingYongBaoUrl = "https://sj.qq.com/myapp/detail.htm?apkName="+BuildConfig.APPLICATION_ID;
        String url = googleUrl;
//        log(Locale.getDefault().getDisplayLanguage());//中文
//        log(Locale.getDefault().getCountry());//CN
        if("CN".equals(Locale.getDefault().getCountry())){
            url = yingYongBaoUrl;
        }
        Intent intent = new Intent(getApplicationContext(), ShareActivity.class);
        intent.putExtra(ShareActivity.SHARE_URL,url);
        startActivity(intent);
    }
}