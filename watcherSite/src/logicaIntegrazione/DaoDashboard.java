package logicaIntegrazione;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import logicaBusiness.entit‡.DatoRilevazione;
import logicaBusiness.entit‡.DatoSintesiDashboard;

/**
 * Classe per il recupero dei dati della dashboard
 * @author Graziano Accogli, Ivan Lamparelli
 */
public class DaoDashboard {
	
	/**Il gestore della connessione con il database*/
	private final DBManager dbm = DBManager.getIstanza();
	
	/**La query finale (costruita a partire dai template)*/
	private PreparedStatement query = null;
	
	/**L'oggetto ResultSet in cui vengono temporaneamente salvati i risultati delle query*/
	private ResultSet risultatoQuery = null;
	
	/**L'oggetto usato per stampare i messaggi d'errore*/
	 private final static Logger LOGGER = Logger.getLogger(DaoDashboard.class.getName());
	
	/**Controlla lo stato della View sui dati di installazione*/
	private static boolean vistaIstanziata = false;
	
	/**Il risultato da restituire per le query su una lista di dati*/
	private LinkedList<DatoSintesiDashboard> listaDatiSintesi = null;
	
	private LinkedList<DatoRilevazione> listaDati = null;
	
	private final static long MILLISEC_IN_GIORNO = 24 * 60 * 60 * 1000;
	
	private final static Calendar CALENDARIO = Calendar.getInstance();
	
	
	//I template delle query
	private final static String TEMPLATE_MOSTRA_DASH_SINTESI = 
			"SELECT di.nomeambiente AS nomeambiente, s.tipo AS tiposensore, ROUND(AVG(dr.valore),2) AS mediavalori, ts.unitmisura AS unitmisura" + 
			" FROM datorilevazione dr, datoinstallazione di, sensore s, tiposensore ts" + 
			" WHERE di.codicesensore = s.codice AND s.codice = dr.codicesensore AND s.tipo = ts.tiposensore" + 
			" AND flagerrore = 0" +
			" AND di.proprietarioambiente = ? AND dr.data >= ?" +
			" GROUP BY di.nomeambiente, s.tipo;";
	
	private final static String TEMPLATE_MOSTRA_DASH_SINTESI_AMBIENTE = 
			"SELECT di.nomeambiente AS nomeambiente, s.tipo AS tiposensore, ROUND(AVG(dr.valore),2) AS mediavalori, ts.unitmisura AS unitmisura" + 
			" FROM datorilevazione dr, datoinstallazione di, sensore s, tiposensore ts" + 
			" WHERE di.codicesensore = s.codice AND s.codice = dr.codicesensore AND s.tipo = ts.tiposensore" + 
			" AND flagerrore = 0" +
			" AND di.proprietarioambiente = ? AND dr.data >= ? AND di.nomeambiente = ?" +
			" GROUP BY di.nomeambiente, s.tipo;";
	
	private final static String VISTA_DASH_CRITICITA = 
			"CREATE VIEW IF NOT EXISTS dashcritici AS" + 
			" SELECT di.proprietarioambiente AS proprietarioambiente, di.nomeambiente AS nomeambiente, s.tipo AS tiposensore, s.codice AS codicesensore," + 
			" dr.valore AS valorerilevazione, dr.flagerrore AS flagerrore, ts.unitmisura AS unitmisura, dr.data AS data, dr.messaggio AS messaggio" +
			" FROM datorilevazione dr, datoinstallazione di, sensore s, tiposensore ts" +
			" WHERE di.codicesensore = s.codice AND s.codice = dr.codicesensore AND s.tipo = ts.tiposensore" +
			" AND (dr.flagerrore = 1 OR dr.messaggio = 'allerta' OR dr.messaggio = 'critico')" +
			" ORDER BY dr.data DESC;";
	
	private final static String TEMPLATE_MOSTRA_DASH_CRITICITA = 
			"SELECT * FROM dashcritici WHERE proprietarioambiente = ? AND data >= ?";
	
