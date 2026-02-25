package docs.storage;

import com.puppynoteserver.storage.controller.StorageController;
import com.puppynoteserver.storage.enums.BucketKind;
import com.puppynoteserver.storage.service.S3StorageService;
import docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StorageControllerDocsTest extends RestDocsSupport {

    private final S3StorageService s3StorageService = mock(S3StorageService.class);

    @Override
    protected Object initController() {
        return new StorageController(s3StorageService);
    }

    @DisplayName("파일 업로드 API")
    @Test
    void upload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "puppy-profile.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        given(s3StorageService.upload(any(), any(BucketKind.class)))
                .willReturn("550e8400-e29b-41d4-a716-446655440000.jpg");

        mockMvc.perform(
                        multipart("/api/v1/storage/{bucketKind}", BucketKind.PUPPY_PROFILE)
                                .file(file)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("storage-upload",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("bucketKind")
                                        .description("버킷 종류. 가능한 값: " + Arrays.toString(BucketKind.values()))
                        ),
                        requestParts(
                                partWithName("file")
                                        .description("업로드할 이미지 파일 (JPEG, PNG, GIF, WebP)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("httpStatus").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.STRING)
                                        .description("업로드된 파일의 S3 Object Key")
                        )
                ));
    }
}
