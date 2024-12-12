package com.example.a2fa_class;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executors;

import database.UserDatabase;
import model.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {
    private EditText emailLoginField, passwordLoginField;
    private Button loginButton;
    private UserDatabase userDatabase;
    private static final String SERVER_URL = "https://api.brevo.com/v3/smtp/email";
    //private static final String API_KEY = "REMOVED"; // Replace with your actual Brevo API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLoginField = findViewById(R.id.emailLoginField);
        passwordLoginField = findViewById(R.id.passwordLoginField);
        loginButton = findViewById(R.id.loginButton);

        userDatabase = UserDatabase.getInstance(this);

        loginButton.setOnClickListener(v -> {
            String email = emailLoginField.getText().toString();
            String password = passwordLoginField.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check user in the database
            Executors.newSingleThreadExecutor().execute(() -> {
                User user = userDatabase.userDao().getUserByEmail(email);
                if (user != null && user.getPassword().equals(password)) {
                    // Credentials are correct, generate verification code
                    String verificationCode = generateVerificationCode();
                    sendVerificationEmail(email, verificationCode); // Send the verification code to email

                    // Pass verification code to VerificationActivity
                    Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                    intent.putExtra("code", verificationCode);
                    startActivity(intent);

                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void sendVerificationEmail(String email, String code) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        try {
            json.put("sender", new JSONObject().put("email", "agnesa.rama04@gmail.com").put("name", "2FA"));
            json.put("to", new JSONArray().put(new JSONObject().put("email", email)));
            json.put("subject", "Verification Code");
            json.put("htmlContent", "<html><body><h1>Your verification code is: " + code + "</h1></body></html>");
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(SERVER_URL)
                .post(body)
                .addHeader("api-key", API_KEY)
                .addHeader("Content-Type",
                        "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Verification code sent to your email", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Failed to send email", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        });
    }
}

