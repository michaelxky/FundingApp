package Program.Tech;

public class showProfileAct {
    private String title;
    private String description;

    public showProfileAct(String title, String description) {
        this.title = title;
        this.description = description;
    }

//    public showProfileAct(String title) {
//        this.title = title;
//    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
