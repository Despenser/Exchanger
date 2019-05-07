package ru.golubyatnikov.money.exchange.model.util;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.util.config.ConfigurationException;


public class Hibernate {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private Session session;
    private Transaction transaction;

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    protected Session getSession() {
        return session;
    }

    private static SessionFactory buildSessionFactory() throws ConfigurationException {
        return new Configuration().configure().buildSessionFactory();
    }

    private Session openSession() {
        return Hibernate.getSessionFactory().openSession();
    }

    private void closeSession() {
        session.close();
    }

    protected void openTransactionSession() {
        session = openSession();
        transaction = session.beginTransaction();
    }

    protected void closeTransactionSession() {
        transaction.commit();
        closeSession();
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}