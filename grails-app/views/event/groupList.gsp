<%--
  Created by IntelliJ IDEA.
  User: Vic
  Date: 26-09-17
  Time: 04:02 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'eventGroup.label', default: 'Event Group')}" />
    <title><g:message code="default.create.label" args="[entityName]" /></title>
</head>
<body>
<a href="#create-eventPhase" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <g:render template="/layouts/menu"/>
    </ul>
</div>
<h1>${eventInstance.eventName}</h1>
<div class="nav" role="navigation">
    <g:render template="/layouts/eventMenu"/>
</div>
<div id="layout_dos_columnas" class="row-fluid">
    <div id="columna_1" class="span5">
        <fieldset>
            <legend>Agregar Grupos al evento</legend>
            <div id="create-eventPhase" class="content scaffold-create" role="main">
                <g:if test="${flash.message}">
                    <div class="message" role="status">${flash.message}</div>
                </g:if>
                <g:hasErrors bean="${eventGroupInstance}">
                    <ul class="errors" role="alert">
                        <g:eachError bean="${eventGroupInstance}" var="error">
                            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                        </g:eachError>
                    </ul>
                </g:hasErrors>
                <g:form action="saveGroup" >
                    <g:hiddenField name="event.id" value="${eventInstance.id}"/>
                    <g:render template="formGroup"/>
                    <g:submitButton name="create" value="${message(code: 'default.button.create.label', default: 'Create')}" class="btn-info" />
                </g:form>
            </div>
        </fieldset>
    </div>
    <div id="columna_2" class="span7">
        <table>
            <thead>
            <tr>
                <th colspan="2">Grupos Del Evento</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${eventInstance.groups.sort {it.nameGroup}}" var="group" status="i">
                <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                    <td>${group.nameGroup}</td>
                    <td style="line-height: 20px;">
                        <g:form action="deleteGroup" style="margin: 0px;">
                            <g:hiddenField name="id" value="${group.id}"/>
                            <g:link controller="event" action="editGroup" id="${group.id}"><g:img dir="images" file="editmin.png" style="width: 20px;" /></g:link>
                            <input type="image" name="submit" src="${createLinkTo(dir: 'images', file: 'deletemin.png')}" border="0" alt="Submit" formnovalidate="" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" style="width: 20px;"/>
                        </g:form>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>