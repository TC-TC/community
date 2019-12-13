
/*
提交回复
*/
function post() {
    var questionId =$("#question_id").val();
    var content =$("#comment_content").val();
    comment2target(questionId,1,content);
}

function comment2target(targetId,type,content) {
    if(!content){
        alert("不能回复空内容~~~~~~");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType:"application/json",
        data: JSON.stringify({
            "parentId":targetId,
            "content":content,
            "type":type
        }),
        success: function (response) {
            if(response.code == 200){
                //当成功获取到评论，返回200时，直接刷新，将评论显示到页面当中
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

/*
提交二级评论
1.question.html点击评论进入community.js的点击响应函数comment
2.comment()通过重构函数comment2target将评论Post到服务器端
3.服务器端的commentController通过@RequestMapping的GET方法拿到具体评论的ID号
4.对ID号还有评论类型的枚举用listByTargetId()返回一个评论列表
5.在listByTargetId()里面对具体一级评论的所有二级评论进行去重，按时间排序等操作
*/
function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content =$("#input-"+commentId).val();
    comment2target(commentId,2,content);
}


/*
展开二级评论
*/
function collapseComment(e){
    //拿到具体评论框的id
    var id = e.getAttribute("data-id");
    var comments = $("#comment-"+id);

    //获取二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");
    if(collapse){
        //折叠二级评论
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    }else{
        var subCommentContainer = $("#comment-"+id);
        //判断一下若之前已经拿到了二级评论列表，及sub-comments下面除了评论子元素还有其他的元素
        //就不执行接口函数，直接展示
        if(subCommentContainer.children().length != 1){
            //class里面的collapse属性多加一个in就会展示二级评论
            comments.addClass("in");
            //标记二级评论展开状态
            e.setAttribute("data-collapse","in");
            e.classList.add("active");
        }else{
            //先将data获取到再展开二级评论
            $.getJSON( "/comment/"+id, function( data ) {
                console.log(data);
                //comment为
                //整体思路为1.首先获取到对应具体ID号的评论的comment
                //2.用一个循环对得到的json对象（所有二级评论）都转换成html对象（给它加上div框，class属性，具体的评论内容）c
                //3.用append函数对一级评论的comment下面加上二级评论
                $.each( data.data.reverse(), function( index,comment) {

                    //左边头像
                    var mediaLeftElement =  $( "<div/>",{
                        "class": "media-left",
                    }).append($( "<img/>",{
                        "class": "media-object img-rounded",
                        "src":comment.user.avatarUrl
                    }));

                    //右边姓名,评论内容，点赞icon
                    var mediaBodyElement = $( "<div/>",{
                        "class": "media-body",
                    }).append($( "<h5/>",{
                        "class": "media-heading",
                        "html":comment.user.name
                    })).append($( "<div/>",{
                        "html":comment.content
                    })).append($( "<div/>",{
                        "class": "menu",
                    }).append($( "<span/>",{
                        "class": "pull-right",
                        "html":moment(comment.gmtCreat).format('YYYY-MM-DD')
                    })));

                    //小整体
                    var mediaElement =  $( "<div/>",{
                        "class": "media",
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    //大整体
                    var commentElement = $( "<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments",
                    }).append(mediaElement);

                    ///放入二级评论 倒序放入
                    subCommentContainer.prepend(commentElement);
                });
                //class里面的collapse属性多加一个in就会展示二级评论
                comments.addClass("in");
                //标记二级评论展开状态
                e.setAttribute("data-collapse","in");
                e.classList.add("active");
            });
        }

    }

}