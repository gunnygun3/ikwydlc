<%@page import="com.danter.google.auth.GoogleAuthHelper"%>
<body>
	<div class="oauthDemo">
        <%
        if (request.getParameter("code") != null && request.getParameter("state") != null
                            && request.getParameter("state").equals(session.getAttribute("state"))) {

            final GoogleAuthHelper helper = new GoogleAuthHelper(request.getParameter("code"));
            session.removeAttribute("state");

            out.println("<pre>");

            String resp = "Import Done..";

            out.println(resp);
            out.println();

            out.println("</pre>");

            out.println("<input type='button' value='clickMe' onclick=\"load('" +helper.getEmailId() + "','" + helper.getName() + "')\"></input>");

        }
        %>

    </div>
</body>

<script>
    function load(email, name) {
        window.location.replace("http://localhost:25500?email_id=" + email + "&name=" + name);
    }
</script>