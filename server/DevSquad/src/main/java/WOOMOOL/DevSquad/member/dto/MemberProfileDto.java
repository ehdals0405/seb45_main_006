package WOOMOOL.DevSquad.member.dto;

import WOOMOOL.DevSquad.infoboard.dto.InfoBoardDto;
import WOOMOOL.DevSquad.projectboard.dto.ProjectDto;
import WOOMOOL.DevSquad.projectboard.entity.Project;
import WOOMOOL.DevSquad.studyboard.dto.StudyDto;
import WOOMOOL.DevSquad.studyboard.entity.Study;
import WOOMOOL.DevSquad.infoboard.entity.InfoBoard;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class MemberProfileDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Patch{
        @NotBlank(message = "닉네임을 입력해주세요")
        @Pattern(regexp = "^[A-Za-z0-9가-힣]{2,8}$",
                message = "닉네임은 2~8글자로 이루어져야 합니다.")
        private String nickname;
        @NotNull
        private String profilePicture;

        @NotNull
        private String githubId;
        @NotNull
        private String introduction;
        @NotNull
        private boolean listEnroll;
        @NotNull
        private Set<String> position;
        @NotNull
        private Set<String> stack;


    }
    // 패치 response
    @Getter
    @AllArgsConstructor
    public static class patchResponse{

        private String nickname;

        private String profilePicture;

        private String githubId;

        private String introduction;

        private boolean listEnroll;

        private Set<String> position;

        private Set<String> stack;


    }

    // 유저리스트 response
    @Getter
    @AllArgsConstructor
    public static class listResponse{

        private String profilePicture;

        private String nickname;

        private String githubId;

        private Set<String> position;

        private Set<String> stack;

    }

    //유저 프로필 response
    @Getter
    @AllArgsConstructor
    public static class detailResponse{

        private String profilePicture;

        private String nickname;

        private String githubId;

        private String introduction;

        private boolean listEnroll;

        private boolean OAuthUser;

        private Set<String> position;

        private Set<String> stack;

        private List<ProjectDto.previewResponseDto> projectList;

        private List<StudyDto.previewResponseDto> studyList;

        private List<InfoBoardDto.Response> infoBoardList;

        private List<String> blockMemberList;

        private LocalDateTime modifiedAt;

    }
    // todo:리스트를 눌렀을 때 나올 응답도 필요하면 추가해야 될 듯

}