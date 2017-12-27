<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <title>WebChat | 聊天</title>
    <jsp:include page="include/commonfile.jsp"/>
   <%-- <script src="${ctx}/static/plugins/sockjs/sockjs.js"></script>--%>
</head>
<body>
<jsp:include page="include/header.jsp"/>
<div class="am-cf admin-main">
    <jsp:include page="include/sidebar.jsp"/>

    <!-- content start -->
    <div class="admin-content">
        <div class="" style="width: 80%;float:left;">
            <!-- 聊天区 -->
            <div class="am-scrollable-vertical" id="chat-view" style="height: 450px;">
                <ul class="am-comments-list am-comments-list-flip" id="chat">
                </ul>
            </div>
            <!-- 输入区 -->
            <div class="am-form-group am-form">
                <textarea class="" id="message" name="message" rows="5"  placeholder="这里输入你想发送的信息..."></textarea>
            </div>
            <!-- 接收者 -->
            <div class="" style="float: left">
                <p class="am-kai">发送给 : <span id="sendto">全体成员</span><button class="am-btn am-btn-xs am-btn-danger" onclick="$('#sendto').text('全体成员')">复位</button></p>
                <span id="sendToVal" hidden></span>
            </div>
            <!-- 按钮区 -->
            <div class="am-btn-group am-btn-group-xs" style="float:right;">
                <button class="am-btn am-btn-default" type="button" onclick="getConnection()"><span class="am-icon-plug"></span> 连接</button>
                <button class="am-btn am-btn-default" type="button" onclick="closeConnection()"><span class="am-icon-remove"></span> 断开</button>
                <button class="am-btn am-btn-default" type="button" onclick="checkConnection()"><span class="am-icon-bug"></span> 检查</button>
                <button class="am-btn am-btn-default" type="button" onclick="clearConsole()"><span class="am-icon-trash-o"></span> 清屏</button>
                <button class="am-btn am-btn-default" type="button" onclick="sendMessage()"><span class="am-icon-commenting"></span> 发送</button>
            </div>
        </div>
        <!-- 列表区 -->
        <div class="am-panel am-panel-default" style="float:right;width: 20%;">
            <div class="am-panel-hd">
                <h3 class="am-panel-title">在线列表 [<span id="onlinenum"></span>]</h3>
            </div>
            <ul class="am-list am-list-static am-list-striped" >
                <li>图灵机器人 <button class="am-btn am-btn-xs am-btn-danger" id="tuling" data-am-button>未上线</button></li>
            </ul>
            <ul class="am-list am-list-static am-list-striped" id="list">
            </ul>
        </div>
    </div>
    <!-- content end -->
</div>

<jsp:include page="include/footer.jsp"/>

