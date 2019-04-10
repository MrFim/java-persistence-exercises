package ua.procamp.dao;

import ua.procamp.model.Account;
import ua.procamp.util.TestDataGenerator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class EntityManagerExample {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("SingleAccountEntityH2");//create a table
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        try{
            Account account = TestDataGenerator.generateAccount();
            System.out.println(account);
            entityManager.persist(account);//save in bd
            System.out.println(account);
            Account findAccount = entityManager.find(Account.class, account.getId());
            System.out.println(findAccount);

            List<Account> accounts = entityManager.createQuery("select a from Account a where a.email = :email", Account.class)
                    .setParameter("email", account.getEmail())
                    .getResultList();
            entityManager.remove(account);
            entityManager.getTransaction().commit();
        } catch (Exception ex){
            entityManager.getTransaction().rollback();
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }
}
