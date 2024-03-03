/*
* ToDoListTaskListActivity.java
* Conrains the java code for the list view of tasks in this project
* created by: Aidan McCormick
* created on 2/27/2024
* last modified by: Aidan McCormick
* Last modified on 3/1/2024
* Final Project: A290 Android development
* Part of: ToDoList , refers to the task_list.xml layout file
* */

package aidmccor.edu.indiana.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Date;


public class TaskListActivity extends Activity implements View.OnClickListener {

    private TinyDB tinydb;
    private ArrayList<Tasks> tasks = new ArrayList<>();
    private ArrayAdapter<Tasks> adapter;

    //function that updates the stored data content, will be called whenever a use performs a crud action
    public void updateDB(){
        // Convert ArrayList<Tasks> to ArrayList<Object> for TinyDB
        ArrayList<Object> tasksObjectsForDB = new ArrayList<>(tasks.size());
        tasksObjectsForDB.addAll(tasks); // Add all Tasks objects to the new list
        tinydb.putListObject("tasks", tasksObjectsForDB); // Store the list as objects
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Defining list view, button and assigning the button an event listener



        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list);
        ListView tasksListView = findViewById(R.id.listView_tasks);
        Button addTask = findViewById(R.id.btn_addTask);
        addTask.setOnClickListener(this);
        /*You have to use arrayList of type object with tinyDB, so we have to fetch and add to it as
        <Object> but have to convert it back to <Tasks> before it goes in the adapter
         */
        tinydb = new TinyDB(getApplicationContext());
        ArrayList<Object> tasksObjects = tinydb.getListObject("tasks", Tasks.class);

        //changing the data in the tasksObjects to be of type Tasks, so we can use the instance's methods
        tasks.clear(); // Clear the existing list to avoid duplicates
        for (Object obj : tasksObjects) {
            if (obj instanceof Tasks) {
                tasks.add((Tasks) obj);
            }
        }

        // The adapter lets us control what data is going into the listview, it will use the tasks ArrayList values
        adapter = new ArrayAdapter<Tasks>(this, 0, tasks) {
            /*this is going to create the view for each item in the list, it takes position so we can find the index, and the parent
           is what it will eventially be attached to, which is the listView
            */
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
                }

                //Get the views from the ids
                TextView taskNameTextView = convertView.findViewById(R.id.textViewTaskName);
                TextView taskDateTextView = convertView.findViewById(R.id.textViewTaskDate);
                Button trashButton = convertView.findViewById(R.id.btn_trash);
                CheckBox completedCheck = convertView.findViewById(R.id.completedCheck);

                //Set the text/state of the views with the current instance values
                Tasks currentTask = getItem(position);
                taskNameTextView.setText(currentTask.getName());
                taskDateTextView.setText(currentTask.getDate().toString());
                completedCheck.setChecked(currentTask.getCompleted());

                //switch statement to change the text color based on priority, gets color from color file
                int colorId;
                switch (currentTask.getPriority()) {
                    case "High":
                        colorId = R.color.highPriority;
                        break;
                    case "Medium":
                        colorId = R.color.mediumPriority;
                        break;
                    case "Low":
                        colorId = R.color.lowPriority;
                        break;
                    default:
                        colorId = R.color.lowPriority; // Default to low priority color if none match
                }
                taskNameTextView.setTextColor(ContextCompat.getColor(getContext(), colorId));

                //check to see if task is completed, if it is add a strikethourgh through it
                if (currentTask.getCompleted()) {
                    taskNameTextView.setPaintFlags(taskNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    taskDateTextView.setText("Completed!");
                } else {
                    taskNameTextView.setPaintFlags(taskNameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }

                //add event for check button, that will change it to the opposite boolean it was before
                completedCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentTask.toggleCompleted();
                        updateDB();
                        notifyDataSetChanged();
                    }
                });

                // add event for trash button, it will delete the event from list at the target event, and update
                trashButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tasks.remove(currentTask);
                        updateDB();
                        notifyDataSetChanged();
                    }
                });


                return convertView;
            }
        };
        tasksListView.setAdapter(adapter);
    }

    // on click listener for the add task button, that will call addTask dialog to inflate the menu
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_addTask) {
            showAddTaskDialog();
        }
    }

    //function to inflate menu, conratins a group of radio buttons and and input for the task tile
    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskListActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_tasks, null);
        final EditText editTextTaskName = view.findViewById(R.id.editTextTaskName);
        final RadioGroup priorityRadioGroup = view.findViewById(R.id.priorityRadioGroup);

        builder.setView(view)
                .setTitle("Add New Task")
                .setPositiveButton("Add", (dialogInterface, i) -> {
                    String taskName = editTextTaskName.getText().toString().trim();
                    if (!taskName.isEmpty()) {
                        int selectedPriorityId = priorityRadioGroup.getCheckedRadioButtonId();
                        RadioButton selectedPriorityButton = view.findViewById(selectedPriorityId);
                        String selectedPriority = selectedPriorityButton.getText().toString();

                        Tasks newTask = new Tasks(taskName, new Date(), selectedPriority);
                        tasks.add(newTask);
                        updateDB();

                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
