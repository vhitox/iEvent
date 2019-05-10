<%--
  Created by IntelliJ IDEA.
  User: Vic
  Date: 06-09-17
  Time: 09:32 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'event.label', default: 'Event')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
</head>
<body>
<script class="text/javascript">
    $(document).ready(function(event){
        $("#checkAll").change(function () {
            $(".ch").prop('checked', $(this).prop("checked"));
        });
    });
</script>
<a href="#show-event" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <g:render template="/layouts/menu"/>
    </ul>
</div>
<h2>Archiovs temporales en el Sistema</h2>
<g:if test="${files.count {it}>0}">
    <g:form method="post">
        <g:actionSubmit value="Eliminar archivo(s)" action="deleteTempFile" class="btn-info"/>
        <table style="width: 600px;">
            <thead>
            <tr>
                <th><input type="checkbox" id="checkAll"/> Check all</th>
                <th>NOMBRE DE ARCHIVO</th>
                <th>FECHA DE CREACIÓN</th>
            </tr>
            </thead>
            <g:set var="cont" value="${0}"/>
            <g:each in="${files}" var="file">
                <tbody>
                <tr>
                    <td><g:checkBox type="checkbox" name="delete_file" value="${file.fileName}" class="ch" checked="false" /></td>
                    <td><a href="${createLinkTo(dir: "images/pdf_files", file: file.fileName, absolute: true)}" target="_blank">${file.fileName}</a></td>
                    <td>${file.createdDate}</td>
                </tr>
                </tbody>
                <g:set var="cont" value="${cont+1}"/>
            </g:each>
        </table>

    </g:form>
</g:if><g:else>
    <div class="message">La carpeta de archivos temporales esta vacia</div>
</g:else>
</body>
</html>