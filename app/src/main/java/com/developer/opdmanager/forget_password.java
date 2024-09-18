package com.developer.opdmanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class forget_password extends AppCompatActivity {
    private EditText otpBox1, otpBox2, otpBox3, otpBox4, otpBox5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_pat);
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(forget_password.this,patient_login.class);
                startActivity(intent);
            }
        });
        otpBox1 = findViewById(R.id.otp_box_1);
        otpBox2 = findViewById(R.id.otp_box_2);
        otpBox3 = findViewById(R.id.otp_box_3);
        otpBox4 = findViewById(R.id.otp_box_4);
        otpBox5 = findViewById(R.id.otp_box_5);
        otpBox1.addTextChangedListener(new OtpTextWatcher(otpBox1, otpBox2, null));
        otpBox2.addTextChangedListener(new OtpTextWatcher(otpBox2, otpBox3, otpBox1));
        otpBox3.addTextChangedListener(new OtpTextWatcher(otpBox3, otpBox4, otpBox2));
        otpBox4.addTextChangedListener(new OtpTextWatcher(otpBox4, otpBox5, otpBox3));
        otpBox5.addTextChangedListener(new OtpTextWatcher(otpBox5, null, otpBox4));
        setOnKeyListeners();
    }
    private void setOnKeyListeners() {
        otpBox2.setOnKeyListener(new OtpOnKeyListener(otpBox2, otpBox1));
        otpBox3.setOnKeyListener(new OtpOnKeyListener(otpBox3, otpBox2));
        otpBox4.setOnKeyListener(new OtpOnKeyListener(otpBox4, otpBox3));
        otpBox5.setOnKeyListener(new OtpOnKeyListener(otpBox5, otpBox4));
    }
    private class OtpTextWatcher implements TextWatcher {

        private View currentView;
        private View nextView;
        private View previousView;

        public OtpTextWatcher(View currentView, View nextView, View previousView) {
            this.currentView = currentView;
            this.nextView = nextView;
            this.previousView = previousView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus(); // Move to the next OTP box
            } else if (s.length() == 0 && previousView != null) {
                previousView.requestFocus(); // Move to the previous OTP box when cleared
            }
        }
    }
    private class OtpOnKeyListener implements View.OnKeyListener {

        private View currentView;
        private View previousView;

        public OtpOnKeyListener(View currentView, View previousView) {
            this.currentView = currentView;
            this.previousView = previousView;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                EditText editText = (EditText) currentView;
                if (editText.getText().toString().isEmpty() && previousView != null) {
                    previousView.requestFocus(); // Move to the previous OTP box on backspace
                    return true;
                }
            }
            return false;
        }
    }
}