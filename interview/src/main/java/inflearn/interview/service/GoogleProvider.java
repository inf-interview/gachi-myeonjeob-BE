package inflearn.interview.service;

import inflearn.interview.domain.dto.SocialTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class GoogleProvider {

    @Value("${spring.google.client_id}")
    private String clientId;

    @Value("${spring.google.client_secret}")
    private String clientSecret;

    private final String GET_TOKEN_URL = "https://oauth2.googleapis.com/token";

    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", "http://localhost:8080/user/google/login"); // 리다이렉트 URL
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity1 = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<SocialTokenResponse> tokenResponse = restTemplate.postForEntity(GET_TOKEN_URL, requestEntity1, SocialTokenResponse.class);

        if (tokenResponse.getStatusCode() == HttpStatus.OK && tokenResponse.getBody() != null) {
            log.info("accessToken={}", tokenResponse.getBody().getAccessToken());
            return tokenResponse.getBody().getAccessToken();
        } else {
            return null;
        }
    }

    public String getGoogleInfo(String accessToken) {
        String requestInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.exchange(requestInfoUrl, HttpMethod.GET, requestEntity, String.class).getBody();
    }
}
