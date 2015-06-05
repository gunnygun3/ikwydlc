<%@page import="com.danter.google.auth.GoogleAuthHelper"%>
<body>
	<div class="oauthDemo">
        <%
        if (request.getParameter("code") != null && request.getParameter("state") != null
                            && request.getParameter("state").equals(session.getAttribute("state"))) {

            final GoogleAuthHelper helper = new GoogleAuthHelper(request.getParameter("code"));
            session.removeAttribute("state");

            String resp = helper.importData(request.getParameter("code"));

            out.println("<pre>");
            out.println(resp);
            out.println();

            out.println("</pre>");

        }

        %>
    </div>
</body>
