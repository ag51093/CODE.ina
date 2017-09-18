package logicaIntegrazione;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe per il recupero e l'inserimento/eliminazione di informazioni sui sensori
 * 
 * @author Lamparelli Ivan
 *
 */
class DaoSensori {
	
	/**Il gestore della connessione con il database*/
	private final DBManager dbm = DBManager.getIstanza();
	
	/**L'oggetto usato per stampare i messaggi d'errore*/
	 private final static Logger LOGGER = Logger.getLogger(DaoSensori.class.getName());
	
	//I template delle stringhe da eseguire
	private final static String TEMPLATE_MOSTRA_TIPI_SENSORI_SISTEMA = 
			"SELECT tiposensore FROM tiposensore;";
	
	private final static String TEMPLATE_MOSTRA_TIPI_SENSORI_CLIENTE = 
			"SELECT s.tipo AS tiposensore FROM sensore s, datoinstallazione di"
			+ " WHERE di.codicesensore = s.codice AND di.proprietarioambiente = ? GROUP BY s.tipo";
	
	private final static String TEMPLATE_INSERISCI_TIPO_SENSORE = 
			"INSERT INTO tiposensore(tiposensore, unitmisura, strutturarilevazione) VALUES (?, ?, ?);";
	
	private final static String TEMPLATE_CERCA_TIPO_SENSORE = 
			"SELECT * FROM tiposensore WHERE tiposensore = ?;";
	
	private final static String TEMPLATE_MOSTRA_STRUTTURA_SENSORE = 
			"SELECT strutturarilevazione FROM tiposensore ts, sensore s" +
			" WHERE s.tipo = ts.tiposensore" +
			" AND s.codice = ?";
	
	private final static String TEMPLATE_INSERISCI_SENSORE = 
			"INSERT INTO sensore(codice, tipo, marca, modello) VALUES (?, ?, ?, ?);";
	
	private final static String TEMPLATE_ELIMINA_SENSORE = 
			"DELETE FROM sensore WHERE codice = ?;";
	
	private final static String TEMPLATE_CERCA_SENSORE = 
			"SELECT * FROM sensore WHERE codice = ?;";
	
	
	/**La query finale (costruita a partire dai template)*/
	private PreparedStatement query = null;
	
	/**L'oggetto ResultSet in cui vengono temporaneamente salvati i risultati delle query*/
	private ResultSet risultatoQuery = null;
	
	/**Il risultato da restituire per le query su una lista di tipi di sensore*/
	private LinkedList<String> tipiSensori = null;
	
	/**Il costruttore*/
	DaoSensori() {}
	
