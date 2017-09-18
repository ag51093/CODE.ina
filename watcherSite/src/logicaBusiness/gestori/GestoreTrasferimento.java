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
import javax.servlet.http.HttpSession;

import logicaBusiness.elaboratori.GestoreEmail;
import logicaIntegrazione.DaoDatiInstallazione;
import logicaIntegrazione.DaoTrasferimento;
import logicaPresentazione.VistaTrasferimento;

/**
 *La classe si occupa di elaborare le personalizzazioni del trasferimento automatico degli utenti
 * 
 * @author Antonio Garofalo, Matteo Forina
 *
 */
@WebServlet("/trasferimento")
public class GestoreTrasferimento extends HttpServlet {
	
	private static final long serialVersionUID = -7825929390498587841L;
	
	/** L'oggetto a cui delegare la lettura e modifica delle impostazioni riguardanti il trasferimento automatico */
	private final DaoTrasferimento daoTrasferimento = new DaoTrasferimento();
	
	/**L'oggetto da utilizzare per la stampa degli errori*/
	private final static Logger LOGGER = Logger.getLogger(GestoreTrasferimento.class.getName());
	
	private final static String DEFAULT_MAIL = "DEFAULT_MAIL";
	private final static String CUSTOM_MAIL = "CUSTOM_MAIL";
	
	/** Messaggio di errore in caso di inserimento di una terza parte non valida */
	private final static String ERRORE_TERZA_PARTE_NON_VALIDA = "ERRORE: L'indirizzo email inserito non Ë valido, digita nuovamente l'indirizzo email e premi su Conferma.";
	
	
	/**
	 * Le elaborazioni da svolgere quando arriva una richiesta POST
	 * @param req La richiesta di elaborazione
	 * @param resp La risposta dell'elaborazione
	 * @throws ServletException Eccezione in caso di richiesta non valida
	 * @throws IOException Eccezione in caso di problemi nei parametri in input/output
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("erroreinserimento", "");
		String idform = req.getParameter("idform");
		switch(idform) {
			case "cambiastatotrasfaut":{
				modificaStatoTrasferimento(req, resp);
				break;
			}
			case "personalizzatrasfaut":{
				personalizzaTrasferimento(req, resp);
				break;
			}
			default:{
				break;
			}
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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.inizializzaPagina(req, resp);
	}
	
	private void inizializzaPagina(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		/** L'oggetto a cui delegare la logica di presentazione */
		VistaTrasferimento vistaTrasferimento = new VistaTrasferimento();
		
		HttpSession sessione = req.getSession();
		String utenteLoggato = (String) sessione.getAttribute("utenteloggato");
		boolean ËAttivoTrasferimento = mostraStatoTrasferimento(utenteLoggato);
		String statoTrasferimento = vistaTrasferimento.scriviStatoTrasferimento(ËAttivoTrasferimento);
		req.setAttribute("statotrasferimento", statoTrasferimento);
		
		if (ËAttivoTrasferimento) {
			String nomiAmbienti = mostraSelezioneAmbienti(utenteLoggato);
			String emailInoltro = mostraEmailInoltro(utenteLoggato);
			String formPersonalizzaTrasferimento = vistaTrasferimento.mostraOpzioniTrasferimento(utenteLoggato, nomiAmbienti, emailInoltro);
			req.setAttribute("formpersonalizzatrasferimento", formPersonalizzaTrasferimento);			
		}
		
