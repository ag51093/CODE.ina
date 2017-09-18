package logicaBusiness.gestori;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import logicaBusiness.elaboratori.Rilevatore;
import logicaBusiness.elaboratori.Validatore;
import logicaBusiness.entit‡.DatoInstallazione;
import logicaBusiness.entit‡.Utente;
import logicaIntegrazione.DaoDatiInstallazione;
import logicaIntegrazione.DaoUtenti;
import logicaPresentazione.VistaDatiInstallazione;

/**
 *La classe si occupa di elaborare i dati di installazione degli utenti, 
 *prelevati dal DAO
 * 
 * @author Antonio Garofalo, Matteo Forina
 *
 */
@WebServlet("/datiInstallazione")
public class GestoreDatiInstallazione extends HttpServlet {
	private final static Pattern EMAIL_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	
	private static final long serialVersionUID = 7323974468467067465L;
	
	private final DaoUtenti daoUtenti = new DaoUtenti();
	private final DaoDatiInstallazione daoDatiInstallazione = new DaoDatiInstallazione();
	private final VistaDatiInstallazione vistaDatiInstallazione = new VistaDatiInstallazione();
	
	private final static Pattern REGEX_CODICE_SENSORE = 
		    Pattern.compile("[a-z]{3}-[a-z]{3}-[a-z]{2}[0-9]{2}#[0-9]{4}");
	
	private final static String ERRORE_ID_SENSORE_NON_VALIDO = "ERRORE: Il formato del codice del sensore inserito non Ë valido, premi Mostra Dati per ricaricare la pagina.";
	private final static String ERRORE_TIPO_SENSORE_NON_VALIDO = "ERRORE: Il tipo o l'unit‡ di misura del sensore inserito non sono validi, premi Mostra Dati per ricaricare la pagina.";
	private final static String ERRORE_POSIZIONE_SENSORE_NON_VALIDA = "ERRORE: La posizione del sensore inserito non Ë valida validi, premi Mostra Dati per ricaricare la pagina.";
	
