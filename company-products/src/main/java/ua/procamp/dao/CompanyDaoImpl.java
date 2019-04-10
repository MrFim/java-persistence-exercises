package ua.procamp.dao;

import org.hibernate.Session;
import ua.procamp.exception.CompanyDaoException;
import ua.procamp.model.Company;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class CompanyDaoImpl implements CompanyDao {
    private EntityManagerFactory entityManagerFactory;

    public CompanyDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Company findByIdFetchProducts(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.unwrap(Session.class).setDefaultReadOnly(true);
        entityManager.getTransaction().begin();
        try {
            Company company = entityManager
            .createQuery("select c from Company c left join fetch c.products where c.id = :id", Company.class)
                    .setParameter("id", id).getSingleResult();
            entityManager.getTransaction().commit();
            return company;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new CompanyDaoException("Error with fetch: ", e);
        } finally {
            entityManager.close();
        }
    }
}
