<%@ page import="ievent.Event" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'event.label', default: 'Event')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
    <div class="nav" role="navigation">
        <ul>
            <g:render template="/layouts/menu"/>
        </ul>
    </div>
    <h1><g:message code="default.create.label" args="[entityName]" /></h1>
    <div id="layout_dos_columnas" class="row-fluid">
        <div id="columna_1" class="span5">
            <fieldset>
                <a href="#create-event" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

                <div id="create-event" class="content scaffold-create" role="main">

                    <g:if test="${flash.message}">
                        <div class="message" role="status">${flash.message}</div>
                    </g:if>
                    <g:hasErrors bean="${eventInstance}">
                        <ul class="errors" role="alert">
                            <g:eachError bean="${eventInstance}" var="error">
                                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                            </g:eachError>
                        </ul>
                    </g:hasErrors>
                    <g:form action="save" >
                        <g:render template="form"/>
                        <g:submitButton name="create" value="${message(code: 'default.button.create.label', default: 'Create')}" class="btn-info" />
                    </g:form>
                </div>
            </fieldset>

        </div>
    </div>

	</body>
</html>
