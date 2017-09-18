package logicaBusiness.gestori;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import logicaBusiness.elaboratori.GestoreEmail;
import logicaBusiness.entit‡.Utente;
import logicaIntegrazione.DaoUtenti;
import logicaPresentazione.VistaClienti;

/**
 * La classe gestisce i dati degli utenti come email e password comunicando con il DAO appropriato
 * 
 * @author Antonio Garofalo, Matteo Forina, Ivan Lamparelli
 */
@WebServlet("/gestioneClienti")
public class GestoreClienti extends HttpServlet {
	
	private static final long serialVersionUID = -6633402973904282910L;
	
	/**L'oggetto da utilizzare per la stampa degli errori*/
	private final static Logger LOGGER = Logger.getLogger(GestoreClienti.class.getName());
	
	/**L'oggetto a cui delegare la lettura e modifica dei dati dei clienti*/
	private final DaoUtenti daoUtenti = new DaoUtenti();
	

	
	/**Il messaggio di errore in caso di registrazione cliente non andata a buon fine*/
	private final static String ERRORE_NUOVO_CLIENTE_NON_VALIDO = "ERRORE: Il formato dei dati inseriti non Ë valido, digita nuovamente i dati e premi su Inserisci Cliente.";
	
	/**
	 * Le elaborazioni da svolgere quando arriva una richiesta POST
	 * @param req La richiesta di elaborazione
	 * @param resp La risposta dell'elaborazione
	 * @throws ServletException Eccezione in caso di richiesta non valida
	 * @throws IOException Eccezione in caso di problemi nei parametri in input/output
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String idform = req.getParameter("idform");
		String formEliminaClienti = null;
		switch(idform) {
			case "inseriscicliente":{
				String email = req.getParameter("emailutente");
				String password = req.getParameter("passwordutente");
				
				boolean valido = inserisciCliente(email, password);
				
				/*In caso di utente inserito non valido, aggiungo un messaggio di errore alla pagina, 
				  altrimenti aggiorno la lista dei clienti presenti*/
				if (!valido) {
					req.setAttribute("messaggioerrore", ERRORE_NUOVO_CLIENTE_NON_VALIDO);  
				} else {
					formEliminaClienti = this.mostraFormEliminaClienti();
					req.setAttribute("formeliminaclienti", formEliminaClienti);
				}
				
				break;
			}
			case "eliminacliente":{
				//Elimino il cliente
				String clienteDaEliminare = req.getParameter("clientedaeliminare");
				eliminaCliente(clienteDaEliminare);
				
				//Aggiorno il form di eliminazione
				formEliminaClienti = mostraFormEliminaClienti();
				req.setAttribute("formeliminaclienti", formEliminaClienti);
				
				break;
			}
			default:{
				break;
			}
	
		}
		req.getRequestDispatcher("/gestioneClienti.jsp").forward(req, resp);
	}
	
	/**
	 * Le elaborazioni da svolgere quando arriva una richiesta GET
	 * @param req La richiesta di elaborazione
	 * @param resp La risposta dell'elaborazione
	 * @throws ServletException Eccezione in caso di richiesta non valida
	 * @throws IOException Eccezione in caso di problemi nei parametri in input/output
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		//Popolo il form per i clienti da eliminare
		String formEliminaClienti = mostraFormEliminaClienti();
		req.setAttribute("formeliminaclienti", formEliminaClienti);		
		req.getRequestDispatcher("/gestioneClienti.jsp").forward(req, resp);
	}
	
	/**
	 * Registra un nuovo cliente nel sistema, e invia una mail al cliente con le credenziali dello stesso
	 * @param unaMail La mail del cliente da registrare
	 * @param unaPassword La password del cliente da registrare
	 * @return true se il cliente Ë stato registrato, false in caso di credenziali non valide o cliente gi‡ presente
	 */
	private boolean inserisciCliente(String unaMail, String unaPassword) {
		boolean inserito = false;
		
		if (GestoreEmail.ËValidaEmail(unaMail) && unaPassword != null) {
			inserito = daoUtenti.inserisciNuovoCliente(unaMail, unaPassword);
			
			if (inserito) {	
				String oggettoEmail = VistaClienti.scriviOggettoMailRegistrazione();
				String corpoEmail = VistaClienti.scriviCorpoMailRegistrazione(unaMail, unaPassword);
				
				GestoreEmail gestoreEmail = new GestoreEmail();
				gestoreEmail.inviaEmail(unaMail, oggettoEmail, corpoEmail, null, null);
			}
		}
		
		return inserito;
	}
	
	
	/**
	 * Elimina il cliente selezionato dal sistema
	 * @param unaMail Il cliente da eliminare
	 * @return true se il cliente Ë stato eliminato, false altrimenti
	 */
	private boolean eliminaCliente(String unaMail) {
		boolean eliminato = daoUtenti.eliminaCliente(unaMail);
		
		return eliminato;
	}
	
	/**
	 * Mostra la lista dei clienti presenti nel sistema
	 * @return stringa formattata per visualizzare la lista dei clienti
	 */
	private String mostraFormEliminaClienti() {
		
		/**L'oggetto a cui delegare la logica di presentazione*/
		VistaClienti vistaClienti = new VistaClienti();
		
		String elencoClienti = "";
		LinkedList<Utente> listaClienti = null;
		try {
			listaClienti = daoUtenti.mostraClienti();
			elencoClienti = vistaClienti.formEliminaClienti(listaClienti);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Nessun cliente trovato");
		}
		
		return elencoClienti;
	}
}

