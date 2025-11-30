package posty.jpa.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "space_photo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpacePhoto extends Photo {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    public SpacePhoto(Space space, String originalName, String path, Long capacity) {
        super(originalName, path, capacity);
        this.space = space;
    }
}
