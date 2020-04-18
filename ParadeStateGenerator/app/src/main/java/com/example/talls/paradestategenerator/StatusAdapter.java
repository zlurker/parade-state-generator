package com.example.talls.paradestategenerator;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

    public static class StatusViewHolder extends RecyclerView.ViewHolder {

        public TextView status;
        public Switch presentCount;
        public Switch listNames;
        public Button deleteButton;

        public StatusViewHolder(View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.statustext);
            presentCount = itemView.findViewById(R.id.presentstatus);
            listNames = itemView.findViewById(R.id.listnames);
            deleteButton = itemView.findViewById(R.id.delButton);
        }
    }

    @Override
    public StatusViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LinearLayout ll = new LinearLayout(viewGroup.getContext());

        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView text = new TextView(viewGroup.getContext());
        Switch swi = new Switch(viewGroup.getContext());
        Switch listSwi = new Switch(viewGroup.getContext());
        Button del = new Button(viewGroup.getContext());

        text.setTextColor(Color.WHITE);
        del.setText("DELETE");

        text.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 5));
        swi.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));
        listSwi.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));
        del.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));

        ll.addView(text);
        ll.addView(swi);
        ll.addView(listSwi);
        ll.addView(del);

        text.setId(R.id.statustext);
        swi.setId(R.id.presentstatus);
        listSwi.setId(R.id.listnames);
        del.setId(R.id.delButton);

        return new StatusViewHolder(ll);
    }


    @Override
    public void onBindViewHolder(StatusViewHolder viewHolder, final int i) {
        viewHolder.status.setText(ParadeStatePage.cStatus.get(i).name);
        viewHolder.presentCount.setChecked(ParadeStatePage.cStatus.get(i).presentStatus);
        viewHolder.listNames.setChecked(ParadeStatePage.cStatus.get(i).listNames);

        viewHolder.presentCount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("IndexTesting",String.valueOf(i));
                ParadeStatePage.cStatus.get(i).presentStatus = isChecked;
            }
        });

        viewHolder.listNames.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ParadeStatePage.cStatus.get(i).listNames = isChecked;
            }
        });

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ParadeStatePage.cStatus.size();
    }

    public void deleteItem(int i){
        try {
            ParadeStatePage.cStatus.remove(i);
            notifyItemRemoved(i);
            notifyItemRangeChanged(i, ParadeStatePage.cStatus.size());
        }catch (Exception e){
            Log.e("EXCEPTIONCAPTURED", e.toString());
        }
    }

    public void addItem(){
        try {
            notifyItemInserted(ParadeStatePage.cStatus.size() - 1);
        }catch (Exception e){
            Log.e("EXCEPTIONCAPTURED", e.toString());
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
