package com.example.laba30.UI;

import static com.example.laba30.data.Constants.APP_KEY_GROUP;
import static com.example.laba30.data.Constants.APP_KEY_NAME;
import static com.example.laba30.data.Constants.APP_KEY_SURNAME;
import static com.example.laba30.data.Constants.APP_KEY_WORKS_ACCEPTED_AMOUNT;
import static com.example.laba30.data.Constants.APP_KEY_WORKS_ACCEPTED_DATES;
import static com.example.laba30.data.Constants.APP_KEY_WORKS_COMPLETED_AMOUNT;
import static com.example.laba30.data.Constants.APP_KEY_WORKS_COMPLETED_DATES;
import static com.example.laba30.data.Constants.APP_MIN_AMOUNT_WORKS_ACCEPTED;
import static com.example.laba30.data.Constants.APP_MIN_AMOUNT_WORKS_COMPLETED;
import static com.example.laba30.data.Constants.APP_MIN_LENGTH_GROUP;
import static com.example.laba30.data.Constants.APP_MIN_LENGTH_NAME;
import static com.example.laba30.data.Constants.APP_MIN_LENGTH_SURNAME;
import static com.example.laba30.data.Constants.APP_POPUP_WINDOW_TEXT;
import static com.example.laba30.data.Constants.APP_TOAST_MESSAGE_EMPTY_FIELDS;
import static com.example.laba30.utils.Utils.getPersonalDataByKey;
import static com.example.laba30.utils.Utils.getPersonalDataKey;
import static com.example.laba30.utils.Utils.moveToActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.laba30.data.PersonalDataFormat;
import com.example.laba30.databinding.ActivityMainBinding;
import com.example.laba30.databinding.BasicPopupWindowBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private BasicPopupWindowBinding popupBinding;

    private boolean nameETFocus = false, surnameETFocus = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle prevActivityBundle = getIntent().getExtras();
        if (prevActivityBundle != null) {
            binding.userName.setText(prevActivityBundle.getString(APP_KEY_NAME));
            binding.userSurname.setText(prevActivityBundle.getString(APP_KEY_SURNAME));
            binding.userGroup.setText(prevActivityBundle.getString(APP_KEY_GROUP));
            binding.worksCompleted.setText("" + prevActivityBundle.getInt(APP_KEY_WORKS_COMPLETED_AMOUNT));
            binding.worksAccepted.setText("" + prevActivityBundle.getInt(APP_KEY_WORKS_ACCEPTED_AMOUNT));
        }

        binding.goFurtherButton.setOnClickListener(view -> {
            String nameText = binding.userName.getText().toString(),
                    surnameText = binding.userSurname.getText().toString(),
                    groupText = binding.userGroup.getText().toString(),
                    worksCompletedText = binding.worksCompleted.getText().toString(),
                    worksAcceptedText = binding.worksAccepted.getText().toString();

            if (nameText.isEmpty() || surnameText.isEmpty() || groupText.isEmpty() || worksCompletedText.isEmpty() || worksAcceptedText.isEmpty()) {
                Toast.makeText(this, APP_TOAST_MESSAGE_EMPTY_FIELDS, Toast.LENGTH_SHORT).show();
                return;
            }

            if (nameText.length() < APP_MIN_LENGTH_NAME) {
                Toast.makeText(this, "The name should be at least " + APP_MIN_LENGTH_NAME + " letter(s) long.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (surnameText.length() < APP_MIN_LENGTH_NAME) {
                Toast.makeText(this, "The surname should be at least " + APP_MIN_LENGTH_SURNAME + " letter(s) long.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (groupText.length() < APP_MIN_LENGTH_GROUP) {
                Toast.makeText(this, "The group should be at least " + APP_MIN_LENGTH_GROUP + " character(s) long.", Toast.LENGTH_SHORT).show();
                return;
            }

            int completedWorksAmount = Integer.parseInt(worksCompletedText), acceptedWorksAmount = Integer.parseInt(worksAcceptedText);
            if (completedWorksAmount < APP_MIN_AMOUNT_WORKS_COMPLETED) {
                Toast.makeText(this, "There should be at least " + APP_MIN_AMOUNT_WORKS_COMPLETED + " completed work(s).", Toast.LENGTH_SHORT).show();
                return;
            }
            if (acceptedWorksAmount < APP_MIN_AMOUNT_WORKS_ACCEPTED) {
                Toast.makeText(this, "There should be at least " + APP_MIN_AMOUNT_WORKS_ACCEPTED + " accepted work(s).", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = new Intent(this, AcceptedWorksDateInsertionActivity.class);
            i.putExtra(APP_KEY_NAME, nameText);
            i.putExtra(APP_KEY_SURNAME, surnameText);
            i.putExtra(APP_KEY_GROUP, groupText);
            i.putExtra(APP_KEY_WORKS_COMPLETED_AMOUNT, completedWorksAmount);
            i.putExtra(APP_KEY_WORKS_ACCEPTED_AMOUNT, acceptedWorksAmount);
            moveToActivity(this, i);
        });

        binding.userName.setOnFocusChangeListener((view, b) -> {
            nameETFocus = b;
            if (!nameETFocus && !surnameETFocus) {
                checkSPreferences();
            }
        });
        binding.userSurname.setOnFocusChangeListener((view, b) -> {
            surnameETFocus = b;
            if (!nameETFocus && !surnameETFocus) {
                checkSPreferences();
            }
        });
    }

    private void checkSPreferences() {
        PersonalDataFormat pData = getPersonalDataByKey(this, getPersonalDataKey(binding.userName.getText().toString() + binding.userSurname.getText().toString()));
        if (pData != null) {
            popupBinding = BasicPopupWindowBinding.inflate(getLayoutInflater());
            View popupView = popupBinding.getRoot();

            int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

            popupBinding.popupText.setText(APP_POPUP_WINDOW_TEXT);

            popupBinding.noButton.setOnClickListener(view -> {
                popupWindow.dismiss();
            });
            popupBinding.yesButton.setOnClickListener(view -> {
                Intent i = new Intent(this, FinalActivity.class);
                i.putExtra(APP_KEY_NAME, pData.name);
                i.putExtra(APP_KEY_SURNAME, pData.surname);
                i.putExtra(APP_KEY_GROUP, pData.group);
                i.putExtra(APP_KEY_WORKS_COMPLETED_AMOUNT, pData.completedWorksAmount);
                i.putExtra(APP_KEY_WORKS_ACCEPTED_AMOUNT, pData.acceptedWorksAmount);
                i.putParcelableArrayListExtra(APP_KEY_WORKS_COMPLETED_DATES, pData.completedWorksDates);
                i.putParcelableArrayListExtra(APP_KEY_WORKS_ACCEPTED_DATES, pData.acceptedWorksDates);
                moveToActivity(this, i);
            });

            popupWindow.setOnDismissListener(() -> {
                //popupWindowIsActive = false;
            });

            popupWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);
        }
    }
}