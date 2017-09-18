package logicaBusiness.gestori;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
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
import logicaBusiness.entit‡.DatoRilevazione;
import logicaBusiness.entit‡.Utente;
import logicaIntegrazione.DaoDatiRilevazioni;
import logicaIntegrazione.DaoUtenti;
import logicaPresentazione.VistaDatiRilevazioni;

/**
 * La classe gestisce i dati delle rilevazioni degli utenti,
 * prelevati dal DAO
 * 
 * @author Antonio Garofalo, Matteo Forina, Ivan Lamparelli, Graziano Accogli
 */
@WebServlet("/datiRilevazioni")
public class GestoreDatiRilevazioni extends HttpServlet {
	private final static Pattern EMAIL_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	
	private static final long serialVersionUID = 3653959764805139182L;
	
	/**L'oggetto a cui delegare la lettura e modifica delle impostazioni riguardanti i dati di rilevazione*/
	private final DaoDatiRilevazioni daoDatiRilevazioni = new DaoDatiRilevazioni();
	
	/**L'oggetto a cui delegare la lettura dei dati dei clienti*/
	private final DaoUtenti daoUtenti = new DaoUtenti();
	
	/**L'oggetto a cui delegare la stampa dei dati*/
	private final VistaDatiRilevazioni vistaRilevazioni = new VistaDatiRilevazioni();
	
	/**L'oggetto da utilizzare per la stampa degli errori*/
	private final static Logger LOGGER = Logger.getLogger(GestoreDatiRilevazioni.class.getName());
	
	
	
	/**
	 * Le elaborazioni da svolgere quando arriva una richiesta POST
	 * @param req La richiesta di elaborazione
	 * @param resp La risposta dell'elaborazione
	 * @throws ServletException Eccezione in caso di richiesta non valida
	 * @throws IOException Eccezione in caso di problemi nei parametri in input/output
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession sessione = req.getSession();
		boolean ËAmministratore = (boolean) sessione.getAttribute("flagadminloggato");
		
		if (ËAmministratore) {
			String idform = req.getParameter("idform");
			
			/* Se ha selezionato un nuovo cliente:
			 * - aggiorno il form di selezione cliente
			 * - mostro i filtri del nuovo cliente
			 */
			if (idform.equals("sceglicliente")) {
				String utenteDaSelezionare = req.getParameter("utenteselezionato");
				if(utenteDaSelezionare != null && EMAIL_REGEX.matcher(utenteDaSelezionare).find()) {
					impostaFormScegliCliente(req, utenteDaSelezionare);
					impostaFormFiltri(req, utenteDaSelezionare);
				}
			} 

			//Se ha richiesto la visualizzazione dei dati, stampo i dati del cliente
			else if (idform.equals("filtradatirilevazioni")) {
				String utenteSelezionato = (String) sessione.getAttribute("utenteselezionato");
				if(utenteSelezionato != null && EMAIL_REGEX.matcher(utenteSelezionato).find()) {
					impostaFormScegliCliente(req, utenteSelezionato);
					impostaFormFiltri(req, utenteSelezionato);
				}
				
				String filtroAmbiente = req.getParameter("filtroambiente");
				String filtroTipoSensore = req.getParameter("filtrotiposensore");
				
				impostaTabellaDati(req, utenteSelezionato, filtroAmbiente, filtroTipoSensore);
			}
		
			req.getRequestDispatcher("/datiRilevazioniAmministratore.jsp").forward(req, resp);			
			
