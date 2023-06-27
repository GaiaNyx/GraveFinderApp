package com.example.gravefinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder>{
    private ArrayList<PDFmemo> memos;
    private View.OnClickListener onItemClickListener;

    public MemoryAdapter(ArrayList<PDFmemo> memos){this.memos = memos;}

    @NonNull
    @Override
    public MemoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View memoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_memory_item, parent, false);
        return new MemoryViewHolder(memoView);
    }

    @Override
    public int getItemCount() {
        if(memos==null)
            return 0;
        return memos.size();
    }

    public void setOnItemClickListener(View.OnClickListener onMemoryClickListener) {
        onItemClickListener = onMemoryClickListener;

    }

    public static class MemoryViewHolder extends RecyclerView.ViewHolder{
        public TextView tvName;

        public MemoryViewHolder(@NonNull View itemView){
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvMemoName);
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryViewHolder holder, int position) {
        PDFmemo memo = memos.get(position);
        holder.tvName.setText(memo.getName());
    }
}
