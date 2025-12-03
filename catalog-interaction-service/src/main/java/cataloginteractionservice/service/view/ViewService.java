package cataloginteractionservice.service.view;

public interface ViewService {
    Long increase(Long catalogId, String userId);

    Long count(Long catalogId);
}
