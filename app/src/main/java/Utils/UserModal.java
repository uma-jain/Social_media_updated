package Utils;

import java.io.Serializable;

public class UserModal implements Serializable {
    String  uid,username,email,phone,image,cover,profession,follower,bio;
    public UserModal() {
    }
    public UserModal(String uid, String username, String email, String phone,  String image, String cover, String profession, String follower, String bio) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.cover = cover;
        this.profession = profession;
        this.follower = follower;
        this.bio = bio;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
