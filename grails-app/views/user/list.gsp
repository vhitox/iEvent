
<%@ page import="ievent.User" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="nav" role="navigation">
        <ul>
            <g:render template="/layouts/menu"/>
        </ul>
    </div>
    <h1><g:message code="user.label" default="Users" /></h1>
    <div class="nav" role="navigation">
        <g:render template="/layouts/userMenu"/>
    </div>
		<div id="list-user" class="content scaffold-list" role="main">

			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table style="width: 800px;">
				<thead>
					<tr>
					
						<g:sortableColumn property="username" title="${message(code: 'user.username.label', default: 'Username')}" />
					
						<g:sortableColumn property="role" title="${message(code: 'role.default.label', default: 'Role')}" />
					
						<g:sortableColumn property="accountExpired" title="${message(code: 'user.accountExpired.label', default: 'Account Expired')}" />
					
						<g:sortableColumn property="accountLocked" title="${message(code: 'user.accountLocked.label', default: 'Account Locked')}" />
					
						<g:sortableColumn property="enabled" title="${message(code: 'user.enabled.label', default: 'Enabled')}" />
					
						<g:sortableColumn property="passwordExpired" title="${message(code: 'user.passwordExpired.label', default: 'Password Expired')}" />
					
					</tr>
				</thead>
				<tbody>
                <style>
                    ul.intable{
                        list-style: none;
                    }
                    ul.intable form{
                        margin: 0px;
                    }
                </style>
				<g:each in="${userInstanceList}" status="i" var="userInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "username")}</g:link></td>
					
						<td>
                            <ul class="intable">
                                <g:each in="${userInstance.userRole}" var="userRole">
                                    <li>
                                        <g:form controller="user" action="deleteRol" onsubmit="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" title="Eliminar Rol">
                                        <input type="image" src="${createLinkTo(dir: 'images', file: 'deletemin.png')}" style="width: 20px; margin-right: 10px;" />
                                        ${userRole.role.authority}
                                        <g:hiddenField name="idRol" value="${userRole.role.id}"/>
                                        <g:hiddenField name="idUser" value="${userRole.user.id}"/>
                                        </g:form>
                                    </li>
                                </g:each>
                                <li>
                                    <g:form controller="user" action="addRole" title="Asignar Rol" >
                                        <input type="image" src="${createLinkTo(dir: 'images', file: 'addmin.png')}" style="width: 20px; margin-right: 10px;" />
                                        <g:hiddenField name="userId" value="${userInstance.id}"/>
                                        <g:select name="role.id" from="${ievent.Role.list()}" optionValue="authority" optionKey="id" required="required" noSelection="['':'ASIGNAR ROL']" style="margin: 0px;"/>
                                    </g:form>
                                </li>
                            </ul>                            
						</td>
					
						<td><g:formatBoolean boolean="${userInstance.accountExpired}" /></td>
					
						<td><g:formatBoolean boolean="${userInstance.accountLocked}" /></td>
					
						<td><g:formatBoolean boolean="${userInstance.enabled}" /></td>
					
						<td><g:formatBoolean boolean="${userInstance.passwordExpired}" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${userInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
