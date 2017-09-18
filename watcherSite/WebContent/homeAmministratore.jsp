<%--
  --   Description: Questa pagina contiene l'interfaccia della home per gli utenti amministratori
  --   Developer :  Antonio Garofalo, Matteo Forina, Ivan Lamparelli
  --%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="ISO-8859-1">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Homepage</title>
        <link rel="stylesheet" type="text/css" href="css/main.css">
        <link rel="stylesheet" type="text/css" href="css/home.css">
    </head>
    <style>
    	div.card{
    		background-image: url("res/eyewatcher-trasp.png");
    		background-repeat: no-repeat;
    		background-position: center;
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
                <li><a class="sidebar" href="dashboard">Dashboard</a></li>
                <li><a class="sidebar" href="datiRilevazioni">Dati Rilevazioni</a></li>
                <li><a class="sidebar" href="datiInstallazione">Dati Installazione</a></li>
                <li><a class="sidebar" href="gestioneClienti">Gestione Clienti&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
            </ul>
        </div>
         <div class="card">
         	<%-- Stampa un messaggio di benvenuto per l'utente --%>
         	Bentornato <strong>${utenteloggato}</strong>,<br><br>
         	seleziona dall'elenco a sinistra la sezione desiderata.
         </div>
    </body>
</html>