		//Se l'utente Ë di tipo standard
		} else {
			//Aggiorno i filtri
			String cliente = (String) sessione.getAttribute("utenteloggato");
			impostaFormFiltri(req, cliente);
			
			//Stampo i dati
			String filtroAmbiente = req.getParameter("filtroambiente");
			String filtroTipoSensore = req.getParameter("filtrotiposensore");
			impostaTabellaDati(req, cliente, filtroAmbiente, filtroTipoSensore);
			
			req.getRequestDispatcher("/datiRilevazioniStandard.jsp").forward(req, resp);		
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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
						
		HttpSession sessione = req.getSession();
		boolean ËAmministratore = (boolean) sessione.getAttribute("flagadminloggato");
		
		if (ËAmministratore) {
			String utenteSelezionato = (String) sessione.getAttribute("utenteselezionato");
			
			//Se non c'Ë un utente selezionato, stampo solo il form di scelta clienti
			if (utenteSelezionato == null || utenteSelezionato.equals("")) {
				impostaFormScegliCliente(req);			
			}
			
			/* Se l'amministratore ha gi‡ scelto un cliente in precedenza, stampo:
			 * - il form di scelta clienti (con incluso l'avviso del cliente attualmente selezionato)
			 * - il form di visualizzazione e filtraggio dati
			 */
			else {
				impostaFormScegliCliente(req, utenteSelezionato);
				
				impostaFormFiltri(req, utenteSelezionato);
			}
			
			req.getRequestDispatcher("/datiRilevazioniAmministratore.jsp").forward(req, resp);			
		
		//Se l'utente Ë standard, mostro il form di visualizzazione e filtraggio dati
		} else {
			String cliente = (String) sessione.getAttribute("utenteloggato");
			
			impostaFormFiltri(req, cliente);
			
			req.getRequestDispatcher("/datiRilevazioniStandard.jsp").forward(req, resp);		
		}
	}
	
	/**
	 * Imposta nella pagina legata alla richiesta l'elenco dei clienti selezionabili e il cliente attualmente selezionato
	 * @param req La richiesta legata alla pagina su cui impostare i dati
	 * @param unUtenteSelezionato Il cliente attualmente selezionato
	 */
	private void impostaFormScegliCliente(HttpServletRequest req, String unUtenteSelezionato) {
		
		try {
			LinkedList<Utente> listaClienti = daoUtenti.mostraClienti();
			String formScegliCliente = vistaRilevazioni.mostraFormSceltaCliente(listaClienti, unUtenteSelezionato);
			req.setAttribute("formsceglicliente", formScegliCliente);
			HttpSession sessione = req.getSession();
			sessione.setAttribute("utenteselezionato", Validatore.sanitize(unUtenteSelezionato));
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Problemi nell'impostare il form di scelta cliente");
		}
	}
	
	/**
	 * Imposta nella pagina legata alla richiesta l'elenco dei clienti selezionabili
	 * @param req La richiesta legata alla pagina su cui impostare i dati
	 */
	private void impostaFormScegliCliente(HttpServletRequest req) {
		try {
			LinkedList<Utente> listaClienti = daoUtenti.mostraClienti();
			String formScegliCliente = vistaRilevazioni.mostraFormSceltaCliente(listaClienti, null);
			req.setAttribute("formsceglicliente", formScegliCliente);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Problemi nell'impostare il form di scelta cliente");
		}
	}
	
	/**
	 * Imposta nella pagina legata alla richiesta il form dei filtri per le rilevazioni
	 * @param req La richiesta legata alla pagina su cui impostare i dati
	 * @param unCliente Il cliente per cui personalizzare i filtri da mostrare
	 */
	private void impostaFormFiltri(HttpServletRequest req, String unCliente) {	
		try {
			LinkedList<String> listaAmbienti = daoDatiRilevazioni.mostraNomiAmbienti(unCliente);
			LinkedList<String> listaTipiSensori = daoDatiRilevazioni.mostraTipiSensori(unCliente);
			String formFiltri = vistaRilevazioni.mostraFiltriDatiRilevazioni(unCliente, listaAmbienti, listaTipiSensori);
			
			req.setAttribute("filtridatirilevazioni", formFiltri);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Problemi nell'impostare il form con i filtri sui dati del cliente");
		}
	}
			
	/**
	 * Imposta nella pagina legata alla richiesta la tabella con i dati di rilevazioni di un cliente
	 * @param req La richiesta legata alla pagina su cui impostare i dati
	 * @param unCliente Il cliente di cui mostrare i dati
	 * @param unAmbiente L'ambiente di cui mostrare i dati
	 * @param unTipoSensore Il sensore di cui mostrare i dati
	 */
	private String impostaTabellaDati(HttpServletRequest req, String unCliente, String unAmbiente, String unTipoSensore) {
		String tabellaDati = "";
		LinkedList<DatoRilevazione> listaRilevazioni = null;
		try {
			if (unAmbiente.equals("") && unTipoSensore.equals("")) {
				listaRilevazioni = daoDatiRilevazioni.visualizzaDatiRilevazioni(unCliente);
			} else {
				listaRilevazioni = daoDatiRilevazioni.visualizzaDatiRilevazioni(unCliente, unAmbiente, unTipoSensore);				
			}
			
			tabellaDati = vistaRilevazioni.visualizzaDatiRilevazioni(listaRilevazioni);
			req.setAttribute("datirilevazioni", tabellaDati);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Problemi nell'impostare la tabella con i dati del cliente");
		}
		
		return tabellaDati;
	}
}