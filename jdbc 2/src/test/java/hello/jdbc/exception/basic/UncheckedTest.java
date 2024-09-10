package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {


    static class MyUncheckedException extends RuntimeException{
        public MyUncheckedException(String message){
            super(message);
        }
    }

    static class Repository{
        public void call(){
            throw new MyUncheckedException("ex");
        }


    }

    /**
     * unChecked 예의는
     * 예의를 잡거나 , 던지지 않아도 된다.
     * 예외를 잡지 않으면 자동으로 밖으로 던진다.
     */

    static class Service{
        Repository repository = new Repository();
        /**
         * 필요한 경우 예를 잡아서 처리하면 된다.
         *
         */

        public void callCatch(){

            try{
                repository.call();
            }catch(MyUncheckedException e){
                log.info("예외 처리, message={}",e.getMessage(),e);
            }

        }

        /**
         * 예외를 잡지 않아도 된다. 자연스럽게 상위로 넘어간다.
         * 체크 예와와 다르게 throw 예외를 선언하지 않아도 된다.
         */
        public void callThrow() throws MyUncheckedException{
            repository.call();
        }


    }

    @Test
    void unchecked_catch(){
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throw(){
        Service service = new Service();
        Assertions.assertThatThrownBy(()->service.callThrow())
                .isInstanceOf(MyUncheckedException.class);
    }




}
