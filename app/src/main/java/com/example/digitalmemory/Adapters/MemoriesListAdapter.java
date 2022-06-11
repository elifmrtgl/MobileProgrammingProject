package com.example.digitalmemory.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalmemory.MemoriesClickListener;
import com.example.digitalmemory.R;
import com.example.digitalmemory.database.SqliteDB;
import com.example.digitalmemory.models.Memories;

import java.util.List;

public class MemoriesListAdapter extends RecyclerView.Adapter<MemoriesViewHolder>{

    Context context;
    List<Memories> memories;
    MemoriesClickListener listener;
    SqliteDB database;

    public MemoriesListAdapter(Context context, List<Memories> memories, MemoriesClickListener listener) {
        this.context = context;
        this.memories = memories;
        this.listener = listener;
        database = new SqliteDB(context);
    }

    @NonNull
    @Override
    public MemoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MemoriesViewHolder(LayoutInflater.from(context).inflate(R.layout.memory, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MemoriesViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.baslik.setText(memories.get(position).getTitle());
        holder.baslik.setSelected(true); //horizontal scrolling effect
        holder.content.setText(memories.get(position).getContent());
        holder.content.setSelected(true);
        holder.editTextDate.setText(memories.get(position).getDate());
        holder.location.setText(memories.get(position).getLocation());
        holder.emoji.setText(memories.get(position).getEmoji());

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(memories.get(holder.getAdapterPosition()));
            }
        });


        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Memories memory = memories.get(holder.getAdapterPosition());
                database.deleteMemory(memory);
                memories.remove(memory);
                notifyItemRemoved(holder.getAdapterPosition());
                //notifyDataSetChanged();
            }
        });

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Memories memory = memories.get(holder.getAdapterPosition());

                String memoryTitle = memory.getTitle().toString();
                String memoryContent = memory.getContent().toString();
                String memoryLocation = memory.getLocation().toString();
                String memoryDate = memory.getDate().toString();

                Intent smsIntent = new Intent(Intent.ACTION_SEND);
                smsIntent.setType("text/plain");
                String text=""+memoryTitle+"\n" +memoryContent + "\n\nLocation: " +memoryLocation+ "\n" +memoryDate;
                smsIntent.putExtra(Intent.EXTRA_TEXT,text);
                view.getContext().startActivity(Intent.createChooser(smsIntent, null));
            }
        });

    }



    @Override
    public int getItemCount() {

        return memories.size();
    }
}

class MemoriesViewHolder extends RecyclerView.ViewHolder{


    CardView card_view;
    ImageView notesIcon;
    EditText editTextDate;
    TextView baslik, location, content, emoji;
    ImageButton deleteButton, shareButton;
    public MemoriesViewHolder(@NonNull View itemView) {
        super(itemView);
        card_view = itemView.findViewById(R.id.card_view);
        notesIcon = itemView.findViewById(R.id.notesicon);
        editTextDate = itemView.findViewById(R.id.editTextDate);
        baslik = itemView.findViewById(R.id.baslik);
        location = itemView.findViewById(R.id.location);
        content = itemView.findViewById(R.id.content);
        deleteButton = itemView.findViewById(R.id.deleteButton);
        shareButton = itemView.findViewById(R.id.shareButton);
        emoji = itemView.findViewById(R.id.emoji);

    }
}
