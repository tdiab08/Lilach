package il.cshaifasweng.OCSFMediatorExample.server.services;

import il.cshaifasweng.OCSFMediatorExample.server.models.Order;
import il.cshaifasweng.OCSFMediatorExample.server.models.OrderItem;
import il.cshaifasweng.OCSFMediatorExample.server.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public class OrderService {
    public static Order createOrder(Order order) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            
            // Set order date
            order.setOrderDate(LocalDateTime.now());
            
            // Calculate total price
            double total = order.getItems().stream()
                .mapToDouble(item -> {
                    if (item.getProduct() != null) {
                        return item.getProduct().getPrice() * item.getQuantity();
                    } else {
                        // Custom item price calculation
                        return calculateCustomItemPrice(item);
                    }
                })
                .sum();
            order.setTotalPrice(total);
            
            // Save order and items
            session.persist(order);
            for (OrderItem item : order.getItems()) {
                item.setOrder(order);
                session.persist(item);
            }
            
            transaction.commit();
            return order;
        }
    }
    
    private static double calculateCustomItemPrice(OrderItem item) {
        // Simplified custom pricing logic
        if (item.getCustomPriceRange() != null) {
            if (item.getCustomPriceRange().equals("LOW")) return 50.0;
            if (item.getCustomPriceRange().equals("MEDIUM")) return 100.0;
            if (item.getCustomPriceRange().equals("HIGH")) return 200.0;
        }
        return 80.0; // Default price
    }
    
    public static List<Order> getUserOrders(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Order WHERE user.id = :userId ORDER BY orderDate DESC", Order.class)
                .setParameter("userId", userId)
                .list();
        }
    }
    
    public static Order cancelOrder(int orderId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            
            Order order = session.get(Order.class, orderId);
            if (order != null && canCancelOrder(order)) {
                order.setStatus(Order.OrderStatus.CANCELLED);
                session.update(order);
                transaction.commit();
                return order;
            }
            return null;
        }
    }
    
    private static boolean canCancelOrder(Order order) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deliveryTime = order.getDeliveryDate();
        
        // Can cancel if more than 3 hours before delivery
        return deliveryTime.minusHours(3).isAfter(now);
    }
}