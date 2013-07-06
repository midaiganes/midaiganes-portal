<%@ page pageEncoding="UTF-8" language="java" session="false" trimDirectiveWhitespaces="true" %>
<%@ include file="../init.jsp" %>

<div>
	<portlet:renderURL var="groupsListUrl">
		<portlet:param name="action" value="view-groups"/>
	</portlet:renderURL>
	<a href="${groupsListUrl}">View groups</a>
</div>
<table>
	<tr>
		<th>User id</th>
		<th>Username</th>
		<th>Actions</th>
	</tr>
	<c:forEach items="${users}" var="user">
		<tr>
			<td>${user.user.id}</td>
			<td><c:out value="${user.user.username}"/></td>
			<td>
				<ul class="action-menu-button">
					<li>
						<a href="#">Actions</a>
						<ul>
							<li>
								<a href="#">Groups</a>
								<ul>
									<c:forEach items="${user.userGroups}" var="g">
										<li>
											<portlet:actionURL var="removeUserGroupUrl">
												<portlet:param name="action" value="remove-user-group"/>
												<portlet:param name="user-id" value="${user.user.id}"/>
												<portlet:param name="group-id" value="${g.id}"/>
											</portlet:actionURL>
											<a href="${removeUserGroupUrl}">Remove '${g.name}'</a>
										</li>
									</c:forEach>
									<c:forEach items="${user.notUserGroups}" var="g">
										<li>
											<portlet:actionURL var="addUserGroupUrl">
												<portlet:param name="action" value="add-user-group"/>
												<portlet:param name="user-id" value="${user.user.id}"/>
												<portlet:param name="group-id" value="${g.id}"/>
											</portlet:actionURL>
											<a href="${addUserGroupUrl}">Add '${g.name}'</a>
										</li>
									</c:forEach>
								</ul>
							</li>
						</ul>
					</li>
				</ul>
			</td>
		</tr>
	</c:forEach>
</table>