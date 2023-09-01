package WOOMOOL.DevSquad.member.service;

import WOOMOOL.DevSquad.auth.userdetails.MemberAuthority;
import WOOMOOL.DevSquad.member.entity.Member;
import WOOMOOL.DevSquad.member.entity.MemberProfile;
import WOOMOOL.DevSquad.member.repository.MemberProfileRepository;
import WOOMOOL.DevSquad.member.repository.MemberRepository;
import WOOMOOL.DevSquad.position.service.PositionService;
import WOOMOOL.DevSquad.projectboard.entity.Project;
import WOOMOOL.DevSquad.projectboard.repository.ProjectRepository;
import WOOMOOL.DevSquad.projectboard.service.ProjectService;
import WOOMOOL.DevSquad.stacktag.service.StackTagService;
import WOOMOOL.DevSquad.studyboard.repository.StudyRepository;
import WOOMOOL.DevSquad.studyboard.service.StudyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static WOOMOOL.DevSquad.member.entity.MemberProfile.MemberStatus.MEMBER_ACTIVE;
import static WOOMOOL.DevSquad.member.entity.MemberProfile.MemberStatus.MEMBER_QUIT;

@Service
@Transactional
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final PositionService positionService;
    private final PasswordEncoder passwordEncoder;
    private final MemberAuthority memberAuthority;
    private final StackTagService stackTagService;

    private final ProjectRepository projectRepository;

    private final StudyRepository studyRepository;

    public MemberService(MemberRepository memberRepository, MemberProfileRepository memberProfileRepository, PositionService positionService, PasswordEncoder passwordEncoder, MemberAuthority memberAuthority, StackTagService stackTagService, ProjectRepository projectRepository, StudyRepository studyRepository) {
        this.memberRepository = memberRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.positionService = positionService;
        this.passwordEncoder = passwordEncoder;
        this.memberAuthority = memberAuthority;
        this.stackTagService = stackTagService;
        this.projectRepository = projectRepository;
        this.studyRepository = studyRepository;
    }

    // 멤버 생성
    public Member createMember(Member member) {

        verifyExistEmail(member.getEmail());
        // 기본 값 프로필 객체 생성하고 넣기
        MemberProfile memberProfile = new MemberProfile();
        memberProfile.setNickname(member.getNickname());
        member.addProfile(memberProfile);
        // 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);
        // 권한 추가
        List<String> roles = memberAuthority.createRoles(member.getEmail());
        member.setRoles(roles);


        Member createMember = memberRepository.save(member);

        return createMember;

    }

    // 프로필 수정
    public MemberProfile updateMemberProfile(MemberProfile memberProfile, List<String> position,List<String> stackTag) {

        Member findMember = findMemberFromToken();
        MemberProfile findMemberProfile = findMember.getMemberProfile();

        positionService.createPosition(position, findMemberProfile);
        stackTagService.createStackTag(stackTag, findMemberProfile);

        Optional.ofNullable(memberProfile.getNickname()).ifPresent(nickname -> findMemberProfile.setNickname(nickname));
        Optional.ofNullable(memberProfile.getProfilePicture()).ifPresent(profilePicture -> findMemberProfile.setProfilePicture(profilePicture));
        Optional.ofNullable(memberProfile.getGithubId()).ifPresent(githubId -> findMemberProfile.setGithubId(githubId));
        Optional.ofNullable(memberProfile.getIntroduction()).ifPresent(introduction -> findMemberProfile.setIntroduction(introduction));
        Optional.ofNullable(memberProfile.isListEnroll()).ifPresent(listEnroll -> findMemberProfile.setListEnroll(listEnroll));

        return findMemberProfile;
    }

    // 프로필 조회
    @Transactional(readOnly = true)
    public MemberProfile getMemberProfile() {

        Member findMember = findMemberFromToken();
        MemberProfile findMemberProfile = findMember.getMemberProfile();

        List<Project> projectList = getMemberProjectList(findMember.getMemberId());
        findMemberProfile.setProjectlist(projectList);

        return findMemberProfile;
    }

    // 유저 리스트 페이지
    public Page<MemberProfile> getMemberProfilePage(int page) {

        return memberProfileRepository.findAll(PageRequest.of(page, 10, Sort.by("memberProfileId")));
    }

    // 활동중, 등록 처리함, 블랙리스트에 없는 유저리스트 조회
    public List<MemberProfile> getMemberProfiles(Page<MemberProfile> memberProfilePage) {

        // 블랙리스트 멤버에 있는 memberId를 List로 추출
        List<Long> blockMemberList = findMemberFromToken().getMemberProfile().getBlockMemberList().stream()
                .map(blockMember -> blockMember.getBlockMemberId())
                .collect(Collectors.toList());

        // 추출한 memberId와 memberProfile 의 id가 같으면 필터링
        return memberProfilePage.getContent().stream()
                .filter(memberProfile -> memberProfile.getMemberStatus().equals(MEMBER_ACTIVE))
                .filter(memberProfile -> memberProfile.isListEnroll() == true)
                .filter(memberProfile -> !blockMemberList.contains(memberProfile.getMemberProfileId()))
                .collect(Collectors.toList());
    }

    // 회원 탈퇴 soft delete
    public void deleteMember() {

        Member findMember = findMemberFromToken();
        findMember.getMemberProfile().setMemberStatus(MEMBER_QUIT);
    }


    // 동일 이메일 가입 확인 메서드
    private void verifyExistEmail(String email) {

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        // todo: 예외처리하기
        if (optionalMember.isPresent()) throw new RuntimeException();
    }

    // 토큰으로 멤버객체 찾기
    public Member findMemberFromToken() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Member> optionalMember = memberRepository.findByEmail(username);

        //todo: 예외처리
        Member findMember = optionalMember.orElseThrow(() -> new RuntimeException());
        isDeletedMember(findMember);

        return findMember;

    }
    public Member findMember(long memberId){
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member findMember = optionalMember.orElseThrow(()-> new RuntimeException());

        return findMember;
    }

    // 중복 닉네임 확인
    public void checkNickname(String nickname) {

        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        optionalMember.ifPresent(member -> {
                    if (member.getMemberProfile().getMemberStatus().equals(MEMBER_ACTIVE)) {
                        //todo: 중복된 닉네임입니다.
                        throw new RuntimeException("중복");
                    }
                }
        );
    }
    // 비밀번호 변경 메서드
        public void changePassword (String rawPassword, String changePassword){

            checkPassword(rawPassword);
            String newEncodedPassword = passwordEncoder.encode(changePassword);

            Member updateMember = findMemberFromToken();
            updateMember.setPassword(newEncodedPassword);

        }

        // 비밀변호 변경 전 확인 메서드
        public void checkPassword (String rawPassword){

            Member findMember = findMemberFromToken();
            String encodedPassword = findMember.getPassword();

            if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
                // todo: 비밀번호가 다릅니다
                throw new RuntimeException();
            }
        }

        // 탈퇴한 회원인지 확인 - 토큰쓰면 필요 없을 듯?
        private void isDeletedMember (Member member){
            if (member.getMemberProfile().getMemberStatus().equals(MEMBER_QUIT)) {
                // todo: 예외처리하기
                throw new RuntimeException();
            }
        }

        // 특정 멤버가 가지고 있는 프로젝트 리스트
    private List<Project> getMemberProjectList(Long memberProfileId) {

        List<Project> projects = projectRepository.findByProjectStatusAndMemberProfile(memberProfileId);

        return projects;
    }
}



