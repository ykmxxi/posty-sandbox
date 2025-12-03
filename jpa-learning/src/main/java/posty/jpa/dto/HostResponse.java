package posty.jpa.dto;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

public record HostResponse(
    List<HostDetailResponse> hosts,
    int currentPage,
    int pageSize,
    long totalCount,
    int totalPages
) {

    public static HostResponse from(Page<HostDetailResponse> hosts) {
        return new HostResponse(
            hosts.map(Function.identity()).toList(),
            hosts.getNumber() + 1,
            hosts.getSize(),
            hosts.getTotalElements(),
            hosts.getTotalPages()
        );
    }
}
