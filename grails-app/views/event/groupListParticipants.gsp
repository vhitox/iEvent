<%--
  Created by IntelliJ IDEA.
  User: Vic
  Date: 02-10-17
  Time: 10:50 PM
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
<script type="text/javascript">
    $(document).ready(function() {
        $(".accordion").accordion({
            active: false,
            collapsible: true
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
<h2><g:message code="default.participants.control.list.label" default="Control Participants List" /></h2>
<g:each in="${eventInstance.groups.sort {it.nameGroup}}" var="group">
    <h3>${group.nameGroup}</h3>
    <h4>(${group.participant.count {it.id}} <g:message code="default.participants.label" default="Participants" />)</h4>
    <table style="width: 800px;">
    <thead>
    <tr>
        <th style="width: 80px;"></th>
        <th><g:message code="default.name.label" default="Names" /></th>
    <th><g:message code="default.idNumberCi.label" default="ID" /></th>
    <g:each in="${eventInstance.phase.sort {it.orderPhase}}" var="eventPhase">
        <th title="${eventPhase.phase}" style="text-align: center" > <g:message code="event.phase.label"/> ${p}</th>
    </g:each>
    </tr>
    </thead>
    <tbody>
    <g:each in="${group.participant.sort {it.name}}" var="participant">
        <tr>
            <td>
                <g:link controller="event" action="editParticipant" id="${participant.id}" params="[event: eventInstance.id]" title="Editar" ><g:img dir="images" file="editmin.png" style="width: 20px;"/></g:link>
                <g:link controller="event" action="printInscription" id="${participant.id}" target="_blank" title="Imprimir Inscripción" ><g:img dir="images" file="simpleprint.png" style="width: 20px;"/></g:link>
            </td>
            <td>
                <div class="accordion" style="width: 350px;">
                    <h3>${participant.name+" "+participant.surnames}</h3>
                    <div>
                        <legend style="margin-top: 0px; "><g:message code="default.personalInformation.label" default="Personal Information" /></legend>
                        <label><g:message code="default.name.label" default="Names" />: </label>${participant.name}
                        <label><g:message code="default.surnames.label" default="Surnames" />: </label>${participant.surnames}
                        <label><g:message code="default.idNumber.label" default="ID number" />: </label>${participant.idCardNumber}
                        <label><g:message code="default.country.label" default="Country" />: </label>${participant.country}
                        <label><g:message code="default.city.label" default="City" />: </label>${participant.city}
                        <legend><g:message code="professional.information.label" default="Professional Information" /></legend>
                        <label><g:message code="default.unity.label" default="Institution" />: </label>${participant.unity}
                        <label><g:message code="default.position.label" default="Position" />: </label>${participant.position}
                        <label><g:message code="default.profession.label" default="Profession" />: </label>${participant.profession}
                        <legend><g:message code="contact.information.label" default="Contact Information" /></legend>
                        <label><g:message code="default.email.label" default="Email" />: </label>${participant.email}
                        <label><g:message code="default.phoneNumber.label" default="Phone Number" />: </label>${participant.phoneNumber}
                    </div>
                </div>
            </td>
            <td>${participant.idCardNumber}</td>
            <g:each in="${participant.phase.sort {it.phase.orderPhase}}" var="phase">
                <td style="text-align: center">
                    <g:if test="${phase.checkPhase}">
                        <input type="checkbox" name="check" value="true" checked="1" onclick="${remoteFunction(action: 'unCheckPhase', id: phase.id)}" />
                    </g:if><g:else>
                    <input type="checkbox" name="check" value="false" onclick="${remoteFunction(action: 'checkPhase', id: phase.id)}" />
                </g:else>
                </td>
            </g:each>
        </tr>
    </g:each>
    </tbody>
    </table>
</g:each>

</body>
</html>