
<%@ page import="ievent.Phase" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'phase.label', default: 'Phase')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
    <script type="text/javascript">
        $(document).ready(function() {

        });
    </script>
		<a href="#list-phase" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="nav" role="navigation">
        <ul>
            <g:render template="/layouts/menu"/>
        </ul>
    </div>
		<div id="list-phase" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="phaseName" title="${message(code: 'phase.phaseName.label', default: 'Phase Name')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${phaseInstanceList}" status="i" var="phaseInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${phaseInstance.id}">${fieldValue(bean: phaseInstance, field: "phaseName")}</g:link></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${phaseInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
