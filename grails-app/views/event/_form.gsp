<%@ page import="ievent.Event" %>



<div class="fieldcontain ${hasErrors(bean: eventInstance, field: 'eventName', 'error')} ">
	<label for="eventName">
		<g:message code="event.eventName.label" default="Event Name" />
		
	</label>
	<g:textArea name="eventName" cols="40" rows="5" maxlength="500" value="${eventInstance?.eventName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: eventInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="event.description.label" default="Description" />
		
	</label>
	<g:textArea name="description" cols="40" rows="5" maxlength="3000" value="${eventInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: eventInstance, field: 'eventPlace', 'error')} ">
    <label for="description">
        <g:message code="event.eventPlace.label" default="Event Place" />

    </label>
    <g:textArea name="eventPlace" cols="40" rows="5" maxlength="500" value="${eventInstance?.eventPlace}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: eventInstance, field: 'startDate', 'error')} required">
	<label for="startDate">
		<g:message code="event.startDate.label" default="Start Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="startDate" precision="day"  value="${eventInstance?.startDate}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: eventInstance, field: 'endDate', 'error')} ">
	<label for="endDate">
		<g:message code="event.endDate.label" default="End Date" />
		
	</label>
	<g:datePicker name="endDate" precision="day"  value="${eventInstance?.endDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: eventInstance, field: 'type', 'error')} ">
    <label for="type">
        <g:message code="event.type.label" default="Type of Event" />

    </label>
    <g:select name="type" from="${eventInstance.constraints.type.inList}" value="${eventInstance.type}"/>
</div>

<legend>Control por fecha limite de inscripción</legend>
<div class="fieldcontain ${hasErrors(bean: eventInstance, field: 'passwordControl', 'error')} ">
    <label for="passwordEvent">
        <g:message code="event.limitDateControl.label" default="Limit Date Control" />

    </label>
    <g:checkBox name="limitDateControl" value="${eventInstance.limitDateControl}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: eventInstance, field: 'endDate', 'error')} ">
    <label for="limitDate">
        <g:message code="event.limitDate.label" default="Limit Date" />

    </label>
    <g:datePicker name="limitDate" precision="minute"  value="${eventInstance?.limitDate}" default="none" noSelection="['': '']" />
</div>

<legend>Control por Contraseña</legend>
<div class="fieldcontain ${hasErrors(bean: eventInstance, field: 'passwordControl', 'error')} ">
    <label for="passwordEvent">
        <g:message code="event.passwordControl.label" default="Password Control" />

    </label>
    <g:checkBox name="passwordControl" value="${eventInstance.passwordControl}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: eventInstance, field: 'passwordEvent', 'error')} ">
    <label for="passwordEvent">
        <g:message code="event.passwordEvent.label" default="Event Password" />

    </label>
    <input id="passwordEvent" type="password" name="passwordEvent" value="${eventInstance?.passwordEvent}" maxlength="250">
    %{--<g:textField type="password" name="passwordEvent" maxlength="250" value="${eventInstance?.passwordEvent}"/>--}%
</div>
<legend>Control por Limite de Participantes</legend>
<div class="fieldcontain ${hasErrors(bean: eventInstance, field: 'limitParticipantsControl', 'error')} ">
    <label for="passwordEvent">
        <g:message code="event.limitParticipantsControl.label" default="Password Control" />

    </label>
    <g:checkBox name="limitParticipantsControl" value="${eventInstance.limitParticipantsControl}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: eventInstance, field: 'limitParticipants', 'error')} ">
    <label for="passwordEvent">
        <g:message code="event.limitParticipants.label" default="Limit Participants" />

    </label>
    <g:textField name="limitParticipants" type="number" value="${eventInstance?.limitParticipants}"/>
</div>

%{--
<div class="fieldcontain ${hasErrors(bean: eventInstance, field: 'phase', 'error')} ">
    <label for="phase">
        <g:message code="event.phase.label" default="Phase" />

    </label>

<ul class="one-to-many">
<g:each in="${eventInstance?.phase?}" var="p">
    <li><g:link controller="eventPhase" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="eventPhase" action="create" params="['event.id': eventInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'eventPhase.label', default: 'EventPhase')])}</g:link>
</li>
</ul>
</div>
--}%


%{--<div class="fieldcontain ${hasErrors(bean: eventInstance, field: 'phaseOrder', 'error')} ">
	<label for="phaseOrder">
		<g:message code="event.phaseOrder.label" default="Phase Order" />
		
	</label>
	<g:field type="number" name="phaseOrder" value="${eventInstance.phaseOrder}"/>
</div>--}%

