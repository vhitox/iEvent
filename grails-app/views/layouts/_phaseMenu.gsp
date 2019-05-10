<%--
  Created by IntelliJ IDEA.
  User: Vic
  Date: 2/09/17
  Time: 13:29
  To change this template use File | Settings | File Templates.
--%>

<ul>
    <li><g:link controller="phase" action="show" id="${phaseInstance.id}"><g:img dir="images" file="eventinfo.png" title="Información del Evento" class="menu"/></g:link> </li>
    <li><g:link controller="phase" action="edit" id="${phaseInstance.id}"><g:img dir="images" file="edit.png" title="Editar el Evento" class="menu"/></g:link> </li>
    <li>
        <g:form action="delete" onsubmit="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" title="Eliminar el Evento">
            <g:hiddenField name="id" value="${phaseInstance?.id}" />
            <input type="image" src="${createLinkTo(dir: 'images', file: 'delete.png')}" class="input-menu" />
        </g:form>
    </li>
</ul>