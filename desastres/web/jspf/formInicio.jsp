<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="security.SecurityConstants"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:bundle basename="fmt.eji8n">
	<h2><fmt:message key="iniciarsesion"/></h2>
	<form name="<%=SecurityConstants.LOGIN_FORM_ID%>" action="<%=response.encodeURL(SecurityConstants.LOGIN_FORM_ACTION)%>" method="POST">
		<table>
			<tr><td id="regMsg" class="verde" colspan="2"></td></tr>
			<tr>
				<td><fmt:message key="usuario"/>:</td>
				<td><input type="text" id="username" name="<%=SecurityConstants.LOGIN_USERNAME_FIELD%>" size="26"/></td>
			</tr>
			<tr>
				<td><fmt:message key="contrasenna"/>:</td>
				<td><input type="password" id="pwd" name="<%=SecurityConstants.LOGIN_PASSWORD_FIELD%>" size="26"/></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit" name="Submit" id="submit_butt" value="<fmt:message key="aceptar"/>"/></td>
			</tr>
		</table>
		<p>
			<fmt:message key="registrado"/>
			<input type="button" value="<fmt:message key="registrarse"/>" onclick="registro()"/>
		</p>
	</form>
	<form id="registro" class="oculto" action="#">
		<table>
			<tr><td id="userError" class="rojo" colspan="2"></td></tr>
			<tr id="user">
				<td><fmt:message key="nombreUsuario"/></td>
				<td><input type="text" name="user" size="20"/></td>
			</tr>
			<tr><td id="passError" class="rojo" colspan="2"></td></tr>
			<tr id="pass1">
				<td><fmt:message key="contrasenna"/></td>
				<td><input type="password" name="pass1" size="20"/></td>
			</tr>
			<tr id="pass2">
				<td><fmt:message key="repContrasenna"/></td>
				<td><input type="password" name="pass2" size="20"/></td>
			</tr>
			<tr id="nombre">
				<td><fmt:message key="nombreReal"/></td>
				<td><input type="text" name="nombre" size="20"/></td>
			</tr>
			<tr id="email">
				<td><fmt:message key="correo"/></td>
				<td><input type="email" name="email" size="20"/></td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="button" value="<fmt:message key="aceptar"/>" onclick="registrar(user.value,pass1.value,pass2.value,nombre.value,email.value)"/>
					<input type="button" value="<fmt:message key="cancelar"/>" onclick="cancelarRegistro()"/>
				</td>
			</tr>
		</table>
	</form>
</fmt:bundle>