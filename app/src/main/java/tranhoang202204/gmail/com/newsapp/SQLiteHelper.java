package tranhoang202204.gmail.com.newsapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "news.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NEWS = "news";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE_URL = "imageUrl";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_LINK = "link";
    private static final String COLUMN_TAG = "tag";
    private static final String COLUMN_BOOKMARK = "bookmark";

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create, Update, Delete
    public void QueryData(String query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
    }

    // Select
    public Cursor GetData(String query) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(query, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NEWS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT UNIQUE, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_IMAGE_URL + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_LINK + " TEXT, " +
                COLUMN_TAG + " TEXT, " +
                COLUMN_BOOKMARK + " TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        onCreate(sqLiteDatabase);
    }

    // Thêm tin tức mới
    public void addNews(Map<String, String> newsData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, newsData.get("title"));
        values.put(COLUMN_DESCRIPTION, newsData.get("description"));
        values.put(COLUMN_IMAGE_URL, newsData.get("imageUrl"));
        values.put(COLUMN_DATE, newsData.get("date"));
        values.put(COLUMN_LINK, newsData.get("link"));
        values.put(COLUMN_TAG, newsData.get("tag"));
        values.put(COLUMN_BOOKMARK, newsData.get("bookmark"));

        db.insert(TABLE_NEWS, null, values);
        db.close();
    }

    // Lấy tất cả tin tức
    public Cursor getAllNews() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NEWS, null);
    }

    public Cursor getNewsByTag(String tag) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NEWS + " WHERE " + COLUMN_TAG + " = ?";
        return db.rawQuery(query, new String[]{tag});
    }

    @SuppressLint("Range")
    public Map<String, String> mapCursorToNewsData(Cursor cursor) {
        Map<String, String> newsData = new HashMap<>();
        newsData.put("title", cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
        newsData.put("description", cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
        newsData.put("date", cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
        newsData.put("imageUrl", cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL)));
        newsData.put("link", cursor.getString(cursor.getColumnIndex(COLUMN_LINK)));
        newsData.put("tag", cursor.getString(cursor.getColumnIndex(COLUMN_TAG)));
        newsData.put("bookmark", cursor.getString(cursor.getColumnIndex(COLUMN_BOOKMARK)));

        return newsData;
    }


    // Kiểm tra tin tức có tồn tại không
    public boolean isNewsExists(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM news WHERE title = ?";
        Cursor cursor = db.rawQuery(query, new String[]{title});

        boolean exists = cursor.moveToFirst(); // Nếu có dữ liệu thì tiêu đề đã tồn tại
        cursor.close();
        return exists;
    }

    public void fetchNewsFromSQLite(List<News> newsList, NewsViewAdapter newsAdapter) {
        Cursor cursor = this.getAllNews();
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> newsData = this.mapCursorToNewsData(cursor);

                // Tạo đối tượng News từ dữ liệu SQLite
                News news = new News(
                        newsData.get("id"),
                        newsData.get("imageUrl"),
                        newsData.get("title"),
                        newsData.get("description"),
                        newsData.get("tag"),
                        newsData.get("date"),
                        newsData.get("bookmark"),
                        newsData.get("link")
                );

                // Thêm tin vào danh sách hiển thị
                newsList.add(news);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Cập nhật RecyclerView
        newsAdapter.update(newsList);
        newsAdapter.notifyDataSetChanged();
    }

    public void fetchNewsByTagFromSQLite(String tag, List<News> newsList, NewsViewAdapter newsAdapter) {
        // Lấy dữ liệu từ SQLite theo tag
        Cursor cursor = this.getNewsByTag(tag);
        if (cursor.moveToFirst()) {
            newsList.clear();  // Xóa danh sách tin tức cũ

            do {
                // Chuyển đổi dữ liệu từ Cursor thành Map<String, String>
                Map<String, String> newsData = this.mapCursorToNewsData(cursor);

                // Tạo đối tượng News từ dữ liệu
                News news = new News(
                        newsData.get("id"),
                        newsData.get("imageUrl"),
                        newsData.get("title"),
                        newsData.get("description"),
                        newsData.get("tag"),
                        newsData.get("date"),
                        newsData.get("bookmark"),
                        newsData.get("link")
                );

                // Thêm tin vào danh sách hiển thị
                newsList.add(news);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Cập nhật RecyclerView với dữ liệu mới
        newsAdapter.update(newsList);
        newsAdapter.notifyDataSetChanged();
    }

    // Hàm xóa tất cả dữ liệu trong bảng news
    public void deleteAllNews() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NEWS); // Xóa tất cả dữ liệu trong bảng news
        db.close();
    }
}
