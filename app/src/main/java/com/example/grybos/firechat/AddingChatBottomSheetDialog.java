package com.example.grybos.firechat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AddingChatBottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.adding_chat_bottom_sheet, container, false);

        Button button = v.findViewById(R.id.button);
        final EditText editText = v.findViewById(R.id.edit_text);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editText.getText().toString();

                if (name.isEmpty()){

                    editText.setError("Musisz podać jakąś nazwę!");
                    editText.requestFocus();

                }
                else {

                    mListener.onButtonClicked(name);
                    dismiss();

                }

            }
        });

        return v;
    }

    public interface BottomSheetListener{

        void onButtonClicked(String text);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {

            mListener = (BottomSheetListener) context;

        }catch (ClassCastException e){

            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");

        }
    }
}
