package catalogservice.service.catalog;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageLimitCalculator {

    //페이지네이션 공식 = (((n – 1) / k) + 1) * m * k + 1
    // • 현재 페이지(n)
    // • n > 0
    // • 페이지당 게시글 개수(m)
    // • 이동 가능한 페이지 개수(k)
    // • ((n - 1) / k)의 나머지는 버림
    public static Long calculatePageLimit(Long page, Long pageSize, Long movablePageCount) {
        return (((page - 1) / movablePageCount) + 1) * pageSize * movablePageCount + 1;
    }
}
