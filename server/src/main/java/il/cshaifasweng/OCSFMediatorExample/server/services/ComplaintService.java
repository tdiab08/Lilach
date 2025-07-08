package il.cshaifasweng.OCSFMediatorExample.server.services;

import il.cshaifasweng.OCSFMediatorExample.server.models.Complaint;
import il.cshaifasweng.OCSFMediatorExample.server.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;

public class ComplaintService {
    public static Complaint createComplaint(Complaint complaint) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            
            // Set creation date
            complaint.setCreatedAt(LocalDateTime.now());
            complaint.setStatus(Complaint.ComplaintStatus.OPEN);
            
            session.persist(complaint);
            transaction.commit();
            return complaint;
        }
    }
    
    public static Complaint resolveComplaint(int complaintId, double compensation) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            
            Complaint complaint = session.get(Complaint.class, complaintId);
            if (complaint != null && complaint.getStatus() == Complaint.ComplaintStatus.OPEN) {
                complaint.setStatus(Complaint.ComplaintStatus.RESOLVED);
                complaint.setCompensation(compensation);
                session.update(complaint);
                transaction.commit();
                return complaint;
            }
            return null;
        }
    }
}