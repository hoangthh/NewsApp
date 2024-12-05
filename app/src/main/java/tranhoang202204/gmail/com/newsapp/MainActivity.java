package tranhoang202204.gmail.com.newsapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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

public class MainActivity extends AppCompatActivity {
    FirebaseHelper firebaseHelper;
    Category category;

    SearchView svSearch;

    RecyclerView recyclerViewNews, recyclerViewCategory;
    CategoryViewAdapter categoryAdapter;
    NewsViewAdapter newsAdapter;

    List<News> newsList;

    ActivityMainBinding binding;
    LinearLayout linearLayoutLogo, lnlHome;

    TextView tvBookmark;
    ListenerRegistration bookmarkListener;

    Fragment settingFragment;

    @SuppressLint({"MissingInflatedId", "NonConstantResourceId"})
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

        // Khởi tạo lớp quản lí Firebase
        firebaseHelper = new FirebaseHelper();
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

        // Ánh xa lnlHome
        lnlHome = findViewById(R.id.lnlHome);

        //Khoi tao Setting Fragment
        settingFragment = new SettingFragment();

        // Tạo và gán Adapter News
        newsAdapter = new NewsViewAdapter(this, getBaseContext(), newsList);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerViewNews.setAdapter(newsAdapter);

        // Tạo và gán Adapter Category
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryViewAdapter(this, category.getCategoryList(), newsList, newsAdapter);
        recyclerViewCategory.setAdapter(categoryAdapter);

        // Tải dữ liệu từ Firestore
        String rssUrl = "https://thethao247.vn/" + "trang-chu" + ".rss";
        new ReadRss("trang-chu", new RssReadListener() {
            @Override
            public void onRssReadComplete() {
                firebaseHelper.getNews(newsList, newsAdapter);
            }
        }).execute(rssUrl);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case 2131230947: //home
                    // Hủy listener nếu đang ở Bookmark
                    if (bookmarkListener != null) {
                        bookmarkListener.remove();
                        bookmarkListener = null;
                    }

                    linearLayoutLogo.setVisibility(View.VISIBLE);
                    recyclerViewCategory.setVisibility(View.VISIBLE);
                    recyclerViewNews.setVisibility(View.VISIBLE);
                    svSearch.setVisibility(View.GONE);
                    tvBookmark.setVisibility(View.GONE);
                    RemoveFragment(settingFragment);

                    // Tải dữ liệu từ Firestore
                    firebaseHelper.getNews(newsList, newsAdapter);
                    return true;

                case 2131231124: //search
                    // Hủy listener nếu đang ở Bookmark
                    if (bookmarkListener != null) {
                        bookmarkListener.remove();
                        bookmarkListener = null;
                    }

                    // Tập trung vào item "search"
                    linearLayoutLogo.setVisibility(View.GONE);
                    recyclerViewCategory.setVisibility(View.GONE);
                    recyclerViewNews.setVisibility(View.VISIBLE);
                    svSearch.setVisibility(View.VISIBLE);
                    tvBookmark.setVisibility(View.GONE);
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
                    return true;

                case 2131230818: //mark
                    recyclerViewCategory.setVisibility(View.GONE);
                    recyclerViewNews.setVisibility(View.VISIBLE);
                    svSearch.setVisibility(View.GONE);
                    tvBookmark.setVisibility(View.VISIBLE);
                    RemoveFragment(settingFragment);

                    if (bookmarkListener == null) {
                        bookmarkListener = firebaseHelper.getBookmarkedNews(newsList, newsAdapter);
                    }

                    return true;
                case 2131231138: //setting
                    // Hủy listener nếu đang ở Bookmark
                    if (bookmarkListener != null) {
                        bookmarkListener.remove();
                        bookmarkListener = null;
                    }

                    recyclerViewCategory.setVisibility(View.GONE);
                    recyclerViewNews.setVisibility(View.GONE);
                    svSearch.setVisibility(View.GONE);
                    tvBookmark.setVisibility(View.GONE);
                    LoadFragment(settingFragment, false);

                    return true;
            }
            return false;
        });
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
}

