package inflearn.interview.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostComment> postComments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostLike> postLikes;

    private String title;

    private String content;

    private String tag;

    private String category;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private int numOfLike;

    public Post(User user, String title, String content, String tag, String category) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.numOfLike = 0;
    }
}
