package com.puppynoteserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puppynoteserver.user.users.controller.LoginController;
import com.puppynoteserver.user.users.service.LoginService;
import com.puppynoteserver.user.users.service.UserReadService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = {
        LoginController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected EntityManager entityManager;

    @MockitoBean
    protected LoginService loginService;

    @MockitoBean
    protected UserReadService userReadService;

}