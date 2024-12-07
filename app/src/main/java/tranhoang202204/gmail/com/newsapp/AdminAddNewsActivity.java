package tranhoang202204.gmail.com.newsapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AdminAddNewsActivity extends AppCompatActivity {
    TextView tvPage;
    ImageView imvImage;
    EditText edtImageUrl, edtTitle, edtDescription, edtContent;
    Button btnCreate;
    ImageButton btnBack;

    String imageUrl;
    String title;
    String description;
    String content;
    String tag;
    String date;

    News currentNews;
    Map<String, Object> newsItem;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_add_news);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitCreate();

        // Nhận dữ liệu từ Intent
        currentNews = (News) getIntent().getSerializableExtra("newsItem");
        if (currentNews != null){
            imageUrl = currentNews.getImageUrl();
            title = currentNews.getTitle();
            description = currentNews.getDescription();
            content = currentNews.getContent();

            tvPage.setText("Chỉnh sửa tin");
            edtImageUrl.setText(imageUrl);
            edtTitle.setText(title);
            edtDescription.setText(description);
            edtContent.setText(content);
            Picasso.get().load(imageUrl).into(imvImage);
            btnCreate.setText("Chỉnh sửa");
        }

        edtImageUrl.setOnFocusChangeListener((v, focus) -> {
            if (!focus && edtImageUrl != null){
                Picasso.get().load(edtImageUrl.getText().toString()).into(imvImage);
            }
        });

        btnCreate.setOnClickListener(v -> {
            imageUrl = edtImageUrl.getText().toString();
            title = edtTitle.getText().toString();
            description = edtDescription.getText().toString();
            content = edtContent.getText().toString();
            tag = "trang-chu";

            if (imageUrl.isEmpty()) {
                Toast.makeText(this, "Link ảnh bìa không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            if (title.isEmpty()) {
                Toast.makeText(this, "Tiêu đề không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            if (description.isEmpty()) {
                Toast.makeText(this, "Mô tả không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            if (content.isEmpty()) {
                Toast.makeText(this, "Nội dung không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hiển thị AlertDialog xác nhận
            String action = btnCreate.getText().toString(); // Lấy tên hành động (Tạo tin hoặc Chỉnh sửa)
            new AlertDialog.Builder(this)
                    .setTitle(action.equals("Tạo tin") ? "Xác nhận tạo tin tức mới" : "Xác nhận chỉnh sửa tin tức")
                    .setMessage("Bạn có chắc chắn muốn " + action.toLowerCase() + " không?")
                    .setPositiveButton("Xác nhận", (dialog, which) -> {
                        // Thực hiện hành động sau khi xác nhận
                        // Lấy thời gian hiện tại và chuyển sang định dạng yêu cầu
                        HandleDate();

                        newsItem = new HashMap<>();
                        newsItem.put("imageUrl", imageUrl);
                        newsItem.put("title", title);
                        newsItem.put("description", description);
                        newsItem.put("link", "");
                        newsItem.put("date", date);
                        newsItem.put("tag", tag);
                        newsItem.put("bookmark", "false");
                        newsItem.put("content", content);

                        if (btnCreate.getText().toString().equals("Tạo tin")){
                            ExecuteAddNews();
                        } else if (btnCreate.getText().toString().equals("Chỉnh sửa")){
                            ExecuteUpdateNews();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void ExecuteUpdateNews() {
        new FirebaseHelper().updateNews(currentNews.getId(), newsItem, isSuccess -> {
            if (isSuccess) {
                Intent intent = new Intent(this, AdminHomeActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Chỉnh sửa tin tức thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Chỉnh sửa tin tức thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ExecuteAddNews() {
        new FirebaseHelper().addNews(newsItem,  new NewsAddListener(){
            @Override
            public void onNewsAddComplete() {
                if (btnCreate.getText().toString().equals("Tạo tin")){
                    Intent intent = new Intent(AdminAddNewsActivity.this, AdminHomeActivity.class);
                    startActivity(intent);
                    Toast.makeText(AdminAddNewsActivity.this, "Tạo tin tức mới thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminAddNewsActivity.this, "Tạo tin tức mới thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void HandleDate() {
        long currentTimeMillis = System.currentTimeMillis(); // Thời gian dạng epoch
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH); // Định dạng yêu cầu
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")); // Thiết lập múi giờ
        String dateBefore = sdf.format(new Date(currentTimeMillis)); // Kết quả định dạng
        date = TimeDifference.getTimeDifference(dateBefore);
    }

    private void InitCreate() {
        tvPage = findViewById(R.id.tvPage);

        edtImageUrl = findViewById(R.id.edtImageUrl);
        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtContent = findViewById(R.id.edtContent);

        imvImage = findViewById(R.id.imvImage);
        btnCreate = findViewById(R.id.btnCreate);

        btnBack = findViewById(R.id.btnBack);
    }
}