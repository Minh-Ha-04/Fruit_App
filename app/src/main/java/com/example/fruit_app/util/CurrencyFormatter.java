package com.example.fruit_app.util;

public class CurrencyFormatter {
    public static String formatCurrency(double amount) {
        return String.format("%,.0f", amount).replace(",", ".");
    }

    public static String formatPrice(double price) {
        return formatCurrency(price) + " VND";
    }
}
