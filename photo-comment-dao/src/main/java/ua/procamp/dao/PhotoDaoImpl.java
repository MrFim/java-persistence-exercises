package ua.procamp.dao;

import ua.procamp.model.Photo;
import ua.procamp.model.PhotoComment;
import ua.procamp.util.EntityManagerUtil;

import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * Please note that you should not use auto-commit mode for your implementation.
 */
public class PhotoDaoImpl implements PhotoDao {
    private EntityManagerUtil entityManagerUtil;

    public PhotoDaoImpl(EntityManagerFactory entityManagerFactory) {
        entityManagerUtil = new EntityManagerUtil(entityManagerFactory);
    }

    @Override
    public void save(Photo photo) {
        entityManagerUtil.performWithinTx(entityManager -> entityManager.persist(photo));
    }

    @Override
    public Photo findById(long id) {
        return entityManagerUtil.performReturningWithinTx(entityManager -> entityManager.find(Photo.class, id));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Photo> findAll() {
        return entityManagerUtil.performReturningWithinTx(entityManager ->
                entityManager.createQuery("select p from Photo p").getResultList());
    }

    @Override
    public void remove(Photo photo) {
        entityManagerUtil.performWithinTx(entityManager -> {
            Photo mergedPhoto = entityManager.merge(photo);
            entityManager.remove(mergedPhoto);
        });
    }

    @Override
    public void addComment(long photoId, String comment) {
        entityManagerUtil.performWithinTx(entityManager -> {
            Photo id = entityManager.find(Photo.class, photoId);
            PhotoComment photoComment = new PhotoComment();
            photoComment.setPhoto(id);
            photoComment.setText(comment);
            entityManager.persist(photoComment);
        });
    }
}
