<%--
  Created by IntelliJ IDEA.
  User: Vic
  Date: 18-10-17
  Time: 03:55 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'event.label', default: 'Event')}" />
    <title><g:message code="default.create.label" args="[entityName]" /></title>
</head>
<body>
<a href="#edit-event" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <g:render template="/layouts/menu"/>
    </ul>
</div>
<h1>${eventInstance.eventName}</h1>
<div class="nav" role="navigation">
    <g:render template="/layouts/eventMenu"/>
</div>
<h1>List</h1>
<g:set var="c" value="${1}"/>
<table style="width: 850px;">
    <thead>
    <tr>
        <th>Nº</th>
        <th><g:message code="default.name.label" default="Names"/></th>
        <th><g:message code="default.idNumberCi.label" default="ID"/></th>
        <th><g:message code="default.email.label" default="e-Mail"/></th>
        <th><g:message code="default.phone.label" default="Phone"/></th>
        <th><g:message code="default.unnity.min.label" default="Unit"/></th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${eventInstance?.participants}" var="participant">
        <tr>
            <td>${c}</td>
            <td>${participant?.name+" "+participant?.surnames}</td>
            <td>${participant?.idCardNumber}</td>
            <td>${participant?.email}</td>
            <td>${participant?.phoneNumber}</td>
            <td style="padding-left: 5px;">${participant?.unity}</td>
        </tr>
        <g:set var="c" value="${c+1}"/>
    </g:each>
    </tbody>
</table>
</body>
</html>