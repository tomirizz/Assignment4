package com.example;

import java.util.Objects;

public class Order {
    private int orderID;
    private MenuItem menuItem;
    private int quantity;

    public Order(int orderID, MenuItem menuItem, int quantity){
        this.orderID = orderID;
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void display(){
        System.out.println("Order ID: " + orderID + ", MenuItem: " + menuItem.getName() + ", Quantity: " + quantity);

    }

    @Override
    public String toString() {
        return "Order{" + "orderID=" + orderID + ", menuItem=" + menuItem + ", quantity=" + quantity + '}';

    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Order order = (Order) obj;
        return orderID == order.orderID && quantity == order.quantity && Objects.equals(menuItem, order.menuItem) && Objects.equals(menuItem, order.menuItem);

    }

    @Override
    public int hashCode() {
        return Objects.hash(orderID, menuItem, quantity);
    }

}




