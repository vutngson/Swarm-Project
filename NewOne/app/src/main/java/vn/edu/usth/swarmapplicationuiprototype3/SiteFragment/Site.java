package vn.edu.usth.swarmapplicationuiprototype3.SiteFragment;

public class Site {
    private String siteID;
    private String numberAndStreet;
    private String District;

    public Site() {
    }

    public Site(String numberAndStreet, String district) {
        District = district;
        this.numberAndStreet = numberAndStreet;
    }

    public Site(String district, String siteID, String numberAndStreet) {
        District = district;
        this.siteID = siteID;
        this.numberAndStreet = numberAndStreet;
    }

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public String getNumberAndStreet() {
        return numberAndStreet;
    }

    public void setNumberAndStreet(String numberAndStreet) {
        this.numberAndStreet = numberAndStreet;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }
}
