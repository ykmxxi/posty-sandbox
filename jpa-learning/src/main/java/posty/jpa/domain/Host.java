package posty.jpa.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "host")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Host extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "picture_url")
    private String pictureUrl;

    @Column(name = "agreed_terms", nullable = false)
    private Boolean agreedTerms = false;

    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
    private List<SpaceHostMap> spaceHostMaps = new ArrayList<>();

    public Host(String name, String pictureUrl, Boolean agreedTerms) {
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.agreedTerms = agreedTerms;
    }
}
