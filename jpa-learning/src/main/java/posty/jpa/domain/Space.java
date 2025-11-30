package posty.jpa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "space")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Space extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String description = "";

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "instagram_username", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String instagramUsername = "";

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String email = "";

    public Space(String code, String name, String description, Boolean isPublic, String instagramUsername,
        String email) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.instagramUsername = instagramUsername;
        this.email = email;
    }
}
