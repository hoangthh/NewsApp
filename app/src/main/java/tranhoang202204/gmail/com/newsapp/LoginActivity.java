package tranhoang202204.gmail.com.newsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    ImageButton btnBack;
    Button btnLoginWithGoogle, btnLogin;
    TextView btnRegister, btnForgotPassword;

    EditText edtEmail, edtPassword;

    private static final int RC_SIGN_IN = 100;
    private FirebaseHelper firebaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLoginWithGoogle = findViewById(R.id.btnLoginWithGoogle);
        btnBack = findViewById(R.id.btnBack);
        btnRegister = findViewById(R.id.btnRegister);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        firebaseHelper = new FirebaseHelper();
        firebaseHelper.initGoogleSignInClient(this);

        btnLoginWithGoogle.setOnClickListener(v -> signInWithGoogle());

        btnForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_in_activity, R.anim.anim_out_activity);
        });

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();

            if (email.equals("")) {
                Toast.makeText(this, "Email không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.equals("")) {
                Toast.makeText(this, "Mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseHelper.loginUser(email, password, new FirebaseHelper.LoginEmailCallback() {
                @Override
                public void onSuccess(String uid) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_in_activity, R.anim.anim_out_activity);
                    // Chuyển đến màn hình chính
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(LoginActivity.this, "" + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_in_activity, R.anim.anim_out_activity);
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    // Thực hiện đăng nhập Google
    private void signInWithGoogle() {
        Intent signInIntent = firebaseHelper.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Xử lý kết quả đăng nhập Google
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (data != null) {
                firebaseHelper.signInWithGoogle(data, this, new FirebaseHelper.LoginCallback() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        Toast.makeText(LoginActivity.this, "Chào mừng " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        // Chuyển đến màn hình chính sau khi đăng nhập
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        overridePendingTransition(R.anim.anim_in_activity, R.anim.anim_out_activity);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập không thành công, mã lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

}