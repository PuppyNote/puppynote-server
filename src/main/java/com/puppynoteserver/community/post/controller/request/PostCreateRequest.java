package com.puppynoteserver.community.post.controller.request;

import com.puppynoteserver.community.post.service.request.PostCreateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostCreateRequest {

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 2000, message = "내용은 2000자를 초과할 수 없습니다.")
    private String content;

    private List<String> imageKeys;

    public PostCreateServiceRequest toServiceRequest() {
        return PostCreateServiceRequest.builder()
                .content(content)
                .imageKeys(imageKeys)
                .build();
    }
}
