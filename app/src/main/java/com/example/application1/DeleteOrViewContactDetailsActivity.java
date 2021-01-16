package com.example.application1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DeleteOrViewContactDetailsActivity extends AppCompatActivity {

    EditText viewName;
    EditText viewPhone;
    Contact contact;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleteorviewcontactdetails);

        contact = new Contact();

        Bundle data = getIntent().getExtras();
        contact = data.getParcelable(Constant.GET_CONTACT);

        viewName = findViewById(R.id.edit_name);
        viewPhone = findViewById(R.id.edit_phone);

        viewName.setText(contact.getName());
        viewPhone.setText(contact.getPhone());

        Button editContact = findViewById(R.id.editContactBtn);
        editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnName = String.valueOf(R.string.save);
                Intent intent = new Intent(DeleteOrViewContactDetailsActivity.this, AddOrUpdateContactActivity.class);
                Bundle data = new Bundle();
                data.putParcelable(Constant.EDIT_CONTACT,contact);
                data.putString(Constant.BTN_NAME,btnName);
                intent.putExtras(data);
                startActivity(intent);
            }
        });

        Button deleteContact = findViewById(R.id.deleteBtn);
        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContact(contact.getContactId(),contact.getName(),contact.getPhone());
            }
        });
    }

    private void deleteContact(int id, String delName, String delPhone){
        MyDatabase db = MyDatabase.getDBInstance(this.getApplicationContext());

        contact = new Contact();

        contact.contactId = id;
        contact.zName = delName;
        contact.zPhone = delPhone;

        db.daoAccess().deleteContact(contact);
        Toast.makeText(this, R.string.delSuccessMsg, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,MainActivity.class);
        //this.finish();
        startActivity(intent);
    }
}
