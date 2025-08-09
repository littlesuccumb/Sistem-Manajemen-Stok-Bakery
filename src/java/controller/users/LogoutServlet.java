package controller.users;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("Logout - menghapus session untuk user: " + session.getAttribute("username"));
            session.invalidate();
        }

        // Hapus cookie JSESSIONID
        Cookie sessionCookie = new Cookie("JSESSIONID", "");
        sessionCookie.setMaxAge(0);
        sessionCookie.setPath(request.getContextPath());
        response.addCookie(sessionCookie);

        // Redirect ke halaman login (auth.jsp)
        response.sendRedirect(request.getContextPath() + "/auth.jsp?action=login");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
