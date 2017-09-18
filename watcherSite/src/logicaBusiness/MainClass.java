package logicaBusiness;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import logicaBusiness.elaboratori.Rilevatore;
import logicaBusiness.elaboratori.Trasferitore;

/**
 * 
 * Classe MainClass per l'esecuzione del Thread che riceve le rilevazione dal rilevatore 
 * 
 * @author Graziano Accogli, Ivan Lamparelli 
 *
 */
public class MainClass {
	
	/**L'oggetto da utilizzare per stampare gli errori*/
	private final static Logger LOGGER = Logger.getLogger(MainClass.class.getName());

	/**L'oggetto incaricato della raccolta e analisi delle rilevazioni*/
	private final static Rilevatore RILEVATORE = Rilevatore.getIstanza();
	
	/**L'oggetto incaricato del trasferimento dei dati delle rilevazioni agli utenti*/
	private final static Trasferitore TRASFERITORE = Trasferitore.getIstanza();
	
	
	/**
	 * Simula l'invio di rilevazioni al rilevatore con lettura in real-time
	 */
	private static void simulaInvioRilevazioni() {	
		String percorsoFileRilevazioni = "C:" + File.separator + "watcher" + File.separator + "rilevazioni.txt";
		
		//Recupero il file con le rilevazioni
		FileReader lettoreFile = null;
		BufferedReader lettoreBuffered = null;
		try {
			lettoreFile =new FileReader(percorsoFileRilevazioni);
			lettoreBuffered = new BufferedReader(lettoreFile);
			
			//Raccolgo e invio una per volta le rilevazioni ad rilevatore
			String rilevazione = lettoreBuffered.readLine();
			while (rilevazione != null) {
				RILEVATORE.riceviRilevazione(rilevazione);
				rilevazione = lettoreBuffered.readLine();
			}
		}
		catch (IOException e) {
			LOGGER.log(Level.SEVERE, "File non trovato");
		} finally {
			try {
				if(lettoreBuffered != null) {
					lettoreBuffered.close();
				}
				
				if(lettoreFile != null) {
					lettoreFile.close();
				}
				
			} catch (IOException e) {
					LOGGER.log(Level.SEVERE, "Problema nella chiusura dei lettori");
			}
		}
	}
	
	
	/**
	 * Avvia il sistema
	 * @param args I possibili argomenti impostabili per parametrizzare il metodo
	 */
	public static void main(String[] args) {
		
		simulaInvioRilevazioni();

		Thread threadRilevazioni = new Thread(RILEVATORE);
		threadRilevazioni.start();
		
		Thread threadTrasferimenti = new Thread(TRASFERITORE);
		threadTrasferimenti.start();
		
	}
}
