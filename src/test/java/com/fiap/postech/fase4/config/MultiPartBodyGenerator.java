package com.fiap.postech.fase4.config;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.server.csrf.DefaultCsrfToken;
import org.springframework.util.MultiValueMap;

public class MultiPartBodyGenerator {
    public static final MultiValueMap<String, HttpEntity<?>> createMultipartBody(MultipartBodyBuilder builder) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);


        CsrfToken csrfToken = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "token_value");

        builder.part(csrfToken.getParameterName(), csrfToken.getToken());

        return builder.build();
    }
}
