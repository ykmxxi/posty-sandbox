package posty.jpa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String category = "";

    @Column(name = "author_name", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String authorName = "";

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(name = "video_url", nullable = false, length = 512, columnDefinition = "VARCHAR(512) DEFAULT ''")
    private String videoUrl = "";

    @Column(name = "is_video_after_photo", nullable = false)
    private Boolean isVideoAfterPhoto = false;

    public Product(Space space, String title, String category, String authorName, String description, String videoUrl,
        Boolean isVideoAfterPhoto) {
        this.space = space;
        this.title = title;
        this.category = category;
        this.authorName = authorName;
        this.description = description;
        this.videoUrl = videoUrl;
        this.isVideoAfterPhoto = isVideoAfterPhoto;
    }
}
