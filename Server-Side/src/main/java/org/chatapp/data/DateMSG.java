package org.chatapp.data;

import org.chatapp.HibernateUtil;
import org.chatapp.entity.MSG;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class DateMSG {
    public static void insertMSG(MSG msg){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            // start a transaction
            Transaction transaction = session.beginTransaction();
            // save the Citerne objects
            session.saveOrUpdate(msg);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<MSG> getMSGs(String destination){
        List<MSG> msgs = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){

            String query = "SELECT * FROM msgsWaiting WHERE destination=:destination";
            // save the Citerne objects
            msgs = session.createNativeQuery(query, MSG.class)
                    .setParameter("destination", destination)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msgs;
    }
    public static List<MSG> deleteMSGs(List<MSG> msgs){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            for (MSG msg:msgs){
                session.delete(msg);
            }
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msgs;
    }

}
