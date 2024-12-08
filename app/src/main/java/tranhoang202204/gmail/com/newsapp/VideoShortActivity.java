package tranhoang202204.gmail.com.newsapp;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class VideoShortActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private List<VideoShort> videoShortList;
    private VideoShortAdapter adapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_short);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        videoShortList = new ArrayList<>();
//        viewPager2 = findViewById(R.id.viewPage2);
//        videoShortList.add(new VideoShort("android.resource://" + getPackageName() + "/" + R.raw.a, "New Title", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. In dignissimos repellendus exercitationem, incidunt consequatur tempore quibusdam fugit distinctio ducimus excepturi illo labore, fugiat harum. Vel officiis rerum repudiandae fugit minima."));
//        videoShortList.add(new VideoShort("android.resource://" + getPackageName() + "/" + R.raw.b, "New Title", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. In dignissimos repellendus exercitationem, incidunt consequatur tempore quibusdam fugit distinctio ducimus excepturi illo labore, fugiat harum. Vel officiis rerum repudiandae fugit minima."));
//        videoShortList.add(new VideoShort("android.resource://" + getPackageName() + "/" + R.raw.c, "New Title", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. In dignissimos repellendus exercitationem, incidunt consequatur tempore quibusdam fugit distinctio ducimus excepturi illo labore, fugiat harum. Vel officiis rerum repudiandae fugit minima."));
//        videoShortList.add(new VideoShort("android.resource://" + getPackageName() + "/" + R.raw.d, "New Title", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. In dignissimos repellendus exercitationem, incidunt consequatur tempore quibusdam fugit distinctio ducimus excepturi illo labore, fugiat harum. Vel officiis rerum repudiandae fugit minima."));

        firestore = FirebaseFirestore.getInstance();
        videoShortList = new ArrayList<>();
        viewPager2 = findViewById(R.id.viewPage2);

        // Lấy dữ liệu từ Firestore
        loadVideoData();

        adapter = new VideoShortAdapter(videoShortList);
        viewPager2.setAdapter(adapter);
    }

    private void loadVideoData() {
        firestore.collection("videos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String url = document.getString("url");
                            String description = document.getString("description");

                            videoShortList.add(new VideoShort(url, title, description));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }
}