package life.tc.community.exception;

public enum CustomErrorCode implements ICustomizeErrorCode{

    QUESTION_NOT_FOUND(2001,"你找到的问题不存在了，要不要换个试试？"),
    TARGET_PARAM_NOT_FOUND(2002,"未选中任何问题或评论进行回复"),
    NO_LOGIN(2003,"当前未登录不能进行评论，请先登录"),
    SYS_ERROR(2004,"服务冒烟了，要不然你稍后再试试！！！"),
    TYPE_PARAM_NOT_FOUND(2005,"评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006,"你操作的评论不存在了，要不要换个试试？"),
    CONTENT_IS_EMPTY(2007,"输入内容不能为空！"),
    READ_NOTIFICATION_FAIL(2008,"所读消息不是自己的"),
    NOTIFICATION_NOT_FOUND(2009,"你找到的消息不存在了，要不要换个试试？");

    private String message;
    private Integer code;


    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    CustomErrorCode(Integer code,String message){
        this.message = message;
        this.code = code;
    }

}
