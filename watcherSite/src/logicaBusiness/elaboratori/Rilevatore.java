package logicaBusiness.elaboratori;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import logicaIntegrazione.DaoDatiInstallazione;
import logicaIntegrazione.DaoDatiRilevazioni;

/**
 * Classe (Singleton) per la gestione dei dati di rilevazione che verranno ricevuti in input dal sistema.
 * 
 * @author Ivan Lamparelli, Graziano Accogli
 * 
 */
public class Rilevatore implements Runnable {
	
	/**Classe interna dove vengono temporaneamente registrati i valori di una rilevazione*/
	private class Rilevazione {
		String codiceSensore;
		String strutturaRilevazione;
		String giorno;
		String mese;
		String anno;
		String ora;
		String minuti;
		boolean errore;
		int valore;
		String messaggio;
		
		void setCodiceSensore(String codiceSensore) {
			this.codiceSensore = codiceSensore;
		}
		void setStrutturaRilevazione(String strutturaRilevazione) {
			this.strutturaRilevazione = strutturaRilevazione;
		}
		void setGiorno(String giorno) {
			this.giorno = giorno;
		}
		void setMese(String mese) {
			this.mese = mese;
		}
		void setAnno(String anno) {
			this.anno = anno;
		}
		void setOra(String ora) {
			this.ora = ora;
		}
		void setMinuti(String minuti) {
			this.minuti = minuti;
		}
		void setErrore(boolean errore) {
			this.errore = errore;
		}
		void setValore(int valore) {
			this.valore = valore;
		}
		void setMessaggio(String messaggio) {
			this.messaggio = messaggio;
		}
	}
	

	/**L'ISTANZA Singleton*/
	private final static Rilevatore ISTANZA = new Rilevatore();
	
	/**L'oggetto usato per stampare i messaggi d'errore*/
	private final static Logger LOGGER = Logger.getLogger(Rilevatore.class.getName());
	
	/**Il Dao da interrogare per conoscere la struttura con cui un sensore salva le rilevazioni*/
	private final DaoDatiInstallazione daoDI = new DaoDatiInstallazione();	
		
	private final static int LUNGHEZZA_ID_SENSORE = 17;
	
	private final static long MILLISEC_ATTESA = 2 * 60 * 1000; //2 minuti
	
	
	/**Il buffer in cui vengono raccolte le stringhe delle rilevazioni inviate dai sensori*/
	private LinkedList<String> bufferStringheRilevazioni = new LinkedList<String>();
	
	int stringheNelBuffer; 
	
	private final static String STR_GIORNO = "GG";
	private final static String STR_MESE = "MM";
	private final static String STR_ANNO = "AAAA";
	private final static String STR_ORA = "oo";
	private final static String STR_MINUTI = "mm";
	private final static String STR_FLAG = "F";
	private final static String STR_VALORESHORT = "VV";
	private final static String STR_VALORELONG = "VVVVVV";
		
