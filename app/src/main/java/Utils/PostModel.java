package Utils;

public class PostModel {
    private String uid, imageUrl, postTitle, postDescription, postId, timestamp, likes, userName;

    public PostModel() {
    }

    public PostModel(String uid, String imageUrl, String postTitle, String postDescription, String postId, String timestamp, String likes, String userName) {
        this.uid = uid;
        this.imageUrl = imageUrl;
        this.postTitle = postTitle;
        this.postDescription = postDescription;
        this.postId = postId;
        this.timestamp = timestamp;
        this.likes = likes;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }
}
