package posty.jpa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_photo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductPhoto extends Photo {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    public ProductPhoto(Product product, Integer sortOrder, String originalName, String path, Long capacity) {
        super(originalName, path, capacity);
        this.product = product;
        this.sortOrder = sortOrder;
    }
}
