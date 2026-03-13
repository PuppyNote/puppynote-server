package com.puppynoteserver.community.post;

import com.google.firebase.messaging.FirebaseMessaging;
import com.puppynoteserver.community.post.document.PostDocument;
import com.puppynoteserver.global.config.FCMConfig;
import com.puppynoteserver.user.users.entity.User;
import com.puppynoteserver.user.users.entity.enums.Role;
import com.puppynoteserver.user.users.entity.enums.SnsType;
import com.puppynoteserver.user.users.repository.UserJpaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * MySQL vs Elasticsearch 검색 성능 벤치마크 테스트
 *
 * - 100만 건의 랜덤 게시물 데이터를 MySQL과 ES에 동시에 삽입한 후
 *   다양한 검색 시나리오에서 성능을 비교합니다.
 * - 테스트 완료 후 삽입된 모든 데이터를 삭제하여 롤백합니다.
 *
 * 실행 방법 (환경 변수 필요):
 *   ./gradlew test --tests "com.puppynoteserver.community.post.SearchPerformanceBenchmarkTest"
 */
@SpringBootTest
@ActiveProfiles("benchmark")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SearchPerformanceBenchmarkTest {

    private static final Logger log = LoggerFactory.getLogger(SearchPerformanceBenchmarkTest.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private UserJpaRepository userJpaRepository;

    // FCM은 벤치마크에서 불필요 — 초기화 오류 방지를 위해 Mock 처리
    @MockitoBean
    private FCMConfig fcmConfig;

    @MockitoBean
    private FirebaseMessaging firebaseMessaging;

    // ==================== 설정 상수 ====================
    private static final int TOTAL_POSTS     = 100_000;
    private static final int POST_BATCH      = 5_000;
    private static final int HASHTAG_BATCH   = 10_000;
    private static final int ES_BATCH        = 5_000;
    private static final int ITERATIONS      = 10;   // 각 검색 반복 횟수 (평균 계산)

    private static final String BENCHMARK_EMAIL = "benchmark-perf-test@puppynote.internal";

    // 해시태그 풀 (30개)
    private static final List<String> HASHTAG_POOL = List.of(
            "강아지", "고양이", "반려동물", "포메라니안", "골든리트리버",
            "말티즈", "푸들", "비숑", "시바이누", "진돗개",
            "산책", "밥시간", "간식", "목욕", "미용",
            "건강검진", "예방접종", "병원", "놀이시간", "훈련",
            "귀여운강아지", "멍멍이", "댕댕이", "냥이", "집사",
            "반려견", "반려묘", "펫스타그램", "강아지스타그램", "고양이스타그램"
    );

    // 컨텐츠 템플릿 (귀여운 포함 템플릿 2개 → 약 25% 게시물이 '귀여운' 포함)
    private static final List<String> TEMPLATES = List.of(
            "오늘 우리 %s 너무 귀여워서 사진 찍었어요!",
            "귀여운 %s 보고 하루 힐링했어요",
            "%s랑 산책했는데 정말 즐거웠어요",
            "우리 %s 간식 먹는 모습 보세요",
            "%s 목욕시켰더니 향기로워요",
            "오늘 %s 병원 다녀왔어요",
            "우리 %s 훈련 중이에요",
            "행복한 우리 %s 일상 기록"
    );

    private static final List<String> PET_NAMES = List.of(
            "포메라니안", "골든리트리버", "말티즈", "푸들", "비숑",
            "시바이누", "진돗개", "시츄", "치와와", "닥스훈트"
    );

    // 벤치마크 검색 키워드
    private static final String TARGET_HASHTAG  = "강아지";   // 해시태그 정확 검색
    private static final String TARGET_KEYWORD  = "귀여운";   // 컨텐츠 전문 검색
    private static final String AUTOCOMPLETE_KW = "강아지";   // 자동완성 검색

    private Long testUserId;
    private final Random random = new Random(42);  // 재현 가능한 시드

    // ==================== 데이터 준비 ====================

    @BeforeAll
    void setUp() {
        log.info("=================================================");
        log.info("  MySQL vs Elasticsearch 검색 성능 벤치마크 시작");
        log.info("  총 데이터: {}건", TOTAL_POSTS);
        log.info("=================================================");

        // 이전 실패로 남은 데이터 정리
        cleanUpByEmail();

        // 테스트 유저 생성
        User user = User.builder()
                .email(BENCHMARK_EMAIL)
                .password("benchmark-dummy-pw")
                .nickName("벤치마크테스터")
                .role(Role.USER)
                .snsType(SnsType.NORMAL)
                .useYn("Y")
                .build();
        user = userJpaRepository.save(user);
        testUserId = user.getId();
        log.info("테스트 유저 생성 완료 - userId: {}", testUserId);

        insertPostsToMysql();
        indexPostsToElasticsearch();
        createHashtagIndex();

        log.info("=== 데이터 준비 완료. 벤치마크 시작 ===\n");
    }

    private void insertPostsToMysql() {
        log.info("[MySQL] 게시물 삽입 시작...");
        long start = System.currentTimeMillis();
        LocalDateTime baseTime = LocalDateTime.now().minusDays(365);
        int totalBatches = TOTAL_POSTS / POST_BATCH;

        // 1단계: posts 삽입
        for (int b = 0; b < totalBatches; b++) {
            List<Object[]> params = new ArrayList<>(POST_BATCH);
            for (int i = 0; i < POST_BATCH; i++) {
                String pet = PET_NAMES.get(random.nextInt(PET_NAMES.size()));
                String content = String.format(TEMPLATES.get(random.nextInt(TEMPLATES.size())), pet);
                LocalDateTime createdAt = baseTime.plusSeconds((long) b * POST_BATCH + i);
                params.add(new Object[]{testUserId, content, createdAt, createdAt});
            }
            jdbcTemplate.batchUpdate(
                    "INSERT INTO posts (user_id, content, created_date, updated_date) VALUES (?, ?, ?, ?)",
                    params
            );
            if ((b + 1) % 20 == 0) {
                log.info("[MySQL] 게시물 삽입 진행: {}/{}", (long) (b + 1) * POST_BATCH, TOTAL_POSTS);
            }
        }
        log.info("[MySQL] 게시물 {}건 삽입 완료 ({}ms)", TOTAL_POSTS, System.currentTimeMillis() - start);

        // 2단계: post_hashtags 삽입
        log.info("[MySQL] 해시태그 삽입 시작...");
        start = System.currentTimeMillis();

        List<Long> allIds = jdbcTemplate.queryForList(
                "SELECT id FROM posts WHERE user_id = ? ORDER BY id ASC",
                Long.class, testUserId
        );
        log.info("[MySQL] 게시물 ID 로드 완료: {}건", allIds.size());

        List<Object[]> hashtagBuf = new ArrayList<>(HASHTAG_BATCH);
        long totalHashtags = 0;

        for (Long postId : allIds) {
            Set<String> selected = new LinkedHashSet<>();

            // TARGET_HASHTAG를 약 10% 확률로 강제 포함 → 약 10만 건이 '강아지' 태그 보유
            if (random.nextInt(10) == 0) {
                selected.add(TARGET_HASHTAG);
            }

            int tagCount = random.nextInt(3) + 1;  // 1~3개
            while (selected.size() < tagCount) {
                selected.add(HASHTAG_POOL.get(random.nextInt(HASHTAG_POOL.size())));
            }

            for (String tag : selected) {
                hashtagBuf.add(new Object[]{postId, tag});
            }

            if (hashtagBuf.size() >= HASHTAG_BATCH) {
                jdbcTemplate.batchUpdate(
                        "INSERT INTO post_hashtags (post_id, hashtag) VALUES (?, ?)",
                        hashtagBuf
                );
                totalHashtags += hashtagBuf.size();
                hashtagBuf.clear();
                if (totalHashtags % 500_000 == 0) {
                    log.info("[MySQL] 해시태그 삽입 진행: {}건", totalHashtags);
                }
            }
        }

        if (!hashtagBuf.isEmpty()) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO post_hashtags (post_id, hashtag) VALUES (?, ?)",
                    hashtagBuf
            );
            totalHashtags += hashtagBuf.size();
        }

        log.info("[MySQL] 해시태그 {}건 삽입 완료 ({}ms)", totalHashtags, System.currentTimeMillis() - start);
    }

    private void indexPostsToElasticsearch() {
        log.info("[ES] 인덱싱 시작...");
        long start = System.currentTimeMillis();
        int offset = 0;
        long totalIndexed = 0;

        while (true) {
            // MySQL에서 배치 조회
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT id, content, created_date FROM posts WHERE user_id = ? ORDER BY id ASC LIMIT ? OFFSET ?",
                    testUserId, ES_BATCH, offset
            );
            if (rows.isEmpty()) break;

            List<Long> batchIds = rows.stream()
                    .map(r -> ((Number) r.get("id")).longValue())
                    .toList();

            // 해당 배치의 해시태그 조회
            String placeholders = String.join(",", Collections.nCopies(batchIds.size(), "?"));
            List<Map<String, Object>> hashtagRows = jdbcTemplate.queryForList(
                    "SELECT post_id, hashtag FROM post_hashtags WHERE post_id IN (" + placeholders + ")",
                    batchIds.toArray()
            );

            Map<Long, List<String>> hashtagMap = new HashMap<>();
            batchIds.forEach(id -> hashtagMap.put(id, new ArrayList<>()));
            hashtagRows.forEach(row -> {
                Long postId = ((Number) row.get("post_id")).longValue();
                hashtagMap.computeIfAbsent(postId, k -> new ArrayList<>()).add((String) row.get("hashtag"));
            });

            // ES 벌크 인덱싱
            List<IndexQuery> queries = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                Long postId = ((Number) row.get("id")).longValue();
                LocalDateTime createdDate = ((java.sql.Timestamp) row.get("created_date")).toLocalDateTime();

                PostDocument doc = PostDocument.builder()
                        .postId(postId)
                        .userId(testUserId)
                        .userNickname("벤치마크테스터")
                        .content((String) row.get("content"))
                        .hashtags(hashtagMap.getOrDefault(postId, List.of()))
                        .createdDate(createdDate)
                        .build();

                queries.add(new IndexQueryBuilder()
                        .withId(String.valueOf(postId))
                        .withObject(doc)
                        .build());
            }

            elasticsearchOperations.bulkIndex(queries, PostDocument.class);
            totalIndexed += rows.size();
            offset += ES_BATCH;

            if (totalIndexed % 100_000 == 0) {
                log.info("[ES] 인덱싱 진행: {}/{}", totalIndexed, TOTAL_POSTS);
            }
        }

        // 즉시 검색 가능하도록 refresh
        elasticsearchOperations.indexOps(PostDocument.class).refresh();
        log.info("[ES] 인덱싱 완료 - {}건 / {}ms", totalIndexed, System.currentTimeMillis() - start);
    }

    private void createHashtagIndex() {
        // post_hashtags.hashtag 에 인덱스 생성 (공정한 비교)
        try {
            jdbcTemplate.execute(
                    "CREATE INDEX idx_bm_post_hashtags ON post_hashtags (hashtag)"
            );
            log.info("[MySQL] 해시태그 인덱스 생성 완료");
        } catch (Exception e) {
            log.info("[MySQL] 해시태그 인덱스 이미 존재하거나 생성 불필요: {}", e.getMessage());
        }
    }

    // ==================== 벤치마크 테스트 ====================

    @Test
    @Order(1)
    @DisplayName("[벤치마크 1] 해시태그 정확 검색: MySQL JOIN vs ES term query")
    void benchmarkHashtagExactSearch() {
        log.info("\n========== [벤치마크 1] 해시태그 정확 검색 ==========");
        log.info("키워드: '{}' | 반복: {}회", TARGET_HASHTAG, ITERATIONS);

        // --- MySQL: posts JOIN post_hashtags WHERE hashtag = ? ---
        long mysqlTotalMs = 0;
        long mysqlMatchCount = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            long s = System.nanoTime();

            mysqlMatchCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(DISTINCT p.id) FROM posts p " +
                    "JOIN post_hashtags ph ON p.id = ph.post_id " +
                    "WHERE ph.hashtag = ?",
                    Long.class, TARGET_HASHTAG
            );
            jdbcTemplate.queryForList(
                    "SELECT DISTINCT p.id FROM posts p " +
                    "JOIN post_hashtags ph ON p.id = ph.post_id " +
                    "WHERE ph.hashtag = ? " +
                    "ORDER BY p.created_date DESC LIMIT 20",
                    Long.class, TARGET_HASHTAG
            );

            mysqlTotalMs += (System.nanoTime() - s) / 1_000_000;
        }

        // --- ES: term query on hashtags field ---
        long esTotalMs = 0;
        long esMatchCount = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            long s = System.nanoTime();

            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q.term(t -> t.field("hashtags").value(TARGET_HASHTAG)))
                    .withPageable(PageRequest.of(0, 20))
                    .withSort(Sort.by(Sort.Direction.DESC, "createdDate"))
                    .build();
            SearchHits<PostDocument> hits = elasticsearchOperations.search(query, PostDocument.class);

            esTotalMs += (System.nanoTime() - s) / 1_000_000;
            esMatchCount = hits.getTotalHits();
        }

        printCompareResult("MySQL JOIN", mysqlTotalMs, mysqlMatchCount,
                           "ES term query", esTotalMs, esMatchCount);
    }

    @Test
    @Order(2)
    @DisplayName("[벤치마크 2] 컨텐츠 전문 검색: MySQL LIKE vs ES match query (nori 분석기)")
    void benchmarkContentFullTextSearch() {
        log.info("\n========== [벤치마크 2] 컨텐츠 전문 검색 ==========");
        log.info("키워드: '{}' | 반복: {}회", TARGET_KEYWORD, ITERATIONS);

        // --- MySQL: LIKE '%귀여운%' (인덱스 미사용 풀 스캔) ---
        long mysqlTotalMs = 0;
        long mysqlMatchCount = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            long s = System.nanoTime();

            mysqlMatchCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM posts WHERE content LIKE ? AND user_id = ?",
                    Long.class, "%" + TARGET_KEYWORD + "%", testUserId
            );
            jdbcTemplate.queryForList(
                    "SELECT id FROM posts WHERE content LIKE ? AND user_id = ? " +
                    "ORDER BY created_date DESC LIMIT 20",
                    Long.class, "%" + TARGET_KEYWORD + "%", testUserId
            );

            mysqlTotalMs += (System.nanoTime() - s) / 1_000_000;
        }

        // --- ES: match query with nori analyzer ---
        long esTotalMs = 0;
        long esMatchCount = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            long s = System.nanoTime();

            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q
                            .bool(b -> b
                                    .must(m -> m.match(match -> match
                                            .field("content")
                                            .query(TARGET_KEYWORD)))
                                    .filter(f -> f.term(t -> t
                                            .field("userId")
                                            .value(testUserId)))
                            )
                    )
                    .withPageable(PageRequest.of(0, 20))
                    .withSort(Sort.by(Sort.Direction.DESC, "createdDate"))
                    .build();
            SearchHits<PostDocument> hits = elasticsearchOperations.search(query, PostDocument.class);

            esTotalMs += (System.nanoTime() - s) / 1_000_000;
            esMatchCount = hits.getTotalHits();
        }

        printCompareResult("MySQL LIKE", mysqlTotalMs, mysqlMatchCount,
                           "ES match (nori)", esTotalMs, esMatchCount);
    }

    @Test
    @Order(3)
    @DisplayName("[벤치마크 3] 해시태그 자동완성: MySQL LIKE vs ES wildcard query")
    void benchmarkHashtagAutocomplete() {
        log.info("\n========== [벤치마크 3] 해시태그 자동완성 검색 ==========");
        log.info("키워드: '{}' | 반복: {}회", AUTOCOMPLETE_KW, ITERATIONS);

        // --- MySQL: DISTINCT hashtag LIKE '%강아지%' ---
        long mysqlTotalMs = 0;
        int mysqlTagCount = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            long s = System.nanoTime();

            List<String> tags = jdbcTemplate.queryForList(
                    "SELECT DISTINCT hashtag FROM post_hashtags WHERE hashtag LIKE ? LIMIT 20",
                    String.class, "%" + AUTOCOMPLETE_KW + "%"
            );

            mysqlTotalMs += (System.nanoTime() - s) / 1_000_000;
            mysqlTagCount = tags.size();
        }

        // --- ES: wildcard query on hashtags field ---
        long esTotalMs = 0;
        long esResultCount = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            long s = System.nanoTime();

            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q.wildcard(w -> w
                            .field("hashtags")
                            .wildcard("*" + AUTOCOMPLETE_KW + "*")))
                    .withPageable(PageRequest.of(0, 200))
                    .build();
            SearchHits<PostDocument> hits = elasticsearchOperations.search(query, PostDocument.class);

            esTotalMs += (System.nanoTime() - s) / 1_000_000;
            esResultCount = hits.getTotalHits();
        }

        log.info("--- 결과 ({} 반복 평균) ---", ITERATIONS);
        log.info("MySQL LIKE   | 평균 {}ms | 태그 {}개 반환", mysqlTotalMs / ITERATIONS, mysqlTagCount);
        log.info("ES wildcard  | 평균 {}ms | 매칭 문서 {}건", esTotalMs / ITERATIONS, esResultCount);
        logSpeedRatio(mysqlTotalMs, esTotalMs);
    }

    @Test
    @Order(4)
    @DisplayName("[벤치마크 4] 최신순 페이지네이션: MySQL ORDER BY vs ES sort query")
    void benchmarkPagination() {
        log.info("\n========== [벤치마크 4] 최신순 페이지네이션 (1페이지, 20건) ==========");
        log.info("반복: {}회", ITERATIONS);

        // --- MySQL: ORDER BY created_date DESC LIMIT 20 ---
        long mysqlTotalMs = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            long s = System.nanoTime();

            jdbcTemplate.queryForList(
                    "SELECT p.id, p.content FROM posts p " +
                    "JOIN users u ON p.user_id = u.id " +
                    "ORDER BY p.created_date DESC LIMIT 20",
                    Long.class
            );

            mysqlTotalMs += (System.nanoTime() - s) / 1_000_000;
        }

        // --- ES: match_all + sort by createdDate DESC ---
        long esTotalMs = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            long s = System.nanoTime();

            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q.matchAll(m -> m))
                    .withPageable(PageRequest.of(0, 20))
                    .withSort(Sort.by(Sort.Direction.DESC, "createdDate"))
                    .build();
            elasticsearchOperations.search(query, PostDocument.class);

            esTotalMs += (System.nanoTime() - s) / 1_000_000;
        }

        log.info("--- 결과 ({} 반복 평균) ---", ITERATIONS);
        log.info("MySQL ORDER BY  | 평균 {}ms", mysqlTotalMs / ITERATIONS);
        log.info("ES sort query   | 평균 {}ms", esTotalMs / ITERATIONS);
        logSpeedRatio(mysqlTotalMs, esTotalMs);
    }

    // ==================== 데이터 정리 ====================

    @AfterAll
    void tearDown() {
        log.info("\n=================================================");
        log.info("  테스트 데이터 롤백 시작");
        log.info("=================================================");

        // 해시태그 인덱스 제거 (테스트용으로 생성한 것만)
        try {
            jdbcTemplate.execute("DROP INDEX idx_bm_post_hashtags ON post_hashtags");
            log.info("[MySQL] 벤치마크 인덱스 삭제 완료");
        } catch (Exception e) {
            log.info("[MySQL] 벤치마크 인덱스 삭제 불필요: {}", e.getMessage());
        }

        cleanUpByUserId(testUserId);
        log.info("=================================================");
        log.info("  데이터 롤백 완료");
        log.info("=================================================");
    }

    // ==================== 헬퍼 메서드 ====================

    /**
     * 이전 실패로 남은 데이터를 이메일 기준으로 정리
     */
    private void cleanUpByEmail() {
        List<Long> ids = jdbcTemplate.queryForList(
                "SELECT id FROM users WHERE email = ?",
                Long.class, BENCHMARK_EMAIL
        );
        if (ids.isEmpty()) return;

        log.info("이전 벤치마크 데이터 정리 중 (userId: {})...", ids.get(0));
        cleanUpByUserId(ids.get(0));
    }

    private void cleanUpByUserId(Long userId) {
        if (userId == null) return;

        // post_hashtags 삭제 (MySQL JOIN DELETE는 LIMIT 미지원)
        int deleted = jdbcTemplate.update(
                "DELETE ph FROM post_hashtags ph " +
                "JOIN posts p ON ph.post_id = p.id " +
                "WHERE p.user_id = ?",
                userId
        );
        log.info("[MySQL] post_hashtags 삭제: {}건", deleted);

        // posts 삭제
        deleted = jdbcTemplate.update(
                "DELETE FROM posts WHERE user_id = ?",
                userId
        );
        log.info("[MySQL] posts 삭제: {}건", deleted);

        // ES 삭제 (userId 기준 delete by query)
        try {
            NativeQuery deleteQuery = NativeQuery.builder()
                    .withQuery(q -> q.term(t -> t.field("userId").value(userId)))
                    .build();
            elasticsearchOperations.delete(deleteQuery, PostDocument.class);
            elasticsearchOperations.indexOps(PostDocument.class).refresh();
            log.info("[ES] 데이터 삭제 완료");
        } catch (Exception e) {
            log.warn("[ES] 데이터 삭제 실패 (수동 확인 필요): {}", e.getMessage());
        }

        // 테스트 유저 삭제
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
        log.info("[MySQL] 테스트 유저 삭제 완료");
    }

    private void printCompareResult(String mysqlLabel, long mysqlTotalMs, long mysqlCount,
                                    String esLabel, long esTotalMs, long esCount) {
        long mysqlAvg = mysqlTotalMs / ITERATIONS;
        long esAvg = esTotalMs / ITERATIONS;

        log.info("--- 결과 ({} 반복 평균) ---", ITERATIONS);
        log.info("{} | 평균 {}ms | 매칭: {}건", mysqlLabel, mysqlAvg, mysqlCount);
        log.info("{} | 평균 {}ms | 매칭: {}건", esLabel, esAvg, esCount);
        logSpeedRatio(mysqlTotalMs, esTotalMs);
    }

    private void logSpeedRatio(long mysqlTotalMs, long esTotalMs) {
        long mysqlAvg = mysqlTotalMs / ITERATIONS;
        long esAvg = esTotalMs / ITERATIONS;

        if (mysqlAvg > esAvg && esAvg > 0) {
            log.info(">>> ES가 MySQL보다 {}배 빠름",
                    String.format("%.1f", (double) mysqlAvg / esAvg));
        } else if (esAvg > mysqlAvg && mysqlAvg > 0) {
            log.info(">>> MySQL이 ES보다 {}배 빠름",
                    String.format("%.1f", (double) esAvg / mysqlAvg));
        } else {
            log.info(">>> 두 방식의 성능이 유사함");
        }
    }
}
