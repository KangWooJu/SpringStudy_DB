package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_1 {

   // private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    // 시작
    // 파라미터 제거
    // PlatformTransactionManager 인터페이스의 객체를 받아옴 -> DI가 필요
    public void accountTransfer(String fromId, String toId ,int money) throws SQLException {

        // 트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        // 트랜잭션의 상태 정보를 포함하고 있어 커밋 , 롤백에 필요하다.
        // DefaultTransactionDefinition() : 트랜잭션과 관련된 옵션을 지정할 수 있다.

        try{
            bizLogic(fromId, toId,money);
            transactionManager.commit(status);

        }catch(Exception e){
            // 커넥션 실패시 롤백
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
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

    private void release(Connection con){
        if(con!=null){
            try{
                con.setAutoCommit(true);
                con.close();
            }catch(Exception e){
                log.info("error",e);
            }
        }
    }
}
