<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<link rel="stylesheet" href="${ctx}/static/plugins/amaze/css/amazeui.min.css">
<link rel="stylesheet" href="${ctx}/static/plugins/amaze/css/admin.css">
<link rel="stylesheet" href="${ctx}/static/plugins/contextjs/css/context.standalone.css">
<script src="${ctx}/static/plugins/jquery/jquery-2.1.4.min.js"></script>
<script src="${ctx}/static/plugins/amaze/js/amazeui.min.js"></script>
<script src="${ctx}/static/plugins/amaze/js/app.js"></script>
<script src="${ctx}/static/plugins/layer/layer.js"></script>
<script src="${ctx}/static/plugins/laypage/laypage.js"></script>
<script src="${ctx}/static/plugins/contextjs/js/context.js"></script>
<%-- 上传图片插件 --%>
<link rel="stylesheet" href="${ctx}/static/plugins/amaze/css/amazeui.cropper.css">
<link rel="stylesheet" href="${ctx}/static/plugins/custom/css/custom_up_img.css">
<link rel="stylesheet" href="${ctx}/static/plugins/custom/css/font-awesome.4.6.0.css">
<script src="${ctx}/static/plugins/amaze/js/cropper.min.js"></script>
<script src="${ctx}/static/plugins/custom/js/custom_up_img.js"></script>
<script>
    var path = '${ctx}';
</script>