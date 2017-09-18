package logicaBusiness.gestori;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import logicaBusiness.elaboratori.Validatore;
import logicaBusiness.entit‡.Utente;
import logicaIntegrazione.DaoUtenti;

/**
 * Classe che gestisce l'autenticazione degli utenti
 * 
 * @author Matteo Forina, Ivan Lamparelli
 */
@WebServlet("/login")
public class GestoreLogin extends HttpServlet {
	private final static Pattern EMAIL_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	private final static Pattern PASSWORD_REGEX =
			Pattern.compile(("[a-zA-Z0-9 ]"), Pattern.CASE_INSENSITIVE);
	
	private static final long serialVersionUID = 2502201227758877181L;

	/**L'oggetto utilizzato per stampare i messaggi d'errore*/
	private final static Logger LOGGER = Logger.getLogger(GestoreLogin.class.getName());
	
	private final static String MESSAGGIO_ERRORE = "Credenziali non valide";
	
	
	/**
	 * Le elaborazioni da svolgere quando arriva una richiesta POST
	 * @param req La richiesta di elaborazione
	 * @param resp La risposta dell'elaborazione
	 * @throws ServletException Eccezione in caso di richiesta non valida
	 * @throws IOException Eccezione in caso di problemi nei parametri in input/output
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		/**L'oggetto da contattare per verificare le informazioni sugli utenti*/
		DaoUtenti daoUtenti = new DaoUtenti();
		
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		Utente utente = null;
		
		//Se l'utente ha digitato le credenziali, controllo se sono valide
		if ((email != null && password != null) && EMAIL_REGEX.matcher(email).find() && PASSWORD_REGEX.matcher(password).find()) {
			try {
				utente = daoUtenti.validaUtente(email, password);			
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Credenziali non valide");
			}			
			
		}

		//Se esiste un utente con le credenziali inserite, controllo il tipo di utente, e mostro all'utente la home appropriata 
		if (utente != null) {
			
			HttpSession sessione = req.getSession();
			sessione.setAttribute("utenteloggato", Validatore.sanitize(email));
			sessione.setAttribute("flagadminloggato", utente.ËAmministratore());
			
			if (utente.ËAmministratore()) {
				req.getRequestDispatcher("/homeAmministratore.jsp").forward(req, resp);
			} else {
				req.getRequestDispatcher("/homeStandard.jsp").forward(req, resp);
			}
		} else {
			req.setAttribute("errore", MESSAGGIO_ERRORE);
			req.getRequestDispatcher("/index.jsp").forward(req, resp);
		}

	} 
	
	
	/**
	 * Le elaborazioni da svolgere quando arriva una richiesta GET
	 * @param req La richiesta di elaborazione
	 * @param resp La risposta dell'elaborazione
	 * @throws ServletException Eccezione in caso di richiesta non valida
	 * @throws IOException Eccezione in caso di problemi nei parametri in input/output
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		effettuaLogout(req);
		req.getRequestDispatcher("/index.jsp").forward(req, resp);
	}
	
	
	/**
	 * Distrugge la sessione di connessione di un utente
	 * @param req La richiesta di logout dell'utente
	 */
	private void effettuaLogout(HttpServletRequest req) {
		HttpSession sessione = req.getSession();
		sessione.removeAttribute("utenteloggato");
		sessione.removeAttribute("flagadminloggato");
		sessione.removeAttribute("utenteselezionato");
		sessione.invalidate();
	}
}