	private final static Logger LOGGER = Logger.getLogger(GestoreDatiInstallazione.class.getName());
	
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
				if(utenteSelezionato != null && EMAIL_REGEX.matcher(utenteSelezionato).find()){
					sessione.setAttribute("utenteselezionato", Validatore.sanitize(utenteSelezionato));
				}
				break;
			}
			case "filtradatiinstallazione":{
				this.visualizzaDatiInstallazione(req, resp);
				break;
			}
			case "inseriscidatoinstallazione": {
				this.inserisciDatoInstallazione(req, resp);
				break;
			}
			case "inseriscinuovotiposensore":{
				this.inserisciNuovoTipoSensore(req, resp);
				break;
			}
			case "modificadatoinstallazione": {
				this.modificaDatoInstallazione(req, resp);
				break;
			}
			case "eliminaambiente":{
				this.eliminaAmbiente(req, resp);
				break;
			}
			case "eliminasensore":{
				this.eliminaSensore(req, resp);
				break;
			}default:
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
			String selezioneclienti = vistaDatiInstallazione.mostraClienti(utenteSelezionato, listaClienti);
			//Se Ë stato selezionato un utente, mostra i filtri per ambienti e intervalli di tempo
			if (utenteSelezionato != null) {
				String filtriDatiInstallazione = this.mostraFiltriDatiInstallazione(utenteSelezionato);
				req.setAttribute("filtridatiinstallazione", filtriDatiInstallazione);
			}
			req.setAttribute("selezioneclienti", selezioneclienti);
			String risultato = (String) req.getAttribute("risultato");
			//MOSTRO LE OPERAZIONI DI AMMINISTRAZIONE SOLO SE CI SONO DATI DI INSTALLAZIONE PRESENTI PER L'UTENTE SELEZIONATO
			if(risultato != null) {
				try {
					this.mostraOperazioniAmministrazione(req, resp);
				} catch (SQLException e) {
					LOGGER.log(Level.SEVERE, "Dati mancanti");
				}
			}
			req.getRequestDispatcher("/datiInstallazioneAmministratore.jsp").forward(req, resp);
		} else {
			String filtriDatiInstallazione = this.mostraFiltriDatiInstallazione(utenteLoggato);
			req.setAttribute("filtridatiinstallazione", filtriDatiInstallazione);
			req.getRequestDispatcher("/datiInstallazioneStandard.jsp").forward(req, resp);
		}
	}
	
	/**
	 * Metodo interno per inizializzare le strutture di gestione dei dati di installazione (inserimento nuovo sensore, modifica di un dato di installazione, ecc..)
	 * @param req richiesta della servlet
	 * @param resp risposta della servlet
	 * @throws ServletException paramentri non corretti
	 * @throws IOException dati di input/output mancanti o danneggiati
	 * @throws SQLException dati non presenti nella base di dati
	 */
	private void mostraOperazioniAmministrazione(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, SQLException{
		HttpSession sessione = req.getSession();
		String utenteSelezionato = (String) sessione.getAttribute("utenteselezionato");
		
		//Inizializzazione form inserimento nuovo dato di installazione
		LinkedList<String> listaNomiTipiSensori = this.mostraListaSensori();
		String formInserisciDatoInstallazione = vistaDatiInstallazione.formInserisciDatoInstallazione(listaNomiTipiSensori);
		req.setAttribute("forminseriscidatoinstallazione", formInserisciDatoInstallazione);
		
		//Inizializzazione form inserimento nuovo tipo di sensore
		String[] struttureRilevazioni = this.mostraStruttureRilevazioni();
		String formInserisciNuovoTipoSensore = vistaDatiInstallazione.formInserisciNuovoTipoSensore(struttureRilevazioni);
		req.setAttribute("forminseriscinuovotiposensore", formInserisciNuovoTipoSensore);
		
		//Inizializzazione from modifica dato installazione (modifica della posizione del sensore)
		LinkedList<DatoInstallazione> listaDatiInstallazione = daoDatiInstallazione.visualizzaDatiInstallazione(utenteSelezionato);
		String formModificaDatoInstallazione = vistaDatiInstallazione.formModificaDatoInstallazione(listaDatiInstallazione);
		req.setAttribute("formmodificadatoinstallazione", formModificaDatoInstallazione);
		
		//Inizializzazione form elimina ambiente
		LinkedList<String> listaNomiAmbienti = this.mostraNomiAmbienti(utenteSelezionato);
		String formEliminaAmbiente = vistaDatiInstallazione.formEliminaAmbiente(listaNomiAmbienti);
		req.setAttribute("formeliminaambiente", formEliminaAmbiente);
		
		//Inizializzazione form elimina sensore
		LinkedList<DatoInstallazione> unaListaDatiInstallazione = daoDatiInstallazione.visualizzaDatiInstallazione(utenteSelezionato);
		String formEliminaSensore = vistaDatiInstallazione.formEliminaSensore(unaListaDatiInstallazione);
		req.setAttribute("formeliminasensore", formEliminaSensore);
	}
	
	/**
	 * Metodo interno per mostrare i filtri in base all'utente
	 */
	private String mostraFiltriDatiInstallazione(String unUtente) {
		String filtriDatiInstallazione = "";
		try {
			LinkedList<String> listaNomiAmbienti = this.mostraNomiAmbienti(unUtente);
			LinkedList<String> listaTipiSensori = this.mostraNomiTipiSensori(unUtente);
			filtriDatiInstallazione = vistaDatiInstallazione.mostraFiltriDatiInstallazione(listaNomiAmbienti, listaTipiSensori);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Dati mancanti");
		}
		return filtriDatiInstallazione;
	}
	
	
	/**
	 * Il metodo richiede al DAO la lista dei dati di installazione relativi all'utente target
	 */
	private void visualizzaDatiInstallazione(HttpServletRequest req, HttpServletResponse resp) {
		String risultato = "";
		String proprietario = "";
		String filtroAmbiente = "";
		String filtroTipoSensore = "";
		boolean daFiltrare = true;
		HttpSession sessione = req.getSession();		
		
		filtroAmbiente = req.getParameter("filtroambiente");
		filtroTipoSensore = req.getParameter("filtrotiposensore");
		
		if ((filtroAmbiente == "") && (filtroTipoSensore == "")) {
			daFiltrare = false;
		}
		
		if (!(Boolean)sessione.getAttribute("flagadminloggato")) {
			proprietario = (String)sessione.getAttribute("utenteloggato");
		} else {
			proprietario = (String)sessione.getAttribute("utenteselezionato");
		}
		
		LinkedList<DatoInstallazione> lista = null;
		try {
			if(daFiltrare) {
				lista = daoDatiInstallazione.visualizzaDatiInstallazione(proprietario, filtroAmbiente, filtroTipoSensore);
			} else {
				lista = daoDatiInstallazione.visualizzaDatiInstallazione(proprietario);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Dati mancanti");
		}
		
		risultato = vistaDatiInstallazione.visualizzaDatiInstallazione(lista);
		if (risultato != null) {
			req.setAttribute("risultato", risultato);			
		}
	}
	
	/**
	 * Il metodo privato permette all'amministratore di inserire un nuovo dato di installazione
	 */
	private void inserisciDatoInstallazione(HttpServletRequest req, HttpServletResponse resp) {
		String nomeambiente = req.getParameter("insnomeambiente");
		String tipoambiente = req.getParameter("instipoambiente");
		String tiposensore = req.getParameter("instiposensore");
		String marcasensore = req.getParameter("insmarcasensore");
		String modellosensore = req.getParameter("insmodellosensore");
		String idsensore = req.getParameter("insidsensore");
		String posizionesensore = req.getParameter("insposizionesensore");
		
		HttpSession sessione = req.getSession();
		String proprietarioambiente = (String)sessione.getAttribute("utenteselezionato");
		
		if(ËValidoCodiceSensore(idsensore)) {
			daoDatiInstallazione.inserisciDatoInstallazione(proprietarioambiente, nomeambiente, tipoambiente, 
					tiposensore, marcasensore, modellosensore, idsensore, posizionesensore);
		} else {
			req.setAttribute("erroreinserimento", ERRORE_ID_SENSORE_NON_VALIDO);
		}
	}
	
	/**
	 * Il metodo verifica la correttezza sintattica del codice del sensore inserito
	 * 
	 * @param unCodiceSensore stringa contenente il codice da validare
	 * @return true se la struttura Ë corretta, false altrimenti
	 */
	private static boolean ËValidoCodiceSensore(String unCodiceSensore) {
     	Matcher matcher = REGEX_CODICE_SENSORE.matcher(unCodiceSensore);
     	return matcher.find();
	 }
	
	/**
	 * Il metodo privato permette all'amministratore di inserire un nuovo tipo di sensore
	 */
	private void inserisciNuovoTipoSensore(HttpServletRequest req, HttpServletResponse resp) {
		String nuovoTipoSensore = req.getParameter("insnuovotiposensore");
		String unitamisura = req.getParameter("insunitamisura");
		String strutturaRilevazione = req.getParameter("insstrutturarilevazione");
		boolean inserito = daoDatiInstallazione.inserisciNuovoTipoSensore(nuovoTipoSensore, unitamisura, strutturaRilevazione);
		//Se l'inserimento nel dato non va a buon fine, viene segnalato l'errore 
		if(!inserito) {
			req.setAttribute("erroreinserimento", ERRORE_TIPO_SENSORE_NON_VALIDO);
		}
	}
	
	/**
	 * Il metodo privato consente di modificare la posizione di un sensore nel relativo dato di installazione
	 */
	private void modificaDatoInstallazione(HttpServletRequest req, HttpServletResponse resp) {
		String posizione = "";
		String ambiente = "";
		String codicesensore = "";
		String proprietarioambiente = "";
		String ambientenome = "";
		
		ambientenome = req.getParameter("modificasensore");
		String[] split = ambientenome.split("_");
		ambiente = split[0];
		codicesensore = split[1];
		posizione = req.getParameter("modposizionesensore");
		
		HttpSession sessione = req.getSession();
		proprietarioambiente = (String)sessione.getAttribute("utenteselezionato");
		boolean inserito = daoDatiInstallazione.modificaPosizioneDatoInstallazione(proprietarioambiente, ambiente, codicesensore, posizione);
		//Se l'inserimento non va a buon fine, viene segnalato l'errore
		if(!inserito) {
			req.setAttribute("erroreinserimento", ERRORE_POSIZIONE_SENSORE_NON_VALIDA);
		}
	}
	
	/**
	 * Il metodo privato permette di eliminare un ambiente dal sistema e i dati di installazione associati
	 */
	private void eliminaAmbiente(HttpServletRequest req, HttpServletResponse resp) {
		HttpSession sessione = req.getSession();
		String nomeAmbiente = (String)req.getParameter("eliminaambiente");
		String proprietarioambiente = (String)sessione.getAttribute("utenteselezionato");

		daoDatiInstallazione.eliminaAmbiente(proprietarioambiente, nomeAmbiente);
		
	}
	
	/**
	 * Il metodo privato permette di eliminare un dato di installazione da un ambiente
	 */
	private void eliminaSensore(HttpServletRequest req, HttpServletResponse resp) {
		HttpSession sessione = req.getSession();
		String codicesensore = req.getParameter("eliminasensore");
		String proprietariosensore = (String)sessione.getAttribute("utenteselezionato");

		daoDatiInstallazione.eliminaSensore(proprietariosensore, codicesensore);
		
	}
		
	/**
	 * Il metodo richiede al DAO la lista dei nomi degli ambienti relativi all'utente target
	 * 
	 * @param unaMail email identificativa dell'utente target della ricerca
	 * @return LinkedList lista di stringhe contenente l'insieme dei nomi degli ambienti
	 * @throws SQLException 
	 */
	public LinkedList<String> mostraNomiAmbienti(String unaMail) throws SQLException {
		LinkedList<String> lista = daoDatiInstallazione.mostraNomiAmbienti(unaMail);
		return lista;
	}
	
	/**
	 * Il metodo richiede al DAO la lista dei tipi di sensore relativi all'utente target
	 * 
	 * @param unaMail email identificativa dell'utente target della ricerca
	 * @return LinkedList lista di stringhe contenente l'insieme dei tipi dei sensori installati negli ambienti
	 * @throws SQLException 
	 */
	public LinkedList<String> mostraNomiTipiSensori(String unaMail) throws SQLException {
		LinkedList<String> lista = daoDatiInstallazione.mostraTipiSensori(unaMail);
		return lista;
	}
	

	/**
	 * Il metodo richiede al DAO la lista dei sensori relativi a un utente target
	 * 
	 * @param unaMail email identificativa dell'utente target della ricerca
	 * @return LinkedList lista di oggetti di tipo DatoInstallazione
	 * @throws SQLException
	 */
	public LinkedList<DatoInstallazione> mostraListaSensori (String unaMail) throws SQLException{
		LinkedList<DatoInstallazione> lista = daoDatiInstallazione.visualizzaDatiInstallazione(unaMail);
		return lista;
	}
	
	/**
	 * Il metodo richiede al DAO la lista dei sensori presenti nel sistema
	 * @return LinkedList lista di stringhe di sensori
	 * @throws SQLException
	 */
	public LinkedList<String> mostraListaSensori() throws SQLException {
		LinkedList<String> lista = daoDatiInstallazione.mostraTipiSensori();
		return lista;
	}
	
	/**
	 * Il metodo richiede al DAO la lista dei clienti
	 * 
	 * @return LinkedList di clienti
	 * @throws SQLException
	 */
	public LinkedList<Utente> mostraClienti() throws SQLException {
		LinkedList<Utente> lista = daoUtenti.mostraClienti();
		return lista;
	}
	
	/**	
	 *Il metodo restituisce un array di stringhe con le strutture delle rilevazioni 
	 * @return array di stringhe 
	 */
	public String[] mostraStruttureRilevazioni() {
		return Rilevatore.getIstanza().mostraStruttureRilevazioni();
	}
}