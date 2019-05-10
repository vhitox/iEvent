<%--
  Created by IntelliJ IDEA.
  User: Vic
  Date: 14/10/17
  Time: 12:52
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title></title>
</head>
<body>
<h1>${eventInstance.eventName}</h1>

<g:form action="inscription" params="[id: eventInstance.id]">
    <fieldset>
        <legend>Evento protegido por contraseña</legend>
        <input type="password" value="" name="p"/>
        %{--<g:hiddenField name="id" value="${eventInstance.id}"/>--}%
        <g:submitButton name="enviar" value="Enviar" class="btn-info" style="float: left !important;"/>
    </fieldset>
</g:form>
</body>
</html>