package com.example.grybos.firechat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EditingBottomSheetDialog2 extends BottomSheetDialogFragment {

    private EditingBottomSheetDialog2.EditBottomSheetListener mListener;
    private String id;
    private String userName;
    private long messageDate;
    private String userImage;
    private String messageText;
    private String emailAdress;
    private String userId;

    public EditingBottomSheetDialog2(String userName, String id, String userImage, long messageDate, String messageText, String emailAdress, String userId){

        this.id = id;
        this.userName = userName;
        this.messageDate = messageDate;
        this.userImage = userImage;
        this.messageText = messageText;
        this.emailAdress = emailAdress;
        this.userId = userId;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.editing_bottom_sheet2, container, false);

        TextView textViewName = v.findViewById(R.id.title);
        LinearLayout button_delete = v.findViewById(R.id.button_delete);

        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onViewClicked("0", id, userName, userImage, messageDate, messageText, emailAdress, userId);
                dismiss();

            }
        });

        LinearLayout button_edit = v.findViewById(R.id.button_edit);

        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onViewClicked("1", id, userName, userImage, messageDate, messageText, emailAdress, userId);
                dismiss();

            }
        });

        return v;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {

            mListener = (EditingBottomSheetDialog2.EditBottomSheetListener) context;

        }catch (ClassCastException e){

            throw new ClassCastException(context.toString() + "must implement EditBottomSheetListener");

        }

    }

    public interface EditBottomSheetListener{

        void onViewClicked(String text, String id, String userName, String userImage, long messageDate, String messageText, String userEmail, String userId);

    }

}
