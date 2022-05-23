package control;

import DAO.DAO;
import Model.Account;
import Model.Product;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "DownloadControl", urlPatterns = {"/download"})
public class DownloadControl extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, Exception {
        response.setContentType("text/html;charset=UTF-8");
        
        Account account = (Account) request.getSession().getAttribute("account");
        if (account == null){
            response.sendRedirect("login.jsp");
        }
        DAO dao = new DAO();
        String idP = request.getParameter("idP");
        String idU = request.getParameter("idU");
        Product product = dao.getProductDetail(Integer.parseInt(idP));
        String filePath = getServletContext().getRealPath("/install-file/" + idP + ".zip");
        
        File downloadFile = new File(filePath);
        FileInputStream inputStream = new FileInputStream(downloadFile);
        
        String type = getServletContext().getMimeType(filePath);
        
        if (type == null){
            type = "APPLICATION/OCTET-STREAM";
        }
        
        response.setContentType(type);
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + product.getName() + ".zip") + "\"");
        
        OutputStream outputStream = response.getOutputStream();
        
        byte[] buffer = new byte[4096];
        
        int in = -1;
        while ((in = inputStream.read()) != -1) {
            outputStream.write(in);
        }

        // Close FileInputStream and PrintWriter object
        inputStream.close();
        outputStream.close();
        
        dao.increaseDownloadCount(Integer.parseInt(idP));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(DownloadControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(DownloadControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}