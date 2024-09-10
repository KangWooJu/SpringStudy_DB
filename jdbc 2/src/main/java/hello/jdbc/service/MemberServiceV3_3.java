package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.beans.Transient;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * 트랜잭션 - @Transactional AOP
 *
 */
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberServiceV3_3 {


    private final MemberRepositoryV3 memberRepository;

    // 시작
    // 어노테이션 하나로 끝!


    public void accountTransfer(String fromId, String toId ,int money) throws SQLException {
                bizLogic(fromId,toId,money); // 비지니스 로직이 잘 수행하면 커밋
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        //비지니스 로직 수행
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId,fromMember.getMoney()- money);
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");

        }
        memberRepository.update(toId, toMember.getMoney()+-money);
        validation(toMember);
        memberRepository.update(toId,toMember.getMoney()+ money);
        // 커넥션 커밋 -> 성공시 커밋
    }

    private void validation(Member toMember){
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
