package logicaIntegrazione;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import java.util.logging.Level;
import java.util.logging.Logger;

import logicaBusiness.entit‡.DatoInstallazione;

/**
 * Classe per il recupero e la modifica dei dati di installazione
 * 
 * @author Ivan Lamparelli, Graziano Accogli
 * */
public class DaoDatiInstallazione {
	
	/**Il gestore della connessione con il database*/
	private final DBManager dbm = DBManager.getIstanza();
	
	/**L'oggetto usato per stampare i messaggi d'errore*/
	 private final static Logger LOGGER = Logger.getLogger(DaoDatiInstallazione.class.getName());
	
	/**Il Dao a cui delegare query sugli ambienti dei dati di installazione*/
	private final DaoAmbienti daoAMB = new DaoAmbienti();
	
	/**Il Dao a cui delegare query sui sensori dei dati di installazione*/
	private final DaoSensori daoSens = new DaoSensori();

	/**Controlla lo stato della View sui dati di installazione*/
	private static boolean vistaIstanziata = false;
	
	//I template delle query da eseguire
	private final static String VISTA_INSTALLAZIONI = 
			"CREATE VIEW IF NOT EXISTS installazioni AS" +
			" SELECT di.proprietarioambiente AS proprietarioambiente, a.nome AS nomeambiente, a.tipo AS tipoambiente, s.tipo AS tiposensore, ts.unitmisura AS unitmisura," +
			" s.marca AS marcasensore, s.modello AS modellosensore, di.codicesensore AS codicesensore, di.posizione AS posizionesensore" + 
			" FROM datoinstallazione di, ambiente a, sensore s, tiposensore ts" + 
			" WHERE di.proprietarioambiente = a.proprietario AND di.nomeambiente = a.nome AND di.codicesensore = s.codice AND s.tipo = ts.tiposensore;";
	
	private final static String TEMPLATE_MOSTRA_DATI_CLIENTE = 
			"SELECT * FROM installazioni WHERE proprietarioambiente = ?;";
	
	private final static String TEMPLATE_FILTRA_AMBIENTI = 
			"SELECT * FROM installazioni WHERE proprietarioambiente = ? AND nomeambiente = ?;";
	
	private final static String TEMPLATE_FILTRA_SENSORI = 
			"SELECT * FROM installazioni WHERE proprietarioambiente = ? AND tiposensore = ?;";
	
	private final static String TEMPLATE_FILTRA_AMBIENTI_E_SENSORI = 
			"SELECT * FROM installazioni WHERE proprietarioambiente = ? AND nomeambiente = ? AND tiposensore = ?;";
	
	private final static String TEMPLATE_INSERISCI_DATO = 
			"INSERT INTO datoinstallazione (proprietarioambiente, nomeambiente, codicesensore, posizione)" +
			" VALUES (?, ?, ?, ?);";
	
	private final static String TEMPLATE_ELIMINA_DATO = 
			"DELETE FROM datoinstallazione WHERE proprietarioambiente = ? AND nomeambiente = ? AND codicesensore = ?;";
	
	private final static String TEMPLATE_CERCA_DATO =
			"SELECT * FROM installazioni" +
			" WHERE proprietarioambiente = ? AND nomeambiente = ? AND codicesensore = ?;";
	
	private final static String TEMPLATE_CERCA_PROPRIETARIO_SENSORE =
			"SELECT proprietarioambiente FROM installazioni WHERE codicesensore = ?;";
	
	private final static String TEMPLATE_CERCA_AMBIENTE_SENSORE =
			"SELECT nomeambiente FROM installazioni WHERE codicesensore = ?;";
		
	private final static String TEMPLATE_MODIFICA_POSIZIONE = 
			"UPDATE datoinstallazione SET posizione = ?" +
			" WHERE proprietarioambiente = ? AND nomeambiente = ? AND codicesensore = ?;";
	
	/**La query finale (costruita a partire dai template)*/
	private PreparedStatement query = null;
	
	/**L'oggetto ResultSet in cui vengono temporaneamente salvati i risultati delle query*/
	private ResultSet risultatoQuery = null;
	
