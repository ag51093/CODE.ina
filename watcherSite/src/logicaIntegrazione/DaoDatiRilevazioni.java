package logicaIntegrazione;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;

import java.util.logging.Level;
import java.util.logging.Logger;

import logicaBusiness.entit‡.DatoRilevazione;

/**
 * Classe per il recupero e l'inserimento dei dati di rilevazione
 * 
 * @author Graziano Accogli, Ivan Lamparelli 
 * */
public class DaoDatiRilevazioni {
	
	/**Il gestore della connessione con il database*/
	private final DBManager dbm = DBManager.getIstanza();
	
	/**L'oggetto usato per stampare i messaggi d'errore*/
	 private final static Logger LOGGER = Logger.getLogger(DaoDatiRilevazioni.class.getName());
	
	/**La query finale (costruita a partire dai template)*/
	private PreparedStatement query = null;
	
	/**L'oggetto ResultSet in cui vengono temporaneamente salvati i risultati delle query*/
	private ResultSet risultatoQuery = null;
	
	/**Il risultato da restituire per le query su una lista di dati*/
	private LinkedList<DatoRilevazione> listaDati = null;
	
	/**I risultati da restituire per le query su elenchi di stringhe*/
	private LinkedList<String> listaStringhe = null;
	
	/**Controlla lo stato della View sui dati di installazione*/
	private static boolean vistaIstanziata = false;
	
	private final static String VISTA_RILEVAZIONI = 
			"CREATE VIEW IF NOT EXISTS rilevazioni AS" + 
			" SELECT di.proprietarioambiente AS proprietarioambiente, di.nomeambiente AS nomeambiente, s.tipo AS tiposensore, s.codice AS codicesensore," + 
			" dr.valore AS valorerilevazione, dr.flagerrore AS flagerrore, ts.unitmisura AS unitmisura, dr.data AS data, dr.messaggio AS messaggio" +
			" FROM datorilevazione dr, datoinstallazione di, sensore s, tiposensore ts" +
			" WHERE di.codicesensore = s.codice AND s.codice = dr.codicesensore AND s.tipo = ts.tiposensore" +
			" ORDER BY dr.data DESC;";
	
	private final static String TEMPLATE_MOSTRA_DATI = 
			"SELECT * FROM rilevazioni WHERE proprietarioambiente = ?";
	
	private final static String TEMPLATE_FILTRA_AMBIENTI_E_SENSORI = 
			"SELECT * FROM rilevazioni WHERE proprietarioambiente = ? AND nomeambiente = ? AND tiposensore = ?";
	
	private final static String TEMPLATE_FILTRA_AMBIENTI = 
			"SELECT * FROM rilevazioni WHERE proprietarioambiente = ? AND nomeambiente = ?";

	private final static String TEMPLATE_FILTRO_SENSORI = 
			"SELECT * FROM rilevazioni WHERE proprietarioambiente = ? AND tiposensore = ?";
		
	private final static String TEMPLATE_INSERISCI_DATO = 
			"INSERT INTO datorilevazione(codicesensore, valore, flagerrore, data, messaggio)"
			+ " VALUES (?, ?, ?, ?, ?);";
	
	private final static String TEMPLATE_CERCA_RILEVAZIONE =
			"SELECT * FROM rilevazioni WHERE codicesensore = ? AND data = ?;";
	
	
	/**Il costruttore*/
	public DaoDatiRilevazioni() {
		
		//Se la vista non Ë gi‡ stata creata, provvedo a crearla
		if (!vistaIstanziata) {
			query = dbm.ottieniPreparedStatement(VISTA_RILEVAZIONI);
			try {
				query.executeUpdate();
				vistaIstanziata = true;
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problemi con la creazione della vista");
			}
		}
	}
	
	
	/**
	 * Restituisce una lista con i dati delle rilevazioni del cliente
	 * @param unProprietario Il cliente
	 * @return Una LinkedList di Dati di Rilevazioni
	 * @throws SQLException Eccezione in caso di lista vuota (cliente senza rilevazioni)
	 */
	public LinkedList<DatoRilevazione> visualizzaDatiRilevazioni(String unProprietario) throws SQLException {
		listaDati = new LinkedList<DatoRilevazione>();
		
		query = dbm.ottieniPreparedStatement(TEMPLATE_MOSTRA_DATI);
		query.setString(1, unProprietario);
		risultatoQuery = query.executeQuery();
		
		listaDati = this.popolaListaDati(risultatoQuery);
		
		
		return listaDati;
	}
	
	
	/**
	 * Restituisce i dati di rilevazione di un cliente che rispettano i filtri selezionati
	 * @param unProprietario il cliente di cui servono i dati
	 * @param unNomeAmbiente il filtro sugli ambienti
	 * @param unTipoSensore il filtro sui sensori
	 * @return Una lista con i dati filtrati del cliente
	 * @throws SQLException Eccezione in caso di mancanza di dati
	 */
	public LinkedList<DatoRilevazione> visualizzaDatiRilevazioni(String unProprietario, String unNomeAmbiente, String unTipoSensore) throws SQLException {
		listaDati = new LinkedList<DatoRilevazione>();
		
		//Se i filtri non contengono nessun input
		if(unNomeAmbiente.compareTo("") == 0 && unTipoSensore.compareTo("") == 0) {
			listaDati = this.visualizzaDatiRilevazioni(unProprietario);
			return listaDati;
		}
		
		//Se uno o pi˘ filtri contengono valori
		else {
			
			//Filtro per ambiente e sensore
			if(unNomeAmbiente.compareTo("") != 0 && unTipoSensore.compareTo("") != 0) {
				query = dbm.ottieniPreparedStatement(TEMPLATE_FILTRA_AMBIENTI_E_SENSORI);
				query.setString(1, unProprietario);
				query.setString(2, unNomeAmbiente);
				query.setString(3, unTipoSensore);
			}
			//Filtro solo su ambiente 
			else if(unNomeAmbiente.compareTo("") != 0 && unTipoSensore.compareTo("") == 0) {
				query = dbm.ottieniPreparedStatement(TEMPLATE_FILTRA_AMBIENTI);
				query.setString(1, unProprietario);
				query.setString(2, unNomeAmbiente);
			}
			//Filtro solo su sensore
			else {
				query = dbm.ottieniPreparedStatement(TEMPLATE_FILTRO_SENSORI);
				query.setString(1, unProprietario);
				query.setString(2, unTipoSensore);
			}
			
			risultatoQuery = query.executeQuery();
			
			listaDati = popolaListaDati(risultatoQuery);
			
			return listaDati;
		}
	}
	
