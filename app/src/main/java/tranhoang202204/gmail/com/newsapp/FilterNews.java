package tranhoang202204.gmail.com.newsapp;

public enum FilterNews {
    TIN_MOI_NHAT("tin-moi-nhat"),
    THE_GIOI("the-gioi"),
    THOI_SU("thoi-su"),
    GIAI_TRI("giai-tri"),
    THE_THAO("the-thao"),
    PHAP_LUAT("phap-luat"),
    GIAO_DUC("giao-duc"),
    TIN_MOI_NHAT_UTF8("Tin mới nhất"),
    THE_GIOI_UTF8("Thế giới"),
    THOI_SU_UTF8("Thời sự"),
    GIAI_TRI_UTF8("Giải trí"),
    THE_THAO_UTF8("Thể thao"),
    PHAP_LUAT_UTF8("Pháp luật"),
    GIAO_DUC_UTF8("Giáo dục");
    private final String value;

    // Constructor để gán giá trị chuỗi
    FilterNews(String value) {
        this.value = value;
    }

    // Getter để lấy giá trị chuỗi
    public String getValue() {
        return value;
    }
}
