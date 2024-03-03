/*  ToDoListMainActivity.java
 * contains the java code for the home page in this project
 * created by: aidan mccormick
 * created on 2/27/2024
 * last modified by aidan mccormick
 * last modified on 3/1/2024
 * Final Project: A290 Android Development
 * Part of ToDoList refers to mainactivity.xml layout file*/

//imports
package aidmccor.edu.indiana.todolist;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int completedCount = 0; //initialize count of completed tasks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button viewListButton = findViewById(R.id.btn_view_tasks);
        viewListButton.setOnClickListener(this::onClick);

        //initialize tinydb to be able to access the arrayList of objects we are storing
        TinyDB tinydb = new TinyDB(getApplicationContext());
        ArrayList<Object> tasksObjects = tinydb.getListObject("tasks", Tasks.class);

        //looping through taskObjects, casting it to Task type, and incremented the count if it is completed
        for(Object obj: tasksObjects){
            Tasks task = (Tasks) obj;
            if(task.getCompleted()){
                completedCount += 1;
            }
        }
        //displaying the text where it shows how many tasks user has completed
        TextView taskStatus = findViewById(R.id.txt_taskStatus);
        taskStatus.setText(completedCount + "/" + tasksObjects.size()+ "Tasks Completed");
    }


    // clicks listener that takes user to the task list page when user click
    public void onClick(View v){
        int id = v.getId();
        if(id == R.id.btn_view_tasks){
            Intent i = new Intent(this, TaskListActivity.class);
            startActivity(i);
        }
    }
}

