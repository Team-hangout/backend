package com.hangout.hangout.domain.auth.service;

import static com.hangout.hangout.global.common.domain.entity.Constants.ACCESS_TOKEN_COOKIE_NAME;
import static com.hangout.hangout.global.common.domain.entity.Constants.AUTH_EXCEPTION;
import static com.hangout.hangout.global.common.domain.entity.Constants.REFRESH_TOKEN_COOKIE_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangout.hangout.domain.auth.dto.request.EmailCheckRequest;
import com.hangout.hangout.domain.auth.dto.request.LoginReqeust;
import com.hangout.hangout.domain.auth.dto.request.NicknameCheckRequest;
import com.hangout.hangout.domain.auth.dto.request.SignUpRequest;
import com.hangout.hangout.domain.auth.dto.response.AuthResponse;
import com.hangout.hangout.domain.auth.repository.TokenRepository;
import com.hangout.hangout.domain.user.entity.User;
import com.hangout.hangout.domain.user.repository.UserRepository;
import com.hangout.hangout.global.error.ResponseType;
import com.hangout.hangout.global.exception.AuthException;
import com.hangout.hangout.global.exception.NotFoundException;
import com.hangout.hangout.global.security.JwtService;
import com.hangout.hangout.global.security.UserPrincipal;
import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public Long signup(SignUpRequest request) {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(request.getEmail()))) {
            throw new AuthException(ResponseType.DUPLICATED_EMAIL);
        }
        if (Boolean.TRUE.equals(userRepository.existsByNickname(request.getNickname()))) {
            throw new AuthException(ResponseType.DUPLICATED_NICKNAME);
        }
        User user = request.toEntity(passwordEncoder);
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Boolean checkEmail(EmailCheckRequest request) {
        return userRepository.existsByEmail(request.getEmail());
    }

    public Boolean checkNickname(NicknameCheckRequest request) {
        return userRepository.existsByNickname(request.getNickname());
    }

    public AuthResponse login(LoginReqeust request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new NotFoundException(ResponseType.USER_NOT_FOUND));
        String jwtToken = jwtService.generateToken(UserPrincipal.create(user));
        String refreshToken = jwtService.generateRefreshToken(UserPrincipal.create(user));
        jwtService.revokeAllUserTokens(user);
        jwtService.saveUserToken(user, refreshToken);
        return createAuthResponse(jwtToken, refreshToken);

    }

    private AuthResponse createAuthResponse(String accessToken, String refreshToken) {
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    @Scheduled(fixedRate = 604800000) // 1 week
    @Transactional
    public void deleteExpiredAndRevokedTokens() {
        tokenRepository.deleteExpiredAndRevokedTokens();
    }

    public void refreshToken(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException {
        if (request.getAttribute(AUTH_EXCEPTION) == null) {
            String refreshToken = jwtService.getJwtFromRequest(request);
            String userEmail = jwtService.getUserEmailFromJWT(refreshToken);
            User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException(ResponseType.USER_NOT_FOUND));
            String accessToken = jwtService.generateToken(UserPrincipal.create(user));
            jwtService.revokeAllUserTokens(user);
            jwtService.saveUserToken(user, accessToken);
            var authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
        } else {
            throw (AuthException) request.getAttribute(AUTH_EXCEPTION);
        }
    }

    /**
     * 소셜 로그인에 따른 처리 과정 분리
     *
     * @param request        http request
     * @param registrationId 소셜 로그인 분리
     * @return 처리 후 AuthResponse 반환
     */
    public AuthResponse redirectLogin(HttpServletRequest request, String registrationId) {
        switch (registrationId.toUpperCase()) {
            case "GOOGLE":
                return redirectGoogleLogin(request);
            default:
                throw new AuthException(ResponseType.UNSUPPORTED_PROVIDER);
        }
    }

    /**
     * google 소셜 로그인 처리, cookie에 담긴 jwt 정보를 AuthResponse로 반환
     *
     * @param request http request
     * @return AuthResponse
     */
    private AuthResponse redirectGoogleLogin(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String jwtToken = "", refreshToken = "";

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(ACCESS_TOKEN_COOKIE_NAME)) {
                jwtToken = cookie.getValue();
            } else if (cookie.getName().equals(REFRESH_TOKEN_COOKIE_NAME)) {
                refreshToken = cookie.getValue();
            }
        }

        return AuthResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
    }
}

