package com.example.a2fa_class;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a2fa_class.R;

public class VerificationActivity extends AppCompatActivity {
    private EditText codeField;
    private Button verifyButton;
    private String sentCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        codeField = findViewById(R.id.codeField);
        verifyButton = findViewById(R.id.verifyBtn);

        sentCode = getIntent().getStringExtra("code");

        verifyButton.setOnClickListener(v -> {
            String enteredCodeStr = codeField.getText().toString();
            if (enteredCodeStr.isEmpty()) {
                Toast.makeText(this, "Please enter the verification code", Toast.LENGTH_SHORT).show();

            } else
            {
                if (enteredCodeStr.equals(sentCode)) {
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(this, "Code doesnt match", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}
