package tranhoang202204.gmail.com.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import tranhoang202204.gmail.com.newsapp.databinding.ActivityMainBinding;

public class AdminHomeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    FirebaseHelper firebaseHelper;

    SearchView svSearch;

    RecyclerView recyclerViewNews;
    AdminNewsViewAdapter newsAdapter;

    List<News> newsList;

    SwipeRefreshLayout swipeRefreshLayout;

    ImageView imvEdit, imvDelete;
    FloatingActionButton fabAdd;

    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitMainActivity();

        if (NetworkUtils.isNetworkAvailable(this)) {
            // Có kết nối mạng: Lấy tin từ Firebase
            swipeRefreshLayout.setRefreshing(true);
            // Nếu không phải lịch sử, load dữ liệu mặc định (tin tức chính)
            LoadHomeData();

            Toast.makeText(this, "Internet connected", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Internet DISCONNECTED !!!", Toast.LENGTH_LONG).show();
        }

        // SearchView không bị thu gọn
        svSearch.setIconifiedByDefault(false);

        // Focus vào SearchView
        svSearch.requestFocus();
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Tìm kiếm khi người dùng nhấn Enter
                firebaseHelper.adminSearchNewsByTitle(query, newsList, newsAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Tìm kiếm khi người dùng thay đổi văn bản
                if (!newText.isEmpty()) {
                    firebaseHelper.adminSearchNewsByTitle(newText, newsList, newsAdapter);
                } else {
                    newsList.clear();
                    newsAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminAddNewsActivity.class);
            this.startActivity(intent);
            overridePendingTransition(R.anim.anim_in_activity, R.anim.anim_out_activity);
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void InitMainActivity(){
        // Khởi tạo lớp quản lí Firebase
        firebaseHelper = new FirebaseHelper();

        // Khởi tạo arrayList chứa news
        newsList = new ArrayList<>();

        //Ánh xạ RecyclerView News
        recyclerViewNews = findViewById(R.id.rvNews);

        // Ánh xạ EditText Search
        svSearch = findViewById(R.id.svSearch);

        fabAdd = findViewById(R.id.fabAdd);
        imvEdit = findViewById(R.id.ivEdit);
        imvDelete = findViewById(R.id.ivDelete);

        btnBack = findViewById(R.id.btnBack);

        // Anh xa SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Tạo và gán Adapter News
        newsAdapter = new AdminNewsViewAdapter(this, getBaseContext(), newsList);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerViewNews.setAdapter(newsAdapter);

    }

    private void LoadHomeData(){
        // Tải dữ liệu từ Firestore
        String rssUrl = "https://thethao247.vn/" + "trang-chu" + ".rss";
        new ReadRss("trang-chu", new RssReadListener() {
            @Override
            public void onRssReadComplete() {
                firebaseHelper.adminGetNews(newsList, newsAdapter);
                swipeRefreshLayout.setRefreshing(false);
//                firebaseHelper.getNewsWithPagination(newsList, newsAdapter, sqliteHelper, true, success -> {
//                    if (success) {
//                        Toast.makeText(MainActivity.this, "First loaded news from Firebase", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(MainActivity.this, "Failed to first load news", Toast.LENGTH_LONG).show();
//                    }
//                });
            }
        }).execute(rssUrl);
    }

    @Override
    public void onRefresh() {
        if (!newsList.isEmpty()){
            Toast.makeText(this, "Already have news", Toast.LENGTH_SHORT).show();
        }
        LoadHomeData();
    }
}