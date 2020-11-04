package Utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.HashMap;

public class PostModel implements Serializable {
    private String uid, imageUrl, postTitle, postDescription, postId, likes, userName, commentCnt, profileUrl;
    private HashMap<String, String> comments;
    private String postTime;

    public PostModel() {
    }

    public PostModel(String uid, String imageUrl, String postTitle, String postDescription, String postId, String likes, String userName, String commentCnt, String profileUrl, HashMap<String, String> comments, String postTime) {
        this.uid = uid;
        this.imageUrl = imageUrl;
        this.postTitle = postTitle;
        this.postDescription = postDescription;
        this.postId = postId;
        this.likes = likes;
        this.userName = userName;
        this.commentCnt = commentCnt;
        this.profileUrl = profileUrl;
        this.comments = comments;
        this.postTime = postTime;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getCommentCnt() {
        return commentCnt;
    }

    public void setCommentCnt(String commentCnt) {
        this.commentCnt = commentCnt;
    }

    public HashMap<String, String> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, String> comments) {
        this.comments = comments;
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

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

}