	/**
	 * Metodo interno per la conversione da ResultSet a Lista di Dati Rilevazione
	 * @param rsLista il ResultSet di una query contenente una lista di dati
	 * @return Una Lista con i dati ricevuti in input
	 * @throws SQLException Eccezione in caso di ResultSet senza dati
	 */
	private LinkedList<DatoRilevazione> popolaListaDati(ResultSet rsLista) throws SQLException {
		while(rsLista.next()) {
			String nomeamb = risultatoQuery.getString("nomeambiente");
			String tiposens = risultatoQuery.getString("tiposensore");
			String codsens = risultatoQuery.getString("codicesensore");
			int valore = risultatoQuery.getInt("valorerilevazione");
			boolean flagerr = risultatoQuery.getBoolean("flagerrore");
			String unit‡DiMisura = risultatoQuery.getString("unitmisura");
			Timestamp data = risultatoQuery.getTimestamp("data");
			String messaggio = risultatoQuery.getString("messaggio");	
			
			DatoRilevazione dato = new DatoRilevazione(nomeamb, tiposens, codsens, valore, flagerr, unit‡DiMisura, data, messaggio);
					
			listaDati.add(dato);
		}
		
		return listaDati;
	}
	
	
	/**
	 * Registra un nuovo dato di rilevazione nel sistema
	 * @param unCodiceSensore Il sensore che invia il dato
	 * @param unValore Il valore della rilevazione (in caso di rilevazione positiva)
	 * @param unFlagErrore Segnala se la rilevazione contiene un errore 
	 * @param unaData La data della rilevazione
	 * @param unMessaggio Il messaggio della rilevazione
	 * @return true in caso di inserimento avvenuto, false in caso di codice sensore non valido o rilevazione gi‡ presente
	 */
	public boolean inserisciDatoRilevazione(String unCodiceSensore, int unValore, boolean unFlagErrore,  
			 Timestamp unaData, String unMessaggio){
		
		boolean inserito = false;
		
		//Se NON Ë gi‡ presente, procedo con l'inserimento
		if (!ËPresenteRilevazione(unCodiceSensore, unaData)) {
			query = dbm.ottieniPreparedStatement(TEMPLATE_INSERISCI_DATO);
			try {
				query.setString(1, unCodiceSensore);
				query.setInt(2, unValore);
				query.setBoolean(3, unFlagErrore);
				query.setTimestamp(4, unaData);
				query.setString(5, unMessaggio);
				
				query.executeUpdate();
				
				inserito = true;
			}
			
			catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problemi nell'inserire un dato di rilevazione");
			}
			
		}
		
		return inserito;
	}
	
	/**
	 * Metodo interno per controllare se una rilevazione Ë gi‡ presente
	 * @param unCodiceSensore Il codice sensore da cercare
	 * @param unaData La data da cercare per il codice sensore
	 * @return true in caso di rilevazione presente, false altrimenti
	 */
	private boolean ËPresenteRilevazione(String unCodiceSensore, Timestamp unaData) {
		boolean presente = false;
		
		query = dbm.ottieniPreparedStatement(TEMPLATE_CERCA_RILEVAZIONE);
		
		try {
			query.setString(1, unCodiceSensore);
			query.setTimestamp(2, unaData);
			risultatoQuery = query.executeQuery();
			
			if (risultatoQuery.next()){
				presente = true;
			}
			
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Problemi nel cercare una rilevazione");
		}
		
		return presente;
	}
	
	
	/**
	 * Restituisce i nomi degli ambienti posseduti da un cliente
	 * @param unProprietario il cliente
	 * @return Una lista di stringhe con i nomi degli ambienti
	 * @throws SQLException Eccezione in caso di lista vuota (cliente senza ambienti)
	 */
	public LinkedList<String> mostraNomiAmbienti(String unProprietario) throws SQLException {
		
		/**Il Dao a cui delegare query sugli ambienti dei dati di installazione*/
		DaoAmbienti daoAmb = new DaoAmbienti();
		
		listaStringhe = daoAmb.mostraNomiAmbienti(unProprietario);
				
		return listaStringhe;
	}
	
	
	/**
	 * Restituisce i tipi di sensori posseduti da un cliente
	 * @param unProprietario il cliente
	 * @return Una lista di stringhe con i tipi di sensori
	 * @throws SQLException Eccezione in caso di lista vuota (cliente senza sensori)
	 */
	public LinkedList<String> mostraTipiSensori(String unProprietario) throws SQLException {
		
		/**Il Dao a cui delegare query sui sensori dei dati di installazione*/
		DaoSensori daoSens = new DaoSensori();
		
		listaStringhe = daoSens.mostraTipiSensori(unProprietario);
				
		return listaStringhe;
	}
}