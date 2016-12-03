package com.m01000073.giovanni.myevents;


public class Blog {

    private String id;
    private String userID;
    private String user;
    private String tag;
    private String image;
    private String date_time;
    private String map;
    private String desc;
    private Double latitude;
    private Double longitude;
    private long temp;
    private int visualizzato;
    private long Public;
    private long PublicTemp;

    public Blog(){

    }

    public Blog(String id, String userID, String user, String tag, String image, String date_time, String map, String desc, Double latitude, Double longitude, long temp, int visualizzato, long Public, long PublicTemp) {
        this.userID = userID;
        this.user = user;
        this.tag = tag;
        this.image = image;
        this.date_time =  date_time;
        this.map =  map;
        this.desc = desc;
        this.latitude = latitude;
        this.longitude = longitude;
        this.temp = temp;
        this.visualizzato = visualizzato;
        this.Public = Public;
        this.PublicTemp = PublicTemp;
    }

    public String getId() {return id;}

    public void setId(String id){
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setImage(String image){
        this.image = image;
    }
    public String getImage() {
        return image;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public long getTemp() {
        return temp;
    }

    public void setTemp(long temp) {
        this.temp = temp;
    }


    public int getVisualizzato() {
        return visualizzato;
    }

    public void setVisualizzato(int visualizzato) {
        this.visualizzato = visualizzato;
    }


    public long getPublic() {
        return Public;
    }

    public void setPublic(long aPublic) {
        Public = aPublic;
    }

    public long getPublicTemp() {
        return PublicTemp;
    }

    public void setPublicTemp(long publicTemp) {
        PublicTemp = publicTemp;
    }


}
