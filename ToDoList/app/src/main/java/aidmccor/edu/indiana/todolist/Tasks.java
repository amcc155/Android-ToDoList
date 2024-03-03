package aidmccor.edu.indiana.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Tasks {
    private String name;
    private Date date;

    private String priority;

    private Boolean completed;
    public Tasks(String name, Date date, String priority) {
        this.name = name;
        this.date = date;
        this.priority = priority;
        this.completed = false;
    }

    public String getName(){
        return this.name;
    }

    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.getDefault());
        return dateFormat.format(this.date);
    }

    public String getPriority(){return this.priority;}


    public Boolean getCompleted(){
        return this.completed;
    }
    public void toggleCompleted(){
        this.completed = !this.completed;
    }
}


