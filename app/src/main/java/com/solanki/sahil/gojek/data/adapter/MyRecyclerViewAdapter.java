package com.solanki.sahil.gojek.data.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.solanki.sahil.gojek.databinding.ItemBinding;
import com.solanki.sahil.gojek.ui.success.SuccessViewModel;
import java.util.ArrayList;


public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {

    private ArrayList<SuccessViewModel> mDataset;
    private LayoutInflater layoutInflater;


    public MyRecyclerViewAdapter(ArrayList<SuccessViewModel> myDataset) {
        mDataset = myDataset;

    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemBinding itemBinding = ItemBinding.inflate(layoutInflater, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(itemBinding);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        SuccessViewModel listViewModel = mDataset.get(position);
        holder.bind(listViewModel);
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        ItemBinding itemBinding;

        public DataObjectHolder(ItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;

        }

        public void bind(SuccessViewModel listViewModel) {
            this.itemBinding.setList(listViewModel);
        }

        public ItemBinding getItemBinding() {
            return itemBinding;
        }


    }

}
