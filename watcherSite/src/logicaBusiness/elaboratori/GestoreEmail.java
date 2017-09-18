package logicaBusiness.elaboratori;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.util.logging.Logger;

/**
 * La classe gestisce l'invio delle email
 * 
 * @author Antonio Garofalo, Matteo Forina
 *
 */
public class GestoreEmail {
	
	/** Espresssione regolare che identifica la struttura sintattica delle email */
	private final static Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	/**
	 * Il metodo permette di inviare una email (supporta formato html per il corpo della mail)
	 * 	
	 * @param unDestinatario destinatario della mail da inviare
	 * @param unOggetto oggetto della mail da inviare
	 * @param unCorpo corpo della mail da inviare (supporto per testo html)
	 * @param unAllegato allegato della mail da inviare
	 * @param unNomeAllegato nome dell'allegato della mail da invare
	 * @return true se inviata, false altrimenti
	 */
	public boolean inviaEmail(String unDestinatario, String unOggetto, String unCorpo, File unAllegato, String unNomeAllegato) {
		boolean inviata = false;
		if (èValidaEmail(unDestinatario)) {
			ServerMailConnector mailConnector = ServerMailConnector.getIstanza();
			mailConnector.inviaEmail(unDestinatario, unOggetto, unCorpo, unAllegato, unNomeAllegato);				
			inviata = true;
		} else {
			inviata = false;
		}
		
		return inviata;
    }	
	
	/**
	 * Il metodo controlla la validità della struttura sintattica dell'email inserita
	 * 
	 * @param unaMail email da validare
	 * @return true se valida, false altrimenti
	 */
	public static boolean èValidaEmail(String unaMail) {
		boolean valida = false;
		if (unaMail != null) {
			Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(unaMail);
			valida = matcher.find();
		}
		return valida;
	}

	/**
	 * Classe (Singleton) per la gestione della connessione al server SMTP per l'invio email
	 * 
	 * @author Antonio Garofalo, Matteo Forina
	 * 
	 */
	private static class ServerMailConnector {
		/**L'oggetto usato per stampare i messaggi d'errore*/
		private final static Logger LOGGER = Logger.getLogger(GestoreEmail.class.getName());
		
		/**Istanza usata per incapsulare le impostazioni per l'invio delle mail*/
		private Session sessione = null;
		
		/**Costante per identificare il mittente delle mail del sistema*/
		private final static String MITTENTE_EMAIL_DI_SISTEMA = "watcheritps@gmail.com";
		
		/**Istanza Singleton di MailManager*/
		private final static ServerMailConnector ISTANZA = new ServerMailConnector();

		/**Costruttore privato di MailManager*/
		private ServerMailConnector() {
			this.inizializza();
		}
		/**Restituisce l'istanza Singleton*/
		public static ServerMailConnector getIstanza() {
			return ISTANZA;
		}
		
		/**Imposta i parametri del Server SMTP*/
		private void inizializza() {
			File configFile;
			configFile = new File("C:" + File.separator + "watcher" + 
					File.separator + "emailconfig.ini");
			
			//Acquisisco in un oggetto Properties le proprietà del file config
			InputStream lettoreXML = null;
			Properties config = null;
			try {
				lettoreXML = new FileInputStream(configFile);
				config = new Properties();
				config.loadFromXML(lettoreXML);
			} catch (InvalidPropertiesFormatException e) {
				LOGGER.log(Level.SEVERE, "Formato configurazione non valido");
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "File di configurazione non trovato");
			} finally {
				if (lettoreXML != null) {
					try {
						lettoreXML.close ();
					} catch (java.io.IOException e3) {
						LOGGER.log(Level.SEVERE, "Lettore di risorse non chiuso");
					}
				}
			}
			
			configuraServer(config);
		}
		