	private final static String TEMPLATE_MOSTRA_DASH_CRITICITA_AMBIENTE = 
			"SELECT * FROM dashcritici WHERE proprietarioambiente = ? AND data >= ? AND nomeambiente = ?";
	
	
	/**Il costruttore*/
	public DaoDashboard() {
		//Se la vista non Ë gi‡ stata creata, provvedo a crearla
		if (!vistaIstanziata) {
			query = dbm.ottieniPreparedStatement(VISTA_DASH_CRITICITA);
			try {
				query.executeUpdate();
				vistaIstanziata = true;				
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problemi con la creazione della vista");
			}
			
		}
	}
	
	
	/**
	 * Mostra tutti i dati di sintesi di un utente filtrandoli per intervallo di tempo e/o nome ambienti
	 * @param proprietario Il proprietario dei dati
	 * @param ambiente in cui sono presenti i sensori
	 * @param intervallo di tempo delle rilevazioni
	 * @return I dati da mostrare
	 * @throws SQLException Eccezione in caso di mancanza di dati
	 */
	public LinkedList<DatoSintesiDashboard> visualizzaDatiSintesi(String unProprietario, String unAmbiente, int unIntervalloGiorni) throws SQLException{
		listaDatiSintesi = new LinkedList<DatoSintesiDashboard>();
		
		//Se il filtro dell'ambiente Ë vuoto
		if (unAmbiente.equals("")) {
			listaDatiSintesi = this.visualizzaDatiSintesi(unProprietario, unIntervalloGiorni);
		}
				
		//Se viene utilizzato il filtro dell'ambiente
		else {
			query = dbm.ottieniPreparedStatement(TEMPLATE_MOSTRA_DASH_SINTESI_AMBIENTE);
			query.setString(1, unProprietario);
			Timestamp data = calcolaDataPassata(unIntervalloGiorni);
			query.setTimestamp(2, data);
			query.setString(3, unAmbiente);
			
			risultatoQuery = query.executeQuery();
			
			listaDatiSintesi = popolaListaDati(risultatoQuery);
		}
		
		return listaDatiSintesi;				
	}

	/**
	 * Mostra i dati di sintesi di un utente, nell'intervallo di tempo di default (30 giorni), comprendendo tutti gli ambienti dell'utente selezionato 
	 * @param unProprietario Il proprietario degli ambienti
	 * @param unIntervalloGiorni L'intervallo da sottrarre alla data odierna 
	 * @return I dati da mostrare
	 * @throws SQLException Eccezione in caso di lista vuota (cliente senza dati nell'intervallo di giorni)
	 */
	public LinkedList<DatoSintesiDashboard> visualizzaDatiSintesi(String unProprietario, int unIntervalloGiorni) throws SQLException{
		listaDatiSintesi = new LinkedList<DatoSintesiDashboard>();
	
		query = dbm.ottieniPreparedStatement(TEMPLATE_MOSTRA_DASH_SINTESI);
		query.setString(1, unProprietario);
		Timestamp data = calcolaDataPassata(unIntervalloGiorni);
		query.setTimestamp(2, data);
		
		risultatoQuery = query.executeQuery();
		
		listaDatiSintesi = popolaListaDati(risultatoQuery);

		return listaDatiSintesi;
	}
	

	/**
	 * Calcola una data nel passato distante un determinato intervallo di giorni dalla data odierna.
	 * @param unIntervalloGiorni l'intervallo di giorni da sottrarre alla data odierna
	 * @return Data precedente il dato intervallo di giorni dalla data odierna
	 */
	private Timestamp calcolaDataPassata(int unIntervalloGiorni) {
			
		long oggiInMillisec = CALENDARIO.getTimeInMillis();
		
		long intervalloInMillisec = unIntervalloGiorni * MILLISEC_IN_GIORNO;
		
		long dataPassataMillisec = oggiInMillisec - intervalloInMillisec;
		
		Timestamp dataPassata = new Timestamp(dataPassataMillisec); //converte la data passata da millisecondi a Timestamp
		
		return dataPassata;
	}
	
