package com.biocoda.miov10.ui;

import android.content.Context;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.biocoda.miov10.R;
import com.biocoda.miov10.data.DatabaseHandler;
import com.biocoda.miov10.model.CATransactions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class RVadapter extends RecyclerView.Adapter<RVadapter.ViewHolder> {

    private Context context;
    private List<CATransactions> catTransList;
    private AlertDialog.Builder editBuilder, delBuilder;
    private AlertDialog editDialog, delDialog;
    private LayoutInflater inflater;

    public RVadapter(Context context, List<CATransactions> catTransList) {
        this.context = context;
        this.catTransList = catTransList;
    }

    @NonNull
    @Override
    public RVadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View caView = LayoutInflater.from(context).inflate(R.layout.tr_listrow, viewGroup, false);
        return new ViewHolder(caView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RVadapter.ViewHolder holder, int position) {
        CATransactions caTransactions = catTransList.get(position);
            holder.caPayeeTV.setText(caTransactions.getDescription());
            holder.caAmountTV.setText(caTransactions.getAmount());
            holder.caDateTV.setText(caTransactions.getDateTransaction());

            // set in or out image for transaction view
            switch (caTransactions.getCreditOrDebit()) {
                case "credit":
                    holder.trListRowImage.setImageResource(R.mipmap.in_image);
                    break;
                default:
                    holder.trListRowImage.setImageResource(R.mipmap.out_image);
                    break;
            }
    }

    @Override
    public int getItemCount() {
        return catTransList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView caPayeeTV;
        public TextView caDateTV;
        public ImageView trListRowImage;
        public TextView caAmountTV;
        public CardView catTrCardView;
        public ImageButton updateButton;
        public int id;

        public ViewHolder(@NonNull View caView, Context ctx) {
            super(caView);
            context = ctx;
            caPayeeTV = caView.findViewById(R.id.caPayeeTVXML);
            caDateTV = caView.findViewById(R.id.caDateTVXML);
            caAmountTV = caView.findViewById(R.id.caAmountTVXML);
            trListRowImage = caView.findViewById(R.id.trListRowImageXML);
            catTrCardView = caView.findViewById(R.id.catTrCardViewXML);
            updateButton = caView.findViewById(R.id.filterButton);
            updateButton.setOnClickListener(this);;
        }

        @Override
        public void onClick(View v) {
            int adPosition;
            adPosition = getAdapterPosition();
            CATransactions thisTransaction = catTransList.get(adPosition);
            editTransaction(thisTransaction, adPosition);
        }
    }

    private void editTransaction(final CATransactions thisTransaction, final int thisPosition) {

        editBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        inflater = LayoutInflater.from(context);
        final View editView = inflater.inflate(R.layout.ca_edit_popup, null);

        delBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        inflater = LayoutInflater.from(context);
        final View delConfView = inflater.inflate(R.layout.ca_delete_popup, null);

        final Spinner caDorcSpinner, caTypeSpinner;
        final EditText caPayeeEditText, caAmountEditText;
        final Button saveCatBTN, cancelBTN, deleteBTN, deleteConfBTN, delCancelBTN;

        caDorcSpinner = editView.findViewById(R.id.catDorcSpinnerXML);
        caTypeSpinner = editView.findViewById(R.id.catTypeSpinnerXML);
        caPayeeEditText = editView.findViewById(R.id.caPayeeET);
        caAmountEditText = editView.findViewById(R.id.caAmountET);
        saveCatBTN = editView.findViewById(R.id.updateCatBTNXML);
        cancelBTN = editView.findViewById(R.id.cancelButtonXML);
        deleteBTN = editView.findViewById(R.id.deleteCATBXML);
        deleteConfBTN = delConfView.findViewById(R.id.delConDelBTNXML);
        delCancelBTN = delConfView.findViewById(R.id.delConCanBTNXML);

        // populate popup and set spinner values
        caPayeeEditText.setText(thisTransaction.getDescription());
        caAmountEditText.setText(thisTransaction.getAmount());
        String dorcCheck = thisTransaction.getCreditOrDebit();
        String typeCheck = thisTransaction.getType();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                caDorcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!(caDorcSpinner.getSelectedItem().toString() == "select credit or debit")) {
                            Log.d("po", "onItemSelected: usual spinner is running");
                            if (caDorcSpinner.getSelectedItem().toString() == "debit") {
                                List<String> typeDropList = new ArrayList<>();
                                typeDropList.add("household bill");
                                typeDropList.add("food shopping");
                                typeDropList.add("other shopping");
                                typeDropList.add("entertainment");
                                typeDropList.add("travel");
                                typeDropList.add("miscellaneous");
                                ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, typeDropList);
                                typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                caTypeSpinner.setAdapter(typeAdapter);
                            } else if(caDorcSpinner.getSelectedItem().toString() == "transfer to saving account") {
                                List<String> typeDropList = new ArrayList<>();
                                typeDropList.add("saving account");
                                ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, typeDropList);
                                typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                caTypeSpinner.setAdapter(typeAdapter);
                                caPayeeEditText.setText("Savings");
                            } else {
                                List<String> typeDropList = new ArrayList<>();
                                typeDropList.add("wage");
                                typeDropList.add("other income");
                                ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, typeDropList);
                                typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                caTypeSpinner.setAdapter(typeAdapter);
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        }, 1000);

        if (dorcCheck.equals("credit")) {
            List<String> dorcDropList = new ArrayList<>();
                dorcDropList.add(dorcCheck);
                dorcDropList.add("credit");
                dorcDropList.add("debit");
                dorcDropList.add("transfer to saving account");
            ArrayAdapter<String> dorcAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, dorcDropList);
            dorcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            caDorcSpinner.setAdapter(dorcAdapter);
            List<String> typeDropList = new ArrayList<>();
                typeDropList.add(typeCheck);
                typeDropList.add("wage");
                typeDropList.add("other income");
            ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, typeDropList);
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            caTypeSpinner.setAdapter(typeAdapter);
        } else if (dorcCheck.equals("debit")) {
            List<String> dorcDropList = new ArrayList<>();
                dorcDropList.add(dorcCheck);
                dorcDropList.add("credit");
                dorcDropList.add("debit");
                dorcDropList.add("transfer to saving account");
            ArrayAdapter<String> dorcAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, dorcDropList);
            dorcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            caDorcSpinner.setAdapter(dorcAdapter);
            List<String> typeDropList = new ArrayList<>();
                typeDropList.add(typeCheck);
                typeDropList.add("household bill");
                typeDropList.add("food shopping");
                typeDropList.add("other shopping");
                typeDropList.add("entertainment");
                typeDropList.add("travel");
                typeDropList.add("miscellaneous");
                typeDropList.add("saving account");
            ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, typeDropList);
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            caTypeSpinner.setAdapter(typeAdapter);
        } else if (dorcCheck.equals("transfer to saving account")){
            List<String> dorcDropList = new ArrayList<>();
                dorcDropList.add(dorcCheck);
                dorcDropList.add("credit");
                dorcDropList.add("debit");
                dorcDropList.add("transfer to saving account");
            ArrayAdapter<String> dorcAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, dorcDropList);
            dorcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            caDorcSpinner.setAdapter(dorcAdapter);
            List<String> typeDropList = new ArrayList<>();
            typeDropList.add("saving account");
            ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, typeDropList);
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            caTypeSpinner.setAdapter(typeAdapter);
        }

        editBuilder.setView(editView);
        editDialog = editBuilder.create();
        editDialog.show();

        saveCatBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler upDBH = new DatabaseHandler(context);
                // add db data to row
                thisTransaction.setAmount(caAmountEditText.getText().toString().trim());
                thisTransaction.setDescription(caPayeeEditText.getText().toString().trim());
                thisTransaction.setCreditOrDebit(caDorcSpinner.getSelectedItem().toString().trim());
                thisTransaction.setType(caTypeSpinner.getSelectedItem().toString().trim());
                // add data
                if (!caAmountEditText.getText().toString().isEmpty()
                        && !caPayeeEditText.getText().toString().isEmpty()
                        && !caDorcSpinner.getSelectedItem().toString().isEmpty()
                        && !caTypeSpinner.getSelectedItem().toString().isEmpty()) {
                    upDBH.updateCat(thisTransaction);
                    notifyItemChanged(thisPosition, thisTransaction);
                    Snackbar.make(editView, "Transaction updated", Snackbar.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editDialog.dismiss();
                        }
                    }, 1000);
                } else {
                    Snackbar.make(editView, "Fill in all fields", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog.dismiss();
            }
        });


        deleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open delete dialog

                deleteConfBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseHandler delDBH = new DatabaseHandler(context);
                        Log.d("po", "onClick: delete called for list position: " + thisPosition + " and DB index: " + thisTransaction.getId());

                        delDBH.deleteCat(thisTransaction.getId());
                        catTransList.remove(thisPosition);
                        notifyItemRemoved(thisPosition);
                        delDialog.dismiss();
                    }
                });
                delCancelBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delDialog.dismiss();
                    }
                });
                delBuilder.setView(delConfView);
                delDialog = delBuilder.create();
                delDialog.show();
                editDialog.dismiss();
            }
        });
    }
}
