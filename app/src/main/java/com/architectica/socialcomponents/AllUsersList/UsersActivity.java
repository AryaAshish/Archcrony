package com.architectica.socialcomponents.AllUsersList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.architectica.socialcomponents.R;
import com.architectica.socialcomponents.adapters.ChatsListAdapter;
import com.architectica.socialcomponents.main.Chat.ChatActivity;
import com.architectica.socialcomponents.main.ChatsList.ChatsListActivity;
import com.architectica.socialcomponents.model.Profile;
import com.architectica.socialcomponents.views.CircularImageView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private ChatsListAdapter adapter;
    private RecyclerView recyclerView;

    ProgressDialog pd;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        pd = new ProgressDialog(UsersActivity.this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseDatabase.getInstance().getReference("profiles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Profile> profiles = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                    Profile profile = dataSnapshot1.getValue(Profile.class);
                    profile.setId(dataSnapshot1.getKey());

                    profiles.add(profile);

                }

                initChatsList(profiles);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initChatsList(List<Profile> profiles){

        recyclerView = findViewById(R.id.users_list);
        adapter = new ChatsListAdapter(this,profiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(adapter);

        pd.dismiss();

    }

}
