package org.chatapp.data;

import org.chatapp.HibernateUtil;
import org.chatapp.entity.CodeValidation;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DataCodeValidition {
    public static void insertCodeValidition(CodeValidation codeValidition) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // save the Citerne objects
            session.saveOrUpdate(codeValidition);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void deleteCodeValidition(CodeValidation codeValidition) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // save the Citerne objects
            session.delete(codeValidition);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean checkCodeValidition(String numero, String code) {
        CodeValidation codeValidition=null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String queryStr = "SELECT * FROM codeValidation WHERE numberPhone=:numero AND code=:code";
            codeValidition = session.createNativeQuery(queryStr, CodeValidation.class)
                    .setParameter("numero",numero)
                    .setParameter("code",code)
                    .uniqueResult();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return codeValidition != null;
    }
}
