package tranhoang202204.gmail.com.newsapp;

import java.util.ArrayList;
import java.util.List;

public class Category {
    List<String> categoryList;

    public Category() {
        categoryList = new ArrayList<>();
        categoryList.add("Tin mới nhất");
        categoryList.add("Bóng đá");
        categoryList.add("Bóng rổ");
        categoryList.add("Bóng chuyền");
        categoryList.add("Cầu lông");
        categoryList.add("Tennis");
    }

    public List<String> getCategoryList(){
        return categoryList;
    }

    // Hàm này trả về tag tương ứng với từng category
    public String getTagForCategory(String category) {
        switch (category) {
            case "Trang chủ":
                return "trang-chu";
            case "Bóng đá":
                return "bong-da";
            case "Bóng rổ":
                return "bong-ro-c43";
            case "Bóng chuyền":
                return "bong-chuyen-c45";
            case "Cầu lông":
                return "cau-long-c44";
            case "Tennis":
                return "quan-vot-tennis-c4";
            // Thêm các category và tag khác ở đây nếu cần
            default:
                return "trang-chu";
        }
    }

    public String getCategoryForTag(String tag) {
        switch (tag) {
            case "bong-da":
                return "Bóng đá";
            case "bong-ro-c43":
                return "Bóng rổ";
            case "bong-chuyen-c45":
                return "Bóng chuyền";
            case "cau-long-c44":
                return "Cầu lông";
            case "quan-vot-tennis-c4":
                return "Tennis";
            // Thêm các tag và category khác nếu cần
            default:
                return "Mới nhất";
        }
    }
}
