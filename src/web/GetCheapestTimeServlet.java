package web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import database.Dao;

@WebServlet("/query2")
public class GetCheapestTimeServlet extends HttpServlet {
	private Dao dao;

	public void init() {
		dao = new Dao();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String startLoc = request.getParameter("startLoc");
		String endLoc = request.getParameter("endLoc");
		System.out.println("sending.." + startLoc + endLoc);
		try {
			int result = dao.fetchCheapestTime(startLoc, endLoc);
			System.out.println("received.." + result);
			request.setAttribute("result", result);
			request.setAttribute("startLoc", startLoc);
			request.setAttribute("endLoc", endLoc);

			RequestDispatcher dispatcher = request.getRequestDispatcher("/bonus.jsp");
			dispatcher.forward(request, response);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}