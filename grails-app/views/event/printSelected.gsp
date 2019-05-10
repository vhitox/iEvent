<%--
  Created by IntelliJ IDEA.
  User: Vic
  Date: 14-11-17
  Time: 10:05 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'participant.label', default: 'Participats')}" />
    <title><g:message code="default.participants.label" default="Participants" /></title>
</head>
<body>
<script class="text/javascript">
    $(document).ready(function(event){
        $("#checkAll").change(function () {
            $(".ch").prop('checked', $(this).prop("checked"));
        });
    });
</script>
<div class="nav" role="navigation">
    <ul>
        <g:render template="/layouts/menu"/>
    </ul>
</div>
<h1>${eventInstance.eventName}</h1>
<div class="nav" role="navigation">
    <g:render template="/layouts/eventMenu"/>
</div>
<h2><g:message code="default.participants.print.selected.list.label" default="Print selected participants" /></h2>
<h3>Total de Inscritos (${eventInstance.participants.count {it.id}})</h3>

<div id="layout_dos_columnas" class="row-fluid" style="position: relative;">

    <div id="columna_1" class="span5">
        <g:formRemote name="remote" update="result" url="[action: 'printCodeBarSelected']">
            <g:hiddenField name="idEvent" value="${eventInstance.id}"/>
        <g:set var="p" value="${1}"/>

            <table style="width: 365px; margin: 0px; display: inline-block;">
                <thead>
                <tr>
                    <th><g:message code="default.name.label" default="Names" /></th>
                    <th><g:message code="default.idNumberCi.label" default="ID" /></th>
                    <th><input type="checkbox" id="checkAll" title="${message(code: 'default.selectAll.label', default: 'Select All')}"/></th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${eventInstance.participants.sort {it.name}}" var="participant">
                    <tr>
                        <td>${participant.name+" "+participant.surnames}</td>
                        <td>${participant.idCardNumber}</td>
                        <td>
                            <g:checkBox name="participants" value="${participant.id}" checked="false" class="ch"/>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
            <g:submitButton name="send" value="${message(code: 'default.print.selected.label' , default: 'Print Selected')}" style="display: inline-block;" class="btn-info"/>
            %{--<g:actionSubmit class="btn-info" style="display: inline-block;" name="send" action="printCodeBarSelected" value="${message(code: 'default.print.selected.label' , default: 'Print Selected')}"/>--}%
    </g:formRemote>
    </div>
    <div id="columna_2" class="span7" style="width: 47%;">
        <div id="result"></div>
    </div>

</div>

</body>
</html>