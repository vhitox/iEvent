
<%@ page import="ievent.EventPhase" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'eventPhase.label', default: 'EventPhase')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-eventPhase" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-eventPhase" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="orderPhase" title="${message(code: 'eventPhase.orderPhase.label', default: 'Order Phase')}" />
					
						<th><g:message code="eventPhase.event.label" default="Event" /></th>
					
						<th><g:message code="eventPhase.phase.label" default="Phase" /></th>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${eventPhaseInstanceList}" status="i" var="eventPhaseInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${eventPhaseInstance.id}">${fieldValue(bean: eventPhaseInstance, field: "orderPhase")}</g:link></td>
					
						<td>${fieldValue(bean: eventPhaseInstance, field: "event")}</td>
					
						<td>${fieldValue(bean: eventPhaseInstance, field: "phase")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${eventPhaseInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
