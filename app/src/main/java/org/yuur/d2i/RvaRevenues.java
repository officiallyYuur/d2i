package org.yuur.d2i;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;



import java.util.Calendar;
import java.util.List;

public class RvaRevenues extends RecyclerView.Adapter<RvaRevenues.MyViewHolder> {

    private List<Revenue> mItemList;
    Fragment main;
    final CharSequence[] dialogChoices = {"Income Received", "Delete this single payment", "Delete this and all similar payments in the future"};
    String frequency;
    String tPosition;

    // Main Adapter
    public RvaRevenues(List<Revenue> myItemList, Fragment context) {
        this.mItemList = myItemList;
        main = context;
    }

    // Set the Layout XML files for the different views
    @NonNull
    @Override
    public RvaRevenues.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_revenue, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Set the Content in the Views
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (mItemList.get(position).getPayStatus().equals("paid")) {
            holder.checkBoxPaid.setChecked(true);
            Log.v("RvaRevenues", "onBindViewHolder: paid checkBoxPaid = " + mItemList.get(position).getPayStatus());
        }
        else {
            holder.checkBoxPaid.setChecked(false);
            Log.v("RvaRevenues", "onBindViewHolder: due checkBoxPaid = " + mItemList.get(position).getPayStatus());
        }
        holder.tvRevenueTitle.setText(mItemList.get(position).getTitle());
        holder.tvRevenueAmt.setText(mItemList.get(position).getAmount());
        holder.tPosition = mItemList.get(position).getTPosition();
        Log.v("RvaRevenues", "onBindViewHolder: position = " + position);
    }
    // Return the List Size
    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Initialize View Values
        CheckBox checkBoxPaid;
        TextView tvRevenueTitle;
        TextView tvRevenueAmt;
        ConstraintLayout item_revenue;
        String transmit;
        String tPosition;

        public MyViewHolder(View v) {
            super(v);
            // Set ViewHolders
            checkBoxPaid = v.findViewById(R.id.checkBoxPaid);
            tvRevenueTitle = v.findViewById(R.id.tvRevenueTitle);
            tvRevenueAmt = v.findViewById(R.id.tvRevenueAmt);
            item_revenue = v.findViewById(R.id.item_expense);
            // Set OnClickListener for The Entire View
            v.setOnClickListener(this);
        }

        // OnClickListener Method for the Entire View
        @Override
        public void onClick (View v){
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle(tvRevenueTitle.getText().toString());
            builder.setSingleChoiceItems(dialogChoices, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case 0:
                            frequency = "paid";
                            //Log.v("chckbox", "paid");
                            break;
                        case 1:
                            frequency = "delete";
                            //Log.v("chckbox", "delete");
                            break;
                        case 2:
                            frequency = "deleteAllFuture";
                            //Log.v("chckbox", "deleteAllFuture");
                    }
                }
            });
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface builder, int id) {
                    switch (frequency) {
                        case "paid":
                            transmit = "paidRevenue";
                            //set checkbox to paid
                            break;
                        case "delete":
                            transmit = "deleteRevenue";
                            // delete array item with this name and this date
                            break;
                        case "deleteAllFuture":
                            transmit = "deleteAllRevenue";
                            //delete array object with this name and all dates in the future
                            break;
                        default:
                            break;
                    }
                    sendData();
                    builder.dismiss();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface builder, int id) {
                    // 2.3.1 User cancelled the dialog
                    builder.dismiss();
                }
            });
            builder.show();
        }
        private void sendData()
        {
            //INTENT OBJ
            Intent i = new Intent(main.getContext(), MainActivity.class);

            //PACK DATA
            i.putExtra("tPosition", tPosition);
            i.putExtra("transmit", transmit);
            i.putExtra("editorTitle", tvRevenueTitle.getText().toString());
            i.putExtra("editorAmount", Integer.valueOf(tvRevenueAmt.getText().toString()));

            //START ACTIVITY
            main.getContext().startActivity(i);
        }
    }

}
