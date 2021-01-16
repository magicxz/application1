package com.example.application1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ContactListAdapter contactListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayList<Contact> contactList = new ArrayList<>();

        contactList.clear();

        Button addContactBtn = findViewById(R.id.addContactBtn);
        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, AddOrUpdateContactActivity.class),100);
            }
        });

        RecyclerView reContacts = (RecyclerView) findViewById(R.id.reContact);
        contactListAdapter = new ContactListAdapter(contactList);
        reContacts.setAdapter(contactListAdapter);
        reContacts.setLayoutManager(new LinearLayoutManager(this));

        loadContactList();

        contactListAdapter.setOnItemClickListener(new ContactListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, DeleteOrViewContactDetailsActivity.class);
                intent.putExtra(Constant.GET_CONTACT, contactList.get(position));
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void loadContactList(){
        MyDatabase db = MyDatabase.getDBInstance(this.getApplicationContext());
        List<Contact> contactList = db.daoAccess().getAllContact();
        contactListAdapter.setContactList(contactList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100){
            loadContactList();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}