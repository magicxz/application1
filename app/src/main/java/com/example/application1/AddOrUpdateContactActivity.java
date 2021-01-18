package com.example.application1;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class AddOrUpdateContactActivity extends AppCompatActivity {

    Contact contact;
    EditText name;
    EditText phone;
    Button saveBtn;
    TextInputLayout textNameLayout;
    TextInputLayout textPhoneLayout;
    boolean autoCheck = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addorupdatecontact);

        name = findViewById(R.id.add_name);
        phone = findViewById(R.id.add_phone);
        saveBtn = findViewById(R.id.addBtn);
        textNameLayout = findViewById(R.id.name);
        textPhoneLayout = findViewById(R.id.contact_number);

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
            saveBtn.setText(R.string.edit);

            name.setText(contact.getName());
            phone.setText(contact.getPhone());

            saveBtn.setOnClickListener(new View.OnClickListener() {
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
}
