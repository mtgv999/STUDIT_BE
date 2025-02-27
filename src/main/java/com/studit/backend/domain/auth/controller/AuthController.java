package com.studit.backend.domain.auth.controller;

import com.studit.backend.domain.user.entity.KakaoUser;
import com.studit.backend.domain.user.entity.User;
import com.studit.backend.domain.user.repository.UserRepository;
import com.studit.backend.global.security.JwtTokenProvider;
import com.studit.backend.domain.oauth.kakao.KakaoService;
import com.studit.backend.domain.oauth.kakao.KakaoTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoTokenService kakaoTokenService;
    private final KakaoService kakaoService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;


    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.service_app_admin_key}")
    private String adminKey;

    @Value("${kakao.logout_redirect_uri}")
    private String logoutRedirectUri;

    /**
     * 인가(Authorization)코드 받기
     *
     * @return 카카오 콜백으로 인가코드 발급완료
     */
    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/auth/authorize");
    }
    @GetMapping("/authorize")
    public ResponseEntity<Void> authorize() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, kakaoAuthUrl)
                .build();
    }


    /**
     * 인가코드로 access 토큰 받기 -> 유저정보 가져오기 -> JWT 토큰 생성
     *
     * @param
     * @return
     */
    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> kakaoCallback(@RequestParam("code") String code) {

        // access토큰 받기
        String accessToken = kakaoTokenService.getKakaoToken(code);

        // 카카오 API에서 유저 정보 가져오기
        KakaoUser kakaoUser = kakaoService.getUserInfo(accessToken);

        // DB에 해당 유저가 있는지 확인
        User userInfo = userRepository.findById(kakaoUser.getId())
                .orElseGet(() -> { //없으면
                    User newUser = new User(
                            kakaoUser.getId(),
                            kakaoUser.getKakao_account().getProfile().getNickname(),
                            kakaoUser.getKakao_account().getProfile().getProfile_image_url(),
                            User.Role.ROLE_USER
                    );
                    return userRepository.save(newUser);
                });


        //  JWT 발급
        String jwt = jwtTokenProvider.createToken(userInfo.getId(),userInfo.getRole());

        // 응답에 JWT 포함
        Map<String, String> response = new HashMap<>();
        response.put("message", "로그인 성공");
        response.put("jwt-token", jwt);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .body(response);

    }


    /**
     * 카카오 연결해제  -> 페이지는 넘어간다만.. 근데 JWT토큰 처리는 또 따로 해줘야되고. DB에서도 ID로 정보 지워줘야함
     *
     * @return
     */
    @GetMapping("/kakao-logout")
    public ResponseEntity<String> kakaoLogout(HttpServletRequest request, HttpServletResponse response) {
        String logoutUrl = "https://kauth.kakao.com/oauth/logout" +
                "?client_id=" + clientId +
                "&logout_redirect_uri=" + logoutRedirectUri;

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", adminKey);

        // HttpEntity에 헤더 추가
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // RestTemplate을 이용해 GET 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> restResponse = restTemplate.exchange(logoutUrl, HttpMethod.GET, entity, String.class);

        return ResponseEntity.status(restResponse.getStatusCode()).body("카카오 로그아웃 완료");
    }


}


