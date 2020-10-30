package Utils;

import android.app.Application;

public class UserApi extends Application {
    String email, uid,phone, image, bio, username, profession, cover, followerCount;

    private static UserApi userApi;



    public static UserApi getInstance()
    {
        if(userApi == null)
        {
            userApi = new UserApi();
        }
        return userApi;
    }
    public UserApi(String email, String uid, String phone, String image, String bio, String username, String profession, String cover, String followerCount) {
        this.email = email;
        this.uid = uid;
        this.phone = phone;
        this.image = image;
        this.bio = bio;
        this.username = username;
        this.profession = profession;
        this.cover = cover;
        this.followerCount = followerCount;
    }

    public UserApi(){}

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

    public String getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(String followerCount) {
        this.followerCount = followerCount;
    }

    public static UserApi getUserApi() {
        return userApi;
    }

    public static void setUserApi(UserApi userApi) {
        UserApi.userApi = userApi;
    }
}
