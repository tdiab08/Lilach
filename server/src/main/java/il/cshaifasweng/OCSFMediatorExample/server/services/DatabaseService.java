package il.cshaifasweng.OCSFMediatorExample.server.services;

import org.hibernate.Session;
import il.cshaifasweng.OCSFMediatorExample.server.models.User;
import il.cshaifasweng.OCSFMediatorExample.server.utils.HibernateUtil;

public class DatabaseService {
    public static User authenticate(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM User WHERE username = :username AND password = :password", User.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .uniqueResult();
        }
    }
}