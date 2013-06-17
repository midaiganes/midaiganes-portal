<%@ page pageEncoding="UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ include file="../init.jsp" %>

<portlet:actionURL var="joinChat">
	<portlet:param name="join" value="1"/>
	<portlet:param name="chat-id" value="1"/>
</portlet:actionURL>

<a href="${joinChat}">Join!</a>