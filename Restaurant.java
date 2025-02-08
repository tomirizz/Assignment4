package com.example;

import java.util.*;

public class Restaurant {
    private String name;
    private ArrayList<MenuItem> menu;
    private ArrayList<Order> orders;

    public Restaurant(String name){
        this.name = name;
        this.menu = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public ArrayList<MenuItem> getMenu(){
        return menu;
    }

    public ArrayList<Order> getOrders(){
        return orders;
    }

    public void addMenuItem(MenuItem menuItem){
        menu.add(menuItem);
    }

    public void placeOrder(Order order){
        orders.add(order);
    }

    public List<MenuItem> filterVegetarian(){
        List<MenuItem> vegetarianItems = new ArrayList<>();
        for(MenuItem item : menu){
            if (item instanceof FoodItem && ((FoodItem) item).isVegetarian()){
                vegetarianItems.add(item);
            }
        }
        return vegetarianItems;
    }

    public MenuItem searchMenuItemByName(String name){
        for (MenuItem item : menu){
            if (item.getName().equalsIgnoreCase(name)){
                return item;
            }
        }
        return null;
    }

    public void sortMenuByPrice(){
        Collections.sort(menu, Comparator.comparingDouble(MenuItem::getPrice));
    }

    public void display(){
        System.out.println("Restaurant: " + name);
        System.out.println("Menu: ");
        for (MenuItem item : menu){
            item.display();
        }
        System.out.println("Orders: ");
        for (Order order : orders){
            order.display();
        }
    }

    @Override
    public String toString(){
        return "Restaurant{" + "name='" + name + '\'' + ", menu=" + menu + ", orders=" + orders + '}';
    }

    @Override
    public boolean equals(Object o){
        if (this==o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(name, that.name) && Objects.equals(menu, that.menu) && Objects.equals(orders, that.orders);

    }

    @Override
    public int hashCode(){
        return Objects.hash(name, menu, orders);
    }

}
