package org.seckill.exception;

/**
 * 秒杀关闭异常（运行期异常）
 *
 * Created by sunyinhui on 5/15/17.
 */
public class SeckillCloseException extends SeckillException {

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
