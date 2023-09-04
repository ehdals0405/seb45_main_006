package WOOMOOL.DevSquad.member.entity;

import WOOMOOL.DevSquad.blockmember.entity.BlockMember;
import WOOMOOL.DevSquad.infoboard.entity.InfoBoard;
import WOOMOOL.DevSquad.level.entity.Level;
import WOOMOOL.DevSquad.position.entity.Position;
import WOOMOOL.DevSquad.projectboard.entity.Project;
import WOOMOOL.DevSquad.studyboard.entity.Study;
import WOOMOOL.DevSquad.stacktag.entity.StackTag;

import lombok.*;
import org.springframework.cglib.core.Block;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static WOOMOOL.DevSquad.member.entity.MemberProfile.MemberStatus.MEMBER_ACTIVE;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfile {

    public MemberProfile(String nickname) {
        this.nickname = nickname;
        this.oAuth2Member = true;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberProfileId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String profilePicture = "";

    @Column(nullable = false)
    private String githubId = "GithubId를 입력해주세요";

    @Column(nullable = false)
    private String introduction = "자기소개를 입력해주세요";

    @Column(updatable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    @Column
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @Column
    private boolean listEnroll = false;

    @Column
    private boolean oAuth2Member = false;

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus = MEMBER_ACTIVE;

    ////////매핑////////
    @OneToOne
    @JoinColumn(name = "memberId")
    private Member member;

    @OneToOne(mappedBy = "memberProfile",cascade = CascadeType.PERSIST)
    private Level level;

    @ManyToMany
    @JoinTable(name = "profilePosition",
            joinColumns = @JoinColumn(name = "memberProfileId"),
            inverseJoinColumns = @JoinColumn(name = "positionId")
    )
    private Set<Position> positions;

    @ManyToMany
    @JoinTable(name = "profileStackTag",
            joinColumns = @JoinColumn(name = "memberProfileId"),
            inverseJoinColumns = @JoinColumn(name = "stackTagId")
    )
    private Set<StackTag> stackTags;

    @OneToMany(mappedBy = "memberProfile")
    private List<BlockMember> blockMemberList;

    @OneToMany(mappedBy = "memberProfile")
    private List<Project> projectlist;

    @OneToMany(mappedBy = "memberProfile")
    private List<Study> studyList;

    @OneToMany(mappedBy = "memberProfile")
    private List<InfoBoard> infoBoardList;

    public enum MemberStatus {
        MEMBER_ACTIVE("활동중"),
        MEMBER_QUIT("탈퇴함");

        @Getter
        private String status;

        MemberStatus(String status) {
            this.status = status;
        }
    }

    public void addBlockMember(BlockMember blockMember) {
        this.getBlockMemberList().add(blockMember);
    }

    public void addLevel(Level level) {
        this.level = level;
        if (level.getMemberProfile() != this) {
            level.setMemberProfile(this);
        }

    }
}
