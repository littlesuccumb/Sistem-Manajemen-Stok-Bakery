package controller.users;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;
import dao.UserDAO;
import model.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/auth.jsp?action=login");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("login".equals(action)) {
            handleLogin(request, response);
        } else if ("register".equals(action)) {
            handleRegister(request, response);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = userDAO.validateLogin(username, password);

            if (user != null) {
                HttpSession session = request.getSession(true);
                session.setMaxInactiveInterval(7200);

                session.setAttribute("user", user);
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole());
                session.setAttribute("nama", user.getNama());

                Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
                sessionCookie.setMaxAge(7200);
                sessionCookie.setPath(request.getContextPath());
                sessionCookie.setHttpOnly(true);
                response.addCookie(sessionCookie);

                // ✅ Redirect berdasarkan role
                String redirectUrl;
                if ("kasir".equals(user.getRole())) {
                    redirectUrl = request.getContextPath() + "/view/layout.jsp?page=kasir/index.jsp";
                } else {
                    redirectUrl = request.getContextPath() + "/view/layout.jsp?page=dashboard.jsp";
                }
                
                response.sendRedirect(redirectUrl);

            } else {
                request.setAttribute("error", "Username atau password salah!");
                request.getRequestDispatcher("/auth.jsp?action=login").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Terjadi kesalahan sistem!");
            request.getRequestDispatcher("/auth.jsp?action=login").forward(request, response);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        String nama = request.getParameter("nama");

        try {
            if (userDAO.isUsernameExists(username)) {
                request.setAttribute("error", "Username sudah digunakan!");
                request.getRequestDispatcher("/auth.jsp?action=register").forward(request, response);
                return;
            }

            User newUser = new User(username, password, role, nama);
            boolean success = userDAO.createUser(newUser);

            if (success) {
                request.setAttribute("success", "Registrasi berhasil! Silakan login.");
                request.getRequestDispatcher("/auth.jsp?action=login").forward(request, response);
            } else {
                request.setAttribute("error", "Registrasi gagal!");
                request.getRequestDispatcher("/auth.jsp?action=register").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Terjadi kesalahan sistem!");
            request.getRequestDispatcher("/auth.jsp?action=register").forward(request, response);
        }
    }
}