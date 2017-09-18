package logicaIntegrazione;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import java.util.logging.Level;
import java.util.logging.Logger;

import logicaBusiness.entit‡.Utente;

/**
 * Classe per il recupero e la modifica di dati degli utenti sul database
 * 
 * @author Ivan Lamparelli, Graziano Accogli
 * */
public class DaoUtenti {
	
	/**Il gestore della connessione con il database*/
	private final DBManager dbm = DBManager.getIstanza();
	
	/**L'oggetto usato per stampare i messaggi d'errore*/
	 private final static Logger LOGGER = Logger.getLogger(DaoUtenti.class.getName());
	
	//I template delle query da eseguire
	private final static String TEMPLATE_VERIFICA_CREDENZIALI = 
			"SELECT email, flagadmin FROM utente WHERE email = ? AND password = SHA2(?, 256);";
	
	private final static String TEMPLATE_CERCA_UTENTE = 
			"SELECT email, flagadmin FROM utente WHERE email = ?;";
	
	private final static String QUERY_MOSTRA_ELENCO_CLIENTI = 
			"SELECT email, flagadmin FROM utente WHERE flagadmin = 0;";  
	
	private final static String TEMPLATE_INSERISCI_CLIENTE = 
			"INSERT INTO utente(email, password, flagadmin) VALUES (?, SHA2(?, 256), '0');";
	
	private final static String TEMPLATE_ELIMINA_CLIENTE = 
			"DELETE FROM utente WHERE email = ? AND flagadmin = 0;";
	
	/**La query finale (costruita a partire dai template)*/
	private PreparedStatement query = null;
	
	/**L'oggetto ResultSet in cui vengono temporaneamente salvati i risultati delle query*/
	private ResultSet risultatoQuery = null;
	
	/**Il risultato da restituire per le query su un singolo utente*/
	private Utente utente = null;
	
	/**Il costruttore*/
	public DaoUtenti() {}
	
	/**
	 * Controlla le credenziali di un utente
	 * 
	 * @param unaMail la mail da cercare
	 * @param unaPassword la password da cercare
	 * 
	 * @return Un oggetto utente popolato in caso di credenziali valide, null altrimenti
	 * @throws SQLException Eccezione in caso di utente non presente
	 */
	public Utente validaUtente(String unaMail, String unaPassword) throws SQLException {
		
		query = dbm.ottieniPreparedStatement(TEMPLATE_VERIFICA_CREDENZIALI);
		query.setString(1, unaMail);
		query.setString(2, unaPassword);
		
		risultatoQuery = query.executeQuery();
		
		utente = this.popolaUtente(risultatoQuery);
		
		return utente;
	}
	
	/**
	 * Metodo interno per cercare le informazioni di un utente nel database
	 * 
	 * @param unaMail la mail dell'utente da cercare
	 * 
	 * @return Un oggetto utente popolato in caso di credenziali valide, null altrimenti
	 * @throws SQLException Eccezione in caso di utente non presente
	 */
		
	Utente cercaUtente(String unaMail) throws SQLException {
		
		query = dbm.ottieniPreparedStatement(TEMPLATE_CERCA_UTENTE);
		query.setString(1, unaMail);
		
		risultatoQuery = query.executeQuery();
		
		utente = this.popolaUtente(risultatoQuery);
				
		return utente;
	}
	
	/**
	 * Metodo interno per verificare se un utente Ë presente nel database
	 * @param unaMail La mail dell'utente da cercare
	 * @return true se l'utente Ë presente, false altrimenti
	 */
	public boolean ËPresenteUtente(String unaMail) {
		boolean presente = false;
		
		try {
			if (cercaUtente(unaMail) != null)
				presente = true;
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Problemi nel cercare l'utente");
		}
		return presente;
	}
	
