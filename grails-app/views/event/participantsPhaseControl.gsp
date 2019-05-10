<%--
  Created by IntelliJ IDEA.
  User: Vic
  Date: 06-10-17
  Time: 11:49 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityMarker" value="participantsControlTwo"/>
    <g:set var="entityName" value="${message(code: 'participant.label', default: 'Participats')}" />
    <title><g:message code="default.participants.control.label" default="Participants" /></title>
</head>
<body>
<style>
div.alert{
    margin-top: 15px;
    padding: 20px;
    color: white;
    margin-bottom: 15px;
    font-size: 16px;
}
div.alert.ok{
    background-color: #87C23F;
}
div.alert.wrong{
    background-color: #f44336;
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
<div class="top">
    <div class="nav" role="navigation">
        <ul>
            <g:render template="/layouts/menu"/>
        </ul>
    </div>
    <h1>${eventInstance.eventName}</h1>
    <div class="nav" role="navigation">
        <g:render template="/layouts/eventMenu"/>
    </div>
</div>

<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>
<div id="layout_dos_columnas" class="row-fluid">
    <div id="columna_1" class="span5">
        <h2>
            ${phaseInstance.orderPhase} - ${phaseInstance.phase}
            <g:if test="${phaseInstance.datePhase}">
                <span style="color: #47C9AF; height: 10px;">( día ${phaseInstance.datePhase.format("dd/MM/yyyy")})</span>
            </g:if>
        </h2>
        <fieldset>
            %{--<legend>${phaseInstance.phase}</legend>--}%
            <g:form action="checkBarCode">
                <label><g:message code="barcode" default="Barcode"/> </label>
                <g:textField name="dataCapture" value=""/>
                <g:hiddenField name="phaseId" value="${phaseInstance.id}"/>
                <g:hiddenField name="eventId" value="${eventInstance.id}"/>
                <g:hiddenField name="entity" value="${entityMarker}"/>
                <g:submitButton name="save" value="${message(code: 'register.codebar', default: 'Register')}" class="btn-info"/>
            </g:form>
        </fieldset>
        <table style="width: 100% !important;">
            <thead>
            <tr>
                <th>Total Inscritos</th>
                <th>Total Ingresados</th>
                <th>Total Fase</th>
                <sec:ifAnyGranted roles="ROLE_SUPERADMIN">
                    <th>Comodín</th>
                </sec:ifAnyGranted>
            </tr>
            </thead>
            <tbody>
            <td>${eventInstance.participants.count {it.id}}</td>
            <td>${registerParticipants.count {it}}</td>
            <td>${activeParticipants.count {it}}</td>
                <sec:ifAnyGranted roles="ROLE_SUPERADMIN">
                    <td>${eventInstance.wildcard.counterWildcard}</td>
                </sec:ifAnyGranted>
            </tbody>
        </table>
    </div>
    <div id="columna_2" class="span7">
    </div>
</div>
</body>
</html>