<%--
  Created by IntelliJ IDEA.
  User: Vic
  Date: 30-08-17
  Time: 09:52 AM
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
<style>
div.message{
    margin-top: 15px;
    padding: 20px;
    background-color: #8394A8; /* Red */
    color: white;
    margin-bottom: 15px;
    font-size: 16px;
}
</style>
<h1>${eventInstance.eventName}</h1>
<h2>Registro de Participantes</h2>
<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>
<div id="layout_dos_columnas" class="row-fluid" style="margin-top: 20px;">
    <div id="columna_1" class="span5">
        %{--<h2>Por favor, complete el formulario</h2>--}%
        <g:form name="Inscription" action="saveInscription" useToken="true">
            <fieldset>
                <legend><g:message code="default.personalInformation.label" default="Personal Information" /></legend>
                <label><g:message code="default.name.label" default="Names" /></label>
                <g:textField name="name"  maxlength="40" style="width: 350px" required="" />
                <label><g:message code="default.surnames.label" default="Surnames" /></label>
                <g:textField name="surnames"  maxlength="40" style="width: 350px" required="" />
                <label><g:message code="default.idNumber.label" default="ID number" /></label>
                <g:textField name="idCardNumber"  maxlength="40" style="width: 150px" required="" />
                <label><g:message code="default.country.label" default="Country" /></label>
                <g:textField name="country"  maxlength="40" style="width: 150px" />
                <label><g:message code="default.city.label" default="City" /></label>
                <g:textField name="city"  maxlength="40" style="width: 150px" />
                <legend><g:message code="professional.information.label" default="Professional Information" /></legend>
                <label><g:message code="default.unity.label" default="Institution" /></label>
                <g:textField name="unity"  maxlength="40" style="width: 350px" required="" />
                <label><g:message code="default.position.label" default="Position" /></label>
                <g:textField name="position"  maxlength="40" style="width: 250px" />
                <label><g:message code="default.profession.label" default="Profession" /></label>
                <g:textField name="profession"  maxlength="40" style="width: 250px" />
                <legend><g:message code="contact.information.label" default="Contact Information" /></legend>
                <label><g:message code="default.email.label" default="Email" /></label>
                <input type="email" name="email" id="email" maxlength="40" style="width: 200px" required="" />
                <label><g:message code="default.phoneNumber.label" default="Phone Number" /></label>
                <g:textField name="phoneNumber"  maxlength="40" style="width: 150px" />
                <g:if test="${eventInstance.groups.count {it.id}>0}">
                    <legend><g:message code="title.group.label" default="Group" /></legend>
                    <label for="nameGroup">
                        <g:message code="eventPhase.groupName.label" default="Group Name" />
                    </label>
                    <g:select name="group.id" from="${eventInstance.groups.sort {it.nameGroup}}" optionValue="nameGroup" optionKey="id" noSelection="['':'Seleccione una opción']" required='required' style="width: 400px;"/>
                </g:if>

                <div>
                    <g:hiddenField name="idEvent" value="${eventInstance.id}"/>
                    <g:submitButton name="save" value="${message(code: 'default.saveInscription.label', default: 'Save Inscription')}" class="btn-info" style="float: left !important;"/>
                    <input type="reset" value="${message(code: 'default.reset.label', default: 'Save')}" class="btn-info" style="float: left !important;">
                </div>
            </fieldset>
        </g:form>
    </div>
    <div id="columna_2" class="span7" style="position: fixed !important;">

    </div>
</div>
</body>
</html>