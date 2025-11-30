package posty.jpa.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "guest_book_card_photo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuestBookCardPhoto extends Photo {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_book_card_id", nullable = false)
    private GuestBookCard guestBookCard;

    public GuestBookCardPhoto(GuestBookCard guestBookCard, String originalName, String path, Long capacity) {
        super(originalName, path, capacity);
        this.guestBookCard = guestBookCard;
    }
}
