package WOOMOOL.DevSquad.questionboard.entity;

import WOOMOOL.DevSquad.answer.entity.Answer;
import WOOMOOL.DevSquad.board.entity.Board;
import WOOMOOL.DevSquad.comment.entity.Comment;
import WOOMOOL.DevSquad.member.entity.MemberProfile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class QuestionBoard extends Board {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberProfile memberProfile;

    private String title;

    private String content;

    private int viewCount;

    private boolean isAnswered = false;

    @OneToMany(mappedBy = "questionBoard")
    private List<Answer> answerList = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime modifiedAt = LocalDateTime.now();



    @Enumerated(EnumType.STRING)
    private QuestionBoardStatus questionBoardStatus = QuestionBoardStatus.QUESTIONBOARD_POSTED;


    public enum QuestionBoardStatus {
        QUESTIONBOARD_POSTED("게시판 등록"),
        QUESTIONBOARD_DELETED("게시판 삭제");

        @Getter
        private String status;

        QuestionBoardStatus(String status) {
            this.status = status;
        }
    }


}