		req.getRequestDispatcher("/trasferimento.jsp").forward(req, resp);
	}
	
	/**
	 * Metodo che recupera le informazioni necessarie per complilare la lista degli ambienti da selezionare
	 * 
	 * @param unaMail - email dell'utente loggato
	 * @return una stringa contenente la lista degli ambienti strutturata in checkbox
	 */
	private String mostraSelezioneAmbienti(String unaMail) {
		String risultato = "";
		LinkedList<String> listaAmbientiCompleta = null;
		LinkedList<String> listaAmbientiSelezionati = null;
		try {
			listaAmbientiCompleta = mostraNomiAmbienti(unaMail);				
			listaAmbientiSelezionati = mostraNomiAmbientiSelezionati(unaMail);			
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, "Dati mancanti");
		}
		if (listaAmbientiCompleta == null) {
			listaAmbientiCompleta = new LinkedList<String>();
		}
		if (listaAmbientiSelezionati == null) {
			listaAmbientiSelezionati = new LinkedList<String>();
		}
		risultato += VistaTrasferimento.mostraCheckboxAmbienti(listaAmbientiCompleta, listaAmbientiSelezionati);
		return risultato;
	}
	
	/**
	 * Il metodo permette di attivare o disattivare la funzione Trasferimento Automatico
	 */
	private void modificaStatoTrasferimento(HttpServletRequest req, HttpServletResponse resp) {
		HttpSession sessione = req.getSession();
		String proprietario = (String) sessione.getAttribute("utenteloggato");
		boolean trasferimentoAttivo = mostraStatoTrasferimento(proprietario);
		if (trasferimentoAttivo) {
			daoTrasferimento.disattivaTrasferimento(proprietario);
		} else {
			daoTrasferimento.attivaTrasferimento(proprietario);
		}
		
	}
	
	/**
	 * Il metodo riceve le personalizzazioni dalla vista e le invia al DAO appropriato
	 */
	private void personalizzaTrasferimento(HttpServletRequest req, HttpServletResponse resp) {
		LinkedList<String> listaAmbientiSelezionati = new LinkedList<String>();
		String emailPerInoltro = "";
		String[] listaAmbienti = req.getParameterValues("selezionaambiente");
		String opzioneEmail = req.getParameter("selezionaemail");
		HttpSession sessione = req.getSession();
		String proprietario = (String) sessione.getAttribute("utenteloggato");
		
		/* Selezione degli ambienti da inviare al DAO */
		if (listaAmbienti != null) {
			for (int i = 0; i < listaAmbienti.length; i++) {
				listaAmbientiSelezionati.add(listaAmbienti[i]);
			}
		}
		daoTrasferimento.impostaAmbienti(proprietario, listaAmbientiSelezionati);
		
		/* Selezione della mail per l'inoltro */
		if (opzioneEmail.equals(DEFAULT_MAIL)) {
			emailPerInoltro = proprietario;
			/*Controlla che la mail del proprietario sia diversa da quella inserita precedentemente*/
			if(!emailPerInoltro.equals(mostraEmailInoltro(proprietario))) {
				daoTrasferimento.disattivaTerzaParte(proprietario);
			}
		} else if (opzioneEmail.equals(CUSTOM_MAIL)){
			emailPerInoltro = req.getParameter("insterzaparte");
			/*Controlla che la mail terza parte non sia vuota o nulla e che sia diversa dalla mail specificata precedentemente*/
			if (GestoreEmail.ËValidaEmail(emailPerInoltro)) {
				if (!emailPerInoltro.equals(mostraEmailInoltro(proprietario))) {
					daoTrasferimento.impostaTerzaParte(proprietario, emailPerInoltro);
				}else {
					req.setAttribute("erroreinserimento", ERRORE_TERZA_PARTE_NON_VALIDA);
				}
			}
		}
	}
	
	/**
	 * Il metodo mostra lo stato attuale del trasferimento automatico (attivato/disattivato)
	 * @param unaMail mail identificativa dell'utente target della richiesta
	 * @return true se attivo, false altrimenti
	 */
	public boolean mostraStatoTrasferimento(String unaMail) {
		return daoTrasferimento.ËAttivoTrasferimento(unaMail);
	}
	
	/**
	 * Il metodo richiede al DAO la lista dei nomi degli ambienti relativi all'utente target
	 * 
	 * @param unaMail email identificativa dell'utente target della ricerca
	 * @return LinkedList lista di stringhe contenente l'insieme dei nomi degli ambienti
	 * @throws SQLException 
	 */
	public LinkedList<String> mostraNomiAmbienti(String unaMail) throws SQLException {
		
		/** L'oggetto a cui delegare la lettura dei dati d'installazione per il popolamento delle opzioni */
		DaoDatiInstallazione daoDatiInstallazione = new DaoDatiInstallazione();
		
		LinkedList<String> lista = daoDatiInstallazione.mostraNomiAmbienti(unaMail);
		return lista;
	}
	
	/**
	 * Il metodo richiede al DAO la lista dei nomi degli ambienti selezionati per l'invio dei dati del Trasferimento Automatico
	 * @param unaMail mail identificativa dell'utente targget della ricerca
	 * @return lista di stringhe contenente l'insieme dei nomi degli ambienti
	 * @throws SQLException
	 */
	public LinkedList<String> mostraNomiAmbientiSelezionati(String unaMail) throws SQLException {
		return daoTrasferimento.mostraAmbientiFiltrati(unaMail);
	}
 	
	/**
	 * Il metodo richiede al DAO la mail verso cui inoltrare i dati del Trasferimento Automatico
	 * @param unaMail email identificativa dell'utente target della ricerca
	 * @return email verso cui inoltrare i dati
	 */
	public String mostraEmailInoltro(String unaMail) {
		String mailInoltro = "";
		if(daoTrasferimento.ËAttivaTerzaParte(unaMail)) {
			try {
				mailInoltro = daoTrasferimento.mostraTerzaParte(unaMail);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problema nell'inoltro");
			}
		//Se l'utente ha l'invio a terze parti disattivo, la mail a cui inviare Ë quella dell'utente
		} else {
			mailInoltro = unaMail;
		}
		return mailInoltro;
	}
}
