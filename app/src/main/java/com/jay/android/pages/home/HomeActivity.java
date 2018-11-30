/*
 * Copyright 2017 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jay.android.pages.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.jay.android.R;
import com.jay.android.base.BaseActivity;
import com.jay.android.pages.audio.AudioPlayActivity;
import com.jay.android.pages.select.SelectAudioFileActivity;
import com.jay.android.player.MediaPlayerHolder;
import com.jay.android.player.PlayerAdapter;
import com.jay.android.utils.CollectionUtil;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Allows playback of a single MP3 file via the UI. It contains a {@link MediaPlayerHolder}
 * which implements the {@link PlayerAdapter} interface that the activity uses to control
 * audio playback.
 */
public final class HomeActivity extends BaseActivity {

    public static final String TAG = "MainActivity";
    private static final int ADD_AUDIO_REQUEST_CODE = 100;
    private static final String AUDIO_LIST = "AUDIO_LIST";


    private RecyclerView mRecyclerView;
    private AudioListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Set<String> myDataset;
    private TipDialogFragment dialogFragment;

    @Override
    public int getContentViewId() {
        return R.layout.activity_home;
    }

    @Override
    public void initView() {
        initRecyclerView();
    }

    @Override
    public boolean isShowBackArrow() {
        return false;
    }

    @Override
    public String getPageTitle() {
        return getString(R.string.title_home);
    }

    @Override
    public void fabClickCallback(View view) {
        Intent intent = new Intent(HomeActivity.this, SelectAudioFileActivity.class);
        startActivityForResult(intent, ADD_AUDIO_REQUEST_CODE);
    }

    private void initRecyclerView() {

        mRecyclerView = findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        myDataset = Prefs.getOrderedStringSet(AUDIO_LIST, new LinkedHashSet<String>());
        mAdapter = new AudioListAdapter(CollectionUtil.convertSet2ArrayList(myDataset));
        mAdapter.setListener(new AudioListAdapter.AdapterClickListener() {
            @Override
            public void itemClick(CheckBox cb, String path, String title) {
                Intent intent = new Intent(HomeActivity.this, AudioPlayActivity.class);
                intent.putExtra(AudioPlayActivity.AUDIO_PATH, path);
                intent.putExtra(AudioPlayActivity.AUDIO_TITLE, title);
                startActivity(intent);
            }

            @Override
            public void itemLongClick(String path, String title) {
                deleteAudioFile(path,title);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void deleteAudioFile(final String filePath, String title){
        if(dialogFragment== null){
            dialogFragment = (TipDialogFragment) getSupportFragmentManager().findFragmentByTag(TipDialogFragment.TAG);
            if(dialogFragment == null) {
                dialogFragment = new TipDialogFragment();
            }
        }
        Bundle bundle = new Bundle();
        bundle.putString(TipDialogFragment.DIALOG_TITLE,String.format(getString(R.string.btn_title),title));
        bundle.putString(TipDialogFragment.DIALOG_BTN_CONFIRM,getString(R.string.btn_yes));
        bundle.putString(TipDialogFragment.DIALOG_BTN_CANCEL,getString(R.string.btn_no));
        dialogFragment.setArguments(bundle);
        dialogFragment.setListener(new TipDialogFragment.DialogListener() {
            @Override
            public void confirm() {
                myDataset.remove(filePath);
                Prefs.putOrderedStringSet(AUDIO_LIST, myDataset);
                mAdapter.updateData(CollectionUtil.convertSet2ArrayList(myDataset));
            }

            @Override
            public void cancel() {
                dialogFragment.dismiss();
            }
        });
        dialogFragment.show(getSupportFragmentManager(),TipDialogFragment.TAG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == ADD_AUDIO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> audioList = resultData.getStringArrayListExtra(SelectAudioFileActivity.AUDIO_LIST);
            ArrayList<String> newList = new ArrayList<>();

            for (String path : audioList) {
                if (!myDataset.contains(path)) {
                    newList.add(path);
                    myDataset.add(path);
                }
            }
            mAdapter.addData(newList);
            Prefs.putOrderedStringSet(AUDIO_LIST, myDataset);
        }
    }

}
