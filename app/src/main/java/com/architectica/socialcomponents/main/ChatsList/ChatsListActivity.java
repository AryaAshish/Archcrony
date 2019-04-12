package com.architectica.socialcomponents.main.ChatsList;

import android.app.ProgressDialog;
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

import com.architectica.socialcomponents.AllUsersList.UsersActivity;
import com.architectica.socialcomponents.R;
import com.architectica.socialcomponents.adapters.ChatsListAdapter;
import com.architectica.socialcomponents.model.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsListActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private ChatsListAdapter adapter;
    private RecyclerView recyclerView;

    FloatingActionButton allUsers;
    int i;

    List<Profile> profiles = new ArrayList<>();

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
        setContentView(R.layout.activity_chats_list);

        pd = new ProgressDialog(ChatsListActivity.this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Chats");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseDatabase.getInstance().getReference("messages/" + FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    profiles.clear();

                    int count = (int) dataSnapshot.getChildrenCount();

                    i = 1;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        String id = snapshot.getKey();

                        FirebaseDatabase.getInstance().getReference("profiles/" + id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Profile profile = dataSnapshot.getValue(Profile.class);
                                profile.setId(dataSnapshot.getKey());

                                profiles.add(profile);

                                if (i == count){

                                    initChatsList(profiles);

                                }

                                i++;

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        allUsers = findViewById(R.id.viewAllUsers);

        allUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatsListActivity.this,UsersActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initChatsList(List<Profile> profiles){

        recyclerView = findViewById(R.id.conv_list);
        adapter = new ChatsListAdapter(this,profiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(adapter);

        pd.dismiss();

    }

}