	/**Il risultato da restituire per le query su una lista di dati*/
	private LinkedList<DatoInstallazione> listaDati = null;
	
	/**I risultati da restituire per le query su elenchi di stringhe*/
	private LinkedList<String> listaStringhe = null;
	
	/**Il costruttore*/
	public DaoDatiInstallazione() {
		
		//Se la vista non Ë gi‡ stata creata, provvedo a crearla
		if (!vistaIstanziata) {
			query = dbm.ottieniPreparedStatement(VISTA_INSTALLAZIONI);
			try {
				query.executeUpdate();
				vistaIstanziata = true;				
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problemi con la creazione della vista");
			}
		}
	}
	
	
	/***
	 * Restituisce i dati di installazione di un cliente
	 * @param unProprietario il cliente di cui servono i dati
	 * @return Una lista con i dati del cliente
	 * @throws SQLException Eccezione in caso di dati non presenti
	 */
	public LinkedList<DatoInstallazione> visualizzaDatiInstallazione(String unProprietario) throws SQLException {
		listaDati = new LinkedList<DatoInstallazione>();
		
		query = dbm.ottieniPreparedStatement(TEMPLATE_MOSTRA_DATI_CLIENTE);
		query.setString(1, unProprietario);
		
		risultatoQuery = query.executeQuery();
		
		listaDati = this.popolaListaDati(risultatoQuery);
		
		return listaDati;
	}
		
	
	/**
	 * Restituisce i dati di installazione di un cliente che rispettano i filtri selezionati
	 * @param unProprietario il cliente di cui servono i dati
	 * @param unNomeAmbiente il filtro sugli ambienti
	 * @param unTipoSensore il filtro sui sensori
	 * @return Una lista con i dati filtrati del cliente
	 * @throws SQLException Eccezione in caso di mancanza di dati
	 */
	public LinkedList<DatoInstallazione> visualizzaDatiInstallazione(String unProprietario, String unNomeAmbiente, String unTipoSensore) throws SQLException {
		listaDati = new LinkedList<DatoInstallazione>();
		
		//Se i filtri passati in input sono vuoti
		if (unNomeAmbiente.compareTo("") == 0 && unTipoSensore.compareTo("") == 0) {
			listaDati = this.visualizzaDatiInstallazione(unProprietario);
			return listaDati;
		}
		
		//Se uno o pi˘ filtri contengono valori
		else {
			
			//filtro su ambiente e sensore
			if (unNomeAmbiente.compareTo("") != 0 && unTipoSensore.compareTo("") != 0) {
				query = dbm.ottieniPreparedStatement(TEMPLATE_FILTRA_AMBIENTI_E_SENSORI);
				query.setString(1, unProprietario);
				query.setString(2, unNomeAmbiente);
				query.setString(3, unTipoSensore);
			}
			
			//filtro solo su ambiente
			else if (unNomeAmbiente.compareTo("") != 0 && unTipoSensore.compareTo("") == 0) {
				query = dbm.ottieniPreparedStatement(TEMPLATE_FILTRA_AMBIENTI);
				query.setString(1, unProprietario);
				query.setString(2, unNomeAmbiente);
			}
			
			//filtro solo su sensore
			else {
				query = dbm.ottieniPreparedStatement(TEMPLATE_FILTRA_SENSORI);
				query.setString(1, unProprietario);
				query.setString(2, unTipoSensore);
			}
			
			risultatoQuery = query.executeQuery();
			listaDati = popolaListaDati(risultatoQuery);
			
			return listaDati;
			
		}
				
	}
	
	
	/**
	 * Metodo interno per la conversione da ResultSet a Lista di Dati Installazione
	 * @param rsLista il ResultSet di una query contenente una lista di dati
	 * @return Una Lista con i dati ricevuti in input
	 * @throws SQLException Eccezione in caso di ResultSet senza dati
	 */
	private LinkedList<DatoInstallazione> popolaListaDati(ResultSet rsLista) throws SQLException {
		listaDati = new LinkedList<DatoInstallazione>();
		
		while(rsLista.next()) {
			String nomeAmbiente = rsLista.getString("nomeambiente");
			String tipoAmbiente = rsLista.getString("tipoambiente");
			String tipoSensore = rsLista.getString("tiposensore");
			String unit‡Misura = rsLista.getString("unitmisura");
			String marcaSensore = rsLista.getString("marcasensore");
			String modelloSensore = rsLista.getString("modellosensore");
			String codiceSensore = rsLista.getString("codicesensore");
			String posizioneSensore = rsLista.getString("posizionesensore");
			DatoInstallazione dato = new DatoInstallazione(nomeAmbiente, tipoAmbiente, tipoSensore, unit‡Misura, marcaSensore, modelloSensore, codiceSensore, posizioneSensore);
			listaDati.add(dato);

		}
		
		return listaDati;
	}
	
