package com.hangout.hangout.global.handler;

import static com.hangout.hangout.global.common.domain.entity.Constants.ACCESS_TOKEN_COOKIE_NAME;
import static com.hangout.hangout.global.common.domain.entity.Constants.REFRESH_TOKEN_COOKIE_NAME;
import static com.hangout.hangout.global.common.domain.repository.CookieAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import com.hangout.hangout.domain.user.entity.User;
import com.hangout.hangout.domain.user.repository.UserRepository;
import com.hangout.hangout.global.common.domain.repository.CookieAuthorizationRequestRepository;
import com.hangout.hangout.global.config.AppProperties;
import com.hangout.hangout.global.config.AppProperties.Registration;
import com.hangout.hangout.global.error.ResponseType;
import com.hangout.hangout.global.exception.AuthException;
import com.hangout.hangout.global.security.JwtService;
import com.hangout.hangout.global.security.UserPrincipal;
import com.hangout.hangout.global.util.CookieUtil;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * OAuth2 인증 성공 후 처리 과정을 담당
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
    private final AppProperties appProperties;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * OAuth2 인증 성공 시, customOAuth2UserService logic 처리 후 redirect-url로 반환되는 코드
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during the
     *                       authentication process.
     * @throws IOException 인증 성공 처리 과정에서 생기는 에러
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        clearAuthenticationAttributes(request, response);

        if (response.isCommitted()) {
            log.debug("Response has already been committed.");
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * redirect-url을 검증 및 반환 logic 처리
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during the
     *                       authentication process.
     * @return String (redirect-url)
     */
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
            .map(Cookie::getValue);

        if (redirectUri.isEmpty() || !isAuthorizedRedirectUri(redirectUri.get(),
            request.getServletPath())) {
            throw new AuthException(ResponseType.INVALID_REDIRECT_URL);
        }

        return generateToken(authentication, redirectUri.get(), response);
    }


    /**
     * 토큰 생성 및 저장 logic 처리
     *
     * @param authentication 사용자 인증 정보로, 이를 토대로 User를 가져옴
     * @param targetUrl      최종적으로 redirect url을 생성
     * @return Url (redirect url)
     */
    private String generateToken(Authentication authentication, String targetUrl,
        HttpServletResponse response
    ) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId())
            .orElseThrow(() -> new AuthException(ResponseType.USER_NOT_FOUND));

        writeTokenResponse(response, user);

        return UriComponentsBuilder.fromUriString(targetUrl)
            .encode(StandardCharsets.UTF_8)
            .build().toUriString();
    }

    /**
     * jwt 토큰 생성 및 response cookie에 jwt 추가
     *
     * @param response http response
     * @param user     인증된 user 정보
     */
    private void writeTokenResponse(HttpServletResponse response, User user) {
        UserDetails userDetails = UserPrincipal.create(user);
        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        jwtService.saveUserToken(user, refreshToken); // jwtService를 통해 토큰 저장
        jwtService.revokeAllUserTokens(user); // jwtService를 통해 토큰 폐기

        CookieUtil.addCookie(response, ACCESS_TOKEN_COOKIE_NAME, jwtToken, cookieAuthorizationRequestRepository.COOKIE_EXPIRE_SECONDS,  false);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieAuthorizationRequestRepository.COOKIE_EXPIRE_SECONDS, true);
    }

    /**
     * redirect-url 검증 처리
     *
     * @param uri                 client가 요청한 request의 redirect_url 값
     * @param requestRegistration Client 요청 url
     * @return boolean
     */
    private boolean isAuthorizedRedirectUri(String uri, String requestRegistration) {
        String[] uriInfo = requestRegistration.split("/");
        String registrationInfo = uriInfo[uriInfo.length - 1].toUpperCase();
        Registration registration;

        switch (registrationInfo) {
            case "GOOGLE":
                registration = appProperties.getGoogle();
                break;
            default:
                return false;
        }

        URI clientRedirectUri = URI.create(uri);
        URI authorizedUri = URI.create(registration.getRedirect_client());

        // 외부 요청의 redirect이 application에 등록한 redirect와 일치하는지 검증
        return authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
            && authorizedUri.getPort() == clientRedirectUri.getPort()
            && authorizedUri.getPath().equals(clientRedirectUri.getPath());
    }

    /**
     * 최초 인증 시 frontend의 redirect-url을 담아두었던 cookie 삭제
     *
     * @param request  the request which caused the successful authentication
     * @param response the response
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request,
        HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}

