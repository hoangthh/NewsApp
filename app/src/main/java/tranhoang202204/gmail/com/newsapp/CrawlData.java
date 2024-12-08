package tranhoang202204.gmail.com.newsapp;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class CrawlData extends AsyncTask<String, Void, String> {
    public Context context;

    public CrawlData(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            // Kết nối và tải trang web
            Document document = Jsoup.connect(urls[0]).get();

            // Tìm thẻ <h1>
            Element h1 = document.select("h1").first();

            if (h1 != null) {
                return h1.text(); // Trả về nội dung thẻ <h1>
            } else {
                return "No H1 tag found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Hiển thị kết quả lên UI
        Toast.makeText(context, "" + result, Toast.LENGTH_SHORT).show();
    }
}
