<%@ page import="ievent.Phase" %>



<div class="fieldcontain ${hasErrors(bean: phaseInstance, field: 'phaseName', 'error')} ">
	<label for="phaseName">
		<g:message code="phase.phaseName.label" default="Phase Name" />
		
	</label>
	<g:textArea name="phaseName" cols="40" rows="5" maxlength="500" value="${phaseInstance?.phaseName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: phaseInstance, field: 'phaseName', 'error')} ">
    <label for="imagePhase">
        <g:message code="phase.imagePhase.label" default="Image Phase" />

    </label>
    <input type="file" name="file" id="imagePhase" value="" accept="image/x-png,image/jpeg"/>
</div>


%{--<div class="fieldcontain ${hasErrors(bean: phaseInstance, field: 'event', 'error')} ">
	<label for="event">
		<g:message code="phase.event.label" default="Event" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${phaseInstance?.event?}" var="e">
    <li><g:link controller="eventPhase" action="show" id="${e.id}">${e?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="eventPhase" action="create" params="['phase.id': phaseInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'eventPhase.label', default: 'EventPhase')])}</g:link>
</li>
</ul>

</div>--}%

