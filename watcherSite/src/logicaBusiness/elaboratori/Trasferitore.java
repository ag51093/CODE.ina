package logicaBusiness.elaboratori;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.opencsv.CSVWriter;

import logicaBusiness.entit‡.DatoRilevazione;
import logicaIntegrazione.DaoTrasferimento;

/**
 * Classe (Singleton) per la gestione del trasferimento delle rilevazioni 
 *  
 * @author Ivan Lamparelli, Graziano Accogli
 */
public class Trasferitore implements Runnable {

	/**L'intervallo di tempo per effettuare il trasferimento*/
	private final static long MILLISEC_INTERVALLO_TRASFERIMENTO = 10 * 60 * 1000; //10 minuti

	/**L'ISTANZA Singleton del trasferitore*/
	private final static Trasferitore ISTANZA = new Trasferitore();	
	
	/**L'oggetto usato per stampare i messaggi d'errore*/
	private final static Logger LOGGER = Logger.getLogger(Trasferitore.class.getName());
	
	/**Il dao a cui richiedere i dati del trasferimento*/
	private final DaoTrasferimento daoTA = new DaoTrasferimento();
	
	/**La lista in cui salvare le righe di dati da scrivere sul file da trasferire ad un cliente*/
	LinkedList<String[]> righeDatiDaTrasferire = null;
		
	
	/**
	 * Restituisce l'ISTANZA Singleton del Trasferitore
	 * @return L'ISTANZA Singleton
	 */
	public static Trasferitore getIstanza() {
		return ISTANZA;
	}
	
	
	/**
	 * Il costruttore privato (Per istanziare dall'esterno va usato il metodo getIstanza()
	 */
	private Trasferitore() {}
	
	
	/**
	 * Specifica le istruzioni da eseguire quando viene avviato un thread contenente questo oggetto Runnable
	 */
	@Override
	public void run() {

		try {
			while (true) {
				attivaThread();
			}
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, "Problemi nella messa in pausa del Thread");
		}
	
	}
	
	
	/**
	 * Definisce le operazioni da eseguire quando viene fatto partire il thread
	 * @throws InterruptedException Eccezione in caso di problemi con la messa in pausa del thread
	 */
	private void attivaThread() throws InterruptedException {
		
		GestoreEmail connettoreMail = new GestoreEmail();
		
		synchronized (this) {
			LinkedList<String> clientiTrasferimento = null;
			int numClienti = 0;
			
			try {
				clientiTrasferimento = daoTA.mostraClientiConTrasferimentoAttivo();
				numClienti = clientiTrasferimento.size();
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problemi nella lettura dei clienti con il trasferimento attivo");
			}
			
			
			//Se ci sono clienti con il trasferimento attivo, effettuo il trasferimento
			if (clientiTrasferimento != null && numClienti > 0) {
				
				//Per ogni cliente, procedo col trasferimento
				for (String cliente : clientiTrasferimento) {	
					LinkedList<DatoRilevazione> datiDaTrasferire = null;
					String indirizzoInvio = null;
						
					try {
						//Individuo i dati da trasferire per il cliente
						datiDaTrasferire = daoTA.mostraDatiDaTrasferire(cliente);	
					} catch (SQLException e) {
						LOGGER.log(Level.SEVERE, "Problemi nella visualizzazione dei dati da trasferire");
					}
					
					//Se il cliente ha dei dati da trasferire, effettuo il trasferimento
					if (datiDaTrasferire != null && datiDaTrasferire.size() > 0) {
						
						righeDatiDaTrasferire = new LinkedList<String[]>();
						
						//Converto i dati da trasferire in array di celle da inserire in un file excel
						for(DatoRilevazione rilevazione : datiDaTrasferire) {
							righeDatiDaTrasferire.add(convertiRilevazioneInRigaCelleCsv(rilevazione));
						}
						
						//Creo il file con le rilevazioni
						File rilevazioni = scriviDatiSuCsv(cliente, righeDatiDaTrasferire);
						
						//Individuo l'indirizzo a cui inviare i dati
						if (daoTA.ËAttivaTerzaParte(cliente)) {
							try {
								indirizzoInvio = daoTA.mostraTerzaParte(cliente);
							} catch (SQLException e) {
								LOGGER.log(Level.SEVERE, "Problemi nel cercare la terza parte relativa al cliente inserito");
							}
						} else {
							indirizzoInvio = cliente;
						}
						
						connettoreMail.inviaEmail(indirizzoInvio, scriviOggettoMailTrasferimento(), scriviCorpoMailTrasferimento(cliente), rilevazioni, rilevazioni.getName());
				
					} 
					
				} //fine for clienti
				
				this.wait(MILLISEC_INTERVALLO_TRASFERIMENTO);	
			}
		}		
	}
	
	
	
	/**
	 * Scrive dei dati di rilevazione su un file csv
	 * @param unCliente Il cliente proprietario delle rilevazioni
	 * @param unArrayDiRighe L'array di righe di dati di rilevazioni
	 * @return Il file csv
	 */
	private File scriviDatiSuCsv(String unCliente, LinkedList<String[]> unArrayDiRighe) {
	 	
		File rilevazioniCsv = new File("rilevazioni csv" + File.separator + "rilevazioni " + mostraDataOdierna() + ".csv");
						
		CSVWriter scrittoreCsv = null;
		
		try {
			scrittoreCsv = new CSVWriter(new FileWriter(rilevazioniCsv), ';');
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Problema con il costruttore del Csv");
		}
		
		//Scrivo i nomi delle colonne
		String[] nomiColonne = {"Ambiente", "Tipo sensore", "Codice sensore", "Valore", "Errore", "Unit‡ di misura", "Data", "Messaggio"};
		scrittoreCsv.writeNext(nomiColonne);
		
		//Popolo il file
		for (String[] riga : unArrayDiRighe) {
			scrittoreCsv.writeNext(riga);
		}
		
     	try {
			scrittoreCsv.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Problema con la chiusura del Csv");
		}
     	
     	return rilevazioniCsv;
	}
	
	
	/**
	 * Converte un dato di rilevazione in una riga da inserire in un file csv
	 * @param unaRilevazione La rilevazione da convertire
	 * @return L'array con i valori della riga
	 */
	private String[] convertiRilevazioneInRigaCelleCsv(DatoRilevazione unaRilevazione) {
		String[] rigaCelleCsv = new String[8];
		
		//cella 1, nome ambiente
		rigaCelleCsv[0] = unaRilevazione.getNomeAmbiente();
		
		//cella 2, tipo sensore
		rigaCelleCsv[1] = unaRilevazione.getTipoSensore();
		
		//cella 3, codice sensore
		rigaCelleCsv[2] = unaRilevazione.getCodiceSensore();
		
		//cella 4, valore rilevazione
		rigaCelleCsv[3] = Integer.toString(unaRilevazione.getValore());
		
		//cella 5, flag errore
		rigaCelleCsv[4] = Boolean.toString(unaRilevazione.ËErrore());
		
		//cella 6, unit‡ misura rilevazione
		String unit‡DiMisura = unaRilevazione.getUnit‡DiMisura();
		if (unit‡DiMisura == null) {
			unit‡DiMisura = "";
		}
		rigaCelleCsv[5] = unit‡DiMisura;
		
		//cella 7, data rilevazione
		rigaCelleCsv[6] = unaRilevazione.getData().toString().substring(0, 16);
		
		//cella 8, messaggio rilevazione
		rigaCelleCsv[7] = unaRilevazione.getMessaggio();
						
		return rigaCelleCsv;
	}
	
	
	/**
	 * Compone l'oggetto della mail per l'invio dei dati di rilevazioni
	 * @return L'oggetto della mail
	 */
	private String scriviOggettoMailTrasferimento() {
		String oggetto = "Rilevazioni " + mostraDataOdierna();
		
		return oggetto;
	}
	
	/**
	 * Compone il corpo della mail per l'invio dei dati di rilevazioni
	 * @param unCliente Il cliente proprietario delle rilevazioni per il quale personalizzare la mail
	 * @return Il corpo della mail
	 */
	private String scriviCorpoMailTrasferimento(String unCliente) {
		StringBuilder corpo = new StringBuilder();		
		LinkedList<String> ambientiTrasf = null;
		
		try {
			ambientiTrasf = daoTA.mostraAmbientiFiltrati(unCliente);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Problemi nel cercare i dati dell'ambiente inserito");
		}
		
		corpo.append("In allegato sono presenti le rilevazioni fino alla data " + mostraDataOdierna() + " degli ambienti:");
		
		for (String ambiente : ambientiTrasf) {
			corpo.append("<br>- " + ambiente);
		}
		
		return corpo.toString();
	}
	
	
	/**
	 * Metodo interno per mostrare la data odierna in formato stringa
	 * @return La data in formato stringa
	 */
	private String mostraDataOdierna() {
		String oggiInStringa;
		
		long oggiInMillisec = Calendar.getInstance().getTimeInMillis();
		Timestamp oggiInTimestamp = new Timestamp(oggiInMillisec);

		//la data in formato GG-MM-AAAA
		oggiInStringa = oggiInTimestamp.toString().substring(0, 10); 
		
		return oggiInStringa;
	}
}