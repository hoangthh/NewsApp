package tranhoang202204.gmail.com.newsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText edtEmail;
    Button btnSendOtp;

    FirebaseHelper firebaseHelper;

    ImageButton btnBack;
    TextView btnLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseHelper = new FirebaseHelper();

        edtEmail = findViewById(R.id.edtEmail);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnBack = findViewById(R.id.btnBack);
        btnLogin = findViewById(R.id.btnLogin);

        btnBack.setOnClickListener(v -> finish());

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.anim_in_activity, R.anim.anim_out_activity);
        });

        btnSendOtp.setOnClickListener(v -> {
            String email = edtEmail.getText().toString();

            if (email.equals("")) {
                Toast.makeText(this, "Email không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            firebaseHelper.resetPassword(email, new FirebaseHelper.ResetPasswordCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ForgotPasswordActivity.this, "Kểm tra email để đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(ForgotPasswordActivity.this, "" + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}