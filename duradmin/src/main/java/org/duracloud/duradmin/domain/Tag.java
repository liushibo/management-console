package org.duracloud.duradmin.domain;


public class Tag {
    private String spaceId;
    private String contentId;
    private String tag;
    
    public String getSpaceId() {
        return spaceId;
    }
    
    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }
    
    public String getContentId() {
        return contentId;
    }
    
    public void setContentId(String contentId) {
        this.contentId = contentId;
    }
    
    public String getTag() {
        return tag;
    }
    
    public void setTag(String tag) {
        this.tag = tag;
    }
}
