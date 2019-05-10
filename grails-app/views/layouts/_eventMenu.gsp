<%--
  Created by IntelliJ IDEA.
  User: Vic
  Date: 2/09/17
  Time: 12:16
  To change this template use File | Settings | File Templates.
--%>

<ul>
    <li><g:link controller="event" action="show" id="${eventInstance.id}"><g:img dir="images" file="eventinfo.png" title="Información del Evento" class="menu"/></g:link> </li>
    <sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_SUPERADMIN">
        <li><g:link controller="event" action="edit" id="${eventInstance.id}"><g:img dir="images" file="edit.png" title="Editar el Evento" class="menu"/></g:link> </li>
        <li>
            <g:form action="delete" onsubmit="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
                <g:hiddenField name="id" value="${eventInstance?.id}" />
                <input type="image" src="${createLinkTo(dir: 'images', file: 'delete.png')}" class="input-menu" />
            </g:form>
        </li>
    </sec:ifAnyGranted>
    <g:img dir="images" file="to.png" class="menu"/>
    <sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_SUPERADMIN">
        <li><g:link controller="eventPhase" action="create" id="${eventInstance.id}"><g:img dir="images" file="phaseToEvent.png" title="Agregar Etapas al Evento" class="menu"/></g:link> </li>
        <li><g:link controller="event" action="groupList" id="${eventInstance.id}"><g:img dir="images" file="group.png" title="Grupos para el evento" class="menu"/></g:link></li>
    </sec:ifAnyGranted>
    <li><g:link controller="event" action="groupListParticipants" id="${eventInstance.id}"><g:img dir="images" file="groupparticipants.png" title="Participantes por grupo" class="menu"/></g:link></li>
    <li><g:link controller="event" action="controlParticipantList" id="${eventInstance.id}"><g:img dir="images" file="participantslist.png" title="Lista de Participantes Inscritos" class="menu"/></g:link></li>
    <sec:ifAnyGranted roles="ROLE_SUPERADMIN">
        <li>
            <g:link controller="event" action="participantsControl" id="${eventInstance.id}" params="[currentPhase: eventInstance.phase.sort{it.id}.id[0]]"><g:img dir="images" file="participantscontrol.png" title="Control de Participantes" class="menu"/></g:link>
        </li>
    </sec:ifAnyGranted>
    <li><g:link controller="event" action="participantsControlTwo" id="${eventInstance.id}"><g:img dir="images" file="movilcontrol.png" title="Control de Participantes" class="menu"/></g:link></li>
    <li>
        <g:img dir="images" file="print.png" class="menu"/>
        <ul>
        <li><g:link controller="event" action="printCodeBarParticipants" id="${eventInstance.id}" ><g:img dir="images" file="simpleprint.png" title="Imprimir Codigos de Participantes A - Z " class="menu"/></g:link></li>
        <li><g:link controller="event" action="printCodeBarParticipantsRegister" id="${eventInstance.id}" ><g:img dir="images" file="simpleprint.png" title="Imprimir Codigos de Participantes por Orden de Registro" class="menu"/></g:link></li>
        <sec:ifAnyGranted roles="ROLE_SUPERADMIN">
            <li><g:link controller="event" action="printParticipantsInscription" id="${eventInstance.id}" target="_blank" ><g:img dir="images" file="simpleprint.png" title="Imprimir Fichas de Inscripción de Participantes A - Z " class="menu"/></g:link></li>
        </sec:ifAnyGranted>
        <li><g:link controller="event" action="printSelected" id="${eventInstance.id}" ><g:img dir="images" file="simpleprint.png" title="Imprimir Gafetes seleccionados" class="menu"/></g:link></li>
        </ul>
    </li>
    <li>
        <g:img dir="images" file="list.png" class="menu"/>
        <ul>
        <li><g:link controller="event" action="participantsPdfList" id="${eventInstance.id}" ><g:img dir="images" file="list.png" title="Imprimir Listas de participantes" class="menu"/></g:link></li>
        <li><g:link controller="event" action="participantsPdfListTwo" id="${eventInstance.id}" ><g:img dir="images" file="list.png" title="Imprimir Listas de participantes Dos" class="menu"/></g:link></li>
        <li><g:link controller="event" action="participantsHtmlList" id="${eventInstance.id}" ><g:img dir="images" file="list.png" title="Imprimir Listas de participantes en HTML" class="menu"/></g:link></li>
        </ul>
    </li>
    <li><g:link controller="event" action="participantsPdfListGroup" id="${eventInstance.id}" ><g:img dir="images" file="participantsbygroup.png" title="Imprimir Listas de participantes por grupo" class="menu"/></g:link></li>
    <sec:ifAnyGranted roles="ROLE_SUPERADMIN">
        <li><g:link controller="event" action="pdfwildCard" id="${eventInstance.id}" ><g:img dir="images" file="wildcard.png" title="Imprimir Comodin" class="menu"/></g:link></li>
    </sec:ifAnyGranted>
</ul>