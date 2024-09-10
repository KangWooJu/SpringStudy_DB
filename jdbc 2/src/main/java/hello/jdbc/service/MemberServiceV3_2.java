package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * 트랜잭션 - 트랜잭션 탬플릿
 *
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_2 {

   // private final DataSource dataSource;
    // private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    // 시작
    public void accountTransfer(String fromId, String toId ,int money) throws SQLException {

        txTemplate.executeWithoutResult((status)-> {
            try {
                bizLogic(fromId,toId,money); // 비지니스 로직이 잘 수행하면 커시
            } catch (SQLException e) {
                throw new IllegalStateException(e); // 예외의 경우 롤백
            }
        });
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
