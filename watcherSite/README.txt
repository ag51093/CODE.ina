
Requisiti per eseguire il progetto:
-Scaricare e installare xampp
-Dopo l'installazione avviare i server apache e mysql
-Scaricare e installare Tomcat v7.0
-Se non si possiede Tomcat, i file di configurazione del server sono di default. È neceassario dichiarare un utente per accedere al server:
	- nel file tomcat-users.xml incollare i seguenti comandi
		<tomcat-users>
			<!--
			  <role rolename="tomcat"/>
			  <role rolename="role1"/>
			  <user username="tomcat" password="tomcat" roles="tomcat"/>
			  <user username="both" password="tomcat" roles="tomcat,role1"/>
			  <user username="role1" password="tomcat" roles="role1"/>
			-->
			
				<role rolename="manager-gui"/>
				<user username="admin" password="admin" roles="manager-gui"/>
		</tomcat-users>
-Cliccare su Admin di mysql nel pannello di controllo di xampp e importare il file watcherdb.sql (struttura del database del progetto) presente nella cartella db della root del progetto
-Nella root del progetto aprire la cartella db
-Copiare la cartella watcher in C:/ (la cartella conterrà i file di configurazione del database e del server email)

Per avviare il progetto:
-Avviare Tomcat da Xampp
-Cliccare su admin e accedere al server con le credenziali inserite nel file tomcat-users.xml
-Effettuare il deploy del file watcherSite.war
-Digitare nella barra URL il seguente link http://localhost:8080/watcherSite/index.jsp

------------------------------------------------------------------------------------------------------------


Per configurare il server eclipse:
-Avviare eclipse
-Selezionare window -> show view -> servers
-Selezionare new server -> apache/tomcat 7.0
-Installarlo nella cartella C:/Xampp/tomcat
-Aggiungere il progetto al server

NB: Se eclipse viene chiuso, con conseguente arresto di Tomcat, il progetto non si avvierà

Per avviare il progetto da eclipse:
-Avviare il server tomcat su eclipse
-Aprire il browser e digitare il seguente link http://localhost:8080/watcherSite/index.jsp
Per l'accesso da altri dispositivi sulla stessa rete wifi:
-Controllare dal prompt con "ipconfig" l'IPv4 del pc, e digitare il link con l'ip al posto di "localhost" (lasciando :8080)