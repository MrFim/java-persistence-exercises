package ua.procamp.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * todo:
 * - implement no argument constructor
 * - implement getters and setters
 * - make a setter for field {@link Photo#comments} {@code private}
 * - implement equals() and hashCode() based on identifier field
 *
 * - configure JPA entity
 * - specify table name: "photo"
 * - configure auto generated identifier
 * - configure not nullable and unique column: url
 *
 * - initialize field comments
 * - map relation between Photo and PhotoComment on the child side
 * - implement helper methods {@link Photo#addComment(PhotoComment)} and {@link Photo#removeComment(PhotoComment)}
 * - enable cascade type {@link javax.persistence.CascadeType#ALL} for field {@link Photo#comments}
 * - enable orphan removal
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "photo")
public class Photo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "url", unique = true, nullable = false)
    private String url;

    @Column(name = "description")
    private String description;

    @Setter(value = AccessLevel.PRIVATE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "photo")
    private List<PhotoComment> comments = new ArrayList<>();

    public void addComment(PhotoComment comment) {
        comment.setPhoto(this);
        comments.add(comment);
    }

    public void removeComment(PhotoComment comment) {
        comment.setPhoto(null);
        comments.remove(comment);
    }

}
