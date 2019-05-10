<%--
  Created by IntelliJ IDEA.
  User: Vic
  Date: 2/09/17
  Time: 10:43
  To change this template use File | Settings | File Templates.
--%>

<li><a class="home" href="${createLink(uri: '/')}"><g:img dir="images" file="home.png" title="Home" class="menu"/> </a></li>
<sec:ifAnyGranted roles="ROLE_SUPERADMIN,ROLE_ADMIN">
    <li><g:link class="create" controller="event" action="create"><g:img dir="images" file="newEvent.png" title="Nuevo Evento" class="menu"/></g:link></li>
    <li><g:link controller="phase" action="list"><g:img dir="images" file="phase.png" title="Etapas" class="menu"/></g:link> </li>
    <li><g:link controller="phase" action="create"><g:img dir="images" file="newphase.png" title="Nueva Etapa" class="menu"/></g:link> </li>
    <li><g:link controller="event" action="tempFiles"><g:img dir="images" file="tempFiles.png" title="Archivos Temporales" class="menu"/></g:link> </li>
</sec:ifAnyGranted>
<sec:ifAnyGranted roles="ROLE_SUPERADMIN">
    <li><g:link controller="user" action="list"><g:img dir="images" file="groupparticipants.png" title="Usuarios" class="menu"/></g:link></li>
</sec:ifAnyGranted>
<li><g:link controller="logout" action="index"><g:img dir="images" file="close.png" title="Salir" class="menu"/></g:link></li>