	/**
	 * Restituisce i nomi degli ambienti posseduti da un cliente
	 * @param unProprietario il cliente
	 * @return Una lista di stringhe con i nomi degli ambienti
	 * @throws SQLException Eccezione in caso di lista vuota (cliente senza ambienti)
	 */
	public LinkedList<String> mostraNomiAmbienti(String unProprietario) throws SQLException {
		listaStringhe = daoAMB.mostraNomiAmbienti(unProprietario);
				
		return listaStringhe;
	}
	
	
	/**
	 * Restituisce i tipi di sensori posseduti da un cliente
	 * @param unProprietario il cliente
	 * @return Una lista di stringhe con i tipi di sensori
	 * @throws SQLException Eccezione in caso di lista vuota (cliente senza sensori)
	 */
	public LinkedList<String> mostraTipiSensori(String unProprietario) throws SQLException {
		listaStringhe = daoSens.mostraTipiSensori(unProprietario);
				
		return listaStringhe;
	} 
	
	
	/**
	 * Restituisce tutti i tipi di sensori registrati nel sistema
	 * @return Una lista di stringhe con i tipi di sensori
	 * @throws SQLException Eccezione in caso di lista vuota (nessun sensore registrato)
	 */
	public LinkedList<String> mostraTipiSensori() throws SQLException {
		listaStringhe = daoSens.mostraTipiSensori();
				
		return listaStringhe;
	} 
	
	
	/**
	 * Registra un nuovo tipo di sensore
	 * @param unTipoSensore Il tipo di sensore
	 * @param unaUnit‡DiMisura L'unit‡ di misura per i sensori di questo tipo
	 * @param unaStrutturaDiRilevazione La struttura con cui i sensori di questo tipo inviano le rilevazioni
	 * @return true in caso di inserimento effettuato, false in caso di tipo sensore gi‡ registrato
	 */
	public boolean inserisciNuovoTipoSensore(String unTipoSensore, String unaUnit‡DiMisura, String unaStrutturaDiRilevazione) {
		boolean inserito = daoSens.inserisciNuovoTipoSensore(unTipoSensore, unaUnit‡DiMisura, unaStrutturaDiRilevazione);
		
		return inserito;
	}
	
	
	/**
	 * Restituisce la struttura con cui il sensore produce i dati nella stringa decimale
	 * @param unCodiceSensore Il codice del sensore di interesse
	 * @return La struttura della stringa decimale del sensore
	 * @throws SQLException Eccezione in caso di sensore non trovato
	 */
	public String mostraStrutturaSensore(String unCodiceSensore) throws SQLException {
		String strutturaRilevazione = daoSens.mostraStrutturaSensore(unCodiceSensore);
		
		return strutturaRilevazione;
	}
	
	
	/**
	 * Inserisce un nuovo dato di installazione
	 * @param unProprietario Il proprietario del dato
	 * @param unNomeAmbiente Il nome dell'ambiente relativo al dato
	 * @param unTipoAmbiente Il tipo dell'ambiente
	 * @param unTipoSensore Il tipo di sensore installato
	 * @param unaMarcaSensore La marca del sensore
	 * @param unModelloSensore Il modello del sensore
	 * @param unCodiceSensore Il codice identificativo del sensore
	 * @param unaPosizione La posizione del sensore all'interno dell'ambiente
	 * @return true se il dato Ë stato inserito, false se era gi‡ presente
	 */
	public boolean inserisciDatoInstallazione (String unProprietario, String unNomeAmbiente, String unTipoAmbiente, 
			String unTipoSensore, String unaMarcaSensore, String unModelloSensore, String unCodiceSensore, String unaPosizione) {
		
		boolean datoInserito = false;
		
		//Se il dato NON Ë gi‡ presente, procedo con l'inserimento
		if (!this.ËPresenteDatoInstallazione(unProprietario, unNomeAmbiente, unCodiceSensore)) {
				
			
			//Se l'ambiente in input Ë nuovo, registro il nuovo ambiente
			if (!daoAMB.ËPresenteAmbiente(unProprietario, unNomeAmbiente)) {
				daoAMB.inserisciAmbiente(unProprietario, unTipoAmbiente, unNomeAmbiente);
			} 
			
			
			//Registro l'installazione del nuovo sensore
			
			query = dbm.ottieniPreparedStatement(TEMPLATE_INSERISCI_DATO);
				
			try {
				query.setString(1, unProprietario);
				query.setString(2, unNomeAmbiente);
				query.setString(3, unCodiceSensore);
				query.setString(4, unaPosizione);
				
				query.executeUpdate();
				datoInserito = true;
				
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problemi con l'inserimento di un dato");
			}
			
			//Registro l'anagrafica del nuovo sensore
			daoSens.inserisciSensore(unCodiceSensore, unTipoSensore, unaMarcaSensore, unModelloSensore);
			
		}
			
		return datoInserito;
	
	}
	
	
	/**
	 * Modifica la posizione del sensore selezionato all'interno dell'ambiente in cui Ë installato
	 * @param unProprietario Il proprietario dell'ambiente
	 * @param unNomeAmbiente Il nome dell'ambiente
	 * @param unCodiceSensore Il codice del sensore
	 * @param unaNuovaPosizione La nuova posizione in cui spostare il sensore all'interno dell'ambiente
	 * @return true se il sensore Ë presente nell'ambiente ed Ë stato spostato, false altrimenti
	 */
	public boolean modificaPosizioneDatoInstallazione (String unProprietario, String unNomeAmbiente, String unCodiceSensore, String unaNuovaPosizione) {
		boolean datoModificato = false;
		
		if (this.ËPresenteDatoInstallazione(unProprietario, unNomeAmbiente, unCodiceSensore)) {
			query = dbm.ottieniPreparedStatement(TEMPLATE_MODIFICA_POSIZIONE);
			try {
				query.setString(1, unaNuovaPosizione);
				query.setString(2, unProprietario);
				query.setString(3, unNomeAmbiente);
				query.setString(4, unCodiceSensore);
				
				query.executeUpdate();
				
				datoModificato = true;
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problemi nella modifica della posizione");
			}
		}
		
		return datoModificato;
	}
	
	
	/**
	 * Metodo interno per eliminare un dato di installazione dal sistema
	 * @param unProprietario Il proprietario del sensore
	 * @param unNomeAmbiente L'ambiente in cui Ë installato il sensore
	 * @param unCodiceSensore Il codice del sensore
	 * @return true se il sensore era presente ed Ë stato eliminato, false altrimenti
	 */
	private boolean eliminaDatoInstallazione (String unProprietario, String unNomeAmbiente, String unCodiceSensore) {
		boolean datoEliminato = false;
		
		//Se il dato Ë gi‡ presente, procedo con l'eliminazione
		if(this.ËPresenteDatoInstallazione(unProprietario, unNomeAmbiente, unCodiceSensore)){
			query = dbm.ottieniPreparedStatement(TEMPLATE_ELIMINA_DATO);
			
			try {
				query.setString(1, unProprietario);
				query.setString(2, unNomeAmbiente);
				query.setString(3, unCodiceSensore);
				
				query.executeUpdate();
				datoEliminato = true;
				
				daoSens.eliminaSensore(unCodiceSensore);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problemi nell'eliminazione di un dato");
			}
		}
		return datoEliminato;
	}
		
	
	/**
	 * Elimina un ambiente (e tutti i sensori ivi installati) dai dati di installazione
	 * @param unProprietario Il proprietario dell'ambiente
	 * @param unNomeAmbiente Il nome dell'ambiente
	 * @return true se l'ambiente era presente ed Ë stato eliminato, false altrimenti
	 */
	public boolean eliminaAmbiente(String unProprietario, String unNomeAmbiente) {
		boolean ambienteEliminato = false;
		
		ambienteEliminato = daoAMB.eliminaAmbiente(unProprietario, unNomeAmbiente);
		
		return ambienteEliminato;		
	}
	
	
	/**
	 * Metodo interno per cercare l'ambiente in cui Ë installato un sensore
	 * @param unCodiceSensore Il sensore di cui interessa scoprire l'ambiente
	 * @return Il nome dell'ambiente
	 * @throws SQLException Eccezione in caso di sensore non presente
	 */
	public String mostraAmbienteSensore (String unCodiceSensore) throws SQLException {
		String nomeAmbiente = null;
		
		query = dbm.ottieniPreparedStatement(TEMPLATE_CERCA_AMBIENTE_SENSORE);
		query.setString(1, unCodiceSensore);
		
		risultatoQuery = query.executeQuery();
		
		if (risultatoQuery.next()) {
			nomeAmbiente = risultatoQuery.getString("nomeambiente");
		}
		
		return nomeAmbiente;
	}
	
	
	/**
	 * Elimina un sensore dai dati di installazione
	 * @param unProprietario Il proprietario del sensore
	 * @param unCodiceSensore Il codice del sensore
	 * @return true se il sensore era presente ed Ë stato eliminato, false altrimenti
	 */
	public boolean eliminaSensore(String unProprietario, String unCodiceSensore) {
		boolean sensoreEliminato = false;
		
		//Se Ë presente, procedo con l'eliminazione del dato di installazione e dei dettagli del sensore
		if (daoSens.ËPresenteSensore(unCodiceSensore)) {
			
			try {
				//Elimino il dato di installazione
				String nomeAmbiente = this.mostraAmbienteSensore(unCodiceSensore);
				this.eliminaDatoInstallazione(unProprietario, nomeAmbiente, unCodiceSensore);
				
				
				//Elimino i dettagli sul sensore
				daoSens.eliminaSensore(unCodiceSensore);
				
				sensoreEliminato = true;
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problemi nell'eliminare un sensore");
			}
		}		
		
		return sensoreEliminato;		
	}
	
