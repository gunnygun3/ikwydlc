<%@page import="com.danter.google.auth.GoogleAuthHelper"%>
<body>
	<div class="oauthDemo">
        <%
        if (request.getParameter("code") != null && request.getParameter("state") != null
                            && request.getParameter("state").equals(session.getAttribute("state"))) {

            final GoogleAuthHelper helper = new GoogleAuthHelper(request.getParameter("code"));
            session.removeAttribute("state");

            out.println("<pre>");

            String resp = helper.importData("2015-05-01", "2015-05-31", out);

            out.println(resp);
            out.println();

            out.println("</pre>");

        }

        %>
    </div>
</body>
