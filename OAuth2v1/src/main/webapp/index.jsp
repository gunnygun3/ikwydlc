<%@page import="com.danter.google.auth.GoogleAuthHelper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Google OAuth 2.0 v1 Demo</title>
<style>
body {
	font-family: Sans-Serif;
	margin: 1em;
}

.oauthDemo a {
	display: block;
	border-style: solid;
	border-color: #bbb #888 #666 #aaa;
	border-width: 1px 2px 2px 1px;
	background: #ccc;
	color: #333;
	line-height: 2;
	text-align: center;
	text-decoration: none;
	font-weight: 900;
	width: 13em;
}

.oauthDemo pre {
	background: #ccc;
}

.oauthDemo a:active {
	border-color: #666 #aaa #bbb #888;
	border-width: 2px 1px 1px 2px;
	color: #000;
}

.readme {
	padding: .5em;
	background-color: #F9AD81;
	color: #333;
}
</style>
</head>
<body>
	<div class="oauthDemo">
		<%
			/*
			 * The GoogleAuthHelper handles all the heavy lifting, and contains all "secrets"
			 * required for constructing a google login url.
			 */
			final GoogleAuthHelper helper = new GoogleAuthHelper(null);

			if (request.getParameter("code") == null
					|| request.getParameter("state") == null) {

				/*
				 * initial visit to the page
				 */
				out.println("<a href='" + helper.buildLoginUrl()
						+ "'>log in with google</a>");
						
				/*
				 * set the secure state token in session to be able to track what we sent to google
				 */
				session.setAttribute("state", helper.getStateToken());

			}
		%>
	</div>
</body>
</html>
