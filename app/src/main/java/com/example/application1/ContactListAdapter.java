package com.example.application1;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    private OnItemClickListener iListener;
    private ArrayList<Contact> myContactList;
    AlertDialog.Builder dialogBuilder;
    Context mContext;

    public void ContactListAdapter(Context context){
        mContext = context;
    }

    public void setContactList(List<Contact> contactList){
        this.myContactList = new ArrayList<>(contactList);
    }

    public Contact getContact(int position){
        return myContactList.get(position);
    }

    public interface OnItemClickListener{
        void onItemClick(Contact contact, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        iListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView phone;
        public RelativeLayout rela;
        public ImageView deleteContact;
        public TextView datetime;
        public CircleImageView profile;

        public ViewHolder(View  itemView, final OnItemClickListener listener){
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.contactName);
            phone = (TextView) itemView.findViewById(R.id.contactNum);
            rela = (RelativeLayout) itemView.findViewById(R.id.re1);
            deleteContact = itemView.findViewById(R.id.deleteBtn);
            datetime = itemView.findViewById(R.id.datetime);
            profile = itemView.findViewById(R.id.profile);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(getContact(position),position);
                        }
                    }
                }
            });
        }
    }

    public ContactListAdapter(ArrayList<Contact> contacts){
        myContactList = contacts;
    }

    @Override
    public ContactListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.adapter_contactlist,parent,false);

        ViewHolder viewHolder = new ViewHolder(contactView, iListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ContactListAdapter.ViewHolder holder, int position) {
        final Contact contact = myContactList.get(position);

        holder.name.setText(contact.getName());
        holder.phone.setText(contact.getPhone());
        holder.datetime.setText(contact.getDatetime());

        if (BitmapFactory.decodeFile(contact.getImage()) != null){
            holder.profile.setImageBitmap(BitmapFactory.decodeFile(contact.getImage()));
        }else{
            Picasso.get().load(contact.getImage()).placeholder(R.drawable.profile).into(holder.profile);
        }

        holder.deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder = new AlertDialog.Builder(holder.deleteContact.getContext())
                        .setTitle(R.string.delAlertTitle)
                        .setMessage(R.string.delAlertMsg)
                        .setPositiveButton(R.string.alertYes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyDatabase db = MyDatabase.getDBInstance(holder.deleteContact.getContext());
                                Contact contact1 = new Contact();

                                contact1.contactId = contact.getContactId();
                                contact1.zName = contact.getName();
                                contact1.zPhone = contact.getPhone();

                                db.daoAccess().deleteContact(contact);
                                Toast.makeText(holder.deleteContact.getContext(), R.string.delSuccessMsg, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(holder.deleteContact.getContext(), MainActivity.class);
                                holder.deleteContact.getContext().startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.alertNo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = dialogBuilder.create();
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.myContactList.size();
    }

}
