package tranhoang202204.gmail.com.newsapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    ImageView imvAvatar, imvEdit;
    ImageButton btnBack;
    TextView tvName, tvEmail;
    FirebaseHelper firebaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseHelper = new FirebaseHelper();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        imvAvatar = findViewById(R.id.imvAvatar);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        imvEdit = findViewById(R.id.imvEditPassword);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        if (currentUser != null){
            tvName.setText(currentUser.getDisplayName());
            tvEmail.setText(currentUser.getEmail());

            // Tải ảnh đại diện của người dùng (nếu có) bằng Picasso
            if (currentUser.getPhotoUrl() != null) {
                Picasso.get().load(currentUser.getPhotoUrl()).into(imvAvatar);
            }

            // Lấy name từ Firestore
            firebaseHelper.fetchUserName(new FirebaseHelper.NameFetchCallback() {
                @Override
                public void onNameFetched(String name) {
                    tvName.setText(name != null ? name : "User Name Example");
                }
            });
        }

        imvEdit.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Bạn có chắc muốn đặt lại mật khẩu")
                    .setMessage("Thao tác này sẽ gửi yêu cầu đặt lại mật khẩu về email của bạn")
                    .setPositiveButton("Gửi yêu cầu", (dialog, which) -> {
                        firebaseHelper.resetPassword(currentUser.getEmail(), new FirebaseHelper.ResetPasswordCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(ProfileActivity.this, "Kiểm tra email để đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(ProfileActivity.this, "" + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }
}