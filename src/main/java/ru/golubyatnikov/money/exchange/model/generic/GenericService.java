package ru.golubyatnikov.money.exchange.model.generic;


import org.hibernate.query.NativeQuery;
import ru.golubyatnikov.money.exchange.model.util.Hibernate;
import java.math.BigInteger;
import java.util.List;


public class GenericService<T> extends Hibernate implements GenericDAO<T> {

    private Class<T> type;

    public GenericService(Class<T> type) {
        this.type = type;
    }

    @Override
    public void create(T obj) {
        crud("create", obj);
    }

    @Override
    public void update(T obj) {
        crud("update", obj);
    }

    @Override
    public void delete(T obj) {
        crud("delete", obj);
    }

    @Override
    public List<T> findAll() {
        try {
            openTransactionSession();
            String sql = "select * from " + type.getSimpleName();
            return getSession().createNativeQuery(sql).addEntity(type).list();
        } finally {
            closeTransactionSession();
        }
    }

    @Override
    public T findById(Long id) {
        try {
            openTransactionSession();
            String sql = "select * from " + type.getSimpleName() + " where id = :text";
            return (T) getSession().createNativeQuery(sql).addEntity(type).setParameter("text", id).getSingleResult();
        } finally {
            closeTransactionSession();
        }
    }

    @Override
    public BigInteger isEmptyTable() {
        try {
            openTransactionSession();
            String query = "select exists (select null from " + type.getSimpleName() + ")";
            return (BigInteger) getSession().createNativeQuery(query).getSingleResult();
        } finally {
            closeTransactionSession();
        }
    }

    @Override
    public List<T> customQuery(String query, Class<T> clazz, Object... params) {
        try {
            openTransactionSession();
            NativeQuery nativeQuery = getSession().createNativeQuery(query);
            if (params.length > 0) {
                int i = 1;
                for (Object param : params) nativeQuery.setParameter("text" + i++, param);
            }
            nativeQuery.addEntity(clazz);
            return nativeQuery.list();
        } finally {
            closeTransactionSession();
        }
    }

    private void crud(String action, T obj) {
        try {
            openTransactionSession();
            switch (action) {
                case "create": getSession().save(obj);
                    break;
                case "update": getSession().update(obj);
                    break;
                case "delete": getSession().delete(obj);
                    break;
            }
        } finally {
            closeTransactionSession();
        }
    }
}