package de.vogella.android.shopping;

public class Item {
    private int id;
    private String name;
    private double price;
    private int listId;

    public Item(int id, String name, double price, int listId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.listId = listId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getListId() {
        return listId;
    }
}