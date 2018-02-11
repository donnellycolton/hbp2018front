package com.coolkids.todo.getTogether;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.view.*;

import java.util.ArrayList;

/**
 * Created by colton on 2/10/18.
 */

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
    private ArrayList<PlannedEvent> events;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description, date;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
            date = view.findViewById(R.id.date);
        }
    }

    public TodoAdapter(ArrayList<PlannedEvent> events) {
        this.events = events;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.events_list_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlannedEvent event = events.get(position);
        holder.title.setText(event.getName());
        holder.description.setText(event.description);
        holder.date.setText(event.getEventDate());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
