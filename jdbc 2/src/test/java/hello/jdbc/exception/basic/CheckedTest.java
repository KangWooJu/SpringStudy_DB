package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedTest {

    /**
     * Excoption을 상속받은 예외는 체크 예외가 된다.
     */

    @Test
    void checked_catch(){
        Service service = new Service();
        service.callCatch();

    }



    static class MyCheckedExcption extends Exception{
        public MyCheckedExcption(String message) {
            super(message);
        }
    }

    @Test
    void checked_throw()  {
        Service service = new Service();
        Assertions.assertThatThrownBy(()-> service.callThrow())
                .isInstanceOf(MyCheckedExcption.class);

    }

    /**
     * chekced 예외는
     * 예외를 잡아서 처리하거나 , 던지거나 둘 중하나를 꼭 시행해야 한다.
     */
    static class Service{
        Repository repository = new Repository();

        /**
         * 예외를 잡아서 처리하는 코드
         *
         */

        public void callCatch(){
            try {
                repository.call();
            } catch (MyCheckedExcption e) { // 예외를 잡는다.
                log.info("예외처리 , message={}",e.getMessage(),e);
            }
        }



        /**
         * 체크 예외를 밖으로 던지는 코드
         * 체크 예외는 예외를 잡지 않고 밖으로 던지려면 throws 예를 메서드에 필수로 선언해야한다.
         * @throws MyCheckedExcption
         */
        public void callThrow() throws MyCheckedExcption{ // 예외를 던진다.
            repository.call();

        }


    }

    static class Repository{
        public void call() throws MyCheckedExcption{
            throw new MyCheckedExcption("ex");

        }

    }
}
