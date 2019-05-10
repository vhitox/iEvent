
<%@ page import="ievent.EventPhase" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'eventPhase.label', default: 'EventPhase')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-eventPhase" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-eventPhase" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list eventPhase">
			
				<g:if test="${eventPhaseInstance?.orderPhase}">
				<li class="fieldcontain">
					<span id="orderPhase-label" class="property-label"><g:message code="eventPhase.orderPhase.label" default="Order Phase" /></span>
					
						<span class="property-value" aria-labelledby="orderPhase-label"><g:fieldValue bean="${eventPhaseInstance}" field="orderPhase"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${eventPhaseInstance?.event}">
				<li class="fieldcontain">
					<span id="event-label" class="property-label"><g:message code="eventPhase.event.label" default="Event" /></span>
					
						<span class="property-value" aria-labelledby="event-label"><g:link controller="event" action="show" id="${eventPhaseInstance?.event?.id}">${eventPhaseInstance?.event?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${eventPhaseInstance?.participant}">
				<li class="fieldcontain">
					<span id="participant-label" class="property-label"><g:message code="eventPhase.participant.label" default="Participant" /></span>
					
						<g:each in="${eventPhaseInstance.participant}" var="p">
						<span class="property-value" aria-labelledby="participant-label"><g:link controller="participantPhases" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${eventPhaseInstance?.phase}">
				<li class="fieldcontain">
					<span id="phase-label" class="property-label"><g:message code="eventPhase.phase.label" default="Phase" /></span>
					
						<span class="property-value" aria-labelledby="phase-label"><g:link controller="phase" action="show" id="${eventPhaseInstance?.phase?.id}">${eventPhaseInstance?.phase?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${eventPhaseInstance?.id}" />
					<g:link class="edit" action="edit" id="${eventPhaseInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
