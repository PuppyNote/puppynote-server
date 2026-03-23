package docs.community;

import com.puppynoteserver.community.post.like.controller.PostLikeController;
import com.puppynoteserver.community.post.like.service.PostLikeService;
import com.puppynoteserver.community.post.like.service.response.PostLikeToggleResponse;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostLikeControllerDocsTest extends RestDocsSupport {

    private final PostLikeService postLikeService = mock(PostLikeService.class);

    @Override
    protected Object initController() {
        return new PostLikeController(postLikeService);
    }

    @DisplayName("게시물 좋아요 토글 API")
    @Test
    void toggleLike() throws Exception {
        given(postLikeService.toggleLike(anyLong()))
                .willReturn(PostLikeToggleResponse.of(true, 42L));

        mockMvc.perform(
                        post("/api/v1/community/posts/{postId}/like", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("community-post-like-toggle",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("좋아요 토글할 게시물 ID")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.liked").type(JsonFieldType.BOOLEAN)
                                        .description("좋아요 상태 (true: 좋아요 등록, false: 좋아요 취소)"),
                                fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER)
                                        .description("게시물의 현재 총 좋아요 수")
                        )
                ));
    }
}