	/**
	 * Metodo per controllare se l'utente Ë cliente o amministratore
	 * @param unaMail La Mail da controllare
	 * @return true se Ë un cliente, false altrimenti
	 */
	boolean ËCliente(String unaMail) {
		boolean cliente = false;
		
		try {
			Utente utente = this.cercaUtente(unaMail);
			if (utente != null && utente.ËAmministratore() == false) {
				cliente = true;
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Problemi nella verifica se Ë un cliente o amministratore");
		}
		
		return cliente;
	}
	/**Restituisce l'elenco dei clienti
	 * @return Una lista con l'elenco dei clienti
	 * @throws SQLException Eccezione in caso di lista vuota
	 * */
	public LinkedList<Utente> mostraClienti() throws SQLException {

		/**Il risultato da restituire per le query su una lista di utenti*/
		LinkedList<Utente> listaUtenti = new LinkedList<Utente>();
		
		query = dbm.ottieniPreparedStatement(QUERY_MOSTRA_ELENCO_CLIENTI);
		risultatoQuery = query.executeQuery();

		//FinchÈ ci sono altri clienti 
		while(risultatoQuery.next()) {
			String email = risultatoQuery.getString("email");
			boolean flagAdmin = risultatoQuery.getBoolean("flagadmin");
			utente = new Utente(email, flagAdmin);
			listaUtenti.add(utente);
		}
		return listaUtenti;
	}
	
	
	/**
	 * Registra un nuovo cliente nel database
	 * 
	 * @param unaMail la mail del cliente da registrare
	 * @param unaPassword la password del cliente da registrare
	 * 
	 * @return true se il cliente Ë stato inserito, false se il cliente Ë gi‡ presente
	 * */
	public boolean inserisciNuovoCliente(String unaMail, String unaPassword) {
		boolean clienteInserito = false;
		try {
			utente = this.cercaUtente(unaMail);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Problemi nella ricerca dell'utente");
		}
		
		//Se NON esiste un utente con l'email da inserire, procedo con l'inserimento
		if(utente == null) {
			query = dbm.ottieniPreparedStatement(TEMPLATE_INSERISCI_CLIENTE);
			try {
				query.setString(1, unaMail);
				query.setString(2, unaPassword);
				
				query.executeUpdate();
				clienteInserito = true;
				
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problemi nell'inserimento del nuovo cliente");
			}
			
		}

		return clienteInserito;
	}
	
	/**
	 * Elimina un cliente nel database
	 * @param unaMail la mail del cliente da eliminare
	 * @return true se il cliente Ë stato eliminato, false se la mail non Ë presente o appartiene ad un amministratore
	 * */
	public boolean eliminaCliente(String unaMail) {
		boolean clienteEliminato = false;
		try {
			utente = this.cercaUtente(unaMail);
		} catch (SQLException e1) {
			LOGGER.log(Level.SEVERE, "Problemi nella ricerca dell'utente");
		}
		
		if (utente != null) { //Se l'utente cercato Ë presente 
			if (utente.ËAmministratore() == false) { //Se l'utente Ë un cliente
				query = dbm.ottieniPreparedStatement(TEMPLATE_ELIMINA_CLIENTE);
				try {
					query.setString(1, unaMail);
					query.executeUpdate();
					
				} catch (SQLException e) {
					LOGGER.log(Level.SEVERE, "Problemi nell'eliminazione dell'utente");
				}
				clienteEliminato = true;		
			}
		}
		
		return clienteEliminato;
	}
	
	/**
	 * Metodo interno per la conversione da ResultSet a Utente
	 * 
	 * @param rsUtente Il risultato di una query di ricerca
	 * @return Un oggetto utente popolato in caso di utente presente, null altrimenti
	 * @throws SQLException Eccezione in caso di ResultSet vuoto
	 */
	private Utente popolaUtente(ResultSet rsUtente) throws SQLException {
		//Se il ResultSet contiene un utente
		if(rsUtente.next()) { 
			String email = rsUtente.getString("email");
			boolean flagAdmin = rsUtente.getBoolean("flagadmin");
			utente = new Utente(email, flagAdmin);			
		
		//Se il ResultSet Ë vuoto
		} else {
			utente = null;	//Reimposto a null la variabile utente (riazzerando gli eventuali contenuti presenti)
		}
		
		return utente;
	}
}