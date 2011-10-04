<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.Date" %>
<%@ page isELIgnored = "false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %> 
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<%@ include file="database.jspf" %>

<% String modif = "'" + new Timestamp(new java.util.Date().getTime()).toString() + "'";%>

<%-- Pone como borrado la asociacion anterior... --%>
<sql:update dataSource="${CatastrofesServer}">
	UPDATE actividades
	SET estado = 'erased'
	WHERE id_usuario = (SELECT id FROM usuarios WHERE nombre_usuario=?)
	AND id_emergencia = ?
	<sql:param value="${param.nombreUsuario}"/>
	<sql:param value="${param.idEvento}"/>
</sql:update>

<%-- ... e introduce la nueva --%>
<c:choose>
	<c:when test="${param.estadoEvento == 'erased' || param.accion == 'dejar'}">
		<sql:update dataSource="${CatastrofesServer}">
			INSERT INTO actividades(id_usuario, id_emergencia, id_tipo_actividad, estado)
			VALUES((SELECT id FROM usuarios WHERE nombre_usuario = ?),
				   ?,
			       (SELECT id FROM tipos_actividades WHERE tipo = ?),
				   'erased')
			<sql:param value="${param.nombreUsuario}"/>
			<sql:param value="${param.idEvento}"/>
			<sql:param value="${param.accion}"/>
		</sql:update>
	</c:when>
	<c:otherwise>
		<sql:update dataSource="${CatastrofesServer}">
			INSERT INTO actividades(id_usuario, id_emergencia, id_tipo_actividad, estado)
			VALUES((SELECT id FROM usuarios WHERE nombre_usuario = ?),
				   ?,
			       (SELECT id FROM tipos_actividades WHERE tipo = ?),
				   'active')
			<sql:param value="${param.nombreUsuario}"/>
			<sql:param value="${param.idEvento}"/>
			<sql:param value="${param.accion}"/>
		</sql:update>
	</c:otherwise>
</c:choose>

<sql:update dataSource="${CatastrofesServer}">
	UPDATE catastrofes
	SET estado = ?
	WHERE id = ?
	<sql:param value="${param.estadoEvento}"/>
	<sql:param value="${param.idEvento}"/>
</sql:update>

<sql:update dataSource="${CatastrofesServer}">
	UPDATE catastrofes
	SET estado = ?
	WHERE id = (SELECT id FROM usuarios WHERE nombre_usuario = ?)
	<sql:param value="${param.estadoUsuario}"/>
	<sql:param value="${param.nombreUsuario}"/>
</sql:update>

<c:if test="${param.accion == 'curado' || param.accion == 'rescatado'}">
	<sql:update dataSource="${CatastrofesServer}">
		UPDATE catastrofes
		SET estado = 'active', tipo = 'healthy', modificado = <%=modif%>
		WHERE id = ?;
		<sql:param value="${param.idEvento}"/>
	</sql:update>
</c:if>

<c:if test="${param.accion == 'dejar'}">
	<sql:update dataSource="${CatastrofesServer}">
		UPDATE catastrofes
		SET estado = 'active', modificado = <%=modif%>
		WHERE nombre = ?
		AND nombre NOT IN (SELECT DISTINCT u.nombre_usuario
		                   FROM usuarios u, actividades a
					       WHERE u.id = a.id_usuario
						   AND a.estado = 'active');
		<sql:param value="${param.nombreUsuario}"/>
	</sql:update>
	<sql:update dataSource="${CatastrofesServer}">
		UPDATE catastrofes
		SET estado = 'active', modificado = <%=modif%>
		WHERE id = ?
		AND id NOT IN (SELECT DISTINCT id_emergencia
		               FROM actividades
					   WHERE estado = 'active');
		<sql:param value="${param.idEvento}"/>
	</sql:update>
</c:if>

<%--<c:if test="${param.accion == 'dejar'}">
	
</c:if>--%>