package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;




/**
 * 트랜잭션 - 파라미터 연동 , 풀을 고려한 종료
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    // 시작
    public void accountTransfer(String fromId, String toId ,int money) throws SQLException {

        Connection con = dataSource.getConnection();
        try{
            con.setAutoCommit(false);
            bizLogic(con, fromId, toId,money);
            con.commit();

        }catch(Exception e){
            // 커넥션 실패시 롤백
            con.rollback();
            throw new IllegalStateException(e);
        }finally{
            if(con!=null){
                try{
                    con.setAutoCommit(true); // default 인 true 상태로 바꿔준다.
                    con.close();
                }catch(Exception e){
                    log.info("error",e);
                }finally{
                    release(con);
                }
            }

        }
    }

    private void bizLogic(Connection con,String fromId, String toId, int money) throws SQLException {
        //비지니스 로직 수행
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

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
