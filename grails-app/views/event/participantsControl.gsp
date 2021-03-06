<%--
  Created by IntelliJ IDEA.
  User: Vic
  Date: 31/08/17
  Time: 21:12
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityMarker" value="participantsControlOne"/>
    <g:set var="entityName" value="${message(code: 'participant.label', default: 'Participats')}" />
    <title><g:message code="default.participants.control.label" default="Participants" /></title>
</head>
<body>
<style>
    div.message{
        margin-top: 15px;
        padding: 20px;
        background-color: #f44336; /* Red */
        color: white;
        margin-bottom: 15px;
        font-size: 16px;
    }
</style>
<script type="text/javascript">
    $(document).ready(function() {
        $( document ).tooltip({
            track: true
        });
        $("#dataCapture").focus();
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
<ul class="eventControl">
    <g:each in="${eventInstance?.phase?.sort {it.orderPhase}}" var="phase">
        <g:if test="${phase == currentPhase}">
            <li style="background: #5B9BD5;"><g:link action="participantsControl" params="[id: eventInstance.id, currentPhase: phase.id]" style="font-weight: bold; color: #FFFFFF;">${phase.phase}</g:link></li>
        </g:if><g:else>
            <li><g:link action="participantsControl" params="[id: eventInstance.id, currentPhase: phase.id]">${phase.phase}</g:link> </li>
        </g:else>
    </g:each>
</ul>
<hr>
<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>
<div id="layout_dos_columnas" class="row-fluid">
    %{--<div id="columna_1" class="span5">
        <h2>Control de Participantes</h2>
        <fieldset>
            <legend>${currentPhase.phase}</legend>
            <g:form action="checkBarCode">
                <label><g:message code="barcode" default="Barcode"/> </label>
                <g:textField name="dataCapture" value=""/>
                <g:hiddenField name="phaseId" value="${currentPhase.id}"/>
                <g:hiddenField name="eventId" value="${eventInstance.id}"/>
                <g:hiddenField name="currentPhase" value="${currentPhase.id}"/>
                <g:hiddenField name="entity" value="${entityMarker}"/>
                <g:submitButton name="save" value="${message(code: 'register.codebar', default: 'Register')}" class="btn-info"/>
            </g:form>
        </fieldset>
    </div>--}%
    <h2>Participantes activos</h2>
    <table style="margin: 0px; width: 900px;">
        <thead>
        <tr>
            <th style="width: 300px;"><g:message code="default.participants.list.label"/></th>
            <th><g:message code="default.email.label"/></th>
            <th><g:message code="default.phone.label"/></th>
            <th><g:message code="default.unnity.min.label"/></th>
            <th><g:message code="default.country.label"/></th>
            <th><g:message code="default.city.label"/></th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${participantsPhases}" var="participantsPhase">
            <g:if test="${participantsPhase.checkPhase}">
                <tr>
                    <td title="ID: ${participantsPhase.participant.idCardNumber}">%{--<input type="checkbox" checked="checked" name="checkPhase" disabled="disabled"/>--}% ${participantsPhase.participant.name+" "+participantsPhase.participant.surnames}</td>
                    <td>${participantsPhase?.participant?.email}</td>
                    <td>${participantsPhase?.participant?.phoneNumber}</td>
                    <td>${participantsPhase?.participant?.unity}</td>
                    <td>${participantsPhase?.participant?.country}</td>
                    <td>${participantsPhase?.participant?.city}</td>
                </tr>
            </g:if>
        </g:each>
        </tbody>
    </table>
    <div id="columna_2" class="span7">

    </div>
</div>
</body>
</html>