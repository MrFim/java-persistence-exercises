package ua.procamp.dao;

import ua.procamp.exception.AccountDaoException;
import ua.procamp.model.Account;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class AccountDaoImpl implements AccountDao {
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    public AccountDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public void save(Account account) {
        try  {
            entityManager.getTransaction().begin();
            entityManager.persist(account);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new AccountDaoException("something wrong with saving an account: ", e);
        }
    }

    @Override
    public Account findById(Long id) {
        entityManager.getTransaction().begin();
        Account account = entityManager.find(Account.class, id);
        entityManager.getTransaction().commit();
        return account;
    }

    @Override
    public Account findByEmail(String email) {
        entityManager.getTransaction().begin();
        Account result = entityManager.createQuery("select a from Account a where email = :email", Account.class)
                .setParameter("email", email).getSingleResult();
        entityManager.getTransaction().commit();
        return result;
    }

    @Override
    public List<Account> findAll() {
        entityManager.getTransaction().begin();
        List<Account> accounts = entityManager.createQuery("select a from Account a").getResultList();
        entityManager.getTransaction().commit();
        return accounts;
    }

    @Override
    public void update(Account account) {
        try {
            entityManager.getTransaction().begin();
            Account result = entityManager.find(Account.class, account.getId());

            result.setFirstName(account.getFirstName());
            result.setLastName(account.getLastName());
            result.setBalance(account.getBalance());
            result.setBirthday(account.getBirthday());
            result.setCreationTime(account.getCreationTime());
            result.setEmail(account.getEmail());
            result.setGender(account.getGender());
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            throw new AccountDaoException("something went wrong updating account: ", e);
        }
    }

    @Override
    public void remove(Account account) {
        Account deleted = entityManager.find(Account.class, account.getId());
        entityManager.getTransaction().begin();
        entityManager.remove(deleted);
        entityManager.getTransaction().commit();
    }
}

