package com.example.application1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddOrUpdateContactActivity extends AppCompatActivity {

    Contact contact;
    TextInputEditText name;
    TextInputEditText phone;
    TextView contactName;
    TextInputLayout textNameLayout;
    TextInputLayout textPhoneLayout;
    boolean autoCheck = false;
    ActionMenuItemView item;
    Toolbar toolbar;
    Button custom_button;
    TextView datetime;
    CircleImageView add_img;
    Uri imageUri;
    Menu menu;
    MenuItem menuitem;
    Button take_picture;
    Button choose_img;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addorupdatecontact);

        toolbar = findViewById(R.id.tool_addorupdate);

        menu = toolbar.getMenu();
        menuitem = menu.findItem(R.id.save);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        name = findViewById(R.id.add_name);
        phone = findViewById(R.id.add_phone);
        textNameLayout = findViewById(R.id.name);
        textPhoneLayout = findViewById(R.id.contact_number);
        add_img = findViewById(R.id.add_image);

        Bundle data = getIntent().getExtras();
        String btnName = null;

        if(data != null) {
            contact = (Contact) data.getParcelable(Constant.GET_CONTACT);
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
            menuitem = menu.findItem(R.id.save);
            menuitem.setTitle(Constant.SAVE);

            name.setText(contact.getName());
            phone.setText(contact.getPhone());

            menuitem = menu.findItem(R.id.save).setTitle(Constant.ADD);
            menuitem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autoCheck = true;
                    if (checkValidate()) {
                        editContact(contact.getContactId(),name.getText().toString(),phone.getText().toString());
                    }
                }
            });
        }else{
            menuitem = menu.findItem(R.id.save).setTitle(Constant.ADD);
            menuitem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autoCheck = true;
                    if (checkValidate()) {
                        addContact(name.getText().toString(), phone.getText().toString());
                    }
                }
            });
        }

        add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBottomSheetDialog();
            }
        });

        viewContactDetails();
    }

    private void loadBottomSheetDialog(){
        RelativeLayout mainLayout = findViewById(R.id.relative_addorupdate);
        LayoutInflater layout = getLayoutInflater();
        View myLayout = layout.inflate(R.layout.bottom_sheet_dialog,mainLayout,false);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);

        dialog.setContentView(myLayout);
        dialog.show();

        choose_img = myLayout.findViewById(R.id.choose_picture);
        take_picture = myLayout.findViewById(R.id.take_picture);

        choose_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagesFromGallery();
                dialog.dismiss();
            }
        });


    }

    private boolean checkValidate(){
        boolean valid = true;

        if(!validateName()) {
            valid = false;
        }

        if(!validatePhone()) {
            valid = false;
        }

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
        name = findViewById(R.id.add_name);
        phone = findViewById(R.id.add_phone);
        contactName = findViewById(R.id.contactName);
        datetime = findViewById(R.id.view_date_time);

        Bundle data = getIntent().getExtras();

        if(data != null){
            contact = data.getParcelable(Constant.GET_CONTACT);

            name.setText(contact.getName());
            phone.setText(contact.getPhone());
            contactName.setText(contact.getName());
            datetime.setText(contact.getDatetime());
        }
    }

    private void loadImagesFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1 && data.getData() != null){
            imageUri = data.getData();

            try {
                InputStream is = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                add_img.setImageBitmap(bitmap);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addContact(String addName, String addPhone){
        MyDatabase db = MyDatabase.getDBInstance(this.getApplicationContext());

        contact = new Contact();
        contact.zName = addName;
        contact.zPhone = addPhone;
        contact.datetime = getTime();

        db.daoAccess().insertContact(contact);

        Toast.makeText(this, R.string.addSuccessMsg, Toast.LENGTH_SHORT).show();
        this.finish();
    }

    private void editContact(int id, String editName, String editPhone){
        MyDatabase db = MyDatabase.getDBInstance(this.getApplicationContext());

        contact.contactId = id;
        contact.zName = editName;
        contact.zPhone = editPhone;
        contact.datetime = getTime();

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
