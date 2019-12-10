

function post() {
    var questionId =$("#question_id").val();
    var content =$("#comment_content").val();

    if(!content){
        alert("不能回复空内容~~~~~~");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        contentType:"application/json",
        data: JSON.stringify({
            "parentId":questionId,
            "content":content,
            "type":1
        }),
        success: function (response) {
            if(response.code == 200){
                window.location.reload();
            }else{
                /*if(response.code == 2003){
                    //如果错误异常是因为没有登陆，就会跳转一个确认框确认是否登陆，若点击确认，isAccepted == 1,就会跳转到登陆页面。
                    var isAccepted = confirm(response.message);
                    if(isAccepted){
                        window.open("https://github.com/login/oauth/authorize?client_id=47d5faefee6b98bdf850&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                    }
                } else{
                    alert(response.message);
                }*/
                alert(response.message);
            }
            console.log(response);
        },
        dataType: "json"
    });
}