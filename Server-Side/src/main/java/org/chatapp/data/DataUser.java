package org.chatapp.data;

import org.chatapp.HibernateUtil;
import org.chatapp.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class DataUser {
    public static void insertUser(User user) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            Transaction transaction = session.beginTransaction();
            // save the Citerne objects
            session.saveOrUpdate(user);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void deleteUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // save the Citerne objects
            session.delete(user);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static User getUser(String numero) {
        User user=null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String queryStr = "SELECT * FROM user WHERE phoneNum=:numero";
            user = session.createNativeQuery(queryStr, User.class)
                    .setParameter("numero",numero)
                    .uniqueResult();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static boolean checkPermission(String num, String accessKey){
        User user=null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String queryStr = "SELECT * FROM user WHERE phoneNum=:numero AND accessKey=:accessKey";
            user = session.createNativeQuery(queryStr, User.class)
                    .setParameter("numero",num)
                    .setParameter("accessKey",accessKey)
                    .uniqueResult();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return user != null;
    }

    public static List<User> getActifUsers() {
        List<User> users=null;
        try (Session session = org.chatapp.HibernateUtil.getSessionFactory().openSession()) {

            String queryStr = "SELECT * FROM user WHERE statu=true";
            users = session.createNativeQuery(queryStr, User.class)
                    .list();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
}
