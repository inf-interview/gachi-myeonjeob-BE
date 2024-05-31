package inflearn.interview.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import inflearn.interview.domain.PostComment;
import inflearn.interview.domain.User;
import inflearn.interview.domain.dto.LoginResponse;
import inflearn.interview.domain.dto.MyPostDTO;
import inflearn.interview.domain.dto.PostCommentDTO;
import inflearn.interview.repository.PostCommentRepository;
import inflearn.interview.repository.PostRepository;
import inflearn.interview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final KakaoProvider kakaoProvider;
    private final GoogleProvider googleProvider;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;

    public LoginResponse loginKakao(String code) { // 반환 값 User, RefreshToken
        String accessToken = kakaoProvider.getAccessToken(code);
        String kakaoInfo = kakaoProvider.getKakaoInfo(accessToken);

        log.info("kakaoInfo={}", kakaoInfo);

        JsonObject jsonObject = JsonParser.parseString(kakaoInfo).getAsJsonObject();

        //이메일 추출
        String email = jsonObject.getAsJsonObject("kakao_account").get("email").getAsString();

        //닉네임 추출
        String nickname = jsonObject.getAsJsonObject("properties").get("nickname").getAsString();

        //이미지 추출
        String image = jsonObject.getAsJsonObject("properties").get("thumbnail_image").getAsString();

        //유저 정보가 DB에 있는지 체크
        Optional<User> findUser = userRepository.findUserByEmailAndSocial(email, "KAKAO");
        if (findUser.isEmpty()) {
            User user = new User();
            user.setName(nickname);
            user.setEmail(email);
            user.setSocial("KAKAO");
            user.setCreatedAt(LocalDateTime.now());
            user.setImage(image);
            userRepository.save(user);

            return createLoginResponse(nickname, image, user.getUserId());

        }

        return createLoginResponse(nickname, image, findUser.get().getUserId());
    }

    public LoginResponse loginGoogle(String code) {

        String accessToken = googleProvider.getAccessToken(code);
        String googleInfo = googleProvider.getGoogleInfo(accessToken);

        log.info("info={}", googleInfo);

        JsonObject jsonObject = JsonParser.parseString(googleInfo).getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        String email = jsonObject.get("email").getAsString();
        String image = jsonObject.get("picture").getAsString();


        Optional<User> findUser = userRepository.findUserByEmailAndSocial(email, "GOOGLE");
        if (findUser.isEmpty()) {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setSocial("GOOGLE");
            user.setCreatedAt(LocalDateTime.now());
            user.setImage(image);
            userRepository.save(user);

            return createLoginResponse(name, image, user.getUserId());
        }

        return createLoginResponse(name, image, findUser.get().getUserId());

    }

    public List<MyPostDTO> getMyPost(Long userId, String category) {
        return postRepository.findPostByUserId(userId, category);
    }


    public List<PostCommentDTO> getMyComment(Long userId) {
        List<PostComment> comments = postCommentRepository.findCommentByUserId(userId);
        return comments.stream().map(comment -> new PostCommentDTO(comment)).toList();
    }

    private LoginResponse createLoginResponse(String username, String image, Long userId) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUsername(username);
        loginResponse.setImage(image);
        loginResponse.setUserId(userId);
        return loginResponse;
    }
}
