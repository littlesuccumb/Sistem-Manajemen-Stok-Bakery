package controller.gudang;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.util.Date;
import config.DBConnection;
import model.LogStok;
import model.User;

@WebServlet("/gudang/laporan-penggunaan")
public class LaporanPenggunaanServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"gudang".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String startDateParam = request.getParameter("start");
        String endDateParam = request.getParameter("end");

        Date startDate = null;
        Date endDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (startDateParam != null && !startDateParam.isEmpty()) {
                startDate = sdf.parse(startDateParam);
            }
            if (endDateParam != null && !endDateParam.isEmpty()) {
                // Tambahkan 1 hari biar data di akhir hari tetap ikut
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(endDateParam));
                cal.add(Calendar.DATE, 1);
                endDate = cal.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<LogStok> logList = new ArrayList<>();
        if (startDate != null && endDate != null) {
            logList = fetchLogStok(startDate, endDate);
        }

        request.setAttribute("logList", logList);
        request.setAttribute("startDate", startDateParam);
        request.setAttribute("endDate", endDateParam);
        request.setAttribute("page", "gudang/LaporanPenggunaan.jsp");
        request.getRequestDispatcher("/view/layout.jsp").forward(request, response);
    }

    private List<LogStok> fetchLogStok(Date startDate, Date endDate) {
        List<LogStok> logList = new ArrayList<>();

        String query = "SELECT * FROM log_stok WHERE tanggal BETWEEN ? AND ? ORDER BY tanggal DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(2, new Timestamp(endDate.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LogStok log = new LogStok(
                        rs.getInt("id"),
                        rs.getInt("resep_id"),
                        rs.getInt("bahan_id"),
                        rs.getString("nama_bahan"),
                        rs.getInt("jumlah_dikurangi"),
                        rs.getString("keterangan"),
                        rs.getTimestamp("tanggal")
                    );
                    logList.add(log);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logList;
    }
}
