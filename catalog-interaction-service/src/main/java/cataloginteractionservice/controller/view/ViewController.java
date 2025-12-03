package cataloginteractionservice.controller.view;

import cataloginteractionservice.service.view.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ViewController {
    private final ViewService viewService;

    @PostMapping("/catalogs/{catalogId}/views")
    public Long increase(
            @PathVariable("catalogId") Long catalogId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return viewService.increase(catalogId, userId);
    }

    @GetMapping("/catalogs/{catalogId}/views")
    public Long count(@PathVariable("catalogId") Long catalogId) {
        return viewService.count(catalogId);
    }

}
