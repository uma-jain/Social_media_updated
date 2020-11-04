package Utils;

public class UserModal {
    String  uid,username,email,phone,search,image,cover,profession;
    public UserModal() {
    }

    public UserModal(String uid, String username, String email, String phone, String search, String image, String cover, String profession) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.search = search;
        this.image = image;
        this.cover = cover;
        this.profession = profession;
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

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
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
}
