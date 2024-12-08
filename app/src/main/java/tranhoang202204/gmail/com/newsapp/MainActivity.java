package tranhoang202204.gmail.com.newsapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tranhoang202204.gmail.com.newsapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    FirebaseHelper firebaseHelper;
    Category category;

    SearchView svSearch;

    RecyclerView recyclerViewNews, recyclerViewCategory;
    CategoryViewAdapter categoryAdapter;
    NewsViewAdapter newsAdapter;

    List<News> newsList;

    ActivityMainBinding binding;
    LinearLayout linearLayoutLogo, lnlHome;

    TextView tvBookmark, tvHistory;
    ListenerRegistration bookmarkListener;

    Fragment settingFragment;

    FirebaseUser currentUser;

    SQLiteHelper sqliteHelper;

    SwipeRefreshLayout swipeRefreshLayout;

    String currentCategoryTag = null;

    @SuppressLint({"MissingInflatedId", "NonConstantResourceId", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitMainActivity();

        if (NetworkUtils.isNetworkAvailable(this)) {
            // Có kết nối mạng: Lấy tin từ Firebase và lưu vào SQLite

            // Nhận dữ liệu từ Intent
            String viewType = getIntent().getStringExtra("viewType");
            if ("history".equals(viewType)) {
                recyclerViewCategory.setVisibility(View.GONE);
                tvHistory.setVisibility(View.VISIBLE);

                binding.bottomNavigationView.setSelectedItemId(2131231138);

                firebaseHelper.getHistoryNews(newsList, newsAdapter); // Lấy lịch sử đọc
            } else if ("bookmark".equals(viewType)) {
                String id = currentUser.getUid();

                recyclerViewCategory.setVisibility(View.GONE);
                recyclerViewNews.setVisibility(View.VISIBLE);
                svSearch.setVisibility(View.GONE);
                tvBookmark.setVisibility(View.VISIBLE);
                tvHistory.setVisibility(View.GONE);
                RemoveFragment(settingFragment);

                // binding.bottomNavigationView.setSelectedItemId(2131230818);
                binding.bottomNavigationView.setSelectedItemId(R.id.bookmark);

                if (bookmarkListener == null) {
                    bookmarkListener = firebaseHelper.getBookmarkedNews(newsList, newsAdapter);
                }
            }
            else {
                swipeRefreshLayout.setRefreshing(true);
                sqliteHelper.deleteAllNews();
                // Nếu không phải lịch sử, load dữ liệu mặc định (tin tức chính)
                LoadHomeData();

            }
        } else {
            // Không có kết nối mạng: Lấy tin từ SQLite
            sqliteHelper.fetchNewsFromSQLite(newsList, newsAdapter);

            Toast.makeText(this, "Mất kết nối mạng", Toast.LENGTH_LONG).show();
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == 2131230953) { // home
                // Hủy listener nếu không ở Bookmark
                if (bookmarkListener != null) {
                    bookmarkListener.remove();
                    bookmarkListener = null;
                }
                HandleHome();
                return true;

            } else if (itemId == R.id.search) { // search
                // Hủy listener nếu không ở Bookmark
                if (bookmarkListener != null) {
                    bookmarkListener.remove();
                    bookmarkListener = null;
                }
                HandleSearch();
                return true;

            } else if (itemId == R.id.bookmark) { // mark
                if (currentUser == null) {
                    new AlertDialog.Builder(this)
                            .setTitle("Chưa đăng nhập")
                            .setMessage("Bạn cần đăng nhập để sử dụng tính năng này")
                            .setPositiveButton("Đăng nhập", (dialog, which) -> {
                                // Chuyển hướng đến LoginActivity
                                Intent loginIntent = new Intent(this, LoginActivity.class);
                                this.startActivity(loginIntent);
                            })
                            .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                            .show();
                    return false;
                } else {
                    String id = currentUser.getUid();

                    recyclerViewCategory.setVisibility(View.GONE);
                    recyclerViewNews.setVisibility(View.VISIBLE);
                    svSearch.setVisibility(View.GONE);
                    tvBookmark.setVisibility(View.VISIBLE);
                    tvHistory.setVisibility(View.GONE);
                    RemoveFragment(settingFragment);

                    if (bookmarkListener == null) {
                        bookmarkListener = firebaseHelper.getBookmarkedNews(newsList, newsAdapter);
                    }
                }
                return true;

            } else if (itemId == R.id.video) {
//                bookmarkListener.remove();
                HandleVideo();
                return true;
            } else if (itemId == R.id.setting) { // setting
                // Hủy listener nếu không ở Bookmark
                if (bookmarkListener != null) {
                    bookmarkListener.remove();
                    bookmarkListener = null;
                }
                HandleSetting();
                return true;

            } else {
                return false;
            }
            //return false;
        });

        // Cấu hình RecyclerView và sự kiện cuộn
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        // Thêm sự kiện cuộn để tải thêm tin khi cuộn đến cuối
        recyclerViewNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Tính toán vị trí cuộn
                int scrollExtent = recyclerView.computeVerticalScrollExtent();
                int scrollOffset = recyclerView.computeVerticalScrollOffset();
                int scrollRange = recyclerView.computeVerticalScrollRange();

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (scrollOffset + scrollExtent >= scrollRange - 100) {

                    // Thêm độ trễ 1 giây trước khi tải thêm dữ liệu
                    new android.os.Handler().postDelayed(() -> {
                        if (!currentCategoryTag.equals("trang-chu")) {
                            return;
                        }
                        // Nếu không, gọi getNewsWithPagination
                        firebaseHelper.getNewsWithPagination(newsList, newsAdapter, sqliteHelper, false, success -> {
                            swipeRefreshLayout.setRefreshing(false);
                        });
                    }, 3000); // Độ trễ 1 giây (1000ms)
                }
            }
        });
    }

    private void InitMainActivity(){
        // Khởi tạo lớp quản lí Firebase
        firebaseHelper = new FirebaseHelper();

        // Khởi tạo user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Khởi tạo lớp Category
        category = new Category();
        // Khởi tạo arrayList chứa news
        newsList = new ArrayList();

        //Ánh xạ RecyclerView News
        recyclerViewNews = findViewById(R.id.rvNews);

        // Ánh xạ RecyclerView Category
        recyclerViewCategory = findViewById(R.id.rvCategory);

        //Ánh xạ logo
        linearLayoutLogo = findViewById(R.id.linearLayoutLogo);

        // Ánh xạ EditText Search
        svSearch = findViewById(R.id.svSearch);

        // Ánh xa TextView Bookmark
        tvBookmark = findViewById(R.id.tvBookmark);

        // Ánh xạ TextView History
        tvHistory = findViewById(R.id.tvHistory);

        // Ánh xa lnlHome
        lnlHome = findViewById(R.id.lnlHome);

        // Khoi tao Setting Fragment
        settingFragment = new SettingFragment();

        // Khoi tao Sqlite
        sqliteHelper = new SQLiteHelper(this);

        // Anh xa SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Tạo và gán Adapter News
        newsAdapter = new NewsViewAdapter(this, getBaseContext(), newsList);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerViewNews.setAdapter(newsAdapter);

        currentCategoryTag = "trang-chu";
        // Tạo và gán Adapter Category
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryViewAdapter(this, category.getCategoryList(), newsList, newsAdapter,tag -> {
            // Khi danh mục được chọn
            currentCategoryTag = tag;
        });
        recyclerViewCategory.setAdapter(categoryAdapter);
    }

    private void LoadHomeData(){
        // Tải dữ liệu từ Firestore
        String rssUrl = "https://thethao247.vn/" + "trang-chu" + ".rss";
        new ReadRss("trang-chu", new RssReadListener() {
            @Override
            public void onRssReadComplete() {
                //firebaseHelper.getNewsAndAddNewsToSqlite(newsList, newsAdapter, sqliteHelper);
                //swipeRefreshLayout.setRefreshing(false);
                firebaseHelper.getNewsWithPagination(newsList, newsAdapter, sqliteHelper, true, success -> {
                    swipeRefreshLayout.setRefreshing(false);
                });
            }
        }).execute(rssUrl);
    }

    private void HandleHome(){
        linearLayoutLogo.setVisibility(View.VISIBLE);
        recyclerViewCategory.setVisibility(View.VISIBLE);
        recyclerViewNews.setVisibility(View.VISIBLE);
        svSearch.setVisibility(View.GONE);
        tvBookmark.setVisibility(View.GONE);
        tvHistory.setVisibility(View.GONE);
        RemoveFragment(settingFragment);

        // Tải dữ liệu từ Firestore
        //firebaseHelper.getNewsAndAddNewsToSqlite(newsList, newsAdapter, sqliteHelper);
        LoadHomeData();
    }

    private void HandleSearch(){
        // Tập trung vào item "search"
        linearLayoutLogo.setVisibility(View.GONE);
        recyclerViewCategory.setVisibility(View.GONE);
        recyclerViewNews.setVisibility(View.VISIBLE);
        svSearch.setVisibility(View.VISIBLE);
        tvBookmark.setVisibility(View.GONE);
        tvHistory.setVisibility(View.GONE);
        RemoveFragment(settingFragment);

        // SearchView không bị thu gọn
        svSearch.setIconifiedByDefault(false);

        newsList.clear();
        newsAdapter.notifyDataSetChanged();

        // Focus vào SearchView
        svSearch.requestFocus();
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Tìm kiếm khi người dùng nhấn Enter
                firebaseHelper.searchNewsByTitle(query, newsList, newsAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Tìm kiếm khi người dùng thay đổi văn bản
                if (!newText.isEmpty()) {
                    firebaseHelper.searchNewsByTitle(newText, newsList, newsAdapter);
                } else {
                    newsList.clear();
                    newsAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    private void HandleVideo() {
//        // Kiểm tra nếu người dùng chưa đăng nhập
//        if (currentUser == null) {
//            new AlertDialog.Builder(this)
//                    .setTitle("Chưa đăng nhập")
//                    .setMessage("Bạn cần đăng nhập để sử dụng tính năng này")
//                    .setPositiveButton("Đăng nhập", (dialog, which) -> {
//                        // Chuyển hướng đến LoginActivity
//                        Intent loginIntent = new Intent(this, LoginActivity.class);
//                        startActivity(loginIntent);
//                    })
//                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
//                    .show();
//            return;
//        }
        // Nếu người dùng đã đăng nhập, mở VideoShortActivity
        Intent intent = new Intent(MainActivity.this, VideoShortActivity.class);
        startActivity(intent);
    }

    private void HandleBookmark(){
        String id = currentUser.getUid();

        recyclerViewCategory.setVisibility(View.GONE);
        recyclerViewNews.setVisibility(View.VISIBLE);
        svSearch.setVisibility(View.GONE);
        tvBookmark.setVisibility(View.VISIBLE);
        tvHistory.setVisibility(View.GONE);
        RemoveFragment(settingFragment);

        if (bookmarkListener == null) {
            bookmarkListener = firebaseHelper.getBookmarkedNews(newsList, newsAdapter);
        }
    }

    private void HandleSetting(){
        recyclerViewCategory.setVisibility(View.GONE);
        recyclerViewNews.setVisibility(View.GONE);
        svSearch.setVisibility(View.GONE);
        tvBookmark.setVisibility(View.GONE);
        tvHistory.setVisibility(View.GONE);
        LoadFragment(settingFragment, false);
    }

    private void LoadFragment(Fragment fragment, boolean isAppInitialized){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized){
            fragmentTransaction.add(R.id.frameLayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }

        fragmentTransaction.commit();
    }

    private void RemoveFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(fragment);

        fragmentTransaction.commit();
    }

    @Override
    public void onRefresh() {
        if (!newsList.isEmpty()){
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (!currentCategoryTag.equals("trang-chu")) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        LoadHomeData();
    }
}

