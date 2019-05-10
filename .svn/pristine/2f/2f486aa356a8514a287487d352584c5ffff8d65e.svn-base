<%--
  Created by IntelliJ IDEA.
  User: Vic
  Date: 2/09/17
  Time: 16:59
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
<div class="nav" role="navigation">
    <ul>
        <g:render template="/layouts/menu"/>
    </ul>
</div>
<h1>${eventInstance.eventName}</h1>
<div class="nav" role="navigation">
    <g:render template="/layouts/eventMenu"/>
</div>
<h2><g:message code="default.participants.edit.label" default="Edit participant information" /></h2>
<div id="layout_dos_columnas" class="row-fluid">
    <div id="columna_1" class="span5">
        <g:form name="Inscription" action="updateParticipant">
            <fieldset>
                <legend><g:message code="default.personalInformation.label" default="Personal Information" /></legend>
                <label><g:message code="default.name.label" default="Names" /></label>
                <g:textField name="name"  maxlength="40" style="width: 350px" required="" value="${participantInstance.name}" />
                <label><g:message code="default.surnames.label" default="Surnames" /></label>
                <g:textField name="surnames"  maxlength="40" style="width: 350px" required="" value="${participantInstance.surnames}" />
                <label><g:message code="default.idNumber.label" default="ID number" /></label>
                <g:textField name="idCardNumber"  maxlength="40" style="width: 150px" required="" value="${participantInstance.idCardNumber}" />
                <label><g:message code="default.country.label" default="Country" /></label>
                <g:textField name="country"  maxlength="40" style="width: 150px" value="${participantInstance.country}" />
                <label><g:message code="default.city.label" default="City" /></label>
                <g:textField name="city"  maxlength="40" style="width: 150px" value="${participantInstance.city}" />
                <legend><g:message code="professional.information.label" default="Professional Information" /></legend>
                <label><g:message code="default.unity.label" default="Institution" /></label>
                <g:textField name="unity"  maxlength="40" style="width: 350px" required="" value="${participantInstance.unity}" />
                <label><g:message code="default.position.label" default="Position" /></label>
                <g:textField name="position" maxlength="40" style="width: 250px" value="${participantInstance.position}"/>
                <label><g:message code="default.profession.label" default="Profession" /></label>
                <g:textField name="profession" maxlength="40" style="width: 250px" value="${participantInstance.profession}" />
                <legend><g:message code="contact.information.label" default="Contact Information" /></legend>
                <label><g:message code="default.email.label" default="Email" /></label>
                <input type="email" name="email" id="email" maxlength="40" style="width: 200px" required="" value="${participantInstance.email}" />
                <label><g:message code="default.phoneNumber.label" default="Phone Number" /></label>
                <g:textField name="phoneNumber"  maxlength="40" style="width: 150px" value="${participantInstance.phoneNumber}" />
                <g:if test="${eventInstance.groups.count {it.id}>0}">
                    <legend><g:message code="title.group.label" default="Group" /></legend>
                    <label for="nameGroup">
                        <g:message code="eventPhase.groupName.label" default="Group Name" />
                    </label>
                    <g:select name="group.id" from="${eventInstance.groups.sort {it.nameGroup}}" optionValue="nameGroup" optionKey="id" noSelection="['':'Seleccione una opción']" required='required' value="${participantInstance.group.id}"/>
                </g:if>
                <div>
                    <g:hiddenField name="id" value="${participantInstance.id}"/>
                    <g:submitButton name="save" value="${message(code: 'default.button.update.label', default: 'Update')}" class="btn-info"/>
                    <input type="reset" value="${message(code: 'default.reset.label', default: 'Save')}" class="btn-info" style="float: left !important;">
                </div>
            </fieldset>
        </g:form>
    </div>
</div>
</body>
</html>