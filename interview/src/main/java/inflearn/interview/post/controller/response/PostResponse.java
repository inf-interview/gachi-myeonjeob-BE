package inflearn.interview.post.controller.response;

import com.querydsl.core.annotations.QueryProjection;
import inflearn.interview.common.domain.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostResponse implements BaseDTO {

    public interface valid1{} // create, update
    public interface valid2{} // delete, like

    @NotNull(message = "유저 아이디 누락", groups = {valid1.class, valid2.class})
    private Long userId;

    private String userName;

    @NotNull(message = "게시글 아이디 누락", groups = valid2.class)
    private Long postId;

    @NotBlank(message = "제목은 필수항목 입니다.", groups = valid1.class)
    private String postTitle;

    @NotNull(message = "카테고리는 필수항목 입니다.", groups = valid1.class)
    private String category;

    private String[] tag;

    @NotBlank(message = "내용은 필수항목 입니다.", groups = valid1.class)
    private String content;


    private LocalDateTime time;
    private LocalDateTime updateTime;

    private int numOfLike;
    private int numOfComment;

    private String image;

    private boolean isLiked;

    @QueryProjection
    public PostResponse(Long userId, String userName, Long postId, String postTitle, String content, String category, LocalDateTime time, LocalDateTime updateTime, int numOfLike, Long numOfComment, String tag, String image) {
        this.userId = userId;
        this.userName = userName;
        this.postId = postId;
        this.postTitle = postTitle;
        this.content = content;
        this.category = category;
        this.time = time;
        this.updateTime = updateTime;
        this.numOfLike = numOfLike;
        this.numOfComment = Math.toIntExact(numOfComment);
        if (tag != null) {
            this.tag = entityToDtoTagConverter(tag);
        }
        this.image = image;
    }

    public PostResponse() {
    }

    private String[] entityToDtoTagConverter(String tag) {
        return tag.split("[.]");
    }
}