		/**
		 * Il metodo interno configura i parametri del server SMTP per l'invio delle Email
		 * @param config file contenente le configurazioni del server SMTP
		 */
		private void configuraServer(Properties config) {
			/* 
			 *Nel oggetto config vengono salvate tutte le impostazioni per la corretta connessione al server SMTP
			 *In particolare:
			 *	watchermail e la password per l'accesso saranno le variabili che conterranno i parametri di accesso alla mail del mittente per autorizzare l'invio di altre email per conto di quest'ultimo
			 *	host, i booleani di autenticazione e attivazione del protocollo TLS e port saranno i parametri che permetteranno la connessione al Server SMTP
			 */
			String watchermail = config.getProperty("WATCHERMAIL");
			String password = config.getProperty("PASSWORD");
			config.put("mail.smtp.host", config.getProperty("HOST"));
			config.put("mail.smtp.auth", Boolean.valueOf(config.getProperty("AUTH")));
			config.put("mail.smtp.starttls.enable", Boolean.valueOf(config.getProperty("TLS")));
			config.put("mail.smtp.port", Integer.parseInt(config.getProperty("PORT")));
			
			/* In sessione verranno salvati i parametri di configurazione creati precedentemente
			 * e associati a un oggetto PasswordAutentication che conterrà la logica di autenticazione al Server */
			sessione = Session.getInstance(config,  
					new javax.mail.Authenticator() {  
	    		    	protected PasswordAuthentication getPasswordAuthentication() {  
	    		    		return new PasswordAuthentication(watchermail, password);  
	    		    	}  
	    		  	});  
		}
		
		/**
		 * Il metodo invia una mail con eventuali allegati
		 * 
		 * @param unDestinatario il destinatario della mail
		 * @param unOggetto l'oggetto della mail
		 * @param unCorpo il corpo testuale della mail (supporto per testo html)
		 * @param unAllegato l'allegato da inserire nella mail
		 * @param unNomeAllegato il nome da attribuire al file allegato ("Allegato" di default se non viene specificato)
		 * 
		 */
		private void inviaEmail(String unDestinatario, String unOggetto, String unCorpo, File unAllegato, String unNomeAllegato) {
			try {  
				MimeMessage message = new MimeMessage(sessione);  
				message.setFrom(new InternetAddress(MITTENTE_EMAIL_DI_SISTEMA));  
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(unDestinatario));  
				message.setSubject(unOggetto);
				
				/* La composizione avviene in 3 parti: 
		         * - la prima costruisce il corpo testuale
		         * - la seconda allega il file
		         * - la terza operazione avviene dopo la creazione e strutturazione di ogni BodyPart che viene quindi aggiunta all'istanza di MultiPart
		         * Ogni oggetto MultiPart è composto da più oggetti di tipo BodyPart, il quale ciascuno conterrà un pezzo del corpo della mail finale
		         */
				Multipart multipart = new MimeMultipart();
				   
		        BodyPart messageBodyPart = new MimeBodyPart();     
		        messageBodyPart.setContent(unCorpo, "text/html");
		        multipart.addBodyPart(messageBodyPart);
				
		        if (unAllegato != null) {
		        	messageBodyPart = new MimeBodyPart();
		        	String filename = "ALLEGATO";
		        	// SE IL NOME DEL FILE NON VENISSE SPECIFICATO, NELLA MAIL SAREBBE VISUALIZZATO IL CONTENUTO DEL FILE STESSO COME TESTO!
		            if (unNomeAllegato.length() > 0) {
		            	filename = unNomeAllegato;
		            }
		            DataSource source = new FileDataSource(unAllegato);
		            messageBodyPart.setDataHandler(new DataHandler(source));
		            messageBodyPart.setFileName(filename);
		            multipart.addBodyPart(messageBodyPart);
				} 
		        
		        message.setContent(multipart);
				Transport.send(message);  				  
			}catch (MessagingException e) {
				LOGGER.log(Level.SEVERE, "Invio mail non riuscito");
			}
		}
	}
}

