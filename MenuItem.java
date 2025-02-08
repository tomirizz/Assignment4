package com.example;

import java.util.Objects;

abstract class MenuItem {
    private String name;
    private double price;

    public MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public abstract void display();

    @Override
    public String toString() {
        return "MenuItem{" + "name='" + name + '\'' + ", price=" + price + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MenuItem menuItem = (MenuItem) obj;
        return Double.compare(menuItem.price, price) == 0 && Objects.equals(name, menuItem.name);

    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}
class FoodItem extends MenuItem {
        private boolean isVegetarian;

        public FoodItem(String name, double price, boolean isVegetarian) {
            super(name, price);
            this.isVegetarian = isVegetarian;
        }

        public boolean isVegetarian() {
            return isVegetarian;
        }

        public void setVegetarian(boolean isVegetarian) {
            this.isVegetarian = isVegetarian;
        }

        @Override
        public void display() {
            System.out.println("FoodItem: " + getName() + ", Price: $" + getPrice() + ", Vegetarian: " + isVegetarian);

        }

}
