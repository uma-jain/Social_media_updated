package Utils;

import android.app.Application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserApiModel implements Serializable {
    String email, uid,phone, image, bio, username, profession, cover, follower;
    ArrayList<String> messageuids = new ArrayList<String>();

    public UserApiModel(String email, String uid, String phone, String image, String bio, String username, String profession, String cover, String follower, ArrayList<String> messageuids) {
        this.email = email;
        this.uid = uid;
        this.phone = phone;
        this.image = image;
        this.bio = bio;
        this.username = username;
        this.profession = profession;
        this.cover = cover;
        this.follower = follower;
        this.messageuids = messageuids;
    }

    public UserApiModel(){}

    public ArrayList<String> getMessageuids() {
        return messageuids;
    }

    public void setMessageuids(ArrayList<String> messageuids) {
        this.messageuids = messageuids;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }
}
