# Roue_de_la_fortune_L3MIAGE

# Equipe

Bydon Sacha  
Désiré Stéphane  
Maugard Samuel  
Mercuri Sabrina  


# Comment jouer ?

* Installer Maven  
* Installer les dépendances du projet: *mvn clean install*
* Lancer le Serveur: *mvn exec:java -Dexec.mainClass=server.Serveur*  
* Lancer le Client: *mvn exec:java -Dexec.mainClass=client.Client*  
* Le serveur hébergé: *109.210.118.18*  
__<span style='color:red'>(Le serveur est hébergé sur un Raspberry, il est possible que le serveur crash. Si c'est le cas, envoyez un mail ou un message privé discord à Sacha Bydon dans le groupe Miage-L3)</span>__


# Déroulement du jeu

Le jeu se déroule en 5 manches. Les 4 premières manches sont composées chacune d’une manche rapide et d’une manche longue. Une manche rapide affiche des lettres dans un ordre aléatoire toutes les 2 secondes jusqu’à l’apparition du mot : le but étant de taper la réponse le plus rapidement possible pour empocher 500 de gain et de prendre la main pour la manche longue.  
Pour la manche longue, la roue tourne et affiche la case obtenue. Selon la case obtenue, il passera son tour ou pourra jouer. S’il peut jouer, il pourra alors choisir une consonne (en tapant c puis la consonne), une voyelle s’il a minimum 200 de gain pour la payer (en tapant v puis la voyelle et en se délestant de 200 de gain) ou alors il pourra proposer une réponse entière (en tapant r puis la phrase qu’il pense correcte).  
Nous avons fait en sorte que lorsque le joueur aura proposé toutes les consonnes, il ne pourra plus proposer de consonnes et de même pour les voyelles.  
Lorsque les 4 premières manches sont terminées, le joueur ayant le plus de gain (gagnés lors des manches précédentes) pourra participer à la manche finale (le perdant pour regarder son déroulement mais de pourra pas jouer). Une deuxième roue est alors lancée avec des gains plus importants.  
La phrase affichée aura les lettres r, s, t, l, n et e d’affichées et pourra proposer 3 consonnes et 1 voyelle. Une fois affichée, il aura alors 30sec pour trouver le bon mot. Si le joueur trouve le bon mot, il gagnera les gains obtenus lors de toutes les manches et sinon il ne gagnera que les gains gagnés lors des 4 premières manches.  

Pour plus d'informations, voir dans le document technique.
