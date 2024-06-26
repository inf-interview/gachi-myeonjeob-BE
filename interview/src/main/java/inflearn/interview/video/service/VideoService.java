package inflearn.interview.video.service;

import inflearn.interview.common.service.S3Service;
import inflearn.interview.question.service.QuestionRepository;
import inflearn.interview.user.infrastructure.UserEntity;
import inflearn.interview.video.domain.VideoDTO2;
import inflearn.interview.common.exception.OptionalNotFoundException;
import inflearn.interview.common.exception.RequestDeniedException;
import inflearn.interview.question.domain.Question;
import inflearn.interview.video.domain.Video;
import inflearn.interview.videolike.domain.VideoLike;
import inflearn.interview.videolike.service.VideoLikeRepository;
import inflearn.interview.videoquestion.domain.VideoQuestion;
import inflearn.interview.videoquestion.service.VideoQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoService {
    private final VideoRepository videoRepository;
    private final VideoLikeRepository videoLikeRepository;
    private final VideoDAOToDTOConverter DAOToDTOConverter;
    private final UserRepository userRepository;
    private final VideoQuestionRepository videoQuestionRepository;
    private final QuestionRepository questionRepository;
    private final S3Service s3Service;

    public VideoDTO2 getVideoById(Long videoId, UserEntity userEntity){

        Video video = videoRepository.findById(videoId).orElseThrow(OptionalNotFoundException::new);
        if (video.getExposure()) {
            VideoDTO2 videoDTO = new VideoDTO2(video);

            Optional<VideoLike> vl = videoLikeRepository.findByUserAndVideo(userEntity, video);
            videoDTO.setLiked(vl.isPresent());
            return videoDTO;
        } else {
            if (userEntity.getUserId().equals(video.getUserEntity().getUserId())) {
                VideoDTO2 videoDTO = new VideoDTO2(video);

                Optional<VideoLike> vl = videoLikeRepository.findByUserAndVideo(userEntity, video);
                videoDTO.setLiked(vl.isPresent());
                return videoDTO;
            }
            throw new RequestDeniedException();
        }
    }

    public void updateVideo(Long videoId, VideoDTO2 updatedVideo){
        Video originalVideo = videoRepository.findById(videoId).orElseThrow(OptionalNotFoundException::new);
        if (originalVideo.getUserEntity().getUserId().equals(updatedVideo.getUserId())) {
            originalVideo.setExposure(updatedVideo.isExposure());
            originalVideo.setVideoTitle(updatedVideo.getVideoTitle());
            originalVideo.setTag(dtoToEntityConverter(updatedVideo.getTags()));
        } else {
            throw new RequestDeniedException();
        }
    }

    public void deleteVideo(Long videoId, VideoDTO2 video){
        Video originalVideo = videoRepository.findById(videoId).orElseThrow(OptionalNotFoundException::new);
        if (originalVideo.getUserEntity().getUserId().equals(video.getUserId())) {
            s3Service.deleteVideo(originalVideo.getVideoLink(), originalVideo.getThumbnailLink());
            videoRepository.deleteById(videoId);
        } else {
            throw new RequestDeniedException();
        }
    }

    //정렬 : 최신순, 좋아요순, 댓글순

    public Page<VideoDTO2> getVideoList(String sortType, String keyword, int page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 6);
        return videoRepository.findAllVideoByPageInfo(sortType, keyword, pageRequest);
    }

    private String dtoToEntityConverter(String[] tags) {
        StringBuilder rawTag = new StringBuilder();
        for (String tag : tags) {
            rawTag.append(tag).append(".");
        }
        return rawTag.toString();
    }


    public Long completeVideo(VideoDTO2 videoDTO) {
        UserEntity userEntity = userRepository.findById(videoDTO.getUserId()).orElseThrow(OptionalNotFoundException::new);
        Video video = new Video();
        video.setUserEntity(userEntity);
        video.setExposure(videoDTO.isExposure());
        video.setVideoLink(videoDTO.getVideoLink());
        video.setVideoTitle(videoDTO.getVideoTitle());
        String[] tags = videoDTO.getTags();
        StringBuilder rawTag = new StringBuilder();
        for (String tag : tags) {
            rawTag.append(tag).append(".");
        }
        video.setTag(rawTag.toString());
        video.setThumbnailLink(videoDTO.getThumbnailLink());
        video.setTime(LocalDateTime.now());
        Video saved = videoRepository.save(video);

        Long[] questions = videoDTO.getQuestions();
        for (Long question : questions) {
            Question questionObject = questionRepository.findById(question).orElseThrow(OptionalNotFoundException::new);
            VideoQuestion videoQuestion = new VideoQuestion(video, questionObject.getId(), questionObject.getContent());
            videoQuestionRepository.save(videoQuestion);
        }

        return saved.getVideoId();

    }
}