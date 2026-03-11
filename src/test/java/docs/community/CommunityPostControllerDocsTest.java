package docs.community;

import com.puppynoteserver.community.post.controller.CommunityPostController;
import com.puppynoteserver.community.post.service.CommunityPostReadService;
import com.puppynoteserver.community.post.service.CommunityPostWriteService;
import com.puppynoteserver.community.post.service.response.PostListResponse;
import com.puppynoteserver.community.post.service.response.PostResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommunityPostControllerDocsTest extends RestDocsSupport {

    private final CommunityPostReadService communityPostReadService = mock(CommunityPostReadService.class);
    private final CommunityPostWriteService communityPostWriteService = mock(CommunityPostWriteService.class);

    @Override
    protected Object initController() {
        return new CommunityPostController(communityPostReadService, communityPostWriteService);
    }

    @DisplayName("게시물 등록 API")
    @Test
    void createPost() throws Exception {
        given(communityPostWriteService.createPost(any())).willReturn(1L);

        mockMvc.perform(
                        post("/api/v1/community/posts")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(
                                        new PostCreateRequestDoc("오늘 산책 다녀왔어요 @강아지산책 @골든리트리버",
                                                List.of("uuid-key-1.jpg", "uuid-key-2.jpg"))
                                ))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("community-post-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("게시물 내용 (필수, 최대 2000자). @해시태그 형태로 해시태그 삽입 가능"),
                                fieldWithPath("imageKeys").type(JsonFieldType.ARRAY)
                                        .description("S3에 업로드된 이미지 키 목록 (선택, 최대 5개)").optional()
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.NUMBER).description("생성된 게시물 ID")
                        )
                ));
    }

    @DisplayName("게시물 목록 조회 API")
    @Test
    void getPosts() throws Exception {
        PostResponse postResponse = mock(PostResponse.class);
        given(postResponse.getPostId()).willReturn(1L);
        given(postResponse.getUserId()).willReturn(10L);
        given(postResponse.getUserNickname()).willReturn("퍼피노트");
        given(postResponse.getUserProfileUrl()).willReturn("https://s3.amazonaws.com/profile.jpg?X-Amz-Expires=3600");
        given(postResponse.getContent()).willReturn("오늘 산책 다녀왔어요 @강아지산책 @골든리트리버");
        given(postResponse.getImageUrls()).willReturn(List.of("https://s3.amazonaws.com/photo.jpg?X-Amz-Expires=3600"));
        given(postResponse.getHashtags()).willReturn(List.of("강아지산책", "골든리트리버"));
        given(postResponse.getCreatedDate()).willReturn(LocalDateTime.of(2026, 3, 11, 10, 0, 0));

        PostListResponse listResponse = PostListResponse.of(List.of(postResponse), 0, 1, 1L);

        given(communityPostReadService.getPosts(isNull(), anyInt(), anyInt()))
                .willReturn(listResponse);

        mockMvc.perform(
                        get("/api/v1/community/posts")
                                .param("page", "0")
                                .param("size", "20")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("community-post-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("keyword").description("검색 키워드 (해시태그명 또는 내용). 미입력 시 전체 목록 반환").optional(),
                                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값: 0)").optional(),
                                parameterWithName("size").description("페이지 크기 (기본값: 20)").optional()
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.posts").type(JsonFieldType.ARRAY).description("게시물 목록"),
                                fieldWithPath("data.posts[].postId").type(JsonFieldType.NUMBER).description("게시물 ID"),
                                fieldWithPath("data.posts[].userId").type(JsonFieldType.NUMBER).description("작성자 유저 ID"),
                                fieldWithPath("data.posts[].userNickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("data.posts[].userProfileUrl").type(JsonFieldType.STRING).description("작성자 프로필 이미지 Presigned URL").optional(),
                                fieldWithPath("data.posts[].content").type(JsonFieldType.STRING).description("게시물 내용"),
                                fieldWithPath("data.posts[].imageUrls").type(JsonFieldType.ARRAY).description("첨부 이미지 Presigned URL 목록"),
                                fieldWithPath("data.posts[].hashtags").type(JsonFieldType.ARRAY).description("해시태그 목록 (@기호 제외)"),
                                fieldWithPath("data.posts[].createdDate").type(JsonFieldType.STRING).description("작성일시"),
                                fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("data.totalCount").type(JsonFieldType.NUMBER).description("전체 게시물 수")
                        )
                ));
    }

    @DisplayName("게시물 검색 API")
    @Test
    void searchPosts() throws Exception {
        PostResponse postResponse = mock(PostResponse.class);
        given(postResponse.getPostId()).willReturn(1L);
        given(postResponse.getUserId()).willReturn(10L);
        given(postResponse.getUserNickname()).willReturn("퍼피노트");
        given(postResponse.getUserProfileUrl()).willReturn("https://s3.amazonaws.com/profile.jpg?X-Amz-Expires=3600");
        given(postResponse.getContent()).willReturn("오늘 산책 다녀왔어요 @강아지산책 @골든리트리버");
        given(postResponse.getImageUrls()).willReturn(List.of());
        given(postResponse.getHashtags()).willReturn(List.of("강아지산책", "골든리트리버"));
        given(postResponse.getCreatedDate()).willReturn(LocalDateTime.of(2026, 3, 11, 10, 0, 0));

        PostListResponse listResponse = PostListResponse.of(List.of(postResponse), 0, 1, 1L);

        given(communityPostReadService.getPosts(anyString(), anyInt(), anyInt()))
                .willReturn(listResponse);

        mockMvc.perform(
                        get("/api/v1/community/posts")
                                .param("keyword", "강아지산책")
                                .param("page", "0")
                                .param("size", "20")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("community-post-search",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("keyword").description("검색 키워드. Elasticsearch로 해시태그 및 내용 통합 검색").optional(),
                                parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                                parameterWithName("size").description("페이지 크기").optional()
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.posts").type(JsonFieldType.ARRAY).description("검색된 게시물 목록"),
                                fieldWithPath("data.posts[].postId").type(JsonFieldType.NUMBER).description("게시물 ID"),
                                fieldWithPath("data.posts[].userId").type(JsonFieldType.NUMBER).description("작성자 유저 ID"),
                                fieldWithPath("data.posts[].userNickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("data.posts[].userProfileUrl").type(JsonFieldType.STRING).description("작성자 프로필 이미지 Presigned URL").optional(),
                                fieldWithPath("data.posts[].content").type(JsonFieldType.STRING).description("게시물 내용"),
                                fieldWithPath("data.posts[].imageUrls").type(JsonFieldType.ARRAY).description("첨부 이미지 Presigned URL 목록"),
                                fieldWithPath("data.posts[].hashtags").type(JsonFieldType.ARRAY).description("해시태그 목록 (@기호 제외)"),
                                fieldWithPath("data.posts[].createdDate").type(JsonFieldType.STRING).description("작성일시"),
                                fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("data.totalCount").type(JsonFieldType.NUMBER).description("검색된 게시물 수")
                        )
                ));
    }

    @DisplayName("게시물 단건 조회 API")
    @Test
    void getPost() throws Exception {
        PostResponse postResponse = mock(PostResponse.class);
        given(postResponse.getPostId()).willReturn(1L);
        given(postResponse.getUserId()).willReturn(10L);
        given(postResponse.getUserNickname()).willReturn("퍼피노트");
        given(postResponse.getUserProfileUrl()).willReturn("https://s3.amazonaws.com/profile.jpg?X-Amz-Expires=3600");
        given(postResponse.getContent()).willReturn("오늘 산책 다녀왔어요 @강아지산책 @골든리트리버");
        given(postResponse.getImageUrls()).willReturn(List.of(
                "https://s3.amazonaws.com/photo1.jpg?X-Amz-Expires=3600",
                "https://s3.amazonaws.com/photo2.jpg?X-Amz-Expires=3600"
        ));
        given(postResponse.getHashtags()).willReturn(List.of("강아지산책", "골든리트리버"));
        given(postResponse.getCreatedDate()).willReturn(LocalDateTime.of(2026, 3, 11, 10, 0, 0));

        given(communityPostReadService.getPost(anyLong())).willReturn(postResponse);

        mockMvc.perform(
                        get("/api/v1/community/posts/{postId}", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("community-post-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("조회할 게시물 ID")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.postId").type(JsonFieldType.NUMBER).description("게시물 ID"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("작성자 유저 ID"),
                                fieldWithPath("data.userNickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("data.userProfileUrl").type(JsonFieldType.STRING).description("작성자 프로필 이미지 Presigned URL (유효시간 1시간)").optional(),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("게시물 내용"),
                                fieldWithPath("data.imageUrls").type(JsonFieldType.ARRAY).description("첨부 이미지 Presigned URL 목록 (유효시간 1시간)"),
                                fieldWithPath("data.hashtags").type(JsonFieldType.ARRAY).description("해시태그 목록 (@기호 제외)"),
                                fieldWithPath("data.createdDate").type(JsonFieldType.STRING).description("작성일시")
                        )
                ));
    }

    @DisplayName("게시물 수정 API")
    @Test
    void updatePost() throws Exception {
        doNothing().when(communityPostWriteService).updatePost(anyLong(), any());

        mockMvc.perform(
                        patch("/api/v1/community/posts/{postId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(
                                        new PostCreateRequestDoc("수정된 내용입니다 @강아지산책",
                                                List.of("uuid-key-new.jpg"))
                                ))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("community-post-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("수정할 게시물 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("수정할 내용 (필수, 최대 2000자). @해시태그 형태로 해시태그 삽입 가능"),
                                fieldWithPath("imageKeys").type(JsonFieldType.ARRAY)
                                        .description("새 S3 이미지 키 목록 (선택). 전송 시 기존 이미지 전체 교체").optional()
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 없음")
                        )
                ));
    }

    // 테스트용 내부 클래스
    static class PostCreateRequestDoc {
        public final String content;
        public final List<String> imageKeys;
        PostCreateRequestDoc(String content, List<String> imageKeys) {
            this.content = content;
            this.imageKeys = imageKeys;
        }
    }
}
