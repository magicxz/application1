package com.example.application1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    private OnItemClickListener iListener;
    private ArrayList<Contact> myContactList;

    public void setContactList(List<Contact> contactList){
        this.myContactList = new ArrayList<>(contactList);
    }

    public Contact getContact(int position){
        return myContactList.get(position);
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        iListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView phone;
        public RelativeLayout rela;

        public ViewHolder(View  itemView, final OnItemClickListener listener){
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.contactName);
            phone = (TextView) itemView.findViewById(R.id.contactNum);
            rela = (RelativeLayout) itemView.findViewById(R.id.re1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
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
        Contact contact = myContactList.get(position);
        TextView viewContact = holder.name;
        TextView viewPhone = holder.phone;

        viewContact.setText(contact.getName());
        viewPhone.setText(contact.getPhone());
    }

    @Override
    public int getItemCount() {
        return this.myContactList.size();
    }
}
