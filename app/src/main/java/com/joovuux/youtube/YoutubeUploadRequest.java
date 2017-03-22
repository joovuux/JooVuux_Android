package com.joovuux.youtube;

import android.net.Uri;

public class YoutubeUploadRequest {

    private static final String DEFAULT_VIDEO_CATEGORY = "News";
    private static final String DEFAULT_VIDEO_TAGS = "mobile";

    private String title;
    private String strUri;
    private String description;
    private String category = DEFAULT_VIDEO_CATEGORY;
    private String tags = DEFAULT_VIDEO_TAGS;

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
    public String getStrUri() {
        return strUri;
    }
    public void setStrUri(String strUri) {
        this.strUri = strUri;
    }
    public Uri getUri() {
        return Uri.parse(strUri);
    }
    public void setUri(Uri uri) {
        this.strUri = uri.toString();
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}