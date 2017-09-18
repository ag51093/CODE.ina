<%--
  --   Description: Questa pagina contiene l'interfaccia della dashboard per gli utenti amministratori
  --   Developer :  Antonio Garofalo, Matteo Forina
  --%>
  
<!DOCTYPE html>
<html>
    <head>
        <meta charset="ISO-8859-1">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Dashboard</title>
        <link rel="stylesheet" type="text/css" href="css/main.css">
        <link rel="stylesheet" type="text/css" href="css/home.css">
    </head>
    <style>
        #criticaltable {
    		color: #d53232;
    	}
    	
 		#criticaltable td {
 			border-color: #d53232;
 		}
 		
 		#criticaltable tr:hover {
			background-color: #f8dcdc;
		}
    </style>
    <body>
        <div>
            <ul class="navbar card">
                <li><a class="navbar" href="login">Logout</a></li>
                <li><a class="navbar" href="homeAmministratore.jsp">${utenteloggato}</a></li>
                <li><a href="homeAmministratore.jsp"><img src="res/logowatcher.png" alt="logo" id="logo"></a></li>
            </ul>
        </div>
        <div>
            <ul class="sidebar card">
                <li><a class="sidebar active" href="dashboard">Dashboard</a></li>
                <li><a class="sidebar" href="datiRilevazioni">Dati Rilevazioni</a></li>
                <li><a class="sidebar" href="datiInstallazione">Dati Installazione</a></li>
                <li><a class="sidebar" href="gestioneClienti">Gestione Clienti&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
            </ul>
        </div>
        <div class="card">
        	<form id="formSelezionaCliente" action="dashboard" method="post">
        		<input type="hidden" name="idform" value="sceglicliente"/>
        		<%-- Mostra i clienti del sistema e permette di selezionarne uno --%>
        		${selezioneclienti}
        		<button type="submit" name="ricerca">Seleziona Cliente</button><br>
        		<hr>
        	</form>
        	<br>
        	<form id="filtriDatiDashboard" action="dashboard" method="post">
        		<input type="hidden" name="idform" value="filtradatidashboard"/>
        		<%-- Mostra i filtri della dashboard disponibili per l'utente selezionato --%>
        		${filtridashboard}
        	</form>
        	<br>
        	<%-- Stampa il risultato dell'elaborazione se questo non è nullo --%>
        	${risultato}
    	</div>
    </body>
</html>