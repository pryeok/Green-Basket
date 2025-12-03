package cataloginteractionservice.service.view;

import cataloginteractionservice.entity.ViewCount;
import cataloginteractionservice.repository.view.ViewCountBackUpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ViewCountBackUpProcessor {
    private final ViewCountBackUpRepository viewCountBackUpRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void backUp(Long catalogId, Long viewCount) {
        int result = viewCountBackUpRepository.updateViewCount(catalogId, viewCount);
        if (result == 0) {
            viewCountBackUpRepository.findById(catalogId)
                    .ifPresentOrElse(ignore -> {},
                            () -> viewCountBackUpRepository.save(ViewCount.init(catalogId, viewCount))
                    );
        }
    }
}
