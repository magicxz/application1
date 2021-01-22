package com.example.application1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ContactListAdapter contactListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tool = findViewById(R.id.tool_main);
        tool.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.addContactBtn){
                    Intent intent = new Intent(MainActivity.this, AddOrUpdateContactActivity.class);
                    startActivityForResult(intent,100);
                    return true;
                }
                return false;
            }
        });

        final ArrayList<Contact> contactList = new ArrayList<>();

        contactList.clear();
        RecyclerView reContacts = (RecyclerView) findViewById(R.id.reContact);
        contactListAdapter = new ContactListAdapter(contactList);
        reContacts.setAdapter(contactListAdapter);
        reContacts.setLayoutManager(new LinearLayoutManager(this));

        loadContactList();

        contactListAdapter.setOnItemClickListener(new ContactListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contact contact, int position) {
                String btnName = String.valueOf(R.string.save);
                Bundle data = new Bundle();
                Intent intent = new Intent(MainActivity.this, AddOrUpdateContactActivity.class);
                data.putString(Constant.BTN_NAME,btnName);
                data.putParcelable(Constant.GET_CONTACT, contact);
                intent.putExtras(data);
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
        if(requestCode == 100 || requestCode == 300){
            loadContactList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}