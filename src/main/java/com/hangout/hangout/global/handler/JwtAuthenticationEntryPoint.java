package com.hangout.hangout.global.handler;

import static com.hangout.hangout.global.common.domain.entity.Constants.AUTH_EXCEPTION;

import com.hangout.hangout.global.exception.BaseException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        if (request.getAttribute(AUTH_EXCEPTION) != null) {
            resolver.resolveException(request, response, null,
                (BaseException) request.getAttribute(AUTH_EXCEPTION));
        } else {
            resolver.resolveException(request, response, null, authException);
        }
    }
}