	/**
	 * Restituisce tutti i tipi di sensori registrati nel sistema
	 * @return Una lista di stringhe con i tipi di sensori
	 * @throws SQLException Eccezione in caso di lista vuota (nessun sensore registrato)
	 */
	LinkedList<String> mostraTipiSensori() throws SQLException {
		tipiSensori = new LinkedList<String>();
		
		query = dbm.ottieniPreparedStatement(TEMPLATE_MOSTRA_TIPI_SENSORI_SISTEMA);
				
		risultatoQuery = query.executeQuery();
				
		while (risultatoQuery.next()) {
			tipiSensori.add(risultatoQuery.getString("tiposensore"));
		}
		
		return tipiSensori;
	}
	
	
	/**
	 * Registra un nuovo tipo di sensore
	 * @param unTipoSensore Il tipo di sensore
	 * @param unaUnit‡DiMisura L'unit‡ di misura per i sensori di questo tipo
	 * @param unaStrutturaDiRilevazione La struttura con cui i sensori di questo tipo inviano le rilevazioni
	 * @return true in caso di inserimento effettuato, false in caso di tipo sensore gi‡ registrato
	 */
	boolean inserisciNuovoTipoSensore(String unTipoSensore, String unaUnit‡DiMisura, String unaStrutturaDiRilevazione) {
		boolean inserito = false;
		
		//Se NON Ë gi‡ presente, procedo con l'inserimento
		if (!ËPresenteTipoSensore(unTipoSensore)) {
			
			String strutturaSenzaSegni = unaStrutturaDiRilevazione.replaceAll("/", "");
						
			query = dbm.ottieniPreparedStatement(TEMPLATE_INSERISCI_TIPO_SENSORE);
			try {
				query.setString(1, unTipoSensore);
				query.setString(2, unaUnit‡DiMisura);
				query.setString(3, strutturaSenzaSegni);
				
				query.executeUpdate();
				
				inserito = true;
				
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problemi nell'inserire il tipo di sensore");
			}
			
		} 
				
		return inserito;
	}
	
	
	/**
	 * Metodo interno per controllare se un tipo sensore Ë registrato nel sistema
	 * @param unTipoSensore Il tipo sensore per cui effettuare il controllo
	 * @return true se il tipo sensore Ë registrato nel sistema, false altrimenti
	 */
	private boolean ËPresenteTipoSensore(String unTipoSensore) {
		boolean presente = false;
		
		query = dbm.ottieniPreparedStatement(TEMPLATE_CERCA_TIPO_SENSORE);
		
		try {
			query.setString(1, unTipoSensore);
			risultatoQuery = query.executeQuery();
			presente = risultatoQuery.next();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Problemi nel cercare il tipo di sensore");
		}
		
		return presente;
	}
	
	
	/**
	 * Restituisce i tipi di sensori posseduti da un cliente
	 * @param unProprietario il cliente
	 * @return Una lista di stringhe con i tipi di sensori
	 * @throws SQLException Eccezione in caso di lista vuota (cliente senza sensori)
	 */
	LinkedList<String> mostraTipiSensori(String unProprietario) throws SQLException {
		tipiSensori = new LinkedList<String>();
		
		query = dbm.ottieniPreparedStatement(TEMPLATE_MOSTRA_TIPI_SENSORI_CLIENTE);
		query.setString(1, unProprietario);
		
		risultatoQuery = query.executeQuery();
				
		while (risultatoQuery.next()) {
			tipiSensori.add(risultatoQuery.getString("tiposensore"));
		}
		
		return tipiSensori;
	}
	
	
	/**
	 * Restituisce la struttura con cui il sensore produce i dati nella stringa decimale
	 * @param unCodiceSensore Il codice del sensore di interesse
	 * @return La struttura della stringa decimale del sensore
	 * @throws SQLException Eccezione in caso di sensore non trovato
	 */
	String mostraStrutturaSensore(String unCodiceSensore) throws SQLException {
		String strutturaRilevazione = null;
		
		query = dbm.ottieniPreparedStatement(TEMPLATE_MOSTRA_STRUTTURA_SENSORE);
		query.setString(1, unCodiceSensore);
		
		risultatoQuery = query.executeQuery();
		
		if (risultatoQuery.next()) {
			strutturaRilevazione = risultatoQuery .getString("strutturarilevazione");
		}
				
		return strutturaRilevazione;
	}
	
	
	/**
	 * Registra un nuovo sensore nel sistema
	 * 
	 * @param unCodice il codice del sensore
	 * @param unTipo il tipo del sensore
	 * @param unaMarca la marca del sensore
	 * @param unModello il modello del sensore
	 * 
	 * @return true se il sensore Ë stato inserito, false se Ë gi‡ presente un sensore con lo stesso codice
	 * */
	boolean inserisciSensore(String unCodice, String unTipo, String unaMarca, String unModello) {
		
		//Controllo se Ë gi‡ presente un sensore con lo stesso codice
		boolean sensoreInserito = false;
						
		//Se il codice NON Ë gi‡ occupato, procedo con l'inserimento
		if (!this.ËPresenteSensore(unCodice)) {
		
			query = dbm.ottieniPreparedStatement(TEMPLATE_INSERISCI_SENSORE);
			try {
				query.setString(1, unCodice);
				query.setString(2, unTipo);
				query.setString(3, unaMarca);
				query.setString(4, unModello);
				
				//Eseguo l'inserimento, e salvo il successo/fallimento dell'inserimento in un boolean
				query.executeUpdate();
				sensoreInserito = true;
				
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problemi nell'inserimento del sensore");
			}
		}
				
		return sensoreInserito;
	}
	
	
	/**
	 * Elimina un sensore
	 * @param unCodice Il codice del sensore da eliminare
	 * @return true se il sensore era presente ed Ë stato eliminato, false altrimenti
	 */
	boolean eliminaSensore(String unCodice) {
		boolean sensoreEliminato = false;
		
		//Se Ë presente un sensore con il codice inserito
		if (this.ËPresenteSensore(unCodice)) {
			query = dbm.ottieniPreparedStatement(TEMPLATE_ELIMINA_SENSORE);
			
			try {
				query.setString(1, unCodice);
				query.executeUpdate();
				
				sensoreEliminato = true;
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problemi nell'eliminazione del sensore");
			}
			
		}
		
		return sensoreEliminato;
	}
	
	/**
	 * Metodo interno che controlla se esiste un sensore con il codice passato all'interno del sistema
	 * @ param unCodice il codice da cercare
	 * @return true se esiste un sensore con il codice cercato, false altrimenti
	 * */
	boolean ËPresenteSensore(String unCodice) {
		boolean sensorePresente = false;
		
		query = dbm.ottieniPreparedStatement(TEMPLATE_CERCA_SENSORE);
		try {
			query.setString(1, unCodice);
			risultatoQuery = query.executeQuery();
			
			//Se la query ha restituito un valore, imposto a true il boolean
			sensorePresente = risultatoQuery.next();
			
		} catch (SQLException e1) {
			LOGGER.log(Level.SEVERE, "Problemi nella ricerca del sensore");
		}
		return sensorePresente;
	}
}