package ru.golubyatnikov.money.exchange.model.service;


import ru.golubyatnikov.money.exchange.model.entity.Client;
import ru.golubyatnikov.money.exchange.model.generic.GenericService;
import java.util.List;


public class ClientService extends GenericService<Client> {

    public ClientService() {
        super(Client.class);
    }

    public List<Client> findChoicerPane(String text) {
        String query = "select * from client cl, passport p, contact c " +
                "where cl.passport_id = p.id " +
                "and cl.contact_id = c.id " +
                "and concat(surname, name, middleName, serial, number) like :text1";
        return customQuery(query, Client.class, '%' + text + '%');
    }

    public List<Client> findClientPane(String text) {
        String query = "select * from client cl, passport p, contact c " +
                "where cl.passport_id = p.id " +
                "and cl.contact_id = c.id " +
                "and concat(surname, name, middleName, serial, number, birthPlace, phone, email) like :text1";
        return customQuery(query, Client.class, '%' + text + '%');

    }

    public Client findBySerialNumber(long serial, long number){
        String query = "select * from client c, passport p where  c.passport_id = p.id and p.serial = :text1 and p.number = :text2";
        List<Client> clients = customQuery(query, Client.class, serial, number);
        return !clients.isEmpty() ? clients.get(0) : null;
    }

    public Client findByIdAndSerialNumber(long id, long serial, long number) {
        String query = "select * from client c, passport p where c.passport_id = p.id and c.id != :text1 and p.serial = :text2 and p.number = :text3";
        List<Client> clients = customQuery(query, Client.class, id, serial, number);
        return !clients.isEmpty() ? clients.get(0) : null;
    }
}