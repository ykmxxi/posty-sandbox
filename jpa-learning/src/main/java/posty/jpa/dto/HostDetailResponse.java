package posty.jpa.dto;

import java.time.LocalDateTime;
import java.util.List;

import posty.jpa.domain.Host;

public record HostDetailResponse(
    Long id,
    String name,
    LocalDateTime createdAt,
    List<Long> spaceIds
) {

    public static HostDetailResponse of(Host host, List<Long> spaceIds) {
        return new HostDetailResponse(host.getId(), host.getName(), host.getCreatedAt(), spaceIds);
    }
}
