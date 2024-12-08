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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

//    private void loadVideoData() {
//        firestore.collection("videos")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String title = document.getString("title");
//                            String url = document.getString("url");
//                            String description = document.getString("description");
//
//                            videoShortList.add(new VideoShort(url, title, description));
//                        }
//                        adapter.notifyDataSetChanged();
//                    } else {
//                        Log.e("Firestore", "Error getting documents: ", task.getException());
//                    }
//                });
//    }

    private void loadVideoData() {
        firestore.collection("videos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<VideoShort> tempVideoList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String url = document.getString("url");
                            String description = document.getString("description");

                            VideoShort video = new VideoShort(url, title, description);
                            tempVideoList.add(video);
                        }

                        // Đảm bảo tải video đầu tiên trước khi cập nhật giao diện
                        if (!tempVideoList.isEmpty()) {
                            downloadVideoToCache(tempVideoList.get(0), () -> {
                                videoShortList.addAll(tempVideoList);
                                adapter.notifyDataSetChanged();
                            });
                        }
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }


    // Hàm tải video về cache
    private void downloadVideoToCache(VideoShort videoShort, Runnable onComplete) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(videoShort.getVideoUrl()).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("VideoDownload", "Download failed: " + e.getMessage());
                if (onComplete != null) {
                    runOnUiThread(onComplete);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    File cacheFile = new File(getCacheDir(), videoShort.getFileName());
                    try (FileOutputStream fos = new FileOutputStream(cacheFile)) {
                        fos.write(response.body().bytes());
                        Log.d("VideoDownload", "Video cached: " + cacheFile.getAbsolutePath());
                    }
                } else {
                    Log.e("VideoDownload", "Download failed with response: " + response.message());
                }

                if (onComplete != null) {
                    runOnUiThread(onComplete);
                }
            }
        });
    }

    private void downloadAllVideosToCache(List<VideoShort> videoList, Runnable onComplete) {
        OkHttpClient client = new OkHttpClient();
        int[] pendingCount = {videoList.size()};

        for (VideoShort video : videoList) {
            Request request = new Request.Builder().url(video.getVideoUrl()).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("VideoDownload", "Failed: " + e.getMessage());
                    checkCompletion();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        File cacheFile = new File(getCacheDir(), video.getFileName());
                        try (FileOutputStream fos = new FileOutputStream(cacheFile)) {
                            fos.write(response.body().bytes());
                        }
                    }
                    checkCompletion();
                }

                private void checkCompletion() {
                    synchronized (pendingCount) {
                        pendingCount[0]--;
                        if (pendingCount[0] == 0 && onComplete != null) {
                            runOnUiThread(onComplete);
                        }
                    }
                }
            });
        }
    }


}