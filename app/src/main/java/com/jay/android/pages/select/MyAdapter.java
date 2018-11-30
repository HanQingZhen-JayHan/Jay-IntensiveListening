package com.jay.android.pages.select;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jay.android.R;
import com.jay.android.utils.FileComparator;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<File> mDataset;
    private AdapterClickListener listener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and 
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvContent;
        public CheckBox cbSelect;

        public MyViewHolder(View v) {
            super(v);
            tvContent = v.findViewById(R.id.tv_content);
            cbSelect = v.findViewById(R.id.cb_select);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<File> myDataset) {
        mDataset = myDataset;
    }

    public void updateData(List<File> myDataset) {
        mDataset = myDataset;
        if(mDataset!= null){
            Collections.sort(mDataset, new FileComparator());
        }
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final File file = mDataset.get(position);
        holder.tvContent.setText(file.getName());
        if(file.isFile()){
            holder.cbSelect.setVisibility(View.VISIBLE);
        }else {
            holder.cbSelect.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.itemClick(holder.cbSelect,file);
                }
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }

    public void setListener(AdapterClickListener listener) {
        this.listener = listener;
    }

    public interface AdapterClickListener {
        void itemClick(CheckBox cb,File file);
    }
}
