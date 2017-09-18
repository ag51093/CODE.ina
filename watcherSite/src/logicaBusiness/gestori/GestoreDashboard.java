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
import logicaBusiness.entit‡.DatoSintesiDashboard;
import logicaBusiness.entit‡.Utente;
import logicaIntegrazione.DaoDashboard;
import logicaIntegrazione.DaoUtenti;
import logicaPresentazione.VistaDashboard;

/**
 * La classe si occupa di elaborarare i dati della dashboard degli utenti, 
 * prelevandoli da DAO appropriato e inviarli alla vista adatta per la visualizzazione
 * 
 * @author Antonio Garofalo, Matteo Forina
 *
 */
@WebServlet("/dashboard")
public class GestoreDashboard extends HttpServlet{
	private final static Pattern EMAIL_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	
	private static final long serialVersionUID = 1570722333470457862L;

	private final static int[] INTERVALLI_GIORNI = {30, 14, 7};
	private final static int INTERVALLO_DEFAULT = INTERVALLI_GIORNI[0];
	
	
	private final DaoUtenti daoUtenti = new DaoUtenti();
	
	private final VistaDashboard vistaDashboard = new VistaDashboard();
	private final static Logger LOGGER = Logger.getLogger(GestoreDashboard.class.getName());
	
	
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
		String utenteSelezionato = null;
		String idform = req.getParameter("idform");
		switch(idform) {
			case "sceglicliente": {
				utenteSelezionato = req.getParameter("listaclienti");
				if(utenteSelezionato != null && EMAIL_REGEX.matcher(utenteSelezionato).find()) {
					sessione.setAttribute("utenteselezionato", Validatore.sanitize(utenteSelezionato));
				}
				break;
			}
			case "filtradatidashboard":{
				this.visualizzaDatiDashboard(req, resp);
				break;
			}
			default:
				break;
		}
		
		this.inizializzaPagina(req, resp);
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
		this.inizializzaPagina(req, resp);
	}
	
	/**
	 * Metodo interno per costruire la struttura fissa della pagina al momento dei caricamenti e redirect alla stessa
	 * @param req richiesta della servlet
	 * @param resp risposta della servlet
	 * @throws ServletException paramentri non corretti
	 * @throws IOException dati di input/output mancanti o danneggiati
	 */
	private void inizializzaPagina(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession sessione = req.getSession();
		String utenteLoggato = (String) sessione.getAttribute("utenteloggato");
		req.setAttribute("utenteloggato", utenteLoggato);
		boolean ËAmministratore = (boolean) sessione.getAttribute("flagadminloggato");
		
		/*Determina il tipo di utente se standard o admin, e effettua il redirect alla pagina appropriata con le dovute strutture stampate*/
		if (ËAmministratore) {
			String utenteSelezionato = (String) sessione.getAttribute("utenteselezionato");
			LinkedList<Utente> listaClienti = null;
			try {
				listaClienti = daoUtenti.mostraClienti();
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Dati mancanti");
			}
			String selezioneclienti = vistaDashboard.mostraClienti(listaClienti, utenteSelezionato);
			//Se Ë stato selezionato un utente, mostra i filtri per ambienti e intervalli di tempo
			if (utenteSelezionato != null) {
				String filtriDashboard = this.mostraFiltriDashboard(utenteSelezionato);
				req.setAttribute("filtridashboard", filtriDashboard);
			}
			req.setAttribute("selezioneclienti", selezioneclienti);
			req.getRequestDispatcher("/dashboardAmministratore.jsp").forward(req, resp);
		} else {
			String filtriDashboard = this.mostraFiltriDashboard(utenteLoggato);
			req.setAttribute("filtridashboard", filtriDashboard);
			req.getRequestDispatcher("/dashboardStandard.jsp").forward(req, resp);
		}
	}
	
	/**
	 * Il metodo richiede al DAO la lista dei dati per la dashboard relativi all'utente target
	 */
	private void visualizzaDatiDashboard(HttpServletRequest req, HttpServletResponse resp) {
		
		DaoDashboard daoDashboard = new DaoDashboard();
		
		String risultato = "";
		String proprietario = "";
		String filtroAmbiente = "";
		String intervalloTempo = "";
		boolean daFiltrare = true;
		HttpSession sessione = req.getSession();
		
		filtroAmbiente = req.getParameter("filtroambiente");
		intervalloTempo = req.getParameter("intervallotempo");
		if ((filtroAmbiente.equals("")) && (Integer.parseInt(intervalloTempo) == INTERVALLO_DEFAULT)) {
			daFiltrare = false;
		}
			
		boolean flagAdmin = (Boolean)sessione.getAttribute("flagadminloggato");
		if (!flagAdmin) {
			proprietario = (String)sessione.getAttribute("utenteloggato");
		} else {
			proprietario = (String)sessione.getAttribute("utenteselezionato");
		}
		
		LinkedList<DatoSintesiDashboard> listaSintesi = null;
		LinkedList<DatoRilevazione> listaCritici = null;
		try {
			if(daFiltrare) {
				listaSintesi = daoDashboard.visualizzaDatiSintesi(proprietario, filtroAmbiente, Integer.parseInt(intervalloTempo));
				listaCritici = daoDashboard.visualizzaDatiCriticita(proprietario, filtroAmbiente, Integer.parseInt(intervalloTempo));
			} else {
				listaSintesi = daoDashboard.visualizzaDatiSintesi(proprietario, INTERVALLO_DEFAULT);
				listaCritici = daoDashboard.visualizzaDatiCriticita(proprietario, INTERVALLO_DEFAULT);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Dati mancanti");
		}
		
		risultato = vistaDashboard.visualizzaDatiSintesiDashboard(listaSintesi) + vistaDashboard.visualizzaDatiCriticiDashboard(listaCritici);
		if(risultato != null) {
			req.setAttribute("risultato", risultato);			
		}
	}
	
	/**
	 * Metodo interno per mostrare i filtri in base all'utente
	 */
	private String mostraFiltriDashboard(String unUtente) {
		String filtriDashboard = "";
		try {
			LinkedList<String> listaNomiAmbienti = this.mostraNomiAmbienti(unUtente);
			filtriDashboard = vistaDashboard.mostraFiltriDatiDashboard(listaNomiAmbienti, this.mostraIntervalliTempo());
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Dati mancanti");
		}
		return filtriDashboard;
	}

	/**
	 * Il metodo richiede al DAO la lista dei clienti
	 * 
	 * @return LinkedList lista di clienti
	 * @throws SQLException
	 */
	public LinkedList<Utente> mostraClienti() throws SQLException {
		LinkedList<Utente> lista = daoUtenti.mostraClienti();
		return lista;
	}
	
	/**
	 * Il metodo richiede al DAO la lista dei nomi degli ambienti relativi all'utente target
	 * 
	 * @param unaMail email identificativa dell'utente target della ricerca
	 * @return LinkedList lista di stringhe contenente l'insieme dei nomi degli ambienti
	 * @throws SQLException 
	 */
	public LinkedList<String> mostraNomiAmbienti(String unaMail) throws SQLException {
		
		GestoreDatiInstallazione gestoreDatiInstallazione = new GestoreDatiInstallazione();
		
		LinkedList<String> lista = gestoreDatiInstallazione.mostraNomiAmbienti(unaMail);
		return lista;
	}
	
	/**
	 * Il metodo richiede al DAO la lista degli intervalli di tempo per il filtraggio
	 */
	public int[] mostraIntervalliTempo() {
		
		return INTERVALLI_GIORNI;
	}
}
