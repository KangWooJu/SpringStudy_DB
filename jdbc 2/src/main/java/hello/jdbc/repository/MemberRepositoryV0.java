package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.DBConnectionUtil.getConnection;

/**
 * JDBC - DriverManager 사용하기
 */
@Slf4j
public class MemberRepositoryV0 {

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

    // 데이터베이스를 닫는 메소드
    private void close(Connection con, Statement stmt, ResultSet rs){

        if (rs != null){ // try- catch 문으로 rs 닫기
            try{
                rs.close();
            }catch (SQLException e){
                log.info("error",e);
            }
        }

        if(stmt !=null) { // try - catch 문으로 stmt 닫기
            try {
                stmt.close();

            } catch (SQLException e) {
                log.info("error", e);
            }

        }

        if (con != null){ // try - catch 문으로 con 닫기
            try{
                con.close();
            } catch ( SQLException e ){
                log.info("error",e);
            }
        }

    }
    private static void extracted() {
        getConnection();
    }
}
