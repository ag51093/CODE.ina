package logicaIntegrazione;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.jdbc.Driver;

/**
 * Classe (Singleton) per la gestione della connessione al database
 * 
 * @author Ivan Lamparelli, Graziano Accogli
 */
class DBManager {

	/**Sessione di connessione a MySQL*/
	private Connection connessione = null; 
	
	/**Istanza Singleton di DBManager*/
	private final static DBManager ISTANZA = new DBManager();
	
	/**L'oggetto usato per stampare i messaggi d'errore*/
	private final static Logger LOGGER = Logger.getLogger(DBManager.class.getName());
		
	/**Costruttore privato di DBManager*/
	private DBManager() {
		connessione = connetti();
	}

	/**
	 * Restituisce l'ISTANZA Singleton di DBManagwer
	 * @return L'ISTANZA di DBManager
	 */
	static DBManager getIstanza() {
		return ISTANZA;
	}
		
	/**
	 * Avvia una sessione di connessione al database
	 * @return La sessione di connessione
	 */
	private static Connection connetti() {

		InputStream lettoreXML = null;
		File configFile = null;
		String indirizzodb = null;
		String username = null;
		String password = null;
		Connection sessione = null;
		
		try {
			
			/*Segnalo il percorso dove sono nascosti indirizzo e credenziali del database*/
			configFile = new File("C:" + File.separator + "watcher" + 
					File.separator + "databaseconfig.ini");
			
		
			lettoreXML = new FileInputStream(configFile);
			
			//Acquisisco in un oggetto Properties le proprietà del file config
			Properties config = null;
			config = new Properties();
			config.loadFromXML(lettoreXML);
			
			indirizzodb = config.getProperty("INDIRIZZODB");
			username = config.getProperty("USERNAME");
			password = config.getProperty("PASSWORD");
			
			//Carico il driver
			new Driver();
			
			//Contatto il database, e avvio una sessione di connessione
			sessione = DriverManager.getConnection(indirizzodb, username, password);
		
		}catch (InvalidPropertiesFormatException e) {
			LOGGER.log(Level.SEVERE, "Proprietà nel file di configurazione del database in formato non valido");
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, "File di configurazione del database non trovato");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Problemi nella lettura del file di configurazione del database");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Problemi nellla connessione al database");	
			
		} finally {
			//Procedo a chiudere il lettoreXML solo se è istanziato (evito in caso di istanziazione non riuscita)
			if (lettoreXML != null) {
				try {
					lettoreXML.close();
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, "Problemi nella chiusura del lettore XML");
				}
			}
		}
		
		return sessione;	
	}

	
	/**
	 * Restituisce un oggetto di tipo PreparedStatement legato alla connessione al DB
	 * @param unaQuery la query da eseguire (nella quale possono essere presenti dei "?" per i parametri)
	 * @return l'oggetto PreparedStatement legato alla sessione di connessione
	 */
	PreparedStatement ottieniPreparedStatement (String unaQuery) {
		PreparedStatement query = null;
		
		try {
			query = connessione.prepareStatement(unaQuery);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Problemi con il formato della query");
		}
			
		return query;
	}
	
}