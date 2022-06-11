package com.example.digitalmemory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.digitalmemory.Adapters.MemoriesListAdapter;
import com.example.digitalmemory.database.SqliteDB;
import com.example.digitalmemory.models.Memories;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MemoriesListAdapter memoriesListAdapter;
    List<Memories> memories = new ArrayList<>();
    SqliteDB database;
    FloatingActionButton fab;
    ImageButton deleteBtn;
    LatLng result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_home);
        fab = findViewById(R.id.fab_add);
        deleteBtn = findViewById(R.id.deleteButton);

        database = new SqliteDB(this);
        memories = database.getAllMemories();

        updateRecycler(memories);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MemoriesTakerActivity.class);
                startActivityForResult(intent, 101);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==101){
            if(resultCode== Activity.RESULT_OK){
                Memories newMemories = (Memories) data.getSerializableExtra("memory");
                database.addNewMemory(newMemories.getTitle(), newMemories.getContent(), newMemories.getLocation(), newMemories.getEmoji() ,newMemories.getDate());
                memories.clear();
                memories.addAll(database.getAllMemories());
                //memories = database.getAllMemories();
                memoriesListAdapter.notifyDataSetChanged();
            }
        }else if(requestCode==102){
            if(resultCode == Activity.RESULT_OK){
                Memories newMemories = (Memories) data.getSerializableExtra("memory");
                database.updateMemory(newMemories);
                memories.clear();
                memories.addAll(database.getAllMemories());
                memoriesListAdapter.notifyDataSetChanged();
            }
        }/*else if(requestCode==103){
            if(resultCode == Activity.RESULT_OK){
                result = (LatLng) data.getParcelableExtra("locRes");

            }
        }*/
    }

    private void updateRecycler(List<Memories> memories) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        memoriesListAdapter = new MemoriesListAdapter(MainActivity.this, memories, memoriesClickListener);
        recyclerView.setAdapter(memoriesListAdapter);

    }

    private final MemoriesClickListener memoriesClickListener = new MemoriesClickListener() {
        @Override
        public void onClick(Memories memories) {
            Intent intent = new Intent(MainActivity.this, MemoriesTakerActivity.class);
            intent.putExtra("toBeUpdated", memories);
            startActivityForResult(intent, 102);
        }

    };


}