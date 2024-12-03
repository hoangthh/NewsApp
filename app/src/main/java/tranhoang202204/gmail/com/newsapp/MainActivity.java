package tranhoang202204.gmail.com.newsapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
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
    FirebaseFirestore db;

    TextView tvNewest, tvWorld, tvNews, tvEntertainment, tvSport, tvLaw, tvEducation;
    EditText edtSearch;
    RecyclerView recyclerView;
    NewsViewAdapter adapter;
    List<News> newsList = new ArrayList();
    List<TextView> filterNewsList = new ArrayList();

    ActivityMainBinding binding;
    LinearLayout linearLayoutLogo;
    HorizontalScrollView hsvFilter;

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

        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ các TextView
        tvNewest = findViewById(R.id.tvNewest);
        tvWorld = findViewById(R.id.tvWorld);
        tvNews = findViewById(R.id.tvNews);
        tvEntertainment = findViewById(R.id.tvEntertainment);
        tvSport = findViewById(R.id.tvSport);
        tvLaw = findViewById(R.id.tvLaw);
        tvEducation = findViewById(R.id.tvEducation);

        edtSearch = findViewById(R.id.edtSearch);

        //Ánh xạ RecyclerView
        recyclerView = findViewById(R.id.rvNews);
        adapter = new NewsViewAdapter(getBaseContext(), newsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setAdapter(adapter);

        //Ánh xạ
        linearLayoutLogo = findViewById(R.id.linearLayoutLogo);
        hsvFilter = findViewById(R.id.horizontalScrollView);

        // Gán sự kiện onClickListener cho từng TextView
        tvNewest.setOnClickListener(v -> fetchNews(FilterNews.TIN_MOI_NHAT, tvNewest));
        tvWorld.setOnClickListener(v -> fetchNews(FilterNews.THE_GIOI, tvWorld));
        tvNews.setOnClickListener(v -> fetchNews(FilterNews.THOI_SU, tvNews));
        tvEntertainment.setOnClickListener(v -> fetchNews(FilterNews.GIAI_TRI, tvEntertainment));
        tvSport.setOnClickListener(v -> fetchNews(FilterNews.THE_THAO, tvSport));
        tvLaw.setOnClickListener(v -> fetchNews(FilterNews.PHAP_LUAT, tvLaw));
        tvEducation.setOnClickListener(v -> fetchNews(FilterNews.GIAO_DUC, tvEducation));

        String rssUrl = "https://vnexpress.net/rss/" + FilterNews.TIN_MOI_NHAT.getValue() + ".rss";
        new ReadRss(FilterNews.TIN_MOI_NHAT).execute(rssUrl);

        db.collection("news").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    newsList.clear();
                    for(QueryDocumentSnapshot q : task.getResult()){
                        Map<String, Object> data = q.getData();
                        News news = new News((String) data.get("imageUrl")
                                , (String) data.get("title")
                                , (String) data.get("description")
                                , (String) data.get("tag")
                                , (String) data.get("date"));
                        newsList.add(news);
                    }
                    adapter.update(newsList);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "" + newsList.size(), Toast.LENGTH_SHORT).show();
                }
            }
        });
//        db.collection("news").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
//                if (snapshots != null) {
//                    newsList.clear();
//                    for (QueryDocumentSnapshot q : snapshots) {
//                        Map<String, Object> data = q.getData();
//                        News news = new News((String) data.get("imageUrl"), (String) data.get("title"), (String) data.get("date"));
//                        newsList.add(news);
//                    }
//                    adapter.update(newsList);
//                    adapter.notifyDataSetChanged();
//
//                    Toast.makeText(MainActivity.this, "" + newsList.size(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case 2131230937: //home
                    break;
                case 2131231247: //search
                    // Thay đổi chiều cao của LinearLayout về 0
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearLayoutLogo.getLayoutParams();
                    ViewGroup.LayoutParams hsvParams = (ViewGroup.LayoutParams) hsvFilter.getLayoutParams();
                    params.height = 0;
                    linearLayoutLogo.setLayoutParams(params);
                    hsvFilter.setLayoutParams(hsvParams);

                    edtSearch.setMinimumHeight(5);
                    break;
                case 2131231244: //mark
                    break;
                case 2131231248: //setting
                    break;
            }
            return false;
        });
    }

    // Hàm fetchNews để xử lý việc thay đổi tag và gọi API mới
    private void fetchNews(FilterNews filterNews, TextView selectedTextView) {
        String rssUrl = "https://vnexpress.net/rss/" + filterNews.getValue() + ".rss";
        new ReadRss(filterNews).execute(rssUrl);

        // Reset giao diện của tất cả TextView
        resetStyles();

        // Đặt giao diện được chọn cho TextView hiện tại
        selectedTextView.setBackgroundColor(getResources().getColor(android.R.color.black));
        selectedTextView.setTextColor(getResources().getColor(android.R.color.white));

        // Lấy giá trị tag từ enum
        String tag = filterNews.getValue();

        // Thực hiện truy vấn Firestore với tag đã chọn
        db.collection("news")
            .whereEqualTo("tag", tag)
            .get()  // Sử dụng tag để lọc dữ liệu
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    newsList.clear();
                    for(QueryDocumentSnapshot q : task.getResult()){
                        Map<String, Object> data = q.getData();
                        News news = new News((String) data.get("imageUrl")
                                , (String) data.get("title")
                                , (String) data.get("description")
                                , (String) data.get("tag")
                                , (String) data.get("date"));
                        newsList.add(news);
                    }
                    adapter.update(newsList);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "" + newsList.size(), Toast.LENGTH_SHORT).show();
                }
            }
        });

