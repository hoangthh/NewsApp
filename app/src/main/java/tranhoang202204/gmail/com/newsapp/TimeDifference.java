package tranhoang202204.gmail.com.newsapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeDifference {
    public static String getTimeDifference(String inputDate) {
        // Định dạng ngày tháng theo định dạng mà bạn có
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
        try {
            // Chuyển chuỗi ngày thành đối tượng Date
            Date date = sdf.parse(inputDate);

            // Lấy thời gian hiện tại
            Date currentDate = new Date();

            // Tính toán sự chênh lệch giữa hai ngày
            long diffInMillis = currentDate.getTime() - date.getTime();

            // Tính số ngày, giờ, phút
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);

            // Hiển thị kết quả dựa trên sự chênh lệch
            if (diffInDays > 0) {
                return diffInDays + " ngày trước";
            } else if (diffInHours > 0) {
                return diffInHours + " giờ trước";
            } else {
                return diffInMinutes + " phút trước";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
