
<%@ page import="ievent.Phase" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'phase.label', default: 'Phase')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
    <a href="#show-phase" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="nav" role="navigation">
        <ul>
            <g:render template="/layouts/menu"/>
        </ul>
    </div>
    <h1>${phaseInstance.phaseName}</h1>
    <div class="nav" role="navigation">
        <g:render template="/layouts/phaseMenu"/>
    </div>
    <div id="layout_dos_columnas" class="row-fluid">
        <div id="columna_1" class="span5">
              <fieldset>
                  <div id="show-phase" class="content scaffold-show" role="main">
                      <g:if test="${flash.message}">
                          <div class="message" role="status">${flash.message}</div>
                      </g:if>
                      <ol class="property-list phase">

                          <g:if test="${phaseInstance?.phaseName}">
                              <li class="fieldcontain">
                                  <span id="phaseName-label" class="property-label"><g:message code="phase.phaseName.label" default="Phase Name" /></span>

                                  <span class="property-value" aria-labelledby="phaseName-label"><g:fieldValue bean="${phaseInstance}" field="phaseName"/></span>

                              </li>
                          </g:if>
                          <g:if test="${phaseInstance?.imagePhase}">
                              <li class="fieldcontain">
                                  <span id="imagePhase-label" class="property-label"><g:message code="phase.imagePhase.label" default="Phase Name" /></span>

                                  <img src="${createLink(controller: 'phase', action: 'showImage', id: phaseInstance.id)}"/>

                              </li>
                          </g:if>

                          %{--<g:if test="${phaseInstance?.event}">
                              <li class="fieldcontain">
                                  <span id="event-label" class="property-label"><g:message code="phase.event.label" default="Event" /></span>

                                  <g:each in="${phaseInstance.event}" var="e">
                                      <span class="property-value" aria-labelledby="event-label"><g:link controller="eventPhase" action="show" id="${e.id}">${e?.encodeAsHTML()}</g:link></span>
                                  </g:each>

                              </li>
                          </g:if>--}%

                      </ol>
                  </div>
              </fieldset>
        </div>
    </div>
	</body>
</html>
