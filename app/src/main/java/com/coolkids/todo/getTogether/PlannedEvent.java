package com.coolkids.todo.getTogether;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by colton on 2/10/18.
 */

public class PlannedEvent {
    String name;
    AppUser owner;
    String eventID;
    ArrayList<AppUser> users;
    ArrayList<ToDoTask> tasks;
    String description;
    String eventDate;

    public PlannedEvent(JSONObject serverJSON) {
        try {
            this.name = serverJSON.getString("title");
            this.owner = new AppUser(serverJSON.getJSONObject("owner"));
            this.eventID = serverJSON.getString("eventCode");
            this.users = parseUsers(serverJSON.getJSONArray("users"));
            this.tasks = parseTasks(serverJSON.getJSONArray("tasks"));
            this.description = serverJSON.getString("description");
            this.eventDate = serverJSON.getString("date");
        }
        catch (JSONException e) {
            Log.d("JSON Error",serverJSON.toString());
            throw new IllegalArgumentException("The JSONObject received from" +
                    " the server doesn't have the right names lol");
        }
    }


    private ArrayList<AppUser> parseUsers(JSONArray usersJSON) {
        ArrayList<AppUser> parsedUsers = new ArrayList<>();
        for (int i = 0; i < usersJSON.length(); i++) {
            try {
                parsedUsers.add(new AppUser(usersJSON.getJSONObject(i)));
            }
            catch (JSONException e) {
                throw new IllegalArgumentException("The JSONObject received from" +
                        " the server doesn't have the right names lol");
            }
        }
        return parsedUsers;
    }

    private ArrayList<ToDoTask> parseTasks(JSONArray tasksJSON) {
        ArrayList<ToDoTask> parsedTasks = new ArrayList<>();
        for (int i = 0; i < tasksJSON.length(); i++) {
            JSONObject thisTask;
            boolean isAssigned;
            boolean completed;
            int id;
            String name;
            String description;
            try {
                thisTask = tasksJSON.getJSONObject(i);
                id = thisTask.getInt("id");
                name = thisTask.getString("name");
                completed = thisTask.getBoolean("completed");
                description = thisTask.getString("description");
                isAssigned = thisTask.getBoolean("isAssigned");
            }
            catch (JSONException e) {
                Log.e("parseTasks",e.getMessage());
                throw new IllegalArgumentException("The JSONObject received from" +
                        " the server doesn't have the right names lol");
            }
            if (isAssigned) {
                AppUser assignedTo;
                try {
                    assignedTo = new AppUser(thisTask.getJSONObject("assigned to"));
                }
                catch (JSONException e) {
                    throw new IllegalArgumentException("The JSONObject received from" +
                            " the server doesn't have the right names lol");
                }
                parsedTasks.add(new ToDoTask(id, name, description, completed, isAssigned, assignedTo));
            } else {
                parsedTasks.add(new ToDoTask(id, name, description, completed, isAssigned, null));
            }
        }
        return parsedTasks;
    }

    public String getName() {
        return name;
    }

    public String getEventDate() {
        return eventDate;
    }
}
