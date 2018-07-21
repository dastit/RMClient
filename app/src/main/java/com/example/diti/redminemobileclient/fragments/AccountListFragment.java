package com.example.diti.redminemobileclient.fragments;


import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.diti.redminemobileclient.R;

public class AccountListFragment extends DialogFragment {

    private static final String ARG_ACCNAMES = "accounts_names";

    private CharSequence[] mAccountsNames;
    public AccountListFragment() {
        // Required empty public constructor
    }

   public static AccountListFragment newInstance(CharSequence[] names) {
        AccountListFragment fragment = new AccountListFragment();
        Bundle args = new Bundle();
        args.putCharSequenceArray(ARG_ACCNAMES, names);
        fragment.setArguments(args);
        return fragment;
    }
    public interface AccountListListener {
        public void onItemClick(DialogFragment dialog, int which);
        public void onButtonClick(DialogFragment dialog);
    }

    AccountListListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (AccountListListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                                         + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

        if(!getArguments().equals(0)) {
            mAccountsNames = getArguments().getCharSequenceArray(ARG_ACCNAMES);
            final AccountManager accountManager = AccountManager.get(getActivity());
            return mBuilder.setTitle(R.string.account_list_title).setItems(mAccountsNames, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onItemClick(AccountListFragment.this, which);
                }
            } ).setPositiveButton(R.string.add_new_account, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   mListener.onButtonClick(AccountListFragment.this);
                }
            }).create();
        }
        else return mBuilder.setTitle(R.string.zero_accounts).create();
    }
}
