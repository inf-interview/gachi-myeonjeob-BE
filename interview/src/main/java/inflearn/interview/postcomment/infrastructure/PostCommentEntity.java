package inflearn.interview.postcomment.infrastructure;

import inflearn.interview.post.infrastructure.PostEntity;
import inflearn.interview.postcomment.domain.PostComment;
import inflearn.interview.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class PostCommentEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity postEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    public static PostCommentEntity fromModel(PostComment postComment) {
        PostCommentEntity postCommentEntity = new PostCommentEntity();
        postCommentEntity.id = postComment.getId();
        postCommentEntity.postEntity = PostEntity.fromModel(postComment.getPost());
        postCommentEntity.userEntity = UserEntity.fromModel(postComment.getUser());
        postCommentEntity.content = postCommentEntity.getContent();
        postCommentEntity.createdAt = postCommentEntity.getCreatedAt();
        postCommentEntity.updatedAt = postCommentEntity.getUpdatedAt();
        return postCommentEntity;
    }

    public PostComment toModel() {
        return PostComment.builder()
                .id(id)
                .post(postEntity.toModel())
                .user(userEntity.toModel())
                .content(content)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