	/** 
	 * Metodo interno per il popolamento di una lista dei dati da visualizzare nella Dashboard
	 * @param rsLista Il risultato di una query da convertire in lista
	 * @return La lista di dati
	 * @throws SQLException Eccezione in caso di ResultSet vuoto
	 */
	private LinkedList<DatoSintesiDashboard> popolaListaDati(ResultSet rsLista) throws SQLException {
		while(rsLista.next()) {
			String nomeamb = risultatoQuery.getString("nomeambiente");
			String tiposens = risultatoQuery.getString("tiposensore");			
			double mediaValore= risultatoQuery.getDouble("mediavalori");
			String unit‡Misura = risultatoQuery.getString("unitmisura");
			DatoSintesiDashboard dato = new DatoSintesiDashboard(nomeamb, tiposens, mediaValore, unit‡Misura);
					
			listaDatiSintesi.add(dato);
		}
		
		return listaDatiSintesi;
	}
	
	/**
	 * Metodo per la visualizzazione delle rilevazioni con dati di criticit‡
	 * @param unProprietario Il proprietario delle rilevazioni
	 * @param unIntervalloGiorni L'intervallo a cui sottrarre la data odierna
	 * @return Una lista di dati
	 * @throws SQLException Eccezione in caso di lista vuota (nessuna criticit‡ nell'intervallo di tempo selezionato)
	 */
	public LinkedList<DatoRilevazione> visualizzaDatiCriticita(String unProprietario, int unIntervalloGiorni) throws SQLException {
		listaDati = new LinkedList<DatoRilevazione>();
			
		query = dbm.ottieniPreparedStatement(TEMPLATE_MOSTRA_DASH_CRITICITA);
		query.setString(1, unProprietario);
		Timestamp data = calcolaDataPassata(unIntervalloGiorni);
		query.setTimestamp(2, data);
		risultatoQuery = query.executeQuery();
			
		listaDati = popolaListaDatiCriticita(risultatoQuery);	
			
		return listaDati;
	}
	
	/**
	 * Metodo per la visualizzazione delle rilevazioni con dati di criticit‡
	 * @param unProprietario Il proprietario delle rilevazioni
	 * @param unAmbiente L'ambiente di cui cercare le criticit‡
	 * @param unIntervalloGiorni L'intervallo a cui sottrarre la data odierna
	 * @return Una lista di dati
	 * @throws SQLException Eccezione in caso di lista vuota (nessuna criticit‡ nell'intervallo di tempo selezionato)
	 */
	public LinkedList<DatoRilevazione> visualizzaDatiCriticita(String unProprietario, String unAmbiente, int unIntervalloGiorni) throws SQLException {
		listaDati = new LinkedList<DatoRilevazione>();
		
		if(unAmbiente.equals("")) {
			listaDati = this.visualizzaDatiCriticita(unProprietario, unIntervalloGiorni);
			
		} else {
			
			query = dbm.ottieniPreparedStatement(TEMPLATE_MOSTRA_DASH_CRITICITA_AMBIENTE);
			query.setString(1, unProprietario);
			Timestamp data = calcolaDataPassata(unIntervalloGiorni);
			query.setTimestamp(2, data);
			query.setString(3, unAmbiente);
			risultatoQuery = query.executeQuery();
				
			listaDati = popolaListaDatiCriticita(risultatoQuery);
		
		}	
			
		return listaDati;
	}
	
	
	/**
	 * Metodo interno per il popolamento di una lista con dati di criticit‡ da visualizzare nella Dashboard
	 * @param rsLista Il risultato di una query da convertire in lista
	 * @return La lista dei dati
	 * @throws SQLException Eccezione in caso di ResultSet vuoto
	 */
	private LinkedList<DatoRilevazione> popolaListaDatiCriticita(ResultSet rsLista) throws SQLException {
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
}