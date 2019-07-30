/*
 * Copyright 2018 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.architectica.socialcomponents.main.main;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.architectica.socialcomponents.R;
import com.architectica.socialcomponents.adapters.PostsAdapter;
import com.architectica.socialcomponents.main.Chat.ChatActivity;
import com.architectica.socialcomponents.main.ChatsList.ChatsListActivity;
import com.architectica.socialcomponents.main.base.BaseActivity;
import com.architectica.socialcomponents.main.followPosts.FollowingPostsActivity;
import com.architectica.socialcomponents.main.main.Home.HomeFragment;
import com.architectica.socialcomponents.main.main.Notifications.NotificationsFragment;
import com.architectica.socialcomponents.main.main.Profile.ProfileFragment;
import com.architectica.socialcomponents.main.post.createPost.CreatePostActivity;
import com.architectica.socialcomponents.main.postDetails.PostDetailsActivity;
import com.architectica.socialcomponents.main.profile.ProfileActivity;
import com.architectica.socialcomponents.main.search.SearchActivity;
import com.architectica.socialcomponents.managers.FollowManager;
import com.architectica.socialcomponents.managers.ProfileManager;
import com.architectica.socialcomponents.model.Post;
import com.architectica.socialcomponents.utils.AnimationUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends BaseActivity<MainView, MainPresenter> implements MainView,GoogleApiClient.OnConnectionFailedListener {

    private static final String SELECTED_ITEM = "arg_selected_item";

    private BottomNavigationView mBottomNav;

    private int mSelectedItem;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Rift");

        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                selectFragment(item);

                return true;

            }

        });

        MenuItem selectedItem;

        if (savedInstanceState != null) {

            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);

            selectedItem = mBottomNav.getMenu().findItem(mSelectedItem);

        } else {

            selectedItem = mBottomNav.getMenu().getItem(0);

        }

        selectFragment(selectedItem);

    }

    private void selectFragment(MenuItem item) {

        Fragment frag = null;

        // init corresponding fragment

        switch (item.getItemId()) {

            case R.id.menu_home:

                frag = HomeFragment.newInstance();

                break;


            case R.id.menu_notifications:

                if (checkAuthorization()) {

                    frag = NotificationsFragment.newInstance();

                }

                break;

            case R.id.menu_profile:

                if (checkAuthorization()) {

                    frag = ProfileFragment.newInstance();

                }

                break;

        }

        // update selected item

        mSelectedItem = item.getItemId();

        mBottomNav.getMenu().findItem(mSelectedItem).setChecked(true);

        if (frag != null) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.replace(R.id.container, frag, frag.getTag());

            ft.commit();

        }

    }

    public void showFloatButtonRelatedSnackBar(int messageId) {
        //showSnackBar(floatingActionButton, messageId);
        Toast.makeText(this, "press back button again to exit", Toast.LENGTH_SHORT).show();
    }

    @Override

    public void onBackPressed() {

        MenuItem homeItem = mBottomNav.getMenu().getItem(0);

        if (mSelectedItem != homeItem.getItemId()) {

            // select home item

            selectFragment(homeItem);

        } else {

            super.onBackPressed();

        }

    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        if (presenter == null) {
            return new MainPresenter(this);
        }
        return presenter;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //LogUtil.logDebug(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "connection failed", Toast.LENGTH_SHORT).show();
    }
}
