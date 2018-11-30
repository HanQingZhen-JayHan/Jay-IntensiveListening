package com.jay.android.pages.select;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadata;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.jay.android.R;
import com.jay.android.base.BaseActivity;
import com.jay.android.utils.MetaRetrieverUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static android.os.Environment.DIRECTORY_ALARMS;
import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.DIRECTORY_MUSIC;
import static android.os.Environment.DIRECTORY_PODCASTS;
import static android.os.Environment.DIRECTORY_RINGTONES;

public class SelectAudioFileActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<File> myDataset;
    private TextView tvCurrentPath;
    private Stack<File> storeFile;
    private List<File> targetFileList = new ArrayList<>();

    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public final int EXTERNAL_REQUEST = 138;
    public static final String AUDIO_LIST = "AUDIO_LIST";


    @Override
    public int getContentViewId() {
        return R.layout.activity_select_audio_file;
    }

    @Override
    public String getPageTitle() {
        return getString(R.string.title_select);
    }

    @Override
    public void initView() {

        tvCurrentPath = findViewById(R.id.tv_path);
        storeFile = new Stack<>();
        initRecyclerView();
        getFab().setImageResource(R.drawable.ic_done);
        getLifecycle().addObserver(MetaRetrieverUtil.getInstance());
    }

    private void initRecyclerView() {

        mRecyclerView = findViewById(R.id.my_recycler_view);

        myDataset = new ArrayList<>();

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset);
        mAdapter.setListener(new MyAdapter.AdapterClickListener() {
            @Override
            public void itemClick(CheckBox cb, File file) {
                if (file.isFile()) {
                    if (cb.isChecked()) {
                        cb.setChecked(false);
                        if (targetFileList.contains(file)) {
                            targetFileList.remove(file);
                        }
                    } else {
                        cb.setChecked(true);
                        targetFileList.add(file);
                    }
                } else {
                    updateList(file, true);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void fabClickCallback(View view) {
        selectDone();
    }

    @Override
    public void onStart() {
        super.onStart();
        initAdapter();
        //updateList(Environment.getExternalStorageDirectory(), true);
    }

    private void initAdapter() {
        if (myDataset == null) {
            myDataset = new ArrayList<>();
        }

        tvCurrentPath.setText(String.format(getString(R.string.text_current_path), Environment.getExternalStorageDirectory().getAbsolutePath()));
        myDataset.clear();
        myDataset.add(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC));
        myDataset.add(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS));
        myDataset.add(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS));
        myDataset.add(Environment.getExternalStoragePublicDirectory(DIRECTORY_PODCASTS));
        myDataset.add(Environment.getExternalStoragePublicDirectory(DIRECTORY_RINGTONES));
        myDataset.add(Environment.getExternalStoragePublicDirectory(DIRECTORY_ALARMS));
        mAdapter.updateData(myDataset);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLifecycle().addObserver(MetaRetrieverUtil.getInstance());
    }

    protected void updateList(File file, boolean isNeedStoreFile) {
        if (requestForPermission()) {
            if (file == null || file.isFile()) {
                return;
            }
            tvCurrentPath.setText(String.format(getString(R.string.text_current_path), file.getAbsoluteFile()));
//            myDataset = Arrays.asList(file.listFiles());
            myDataset = extraValidFile(file.listFiles());
            mAdapter.updateData(myDataset);
        }
        if (isNeedStoreFile) {
            storeFile.push(file);
        }
        targetFileList.clear();
    }

    // extract valid file
    private List<File> extraValidFile(File[] files) {
        if (files == null) {
            return null;
        }
        int i = files.length;
        List<File> fileList = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            File file = files[j];
            if (file.isDirectory() || file.isFile() && MetaRetrieverUtil.getInstance().hasAudio(file.getAbsolutePath())) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    // select done
    private void selectDone() {
        ArrayList<String> list = new ArrayList<>();
        for (File file : targetFileList) {
            Log.i("targetFile", "name:" + file.getAbsolutePath());
            list.add(file.getAbsolutePath());
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra(AUDIO_LIST, list);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public boolean requestForPermission() {

        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            if (!canAccessExternalSd()) {
                isPermissionOn = false;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }

        return isPermissionOn;
    }

    public boolean canAccessExternalSd() {
        return (hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    updateList(this.storeFile.peek(), false);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onBackPressed() {
        if (storeFile.size() < 1) {
            super.onBackPressed();
        } else if (storeFile.size() == 1) {
            storeFile.pop();
            initAdapter();
        } else {
            storeFile.pop();
            updateList(storeFile.peek(), false);
        }
    }
}
