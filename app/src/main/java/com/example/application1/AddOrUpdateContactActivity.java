package com.example.application1;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
    Menu menu;
    MenuItem menuitem;
    Button take_picture;
    Button choose_img;
    Bitmap bitmap;
    private static final int PERMISSION_GALLERY_CODE = 1001;
    private static final int PERMISSION_CAMERA_CODE = 1002;
    String profile = null;
    Uri imageUri;
    BottomSheetDialog dialog;
    Bitmap photo;
    Dialog layout_dialog;
    TextView setting;
    TextView close;

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
        contactName = findViewById(R.id.contactName);
        datetime = findViewById(R.id.view_date_time);

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
            //menuitem = menu.findItem(R.id.save);
            //menuitem.setTitle(Constant.SAVE); //display string.xml

            name.setText(contact.getName());
            phone.setText(contact.getPhone());

            menuitem = menu.findItem(R.id.save);
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
            //menuitem = menu.findItem(R.id.save);
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

        if(contact != null){
            name.setText(contact.getName());
            phone.setText(contact.getPhone());
            contactName.setText(contact.getName());
            datetime.setText(contact.getDatetime());
            if (BitmapFactory.decodeFile(contact.getImage()) != null){
                add_img.setImageBitmap(BitmapFactory.decodeFile(contact.getImage()));
            }else{
                Picasso.get().load(contact.getImage()).placeholder(R.drawable.profile).into(add_img);
            }
        }
    }

    private void loadBottomSheetDialog(){
        RelativeLayout mainLayout = findViewById(R.id.relative_addorupdate);
        final LayoutInflater layout = getLayoutInflater();
        View myLayout = layout.inflate(R.layout.bottom_sheet_dialog,mainLayout,false);
        dialog = new BottomSheetDialog(this);

        dialog.setContentView(myLayout);
        dialog.show();

        choose_img = myLayout.findViewById(R.id.choose_picture);
        take_picture = myLayout.findViewById(R.id.take_picture);

        choose_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                            showAlertDialog();
                        }else{
                            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                            requestPermissions(permission,PERMISSION_GALLERY_CODE);
                        }
                    }else{
                        loadImagesFromGallery();
                        dialog.dismiss();
                    }
                }
            }
        });

        take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                            showAlertDialog();
                        }else{
                            String[] permission = {Manifest.permission.CAMERA};
                            requestPermissions(permission,PERMISSION_CAMERA_CODE);
                        }
                    }else{
                        loadCamera();
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    private void showAlertDialog(){
        layout_dialog = new Dialog(AddOrUpdateContactActivity.this);
        layout_dialog.setContentView(R.layout.alert_dialog);

        close = layout_dialog.findViewById(R.id.close);
        setting = layout_dialog.findViewById(R.id.setting);

        layout_dialog.setCancelable(true);
        layout_dialog.getWindow();
        layout_dialog.show();

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts(Constant.PACKAGE,getPackageName(),null);
                intent.setData(uri);
                startActivity(intent);
                layout_dialog.dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_dialog.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_GALLERY_CODE){
            for (int i = 0; i < grantResults.length; i++){
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    loadImagesFromGallery();
                }else{
                    Toast.makeText(this, R.string.permission_result, Toast.LENGTH_SHORT).show();
                }
            }
            dialog.dismiss();
        }else if(requestCode == PERMISSION_CAMERA_CODE){
            for (int i = 0; i < grantResults.length; i++){
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    loadCamera();
                }else{
                    Toast.makeText(this, R.string.permission_result, Toast.LENGTH_SHORT).show();
                }
            }
            dialog.dismiss();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void loadImagesFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private void loadCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                getImagePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(requestCode == 2 && resultCode == Activity.RESULT_OK){
            bitmap = (Bitmap) data.getExtras().get(Constant.DATA);
            getImagePath();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getImagePath(){
        try {
            ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
            File directory = contextWrapper.getDir(Constant.PHOTO_DIR, Context.MODE_PRIVATE);
            File file = new File(directory, System.currentTimeMillis() + ".jpg");

            if (!file.exists()){
                add_img.setImageBitmap(bitmap);
                FileOutputStream fos = null;
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            }
            profile = file.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addContact(String addName, String addPhone){
        MyDatabase db = MyDatabase.getDBInstance(this.getApplicationContext());
        contact = new Contact();

        if (bitmap != null){
            contact.zName = addName;
            contact.zPhone = addPhone;
            contact.datetime = getTime();
            contact.image = profile;
        } else{
            contact.zName = addName;
            contact.zPhone = addPhone;
            contact.datetime = getTime();
            Picasso.get().load(contact.image).placeholder(R.drawable.profile);
        }

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
        contact.image = profile;

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
