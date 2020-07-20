package com.example.grybos.firechat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EditingChatBottomSheetDialog extends BottomSheetDialogFragment {

    private EditBottomSheetListener mListener;
    private String chatName;
    private String id;
    private String image_resource;

    public EditingChatBottomSheetDialog(String chatName, String id , String image_resource){

        this.chatName = chatName;
        this.id = id;
        this.image_resource = image_resource;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.editing_chat_bottom_sheet, container, false);

        TextView textViewName = v.findViewById(R.id.title);
        textViewName.setText(chatName);
        LinearLayout button_delete = v.findViewById(R.id.button_delete);

        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onViewClicked("0", id, image_resource);
                dismiss();

            }
        });

        LinearLayout button_edit = v.findViewById(R.id.button_edit);

        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onViewClicked("1", id, image_resource);
                dismiss();

            }
        });

        return v;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {

            mListener = (EditBottomSheetListener) context;

        }catch (ClassCastException e){

            throw new ClassCastException(context.toString() + "must implement EditBottomSheetListener");

        }

    }

    public interface EditBottomSheetListener{

        void onViewClicked(String text, String id, String image_resource);

    }

}
