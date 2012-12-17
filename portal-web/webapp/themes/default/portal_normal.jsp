<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" language="java" trimDirectiveWhitespaces="true" session="false" %>
<%@ taglib prefix="portal-taglib" uri="http://midaiganes.ee/portal-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE HTML>
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<style type="text/css">
			html, body {
				margin:0;
				padding:0;
			}
			body {
				text-align:center;
				background: #D9D9D9;
				color: #777;
				font:normal normal normal 14px/24px Arial,Verdana,sans-serif;
			}
			ul, li {
				margin:0;
				padding:0;
				list-style:none;
			}
			h1, h2, h3, h4, h5, h6 {
				font-style: normal;
				font-weight: normal;
				margin: 0 0 20px 0;
			}
			h1, h2 {
				font-size:30px;
			}
			a {
				color: #DD7A05;
				text-decoration: none;
			}
			#dockbar {
				position: fixed;
				top: 0;
				left: 0;
				width: 100%;
				height:30px;
			}
			#page {
				text-align:left;
				width:800px;
				margin:0 auto;
				border:1px solid black;
				background: #fff;
			}
			#dockbar + #page {
				margin-top:30px;
			}
			#site-menu {
				color: #8F8F8F;
				height: 50px;
				padding: 18px 0 0 20px;
			}
			#site-menu a {
				color: #8F8F8F;
				padding:5px 15px;
				border: 1px solid #999;
				border-radius:5px;
			}
			ul.menu {
				width:100px;
			}
			ul.menu a {
				color: #8F8F8F;
			}
			ul.menu ul {
				display:none;
				background: white;
			}
			ul.menu li:hover > ul {
				display:block;
				border:1px solid black;
			}
			.content-box {
				color: #777;
				border: 1px solid #999;/* #E5E5E5; */
				margin: 5px;
			}
			.content-box .content-top, .m-ui-dialog-title {
				height:25px;
				background: #EEE;
			}
			.content-box .content-body {
				padding:10px;
			}
			/* dialog */
			.ui-dialog {
				background: #FFF;
				text-align: left;
			}
			.ui-dialog-titlebar {
				text-align: right;
			}
			.ui-dialog-content {
				
			}
			/* -- dialog */
			.ui-widget-overlay {
				background: black;
				position: absolute;
				top: 0;
				left: 0;
				opacity: 0.5;
			}
			.portlet-box.portlet-dropped {
				min-height: 10px;
			}
			.m-ui-overlay {
				background: black;
				position:fixed;
				top: 0;
				left: 0;
				opacity: 0.5;
				width:100%;
				height:100%;
			}
			.m-ui-dialog {
				display: block;
				width: 400px;
				position: absolute;
				top: 0;
				left: 50%;
				margin-left: -200px;
				border: 1px solid #999;
				background:white;
			}
		</style>
		<script type="text/javascript" charset="UTF-8" src="${themeJavascriptDir}/jquery-1.8.0.min.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${themeJavascriptDir}/jquery-ui-1.8.22.custom.min.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${themeJavascriptDir}/javascript.js"></script>
	</head>
	<body>
		<div id="dockbar">
			<ul class="menu">
				<li>
					<a href="#">Actions</a>
					<ul>
						<li><a href="${addLayoutUrl}" class="open-dialog">Add page</a></li>
						<li><a href="${changePageLayoutUrl}" class="open-dialog">Change page layout</a></li>
						<li><a href="${addRemovePortletUrl}" class="open-modal" data-modal-title="Add or remove portlet">Add or remove portlet</a></li>
					</ul>
				</li>
			</ul>
		</div>
		<div id="page">
			<div id="header">
			</div>
			<div id="site-menu">
				<c:forEach items="${navItems}" var="navItem">
					<a href="${navItem.url}">${navItem.layout.defaultLayoutTitle.title}</a>
				</c:forEach>
			</div>
			<div id="content">
				:)
			</div>
			<div>
				<div class="content-box">
					<div class="content-top"></div>
					<div class="content-body">faaaaa</div>
				</div>

				<portal-taglib:runtime-portlet name="snowportal_w_layout-set" />
				<portal-taglib:runtime-portlet name="snowportal_w_layout" />
				<portal-taglib:runtime-portlet name="snowportal_w_registration" />
				<portal-taglib:runtime-portlet name="snowportal_w_web-content" />
				<portal-taglib:page-layout />
				<portal-taglib:runtime-portlet name="snowportal_w_welcome" />
			</div>
		</div>
	</body>
</html>