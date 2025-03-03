package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class RestaurantSystem {
    private static final String URL = "jdbc:postgresql://localhost:5432/restaurant";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/orders", new OrderHandler());
        server.createContext("/menuitem", new MenuHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080...");
    }

    static class OrderHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGetOrders(exchange);
                    break;
                case "POST":
                    handleCreateOrder(exchange);
                    break;
                case "PUT":
                    handleUpdateOrder(exchange);
                    break;
                case "DELETE":
                    handleDeleteOrder(exchange);
                    break;
                default:
                    sendResponse(exchange, 405, "Method Not Allowed");
            }
        }

        private void handleGetOrders(HttpExchange exchange) throws IOException {
            JSONArray orders = new JSONArray();
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM Orders")) {

                while (rs.next()) {
                    JSONObject order = new JSONObject();
                    order.put("order_id", rs.getInt("order_id"));
                    order.put("menu_item_id", rs.getInt("menu_item_id"));
                    order.put("quantity", rs.getInt("quantity"));
                    orders.put(order);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Database Error: " + e.getMessage());
                return;
            }
            sendResponse(exchange, 200, orders.toString());
        }

        private void handleCreateOrder(HttpExchange exchange) throws IOException {
            String requestBody = readRequestBody(exchange);
            try {
                JSONObject json = new JSONObject(requestBody);
                int quantity = json.getInt("quantity");
                int menu_item_id = json.getInt("menu_item_id");
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO Orders (quantity, menu_item_id) VALUES (?, ?) RETURNING order_id")) {
                    pstmt.setInt(1, quantity);
                    pstmt.setInt(2, menu_item_id);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        sendResponse(exchange, 201, "{\"id\":" + rs.getInt("order_id") + "}");
                    }
                }
            } catch (Exception e) {
                sendResponse(exchange, 400, "Invalid JSON: " + e.getMessage());
            }
        }

        private void handleUpdateOrder(HttpExchange exchange) throws IOException {
            String requestBody = readRequestBody(exchange);
            try {
                JSONObject json = new JSONObject(requestBody);
                int order_id = json.getInt("order_id");
                int quantity = json.getInt("quantity");
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(
                         "UPDATE Orders SET quantity = ? WHERE order_id = ?")) {
                    pstmt.setInt(1, quantity);
                    pstmt.setInt(2, order_id);
                    int rowsAffected = pstmt.executeUpdate();
                    sendResponse(exchange, 200, "{\"updated\":" + rowsAffected + "}");
                }
            } catch (Exception e) {
                sendResponse(exchange, 400, "Invalid JSON: " + e.getMessage());
            }
        }

        private void handleDeleteOrder(HttpExchange exchange) throws IOException {
            String requestBody = readRequestBody(exchange);
            try {
                JSONObject json = new JSONObject(requestBody);
                int order_id = json.getInt("order_id");
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(
                         "DELETE FROM Orders WHERE order_id = ?")) {
                    pstmt.setInt(1, order_id);
                    int rowsAffected = pstmt.executeUpdate();
                    sendResponse(exchange, 200, "{\"deleted\":" + rowsAffected + "}");
                }
            } catch (Exception e) {
                sendResponse(exchange, 400, "Invalid JSON: " + e.getMessage());
            }
        }
    }

    static class MenuHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                handleCreateMenu(exchange);
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }

        private void handleCreateMenu(HttpExchange exchange) throws IOException {
            String requestBody = readRequestBody(exchange);
            try {
                JSONObject json = new JSONObject(requestBody);
                String name = json.getString("name");
                double price = json.getDouble("price");
                boolean is_vegetarian = json.getBoolean("is_vegetarian");
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO MenuItem (name, price, is_vegetarian) VALUES (?, ?, ?)")) {
                    pstmt.setString(1, name);
                    pstmt.setDouble(2, price);
                    pstmt.setBoolean(3, is_vegetarian);
                    pstmt.executeUpdate();
                    sendResponse(exchange, 201, "{\"message\":\"Menu Item created\"}");
                }
            } catch (Exception e) {
                sendResponse(exchange, 400, "Invalid JSON: " + e.getMessage());
            }
        }
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
