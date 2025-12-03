package posty.jpa.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import posty.jpa.domain.Host;
import posty.jpa.dto.HostDetailResponse;
import posty.jpa.dto.HostResponse;
import posty.jpa.repository.HostRepository;
import posty.jpa.repository.SpaceHostMapRepository;

@Service
@RequiredArgsConstructor
public class HostService {

    private final HostRepository hostRepository;
    private final SpaceHostMapRepository spaceHostMapRepository;

    // N + 1 발생
    @Transactional(readOnly = true)
    public HostResponse getAllHosts(Pageable pageable) {
        Page<Host> hosts = hostRepository.findAll(pageable);
        return HostResponse.from(hosts.map(host -> {
            List<Long> spaceIds = spaceHostMapRepository.findAllByHost(host)
                .stream()
                .map(spaceHostMap -> spaceHostMap.getSpace().getId())
                .toList();
            return HostDetailResponse.of(host, spaceIds);
        }));
    }

    // N + 1 해결: 양방향 매핑, Batch Fetch
    @Transactional(readOnly = true)
    public HostResponse getAllHosts1(Pageable pageable) {
        Page<Host> hosts = hostRepository.findAll(pageable);

        return HostResponse.from(hosts.map(host -> {
            // [변경] Repository 직접 호출 -> 객체 탐색으로 변경
            // 현재 로딩된 hosts 에 있는 다른 Host 객체들(배치 사이즈 만큼)의 SpaceHostMap 데이터까지 IN 절을 사용해서 한 번의 쿼리로 미리 가져옴
            List<Long> spaceIds = host.getSpaceHostMaps().stream() // 여기서 Batch Fetch 발동!
                .map(shm -> shm.getSpace().getId())
                .toList();
            return HostDetailResponse.of(host, spaceIds);
        }));
    }
}
