package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 * DataSourceUtils.getConnection()
 * DatSourceUtils.releaseConnection()
 */
@Slf4j
public class MemberRepositoryV3 {

    private final DataSource dataSource;

    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException{
        String sql = "insert into member(member_id,money) values (?, ?)"; // 넘겨줄 Query를 sql 에 작성
        // SQL Injection 공격을 피할 수 있는 방법 -> PrepareStatement으로 해결
        Connection con = null; // H2의 JDBC Connection 구현클래스의 객체
        PreparedStatement pstmt = null;

        try{
            con = getConnection(); // Connection 실행
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,member.getMemberId());
            pstmt.setInt(2,member.getMoney());
            pstmt.executeUpdate(); // 작성한 Query가 실행된다. -> 데이터 베이스에 저장
            return member;

        }catch (SQLException e) {
            log.error("DB Error",e);
            throw e;
        } finally{
            close(con,pstmt,null);
        }

    }

    public Member findById(String memberId) throws SQLException{
        String sql = "select * from member where member_id = ? ";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con =getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);

            rs = pstmt.executeQuery();
            //rs.next()를 실행하면 실제 데이터가 존재하는 곳 부터 시작
            if(rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }

        } catch (SQLException e){
            log.error("DB Error",e);
            throw e;
        } finally {
            close(con,pstmt,rs);
        }
    }

    // 커넥션을 유지하는 경우
    public Member findById(Connection con,String memberId) throws SQLException{
        String sql = "select * from member where member_id = ? ";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);

            rs = pstmt.executeQuery();
            //rs.next()를 실행하면 실제 데이터가 존재하는 곳 부터 시작
            if(rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }

        } catch (SQLException e){
            log.error("DB Error",e);
            throw e;
        } finally {
            // connection은 여기서 닫지 않는다.
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);

        }
    }

    // 업데이트 기능
    public void update(String memberId,int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection(); // Connection 실행
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,money);
            pstmt.setString(2,memberId);
            pstmt.executeUpdate(); // 작성한 Query가 실행된다. -> 데이터 베이스에 저장
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}",resultSize);

        }catch (SQLException e) {
            log.error("DB Error",e);
            throw e;
        } finally{
            close(con,pstmt,null);
        }
    }

    // 커넥션을 유지하는 경우
    public void update(Connection con,String memberId,int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        PreparedStatement pstmt = null;

        try{
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,money);
            pstmt.setString(2,memberId);
            pstmt.executeUpdate(); // 작성한 Query가 실행된다. -> 데이터 베이스에 저장
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}",resultSize);

        }catch (SQLException e) {
            log.error("DB Error",e);
            throw e;
        } finally{
            JdbcUtils.closeStatement(pstmt);
        }
    }

    // 삭제 기능
    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection(); // Connection 실행
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);
            pstmt.executeUpdate(); // 작성한 Query가 실행된다. -> 데이터 베이스에 저장

        }catch (SQLException e) {
            log.error("DB Error",e);
            throw e;
        } finally{
            close(con,pstmt,null);
        }

    }

    // 데이터베이스를 닫는 메소드
    private void close(Connection con, Statement stmt, ResultSet rs){

        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }
    private Connection getConnection() throws SQLException {
        // 주의 ! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        Connection con = DataSourceUtils.getConnection(dataSource);

        log.info("get Connection={},class = {}",con,con.getClass());
        return con;
    }
}
