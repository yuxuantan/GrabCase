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

@WebServlet("/query")
public class GetTripCountServlet extends HttpServlet {
	private Dao dao;

	public void init() {
		dao = new Dao();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String qDate = request.getParameter("qDate");

		try {
			int[] result = dao.fetchTripCount(qDate);
			if (result != null) { // means db connected and return data
				request.setAttribute("result", result);
				request.setAttribute("date", qDate);

				RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
				dispatcher.forward(request, response);
			} else { // connection problem
				HttpSession session = request.getSession();

			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}