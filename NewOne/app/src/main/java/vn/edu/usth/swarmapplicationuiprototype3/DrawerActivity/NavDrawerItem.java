package vn.edu.usth.swarmapplicationuiprototype3.DrawerActivity;

public class NavDrawerItem {

    private String title;
    private int icon;
    private String count = "0";


    public NavDrawerItem() {
    }

    public NavDrawerItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public NavDrawerItem(String title, int icon, String count) {
        this.title = title;
        this.icon = icon;
        this.count = count;
    }

    public String getTitle() {
        return this.title;
    }

    public int getIcon() {
        return this.icon;
    }

    public String getCount() {
        return this.count;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setCount(String count) {
        this.count = count;
    }

}