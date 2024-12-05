package tranhoang202204.gmail.com.newsapp;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";
    private final FirebaseFirestore db;

    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;

    public FirebaseHelper() {
        this.db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();  // Khởi tạo FirebaseAuth
    }

    // Khởi tạo GoogleSignInClient
    public void initGoogleSignInClient(Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id)) // ID Web Client từ Firebase Console
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    // Lấy GoogleSignInClient
    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    // Xử lý đăng nhập Google
    public void signInWithGoogle(Intent data, Activity activity, LoginCallback callback) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(Exception.class);
            if (account != null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(activity, authTask -> {
                            if (authTask.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    saveUserToFirestore(user);
                                    callback.onSuccess(user);
                                }
                            } else {
                                callback.onFailure(authTask.getException());
                            }
                        });
            }
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    // Lưu thông tin người dùng vào Firestore
    private void saveUserToFirestore(FirebaseUser user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user.getDisplayName());
        userData.put("email", user.getEmail());
        userData.put("photoUrl", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
        userData.put("uid", user.getUid());

        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User data saved successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving user data", e));
    }

    // Đăng xuất Google
    public void signOut(Activity activity, SignOutCallback callback) {
        auth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(activity, task -> callback.onSignOutComplete());
    }

    // Giao diện Callback để xử lý kết quả đăng nhập
    public interface LoginCallback {
        void onSuccess(FirebaseUser user);

        void onFailure(Exception e);
    }

    public interface SignOutCallback {
        void onSignOutComplete();
    }

    // Thêm dữ liệu
    public void addNews(Map<String, Object> newsData) {
        String title = (String) newsData.get("title"); // Lấy giá trị của title từ newsData

        // Kiểm tra xem title đã tồn tại trong Firestore chưa
        db.collection("news")
                .whereEqualTo("title", title)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Nếu title đã tồn tại
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "Title already exists, not adding new news.");
                        } else {
                            // Nếu title chưa tồn tại, thêm tin tức mới
                            db.collection("news")
                                    .add(newsData)
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error checking for title", e);
                    }
                });
    }


    // Lấy dữ liệu
    public void getNews(List<News> newsList, NewsViewAdapter newsAdapter) {
        db.collection("news").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    newsList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> data = document.getData();
                        News news = new News(
                                (String) data.get("imageUrl"),
                                (String) data.get("title"),
                                (String) data.get("description"),
                                (String) data.get("tag"),
                                (String) data.get("date"),
                                (String) data.get("bookmark"),
                                (String) data.get("link")
                        );
                        newsList.add(news);
                    }
                    newsAdapter.update(newsList);
                    newsAdapter.notifyDataSetChanged();
                } else {
                    Log.e("MainActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    // Hàm fetchNews để xử lý việc thay đổi tag và gọi API mới
    public void fetchNews(String tag, List<News> newsList, NewsViewAdapter newsAdapter) {
        String rssUrl = "https://thethao247.vn/" + tag + ".rss";
        new ReadRss(tag, new RssReadListener() {
            @Override
            public void onRssReadComplete() {
                // Thực hiện truy vấn Firestore với tag đã chọn
                db.collection("news")
                        .whereEqualTo("tag", tag)
                        .get()  // Sử dụng tag để lọc dữ liệu
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                newsList.clear();
                                for (QueryDocumentSnapshot q : task.getResult()) {
                                    Map<String, Object> data = q.getData();
                                    News news = new News((String) data.get("imageUrl")
                                            , (String) data.get("title")
                                            , (String) data.get("description")
                                            , (String) data.get("tag")
                                            , (String) data.get("date")
                                            , (String) data.get("bookmark")
                                            , (String) data.get("link"));
                                    newsList.add(news);
                                }
                                newsAdapter.update(newsList);
                                newsAdapter.notifyDataSetChanged();
                            }
                        });
            }
        }).execute(rssUrl);
    }

    public void searchNewsByTitle(String query, List<News> newsList, NewsViewAdapter newsAdapter) {
        // Tìm kiếm trong Firestore với tiêu đề chứa từ khóa query
        db.collection("news")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            newsList.clear();  // Xóa danh sách hiện tại
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                String title = (String) data.get("title");

                                // Kiểm tra nếu tiêu đề chứa từ khóa (keyword)
                                if (title != null && title.toLowerCase().contains(query.toLowerCase())) {
                                    News news = new News(
                                            (String) data.get("imageUrl"),
                                            title,
                                            (String) data.get("description"),
                                            (String) data.get("tag"),
                                            (String) data.get("date"),
                                            (String) data.get("bookmark"),
                                            (String) data.get("link")
                                    );
                                    newsList.add(news);
                                }
                            }
                            newsAdapter.update(newsList);  // Cập nhật adapter
                            newsAdapter.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void updateBookmark(String title, String bookmarkStatus){
        db.collection("news")
                .whereEqualTo("title", title)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Lấy document ID từ kết quả truy vấn
                        String documentId = task.getResult().getDocuments().get(0).getId();

                        // Cập nhật trường bookmark
                        db.collection("news")
                                .document(documentId)
                                .update("bookmark", bookmarkStatus)
                                .addOnSuccessListener(aVoid -> {

                                    Log.d(TAG, "Bookmark status updated successfully for title: " + title);
                                });
                    }
                });
    }

    public ListenerRegistration getBookmarkedNews(List<News> newsList, NewsViewAdapter newsAdapter) {
        return db.collection("news")
                .whereEqualTo("bookmark", "true")
                .addSnapshotListener((value, error) -> {
                    newsList.clear();
                    if (value != null && !value.isEmpty()) {
                        newsList.clear();
                        for (QueryDocumentSnapshot document : value) {
                            Map<String, Object> data = document.getData();
                            News news = new News(
                                    (String) data.get("imageUrl"),
                                    (String) data.get("title"),
                                    (String) data.get("description"),
                                    (String) data.get("tag"),
                                    (String) data.get("date"),
                                    (String) data.get("bookmark"),
                                    (String) data.get("link")
                            );
                            newsList.add(news);
                        }
                        newsAdapter.update(newsList);
                    }
                    newsAdapter.notifyDataSetChanged();
                });
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        newsList.clear();
//                        if (!task.getResult().isEmpty()) { // Kiểm tra nếu có tài liệu
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Map<String, Object> data = document.getData();
//                                News news = new News(
//                                        (String) data.get("imageUrl"),
//                                        (String) data.get("title"),
//                                        (String) data.get("description"),
//                                        (String) data.get("tag"),
//                                        (String) data.get("date"),
//                                        (String) data.get("bookmark"),
//                                        (String) data.get("link")
//                                );
//                                newsList.add(news);
//                            }
//                            newsAdapter.update(newsList);
//                        }
//                        newsAdapter.notifyDataSetChanged();
//                    }
//                });
    }

}