	/**
	 * Mostra il proprietario di un sensore
	 * @param unCodiceSensore Il codice sensore di cui cercare il proprietario
	 * @return Il proprietario del sensore
	 * @throws SQLException Eccezione in caso sensore non registrato nel sistema
	 */
	public String mostraProprietarioSensore(String unCodiceSensore) throws SQLException {
		String proprietario = "";
		
		query = dbm.ottieniPreparedStatement(TEMPLATE_CERCA_PROPRIETARIO_SENSORE);
		query.setString(1, unCodiceSensore);
		risultatoQuery = query.executeQuery();
		if (risultatoQuery.next()) {
			proprietario = risultatoQuery.getString("proprietarioambiente");
		}
		
		return proprietario;
	}
	
	/**
	 * Metodo interno per verificare se il dato di installazione cercato Ë presente nel sistema
	 * 
	 * @param unProprietario Il proprietario dell'ambiente del dato di installazione
	 * @param unNomeAmbiente L'ambiente relativo al dato di installazione
	 * @param unCodiceSensore Il sensore installato
	 * 
	 * @return true se il dato di installazione Ë presente, false altriment
	 */
	boolean ËPresenteDatoInstallazione(String unProprietario, String unNomeAmbiente, String unCodiceSensore) {
		boolean datoPresente = false;
				
		query = dbm.ottieniPreparedStatement(TEMPLATE_CERCA_DATO);
		try {
			query.setString(1, unProprietario);
			query.setString(2, unNomeAmbiente);
			query.setString(3, unCodiceSensore);
			
			risultatoQuery = query.executeQuery();
			
			datoPresente = risultatoQuery.next();
			
		} catch (SQLException e1) {
			LOGGER.log(Level.SEVERE, "Problemi nel cercare un dato di installazione");
		}
				
		return datoPresente;
	}
}