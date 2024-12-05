package tranhoang202204.gmail.com.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
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
    Button btnLoginWithGoogle;

    private static final int RC_SIGN_IN = 100;
    private FirebaseHelper firebaseHelper;

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

        firebaseHelper = new FirebaseHelper();
        firebaseHelper.initGoogleSignInClient(this);

        btnLoginWithGoogle.setOnClickListener(v -> signInWithGoogle());

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
                        Toast.makeText(LoginActivity.this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        Log.d("LoginActivity", "User logged in: " + user.getDisplayName());
                        // Chuyển đến màn hình chính sau khi đăng nhập
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(LoginActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("LoginActivity", "Error logging in", e);
                    }
                });
            } else {
                Log.e("LoginActivity", "onActivityResult data is null");
                Toast.makeText(LoginActivity.this, "Login Failed: No data received", Toast.LENGTH_SHORT).show();
            }
        }
    }

}