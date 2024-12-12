package com.example.a2fa_class;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import database.UserDatabase;
import model.User;

import java.util.concurrent.Executors;


public class SignUpActivity extends AppCompatActivity {
    private EditText emailField, passwordField,firstNameField,lastNameField,numberField;
    private Button signupButton;
    private UserDatabase userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        firstNameField=findViewById(R.id.firstNameField);
        lastNameField=findViewById(R.id.lastNameField);
        numberField=findViewById(R.id.numberField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        signupButton = findViewById(R.id.signUpBtn);

        userDatabase = UserDatabase.getInstance(this);

        signupButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            String firstName=firstNameField.getText().toString();
            String lastName=lastNameField.getText().toString();
            String number=numberField.getText().toString();

            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || number.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert user into database
            Executors.newSingleThreadExecutor().execute(() -> {
                User existingUser = userDatabase.userDao().getUserByEmail(email);
                if (existingUser != null) {
                    runOnUiThread(() -> Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show());
                } else {
                    User newUser = new User(firstName, lastName,number,email,password); // Password should ideally be hashed
                    userDatabase.userDao().insertUser(newUser);
                    runOnUiThread(() -> Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }
}
