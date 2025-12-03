package posty.jpa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import posty.jpa.dto.HostResponse;
import posty.jpa.repository.HostRepository;
import posty.jpa.repository.SpaceHostMapRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class HostServiceTest {

    @Autowired
    private SpaceHostMapRepository spaceHostMapRepository;

    @Autowired
    private HostRepository hostRepository;

    @Autowired
    private HostService hostService;

    @DisplayName("N+1 테스트")
    @Test
    void nPlusOneTest() {
        HostResponse hostResponse = hostService.getAllHosts1(PageRequest.of(0, 10));

        System.out.println();
        hostResponse.hosts().forEach(System.out::println);
    }
}