//        // Thực hiện truy vấn Firestore với tag đã chọn
//        db.collection("news")
//        .whereEqualTo("tag", tag)  // Sử dụng tag để lọc dữ liệu
//        .addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
//                if (snapshots != null) {
//                    newsList.clear();
//                    for (QueryDocumentSnapshot q : snapshots) {
//                        Map<String, Object> data = q.getData();
//                        News news = new News((String) data.get("imageUrl"), (String) data.get("title"), (String) data.get("date"));
//                        newsList.add(news);
//                    }
//                    adapter.update(newsList);
//                    adapter.notifyDataSetChanged();
//
//                    Toast.makeText(MainActivity.this, "" + newsList.size(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void resetStyles() {
        //Add các TextView vào 1 List
        filterNewsList.add(tvNewest);
        filterNewsList.add(tvWorld);
        filterNewsList.add(tvNews);
        filterNewsList.add(tvEntertainment);
        filterNewsList.add(tvSport);
        filterNewsList.add(tvLaw);
        filterNewsList.add(tvEducation);

        filterNewsList.forEach( tv -> {
            tv.setBackgroundColor(ContextCompat.getColor(this, R.color.ccc));
            tv.setTextColor(getResources().getColor(android.R.color.white));
        });
    }

    private class ReadRss extends AsyncTask<String, Void, String>{
        private FilterNews filterNews;

        public ReadRss(FilterNews filterNews) {
            this.filterNews = filterNews;
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder content = new StringBuilder();
            try {
                URL url = new URL(strings[0]);

                InputStreamReader inputStreamReader = new InputStreamReader(url.openConnection().getInputStream());

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line = "";

                while ((line = bufferedReader.readLine()) != null){
                    content.append(line);
                }

                bufferedReader.close();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return content.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            XMLDOMParser parser = new XMLDOMParser();

            Document document = parser.getDocument(s);

            NodeList nodeList = document.getElementsByTagName("item");

            NodeList nodeListDescription = document.getElementsByTagName("description");
            String imageUrl = "";
            String title = "";
            String description = "";
            String date = "";
            String detailLink = "";
            String tag = "";

            for (int i = 0; i < nodeList.getLength(); i++){
                Element element = (Element) nodeList.item(i);

                String cData = nodeListDescription.item(i + 1).getTextContent();

                Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
                Matcher matcher = p.matcher(cData);

                if (matcher.find()){
                    imageUrl = matcher.group(1);
                }
                // Biểu thức chính quy để loại bỏ thẻ HTML
                description = cData.replaceAll("<[^>]*>", "").trim();
                title = parser.getValue(element, "title");
                String dateRss = parser.getValue(element, "pubDate");
                date = TimeDifference.getTimeDifference(dateRss);
                detailLink = parser.getValue(element, "link");
                tag = filterNews.getValue();

                Map<String, Object> newsItem = new HashMap<>();
                newsItem.put("imageUrl", imageUrl);
                newsItem.put("title", title);
                newsItem.put("description", description);
                newsItem.put("detailLink", detailLink);
                newsItem.put("date", date);
                newsItem.put("tag", tag);
                // Add a new document with a generated ID
                db.collection("news")
                        .add(newsItem)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        }
    }
}

