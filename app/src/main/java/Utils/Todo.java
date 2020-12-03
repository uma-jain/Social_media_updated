package Utils;

public class Todo {
    private int id;
    private String todoNote;
    private String dateTodoAdded;

    public Todo()
    {

    }

    public Todo(int id, String todoNote, String dateTodoAdded) {
        this.id = id;
        this.todoNote = todoNote;
        this.dateTodoAdded = dateTodoAdded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTodoNote() {
        return todoNote;
    }

    public void setTodoNote(String todoNote) {
        this.todoNote = todoNote;
    }

    public String getDateTodoAdded() {
        return dateTodoAdded;
    }

    public void setDateTodoAdded(String dateTodoAdded) {
        this.dateTodoAdded = dateTodoAdded;
    }
}
