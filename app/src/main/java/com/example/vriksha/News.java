package com.example.vriksha;

public class News {
    private String Title;
    private String ImageUrl;
    private String Description;
    private String Date;

    public News(String title, String imageUrl, String description, String date) {
        Title = title;
        ImageUrl = imageUrl;
        Description = description;
        Date = date;
    }

    public String getTitle() {
        return Title;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public String getDescription() {
        return Description;
    }

    public String getDate() {
        return Date;
    }

}

