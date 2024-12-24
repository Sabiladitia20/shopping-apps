package de.vogella.android.shopping;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;


public class ListDetailActivity extends AppCompatActivity {

    private LinearLayout itemContainer;
    private DatabaseHelper dbHelper;
    private long listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);

        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Initialize views
        itemContainer = findViewById(R.id.itemContainer);
        dbHelper = new DatabaseHelper(this);

        // Get list ID from the intent
        listId = getIntent().getLongExtra("LIST_ID", -1);

        // Set up the FloatingActionButton to add an item
        ExtendedFloatingActionButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> showAddItemDialog());

        // Load all items for this list from the database
        loadItemsForList();
    }

    // Show dialog to add an item
    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Item");

        // Inflate custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView);

        EditText etItemName = dialogView.findViewById(R.id.etItemName);
        EditText etItemPrice = dialogView.findViewById(R.id.etItemPrice);
        Button btnAddItem = dialogView.findViewById(R.id.btnAddItem);

        AlertDialog dialog = builder.create();

        btnAddItem.setOnClickListener(v -> {
            String itemName = etItemName.getText().toString().trim();
            String itemPrice = etItemPrice.getText().toString().trim();

            if (itemName.isEmpty() || itemPrice.isEmpty()) {
                Toast.makeText(this, "Please enter both name and price", Toast.LENGTH_SHORT).show();
            } else {
                double price = Double.parseDouble(itemPrice);
                long itemId = dbHelper.insertItem(itemName, price, listId);
                addItemToList(itemId, itemName, price);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Add item to the list with functionality for update and delete
    private void addItemToList(long itemId, String itemName, double itemPrice) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_list_row, itemContainer, false);

        CheckBox cbItem = itemView.findViewById(R.id.cbItem);
        TextView tvItemDetails = itemView.findViewById(R.id.tvItemDetails);
        ImageButton btnUpdateItem = itemView.findViewById(R.id.btnUpdateItem);
        ImageButton btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);

        tvItemDetails.setText(itemName + " - $" + String.format("%.2f", itemPrice));

        // Update button action
        btnUpdateItem.setOnClickListener(v -> showUpdateItemDialog(itemId, itemName, itemPrice));

        // Delete button action
        btnDeleteItem.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(ListDetailActivity.this)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Delete the item from the database
                        dbHelper.deleteItem(itemId);
                        // Remove the item from the UI
                        itemContainer.removeView(itemView);
                        Toast.makeText(ListDetailActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                        updateTotalPrice(); // Recalculate the total price
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Checkbox toggle listener
        cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> updateTotalPrice());

        // Add the item view to the container
        itemContainer.addView(itemView);

        // Attach the item price to the checkbox as a tag for easy retrieval
        cbItem.setTag(itemPrice);
    }

    // Update the total price based on selected items
    private void updateTotalPrice() {
        double totalPrice = 0.0;

        for (int i = 0; i < itemContainer.getChildCount(); i++) {
            View itemView = itemContainer.getChildAt(i);
            CheckBox cbItem = itemView.findViewById(R.id.cbItem);

            if (cbItem.isChecked()) {
                double itemPrice = (double) cbItem.getTag();
                totalPrice += itemPrice;
            }
        }

        TextView tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText("Total: $" + String.format("%.2f", totalPrice));
    }

    // Show dialog to update an item
    private void showUpdateItemDialog(long itemId, String oldName, double oldPrice) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Item");

        // Inflate custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView);

        EditText etItemName = dialogView.findViewById(R.id.etItemName);
        EditText etItemPrice = dialogView.findViewById(R.id.etItemPrice);
        Button btnAddItem = dialogView.findViewById(R.id.btnAddItem);

        // Pre-fill with old values
        etItemName.setText(oldName);
        etItemPrice.setText(String.valueOf(oldPrice));
        btnAddItem.setText("Update");

        AlertDialog dialog = builder.create();

        btnAddItem.setOnClickListener(v -> {
            String newItemName = etItemName.getText().toString().trim();
            String newItemPrice = etItemPrice.getText().toString().trim();

            if (newItemName.isEmpty() || newItemPrice.isEmpty()) {
                Toast.makeText(this, "Please enter both name and price", Toast.LENGTH_SHORT).show();
            } else {
                double updatedPrice = Double.parseDouble(newItemPrice);
                dbHelper.updateItem(itemId, newItemName, updatedPrice);
                loadItemsForList(); // Reload the list
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Load all items for the current list
    private void loadItemsForList() {
        itemContainer.removeAllViews();
        Cursor cursor = dbHelper.getItemsForList(listId);
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            double itemPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            addItemToList(itemId, itemName, itemPrice);
        }
        cursor.close();
    }
}