	private final static String STR_SEPARATORE = "/";
	
	
	/**Le possibili strutture con cui i tipi di sensore contengono i dati nella stringa decimale */
	final static String[] STRUTTURE_RILEVAZIONI = {
		
		//"GG/MM/AAAA/oo/mm/F/VV"
		STR_GIORNO + STR_SEPARATORE + STR_MESE + STR_SEPARATORE + STR_ANNO + STR_SEPARATORE + STR_ORA + STR_SEPARATORE + STR_MINUTI + STR_SEPARATORE + STR_FLAG + STR_SEPARATORE + STR_VALORESHORT,
		
		//"GG/MM/AAAA/oo/mm/F/VVVVVV"
		STR_GIORNO + STR_SEPARATORE + STR_MESE + STR_SEPARATORE + STR_ANNO + STR_SEPARATORE + STR_ORA + STR_SEPARATORE + STR_MINUTI + STR_SEPARATORE + STR_FLAG + STR_SEPARATORE + STR_VALORELONG,
		
		//"AAAA/MM/GG/oo/mm/F/VV"
		STR_ANNO + STR_SEPARATORE + STR_MESE + STR_SEPARATORE + STR_GIORNO + STR_SEPARATORE + STR_ORA + STR_SEPARATORE + STR_MINUTI + STR_SEPARATORE + STR_FLAG + STR_SEPARATORE + STR_VALORESHORT,
		
		//"AAAA/MM/GG/oo/mm/F/VVVVVV"
		STR_ANNO + STR_SEPARATORE + STR_MESE + STR_SEPARATORE + STR_GIORNO + STR_SEPARATORE + STR_ORA + STR_SEPARATORE + STR_MINUTI + STR_SEPARATORE + STR_FLAG + STR_SEPARATORE + STR_VALORELONG,
		
		//"F/VV/GG/MM/AAAA/oo/mm"
		STR_FLAG + STR_SEPARATORE + STR_VALORESHORT + STR_SEPARATORE + STR_GIORNO + STR_SEPARATORE + STR_MESE + STR_SEPARATORE + STR_ANNO + STR_SEPARATORE + STR_ORA + STR_SEPARATORE + STR_MINUTI,
		
		//"F/VVVVVV/GG/MM/AAAA/oo/mm"
		STR_FLAG + STR_SEPARATORE + STR_VALORELONG + STR_SEPARATORE + STR_GIORNO + STR_SEPARATORE + STR_MESE + STR_SEPARATORE + STR_ANNO + STR_SEPARATORE + STR_ORA + STR_SEPARATORE + STR_MINUTI,
		
		//"F/VV/AAAA/MM/GG/oo/mm"
		STR_FLAG + STR_SEPARATORE + STR_VALORESHORT + STR_SEPARATORE + STR_ANNO + STR_SEPARATORE + STR_MESE + STR_SEPARATORE + STR_GIORNO + STR_SEPARATORE + STR_ORA + STR_SEPARATORE + STR_MINUTI,
		
		//"F/VV/AAAA/MM/GG/oo/mm"
		STR_FLAG + STR_SEPARATORE + STR_VALORELONG + STR_SEPARATORE + STR_ANNO + STR_SEPARATORE + STR_MESE + STR_SEPARATORE + STR_GIORNO + STR_SEPARATORE + STR_ORA + STR_SEPARATORE + STR_MINUTI
	};
	
	
	/**
	 * Restituisce l'ISTANZA Singleton del Rilevatore
	 * 
	 * @return L'ISTANZA Singleton
	 */
	public static Rilevatore getIstanza() {
		return ISTANZA;
	}
	
	
	/**
	 * Il costruttore privato (Per istanziare dall'esterno va usato il metodo getIstanza()
	 */
	private Rilevatore() {}
	
	
	/**
	 * Specifica le istruzioni da eseguire quando viene avviato un thread contenente questo oggetto Runnable
	 */
	@Override
	public void run() {
		
		try {
			while (true) {
				//Finché ci sono stringhe nel buffer, le leggo, le salvo nel database, e le rimuovo
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
		
		/**L'oggetto da utilizzare per inviare le email ai clienti*/
		GestoreEmail connettoreMail = new GestoreEmail();
		
		synchronized (this) {
			
			int stringheNelBuffer = bufferStringheRilevazioni.size();
			
			try {
				while (stringheNelBuffer != 0) {
					//Formatto la stringa di rilevazione appena ricevuta
					Rilevazione rilevazione = this.formattaRilevazione(bufferStringheRilevazioni.getFirst());
					
					//Se la rilevazione presenta un codice di errore, allerto tramite email l'utente proprietario del sensore
					if (rilevazione.errore) {
						
						String proprietario = daoDI.mostraProprietarioSensore(rilevazione.codiceSensore);
						String ambiente = daoDI.mostraAmbienteSensore(rilevazione.codiceSensore);
								
						String codicesensore = rilevazione.codiceSensore;
							
						Timestamp data = convertiDataStringa(rilevazione.anno, rilevazione.mese, rilevazione.giorno, 
								rilevazione.ora, rilevazione.minuti, "00", "0");
								
						int codiceErrore = rilevazione.valore;
								
						String messaggio = rilevazione.messaggio;
							
						String oggettoMail = scriviOggettoMailErrore(ambiente);
						String corpoMail = scriviCorpoMailErrore(ambiente, codicesensore, data, codiceErrore, messaggio);
								
						connettoreMail.inviaEmail(proprietario, oggettoMail, corpoMail, null, null);
					}
					
					//Salvo nel database i dati presenti nella stringa di rilevazione appena formattata
					this.salvaRilevazione(rilevazione);
					
					//Elimino la stringa dal buffer
					bufferStringheRilevazioni.removeFirst();
					
					//Aggiorno la variabile usata nel ciclo while (provvedendo a controllare se sono arrivate nuove rilevazioni)
					stringheNelBuffer = bufferStringheRilevazioni.size();
					
				} //fine while
					
				this.wait(MILLISEC_ATTESA);	
			
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Problemi nella comunicazione tra il Thread e il DB ");
			}
				
		}

	}

	
	/**
	 * Metodo interno per comporre l'oggetto della mail da inviare in caso di rilevazione con errore
	 * @param unAmbiente L'ambiente in cui è stato rilevato l'errore
	 * @return L'oggetto della mail
	 */
	private String scriviOggettoMailErrore(String unAmbiente) {
		String oggetto = "Presenza errore nell'ambiente \"" + unAmbiente + "\"";
		
		return oggetto;
	}
	
	
	/**
	 * Metodo interno per comporre il corpo della mail da inviare in caso di rilevazione con errore
	 * @param unAmbiente L'ambiente in cui è stato rilevato l'errore
	 * @param unCodiceSensore Il codice del sensore che ha inviato l'errore
	 * @param unaData La data in cui è stato rilevato l'errore
	 * @param unCodiceErrore Il codice dell'errore
	 * @param unMessaggioErrore Il messaggio dell'errore
	 * @return Il corpo della mail
	 */
	private String scriviCorpoMailErrore(String unAmbiente, String unCodiceSensore, Timestamp unaData, int unCodiceErrore, String unMessaggioErrore) {
		String corpo = "<strong>Ambiente:</strong> \"" + unAmbiente + "\"" + "<br>" + "<strong>Data:</strong> " + unaData.toString().substring(0, 16) + "<br>" + 
				"<strong>Codice sensore:</strong> \"" + unCodiceSensore + "\"" + "<br>" +
				"<strong>Codice errore:</strong> " + unCodiceErrore + "<br>" + "<strong>Messaggio errore:</strong> \"" + unMessaggioErrore + "\"";
		
		return corpo;
	}
	
	
	/**
	 * Riceve le rilevazioni in input dai sensori
	 * @param unaStringaDiRilevazione Una stringa di rilevazione prodotta da un sensore
	 */
	public void riceviRilevazione(String unaStringaDiRilevazione) {
		bufferStringheRilevazioni.add(unaStringaDiRilevazione);
	}
	
	
	/**
	 * Mostra le possibili strutture con cui i sensori inviano i dati nella stringa di cifre decimali
	 * @return Le possibili strutture delle cifre decimali inviate dai sensori
	 */
	public String[] mostraStruttureRilevazioni() {
		return STRUTTURE_RILEVAZIONI;
	}
	
		
	/**
	 * Salva nel sistema una rilevazione di un sensore
	 * @param unaRilevazione La rilevazione da salvare
	 * @return true in caso di salvataggio avvenuto correttamente, false in caso di codice sensore non valido o rilevazione già presente
	 */
	private boolean salvaRilevazione(Rilevazione unaRilevazione) {
		
		/**Il Dao da contattare per salvare le rilevazioni*/
		DaoDatiRilevazioni daoDR = new DaoDatiRilevazioni(); 
		
		Timestamp data = convertiDataStringa(unaRilevazione.anno, unaRilevazione.mese, unaRilevazione.giorno, 
				unaRilevazione.ora, unaRilevazione.minuti, "00", "0");
		
		
		boolean inserito = daoDR.inserisciDatoRilevazione(unaRilevazione.codiceSensore, unaRilevazione.valore, unaRilevazione.errore, 
								data, unaRilevazione.messaggio);
		
		return inserito;
	}
	
	
	/**
	 * Legge la stringa di una rilevazione inviata da un sensore, e individua i valori al suo interno
	 * @param unaStringaDiRilevazione La stringa di una rilevazione di cui leggere i valori
	 * @return I dati della rilevazione
	 */
	private Rilevazione formattaRilevazione(String unaStringaDiRilevazione) {
		Rilevazione valoriRilevazione = new Rilevazione();
		int posInizio = 0;
		int posFine = 0;
		
		//Registro il codice del sensore
		valoriRilevazione.setCodiceSensore(unaStringaDiRilevazione.substring(0, LUNGHEZZA_ID_SENSORE));
		
		//Controllo la struttura con cui il sensore salva i dati nella stringa decimale
		try {
			valoriRilevazione.setStrutturaRilevazione(daoDI.mostraStrutturaSensore(valoriRilevazione.codiceSensore));
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Problemi nella lettura della struttura del sensore");
		}
				
		//Individuo la posizione del giorno all'interno della stringa
		posInizio = LUNGHEZZA_ID_SENSORE + valoriRilevazione.strutturaRilevazione.indexOf(STR_GIORNO);
		posFine = posInizio + STR_GIORNO.length();
		valoriRilevazione.setGiorno(unaStringaDiRilevazione.substring(posInizio, posFine));

		//Individuo la posizione del mese all'interno della stringa
		posInizio = LUNGHEZZA_ID_SENSORE + valoriRilevazione.strutturaRilevazione.indexOf(STR_MESE);
		posFine = posInizio + STR_MESE.length();
		valoriRilevazione.setMese(unaStringaDiRilevazione.substring(posInizio, posFine));

		//Individuo la posizione dell'anno all'interno della stringa
		posInizio = LUNGHEZZA_ID_SENSORE + valoriRilevazione.strutturaRilevazione.indexOf(STR_ANNO);
		posFine = posInizio + STR_ANNO.length();
		valoriRilevazione.setAnno(unaStringaDiRilevazione.substring(posInizio, posFine));

		//Individuo la posizione dell'ora all'interno della stringa
		posInizio = LUNGHEZZA_ID_SENSORE + valoriRilevazione.strutturaRilevazione.indexOf(STR_ORA);
		posFine = posInizio + STR_ORA.length();
		valoriRilevazione.setOra(unaStringaDiRilevazione.substring(posInizio, posFine));

		//Individuo la posizione dei minuti all'interno della stringa
		posInizio = LUNGHEZZA_ID_SENSORE + valoriRilevazione.strutturaRilevazione.indexOf(STR_MINUTI);
		posFine = posInizio + STR_MINUTI.length();
		valoriRilevazione.setMinuti(unaStringaDiRilevazione.substring(posInizio, posFine));

		
		//Individuo la posizione del flag di errore all'interno della stringa
		posInizio = LUNGHEZZA_ID_SENSORE + valoriRilevazione.strutturaRilevazione.indexOf(STR_FLAG);
		posFine = posInizio + STR_FLAG.length();
		int erroreNum = Integer.parseInt(unaStringaDiRilevazione.substring(posInizio, posFine));
		if (erroreNum == 1) {
			valoriRilevazione.setErrore(true);
		} else {
			valoriRilevazione.setErrore(false);
		}

		//Individuo la posizione del valore della rilevazione all'interno della stringa (controllando la lunghezza di cifre del valore)
		if (unaStringaDiRilevazione.contains(STR_VALORELONG)) {
			posInizio = LUNGHEZZA_ID_SENSORE + valoriRilevazione.strutturaRilevazione.indexOf(STR_VALORELONG);
			posFine = posInizio + STR_VALORELONG.length();
		} else {
			posInizio = LUNGHEZZA_ID_SENSORE + valoriRilevazione.strutturaRilevazione.indexOf(STR_VALORESHORT);
			posFine = posInizio + STR_VALORESHORT.length();
		}
		valoriRilevazione.setValore(Integer.parseInt(unaStringaDiRilevazione.substring(posInizio, posFine)));
		
		//Individuo la posizione del messaggio all'interno della stringa
		posInizio = LUNGHEZZA_ID_SENSORE + valoriRilevazione.strutturaRilevazione.length();
		posFine = unaStringaDiRilevazione.length();
		valoriRilevazione.setMessaggio(unaStringaDiRilevazione.substring(posInizio, posFine));
		
		return valoriRilevazione;
	}
	
	
	/**
	 * Metodo interno per la conversione in Timestamp
	 * @param unAnno L'anno della data in formato AAAA
	 * @param unMese Il mese della data in formato MM
	 * @param unGiorno Il giorno della data in formato GG
	 * @param unOra L'ora della data in formato oo
	 * @param unMinuto I minuti della data in formato mm
	 * @param unSecondo I secondi della data in formato ss
	 * @param unMillisec I millisecondi della data in formato uuu
	 * @return La data in formato Timestamp
	 */
	private static Timestamp convertiDataStringa(String unAnno, String unMese, String unGiorno, String unOra, String unMinuto, String unSecondo, String unMillisec) {
		
		String anno = unAnno;
		String mese = unMese;
		String giorno = unGiorno;
		String ora = unOra;
		String minuto = unMinuto;
		String secondo = unSecondo;
		String millisec = unMillisec;
		
		//In caso di valori con una sola cifra, inferiori a 10
		if (unMese.length() == 1) mese = "0" + unMese;
		if (unGiorno.length() == 1) giorno = "0" + unGiorno;
		if (unOra.length() == 1) ora = "0" + unOra;
		if (unMinuto.length() == 1) minuto = "0" + unMinuto;
		if (unSecondo.length() == 1) secondo = "0" + unSecondo;
		
		Timestamp data = Timestamp.valueOf(anno + "-" + mese + "-" + giorno + " " + ora + ":" + minuto + ":" + secondo + "." + millisec);
				
		return data;
	}
}