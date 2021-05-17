package com.diplom.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private static final int SIGN_IN_CODE = 1;
    private RelativeLayout activity_main;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activity_main, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
                displayAllMessages();
            } else {
                Snackbar.make(activity_main, "Вы не авторизованы", Snackbar.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        activity_main = findViewById(R.id.activity_main);
        Button sendBtn = (Button) findViewById(R.id.btnSend);
        sendBtn.setOnClickListener(v -> {
            EditText textField = findViewById(R.id.messageField);
            if (textField.getText().toString().equals(""))
                return;

            FirebaseDatabase.getInstance().getReference().push().setValue(
                    new Message(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(),
                            textField.getText().toString())
            );
            textField.setText("");
        });


        //Пользователь еще не авторизован
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
        else {
            Snackbar.make(activity_main, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
            displayAllMessages();

        }
    }

    private void displayAllMessages() {
        ListView listOfMessages = findViewById(R.id.list_of_messages);
        //this, Message.class, R.layout.list_item, FirebaseDatabase.getInstance().getReference()
        FirebaseListOptions.Builder<Message> builder = new FirebaseListOptions.Builder<>();
        builder.setLayout(R.layout.list_item);
        //todo add query

        FirebaseListAdapter<Message> adapter = new FirebaseListAdapter<Message>(builder.build()) {
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView mess_user, mess_time, mess_text;
                mess_user = v.findViewById(R.id.m_user);
                mess_time = v.findViewById(R.id.ms_time);
                mess_text = v.findViewById(R.id.msg_text);

                mess_user.setText(model.getUserName());
                mess_text.setText(model.getTextMessage());
                mess_time.setText(DateFormat.format("dd-mm-yyyy HH:mm:ss", model.getMessageTime()));


            }
        };

        listOfMessages.setAdapter(adapter);
    }

}