<script>
    $(function () {
        context.init({preventDoubleContext: false});
        context.settings({compress: true});
        context.attach('#chat-view', [
            {header: '操作菜单',},
            {text: '清理', action: clearConsole},
            {divider: true},
            {
                text: '选项', subMenu: [
                {header: '连接选项'},
                {text: '检查', action: checkConnection},
                {text: '连接', action: getConnection},
                {text: '断开', action: closeConnection}
            ]
            },
            {
                text: '销毁菜单', action: function (e) {
                e.preventDefault();
                context.destroy('#chat-view');
            }
            }
        ]);
    });
    if("${message}"){
        layer.msg('${message}', {
            offset: 0
        });
    }
    if("${error}"){
        layer.msg('${error}', {
            offset: 0,
            shift: 6
        });
    }
    $("#tuling").click(function(){
        var onlinenum = $("#onlinenum").text();
        if($(this).text() == "未上线"){
            $(this).text("已上线").removeClass("am-btn-danger").addClass("am-btn-success");
            showNotice("图灵机器人加入聊天室");
            $("#onlinenum").text(parseInt(onlinenum) + 1);
        }
        else{
            $(this).text("未上线").removeClass("am-btn-success").addClass("am-btn-danger");
            showNotice("图灵机器人离开聊天室");
            $("#onlinenum").text(parseInt(onlinenum) - 1)
        }
    });
    var wsServer = null;
    var ws = null;
    wsServer = "ws://" + location.host+"${pageContext.request.contextPath}" + "/chatServer/${userid}";
    ws = new WebSocket(wsServer); //创建WebSocket对象
    ws.onopen = function (evt) {
        layer.msg("已经建立连接", { offset: 0});
    };
    ws.onmessage = function (evt) {
        analysisMessage(evt.data);  //解析后台传回的消息,并予以展示
    };
    ws.onerror = function (evt) {
        layer.msg("产生异常", { offset: 0});
    };
    ws.onclose = function (evt) {
        layer.msg("已经关闭连接," + evt.reason, { offset: 0});
    };

    /**
     * 连接
     */
    function getConnection(){
        if(ws == null){
            ws = new WebSocket(wsServer); //创建WebSocket对象
            ws.onopen = function (evt) {
                layer.msg("成功建立连接!", { offset: 0});
            };
            ws.onmessage = function (evt) {
                analysisMessage(evt.data);  //解析后台传回的消息,并予以展示
            };
            ws.onerror = function (evt) {
                layer.msg("产生异常", { offset: 0});
            };
            ws.onclose = function (evt) {
                layer.msg("已经关闭连接," + evt.reason, { offset: 0});
            };
        }else{
            layer.msg("连接已存在!", { offset: 0, shift: 6 });
        }
    }

    /**
     * 关闭连接
     */
    function closeConnection(){
        if(ws != null){
            ws.close();
            ws = null;
            $("#list").html("");    //清空在线列表
            layer.msg("已经关闭连接", { offset: 0});
        }else{
            layer.msg("未开启连接", { offset: 0, shift: 6 });
        }
    }

    /**
     * 检查连接
     */
    function checkConnection(){
        if(ws != null){
            layer.msg(ws.readyState == 0? "连接异常":"连接正常", { offset: 0});
        }else{
            layer.msg("连接未开启!", { offset: 0, shift: 6 });
        }
    }

    /**
     * 发送信息给后台
     */
    function sendMessage(){
        if(ws == null){
            layer.msg("连接未开启!", { offset: 0, shift: 6 });
            return;
        }
        var message = $("#message").val();
        var to = $("#sendto").text() == "全体成员"? "": $("#sendToVal").text();
        if(message == null || message == ""){
            layer.msg("请不要惜字如金!", { offset: 0, shift: 6 });
            return;
        }
        $("#tuling").text() == "已上线"? tuling(message):console.log("图灵机器人未开启");  //检测是否加入图灵机器人
        ws.send(JSON.stringify({
            message : {
                content : message,
                from : '${userid}',
                to : to,      //接收人,如果没有则置空,如果有多个接收人则用,分隔
                time : getDateFull()
            },
            type : "message"
        }));
    }

    /**
     * 解析后台传来的消息
     * "massage" : {
     *              "from" : "xxx",
     *              "to" : "xxx",
     *              "content" : "xxx",
     *              "time" : "xxxx.xx.xx"
     *          },
     * "type" : {notice|message},
     * "list" : {[xx],[xx],[xx]}
     */
    function analysisMessage(data){
        data = JSON.parse(data);
        if(data.type == "message"){      //会话消息
            showChat(data.message);
        }
        if(data.type == "notice"){       //提示消息
            showNotice(data.message.content);
        }
        if(data.list != null && data.list != undefined){      //在线列表
            showOnline(data.list);
        }
    }

    /**
     * 展示提示信息
     */
    function showNotice(notice){
        $("#chat").append("<div><p class=\"am-text-success\" style=\"text-align:center\"><span class=\"am-icon-bell\"></span> "+notice+"</p></div>");
        var chat = $("#chat-view");
        chat.scrollTop(chat[0].scrollHeight);   //让聊天区始终滚动到最下面
    }

    /**
     * 展示会话信息
     */
    function showChat(message){
        var to = message.to == null || message.to == ""? "全体成员" : message.to;   //获取接收人
        var isSef = '${userid}' == message.from.userid ? "am-comment-flip" : "";   //如果是自己则显示在右边,他人信息显示在左边

        var pic;
        var url = message.from.userid + "/head";
        $.getJSON(url,function(data){
            pic = "/pic/" + data.profilehead;

            var html = "<li class=\"am-comment "+isSef+" am-comment-primary\"><a href=\"#link-to-user-home\"><img width=\"48\" height=\"48\" class=\"am-comment-avatar\" alt=\"\" src=\""+pic+"\"></a><div class=\"am-comment-main\">\n" +
                "<header class=\"am-comment-hd\"><div class=\"am-comment-meta\">   <a class=\"am-comment-author\" href=\"#link-to-user\">"+message.from.nickname+"</a> 发表于<time> "+message.time+"</time> 发送给: "+to+" </div></header><div class=\"am-comment-bd\"> <p>"+message.content+"</p></div></div></li>";
            $("#chat").append(html);
            $("#message").val("");  //清空输入区
            var chat = $("#chat-view");
            chat.scrollTop(chat[0].scrollHeight);   //让聊天区始终滚动到最下面

        });


    }

    /**
     * 展示在线列表
     */
    function showOnline(list){
        $("#list").html("");    //清空在线列表

        $.each(list, function(index, item){     //添加私聊按钮

            var username = item.nickname + "," + item.userid;

            var li = "<li>"+item.nickname+"</li>";
            if('${userid}' != item.userid){    //排除自己
                li = "<li>"+item.nickname+" <button type=\"button\" class=\"am-btn am-btn-xs am-btn-primary am-round\" onclick=\"addChat('"+username+"');\"><span class=\"am-icon-phone\"><span> 私聊</button></li>";
            }
            $("#list").append(li);
        });
        $("#onlinenum").text($("#list li").length);     //获取在线人数
    }

    /**
     * 图灵机器人
     * @param message
     */
    function tuling(message){
        var html;
        $.getJSON("http://www.tuling123.com/openapi/api?key=6ad8b4d96861f17d68270216c880d5e3&info=" + message,function(data){
            if(data.code == 100000){
                html = "<li class=\"am-comment am-comment-primary\"><a href=\"#link-to-user-home\"><img width=\"48\" height=\"48\" class=\"am-comment-avatar\" alt=\"\" src=\"${ctx}/static/source/img/robot.jpg\"></a><div class=\"am-comment-main\">\n" +
                        "<header class=\"am-comment-hd\"><div class=\"am-comment-meta\">   <a class=\"am-comment-author\" href=\"#link-to-user\">Robot</a> 发表于<time> "+getDateFull()+"</time> 发送给: ${userid}</div></header><div class=\"am-comment-bd\"> <p>"+data.text+"</p></div></div></li>";
            }
            if(data.code == 200000){
                html = "<li class=\"am-comment am-comment-primary\"><a href=\"#link-to-user-home\"><img width=\"48\" height=\"48\" class=\"am-comment-avatar\" alt=\"\" src=\"${ctx}/static/source/img/robot.jpg\"></a><div class=\"am-comment-main\">\n" +
                        "<header class=\"am-comment-hd\"><div class=\"am-comment-meta\">   <a class=\"am-comment-author\" href=\"#link-to-user\">Robot</a> 发表于<time> "+getDateFull()+"</time> 发送给: ${userid}</div></header><div class=\"am-comment-bd\"> <p>"+data.text+"</p><a href=\""+data.url+"\" target=\"_blank\">"+data.url+"</a></div></div></li>";
            }
            $("#chat").append(html);
            var chat = $("#chat-view");
            chat.scrollTop(chat[0].scrollHeight);
            $("#message").val("");  //清空输入区
        });
    }

    /**
     * 添加接收人
     */
    function addChat(username){

        var name = username.split(",");

        var sendToVal = $("#sendToVal");
        var sendto = $("#sendto");
        var receive = sendto.text() == "全体成员" ? "" : sendToVal.text() + ",";
        if(receive.indexOf(name[1]) == -1){    //排除重复
            sendto.text(receive + name[0]);
            sendToVal.text(receive + name[1]);
        }
    }

    /**
     * 清空聊天区
     */
    function clearConsole(){
        $("#chat").html("");
    }

    function appendZero(s){return ("00"+ s).substr((s+"").length);}  //补0函数

    function getDateFull(){
        var date = new Date();
        var currentdate = date.getFullYear() + "-" + appendZero(date.getMonth() + 1) + "-" + appendZero(date.getDate()) + " " + appendZero(date.getHours()) + ":" + appendZero(date.getMinutes()) + ":" + appendZero(date.getSeconds());
        return currentdate;
    }
</script>
</body>
</html>
