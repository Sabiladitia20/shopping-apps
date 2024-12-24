package de.vogella.android.shopping;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private LinearLayout listContainer;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listContainer = findViewById(R.id.listContainer);
        dbHelper = new DatabaseHelper(this);

        // Use FloatingActionButton for the "New List" action
        ExtendedFloatingActionButton fabNewList = findViewById(R.id.btnNewList);
        fabNewList.setOnClickListener(v -> showNewListDialog());

        // Load all lists from the database
        loadAllLists();
    }

    // Show dialog for creating a new list
    private void showNewListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New List");

        // Inflate custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_list, null);
        builder.setView(dialogView);

        EditText etListName = dialogView.findViewById(R.id.etListName);
        Button btnCreate = dialogView.findViewById(R.id.btnCreate);

        AlertDialog dialog = builder.create();
        btnCreate.setOnClickListener(v -> {
            String listName = etListName.getText().toString().trim();

            if (listName.isEmpty()) {
                Toast.makeText(this, "List name cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                long listId = dbHelper.insertList(listName);
                addNewListCard(listName, listId);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Add a new card for the created list
    private void addNewListCard(String listName, long listId) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.card_list_item, listContainer, false);

        TextView tvListName = cardView.findViewById(R.id.tvListName);
        tvListName.setText(listName);

        ImageButton btnUpdate = cardView.findViewById(R.id.btnUpdateList);
        ImageButton btnDelete = cardView.findViewById(R.id.btnDeleteList);

        // Set click listener for the card
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListDetailActivity.class);
            intent.putExtra("LIST_ID", listId); // Pass the list ID to the next activity
            startActivity(intent);
        });

        // Set up button listeners for updating and deleting the list
        btnUpdate.setOnClickListener(v -> showUpdateListDialog(listId, listName));
        btnDelete.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete List")
                    .setMessage("Are you sure you want to delete this list?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Delete the list from the database
                        dbHelper.deleteList(listId);
                        // Remove the list from the UI
                        listContainer.removeView(cardView);
                        Toast.makeText(MainActivity.this, "List deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Add card to the container
        listContainer.addView(cardView);
    }

    // Show dialog for updating list name
    private void showUpdateListDialog(long listId, String currentListName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update List");

        // Inflate custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_list, null);
        builder.setView(dialogView);

        EditText etListName = dialogView.findViewById(R.id.etListName);
        etListName.setText(currentListName);
        Button btnUpdate = dialogView.findViewById(R.id.btnCreate);

        AlertDialog dialog = builder.create();
        btnUpdate.setOnClickListener(v -> {
            String newListName = etListName.getText().toString().trim();

            if (newListName.isEmpty()) {
                Toast.makeText(this, "List name cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.updateList(listId, newListName);
                loadAllLists(); // Refresh the list to show updated name
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Load all lists from the database
    private void loadAllLists() {
        listContainer.removeAllViews(); // Clear the existing views before reloading
        Cursor cursor = dbHelper.getAllLists();
        while (cursor.moveToNext()) {
            String listName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            long listId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            addNewListCard(listName, listId);
        }
        cursor.close();
    }
}
