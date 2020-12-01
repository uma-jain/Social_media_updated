package Utils;

public class CommentModel {
    private String userName, comment, commentTimeStamp;

    public CommentModel() {
    }

    public CommentModel(String userName, String comment, String commentTimeStamp) {
        this.userName = userName;
        this.comment = comment;
        this.commentTimeStamp = commentTimeStamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentTimeStamp() {
        return commentTimeStamp;
    }

    public void setCommentTimeStamp(String commentTimeStamp) {
        this.commentTimeStamp = commentTimeStamp;
    }
}
