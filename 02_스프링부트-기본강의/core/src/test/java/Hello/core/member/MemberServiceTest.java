package Hello.core.member;

import Hello.core.AppConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MemberServiceTest {

    MemberService memberService;

    @BeforeEach
    void BeforeEach(){
        AppConfig appConfig = new AppConfig();
        memberService = appConfig.memberService();
    }

    @Test
    void join() {
        // given: 이게 주어졌을 때
        Member member = new Member(1L, "memberA", Grade.VIP);

        // when: 이런 상황에서
        memberService.join(member);
        Member findMember = memberService.findMember(1L);

        // then: 이런 결괏값을 기대
        Assertions.assertThat(findMember).isEqualTo(member);
    }
}
