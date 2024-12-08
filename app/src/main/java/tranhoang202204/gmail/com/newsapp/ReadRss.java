package tranhoang202204.gmail.com.newsapp;

import static android.content.ContentValues.TAG;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadRss extends AsyncTask<String, Void, String> {
    String tag;
    String imageUrl = "";
    String title = "";
    String description = "";
    String date = "";
    String link = "";
    String bookmark;
    private RssReadListener listener;

    public ReadRss(String tag, RssReadListener listener) {
        this.tag = tag;
        this.listener = listener;
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

        // Xử lí lỗi đọc Rss
        if (s == null || s.isEmpty()) {
            Log.e(TAG, "Failed to fetch RSS data.");
            if (listener != null) listener.onRssReadComplete();
            return;
        }

        XMLDOMParser parser = new XMLDOMParser();

        Document document = parser.getDocument(s);

        NodeList nodeList = document.getElementsByTagName("item");
        NodeList nodeListTitle = document.getElementsByTagName("title");
        NodeList nodeListDescription = document.getElementsByTagName("description");

        int totalItems = nodeList.getLength();  // Tổng số mục tin tức
        final int[] processedItems = {0};  // Biến đếm số lượng mục tin tức đã được xử lý

        for (int i = 0; i < nodeList.getLength(); i++){
            Element element = (Element) nodeList.item(i);

            // Lấy nội dung CDATA từ thẻ title
            String titleCDATA = nodeListTitle.item(i + 1).getTextContent().trim();
            String descriptionCDATA = nodeListDescription.item(i + 1).getTextContent();

            Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
            Matcher matcher = p.matcher(descriptionCDATA);

            if (matcher.find()){
                imageUrl = matcher.group(1);
            }

            // Biểu thức chính quy để loại bỏ thẻ HTML
            description = descriptionCDATA.replaceAll("<[^>]*>", "").trim();
            title = titleCDATA;
            date = parser.getValue(element, "pubDate");
            link = parser.getValue(element, "link");
            tag = this.tag;
            bookmark = "false";

            Map<String, Object> newsItem = new HashMap<>();
            newsItem.put("imageUrl", imageUrl);
            newsItem.put("title", title);
            newsItem.put("description", description);
            newsItem.put("link", link);
            newsItem.put("date", date);
            newsItem.put("tag", tag);
            newsItem.put("bookmark", bookmark);

            new FirebaseHelper().addNews(newsItem, new NewsAddListener() {
                @Override
                public void onNewsAddComplete() {
                    // Sau khi hoàn tất thêm tin, gọi lại phương thức trong MainActivity
                    if (listener != null) {
                        listener.onRssReadComplete();
                    }
                }
            });

        }
        // Gọi callback khi hoàn thành
        if (listener != null) {
            listener.onRssReadComplete();
        }
    }
}


