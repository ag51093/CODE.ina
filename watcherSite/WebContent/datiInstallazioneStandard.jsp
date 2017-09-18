<%--
  --   Description: Questa pagina contiene l'interfaccia dei dati d'installazione per gli utenti standard
  --   Developer :  Antonio Garofalo, Matteo Forina
  --%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="ISO-8859-1">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Dati Installazione</title>
        <link rel="stylesheet" type="text/css" href="css/main.css">
        <link rel="stylesheet" type="text/css" href="css/home.css">
    </head>
  
    <body>
        <div>
            <ul class="navbar card">
                <li><a class="navbar" href="login">Logout</a></li>
                <li><a class="navbar" href="homeStandard.jsp">${utenteloggato}</a></li>
                <li><a href="homeStandard.jsp"><img src="res/logowatcher.png" alt="logo" id="logo"></a></li>
            </ul>
        </div>
        <div>
            <ul class="sidebar card">
                <li><a class="sidebar" href="dashboard">Dashboard</a></li>
                <li><a class="sidebar" href="datiRilevazioni">Dati Rilevazioni</a></li>
                <li><a class="sidebar active" href="datiInstallazione">Dati Installazione</a></li>
                <li><a class="sidebar" href="trasferimento">Trasferimento Automatico</a></li>
            </ul>
        </div>
        <div class="card">
        	<form id="filtriDatiInstallazione" action="datiInstallazione" method="post">
        		<input type="hidden" name="idform" value="filtradatiinstallazione"/>
        		<%-- Mostra i filtri dei dati d'installazione disponibili per l'utente --%>
        		${filtridatiinstallazione}
        	</form>
        	<br><br>
        	<%-- Stampa il risultato dell'elaborazione se questo non è nullo --%>
	        ${risultato}
        </div>
	</body>
</html>