package com.example.application1;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class AddOrUpdateContactActivity extends AppCompatActivity {

    Contact contact;
    EditText name;
    EditText phone;
    TextView contactName;
    Button saveBtn;
    TextInputLayout textNameLayout;
    TextInputLayout textPhoneLayout;
    boolean autoCheck = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addorupdatecontact);

        Toolbar toolbar = findViewById(R.id.toolbar2);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        name = findViewById(R.id.add_name);
        phone = findViewById(R.id.add_phone);
        saveBtn = findViewById(R.id.addBtn);
        textNameLayout = findViewById(R.id.name);
        textPhoneLayout = findViewById(R.id.contact_number);
        TextView txt = findViewById(R.id.save);

        Bundle data = getIntent().getExtras();
        String btnName = null;

        if(data != null) {
            contact = (Contact) data.getParcelable(Constant.EDIT_CONTACT);
            btnName = data.getString(Constant.BTN_NAME);
        }

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (autoCheck){
                    validateName();
                }
            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (autoCheck){
                    validatePhone();
                }
            }
        });

        if(btnName != null){
            saveBtn.setText(R.string.save);

            name.setText(contact.getName());
            phone.setText(contact.getPhone());

            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autoCheck = true;

                    if(checkValidate()){
                        editContact(contact.getContactId(),name.getText().toString(),phone.getText().toString());
                    }
                }
            });
        }else{
            saveBtn.setText(R.string.add_button);

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autoCheck = true;

                    if(checkValidate()) {
                        addContact(name.getText().toString(),phone.getText().toString());
                    }
                }
            });
        }

        viewContactDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.topsave,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if (id == R.id.save){
            Toast.makeText(this, "hi",Toast.LENGTH_LONG).show();
            //startActivityForResult(new Intent(this, AddOrUpdateContactActivity.class),100);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private boolean checkValidate(){
        boolean valid = false;

        validateName();
        validatePhone();

        return valid;
    }

    private boolean validateName(){

        if(name.getText().toString().isEmpty()){
            textNameLayout.setError(getString(R.string.validEmpty));
            return false;
        }else if (name.length() > 50){
            textNameLayout.setError(getString(R.string.validNameLong));
            return false;
        }else if(name.length() < 3){
            textNameLayout.setError(getString(R.string.validNameShort));
            return false;
        } else{
            textNameLayout.setError(null);
        }
        return true;
    }

    private boolean validatePhone(){

        if(phone.getText().toString().isEmpty()){
            textPhoneLayout.setError(getString(R.string.validEmpty));
            return false;
        }else{
            textPhoneLayout.setError(null);
        }
        return true;
    }

    private void viewContactDetails(){
        contact = new Contact();

        Bundle data = getIntent().getExtras();
        contact = data.getParcelable(Constant.GET_CONTACT);

        name = findViewById(R.id.add_name);
        phone = findViewById(R.id.add_phone);
        contactName = findViewById(R.id.contactName);

        name.setText(contact.getName());
        phone.setText(contact.getPhone());
        contactName.setText(contact.getName());
    }

    private void addContact(String addName, String addPhone){
        MyDatabase db = MyDatabase.getDBInstance(this.getApplicationContext());

        if(addName != null && addPhone != null){
            contact = new Contact();
            contact.zName = addName;
            contact.zPhone = addPhone;

            db.daoAccess().insertContact(contact);
        }

        Toast.makeText(this, R.string.addSuccessMsg, Toast.LENGTH_SHORT).show();
        this.finish();
    }

    private void editContact(int id, String editName, String editPhone){
        MyDatabase db = MyDatabase.getDBInstance(this.getApplicationContext());

        contact.contactId = id;
        contact.zName = editName;
        contact.zPhone = editPhone;

        db.daoAccess().insertContact(contact);

        Toast.makeText(this, R.string.editSuccessMsg, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private String getTime(){
        LocalDateTime today = LocalDateTime.now(ZoneId.systemDefault());

        return today.format(DateTimeFormatter.ofPattern("d MMM uuuu HH:mm:ss "));
    }
}
