<%--
  Created by IntelliJ IDEA.
  User: Vic
  Date: 05-09-17
  Time: 02:38 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'event.label', default: 'Event')}" />
    <title><g:message code="default.inscription.label" default="Inscription" /></title>
</head>
<body>
<div class="nav" role="navigation">
    <ul>
        <g:render template="/layouts/menu"/>
    </ul>
</div>
<h1>${eventInstance.eventName}</h1>
<div class="nav" role="navigation">
    <g:render template="/layouts/eventMenu"/>
</div>
<embed src="${createLinkTo(dir: 'images/pdf_files', file: filename)}" type="application/pdf" style="width: 800px; height: 600px;" />
</body>
</html>