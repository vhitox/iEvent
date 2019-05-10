<%--
  Created by IntelliJ IDEA.
  User: Vic
  Date: 06-10-17
  Time: 11:39 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
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
<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>
<ul class="eventControlTwo">
    <g:each in="${eventInstance?.phase?.sort {it.orderPhase}}" var="phase">
        <li style="background: #5B9BD5; text-align: center;">
            <g:link action="participantsPhaseControl" params="[id: eventInstance.id, currentPhase: phase.id]" style="font-weight: bold; color: #FFFFFF; width: 100%;">
                <img src="${createLink(controller: 'phase', action: 'showImage', id: phase.phase.id)}" alt="" style="display: block; margin: 0px auto; display: block; background-color: #FFFFFF; width: 100px; height: 100px; border-radius: 5px;">
                <div>
                    ${phase.orderPhase} - ${phase.phase}
                    <g:if test="${phase.datePhase}">
                        <span style="font-size: 11px; color: #DEEAF6; height: 10px; display: block;">(${phase.datePhase.format("dd/MM/yyyy")})</span>
                    </g:if>
                </div>
            </g:link>
        </li>
    </g:each>
</ul>

</body>
</html>