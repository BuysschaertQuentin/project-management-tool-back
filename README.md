# README en français

## Lancement de l'application en local

 Pour lancer l'application en local, il suffit de lancer la commande `mvn spring-boot:run` dans le répertoire racine du projet.

## Installation de Docker et build de l'application

 Pour lancer l'application dans un conteneur Docker, il est nécessaire de l'installer sur votre ordinateur. Voici les étapes àsuivre pour cela :

  1. Installez Docker sur votre ordinateur en suivant les instructions sur le site web de Docker : https://docs.docker.com/engine/installation/
  2. Vous pouvez aussi utiliser Docker Desktop (https://www.docker.com/products/docker-desktop) pour installer Docker sur votre ordinateur. 
  2. Une fois Docker installé , exécutez la commande `docker build -t project-management-tool-back .` dans le répertoire racine du projet pour build l'image Docker de l'application.
  3. Pour lancer le conteneur, exécutez la commande `docker run -p 8080:8080 project-management-tool-back`

Alternativement, vous pouvez utiliser la commande `docker-compose up -d --build` dans le répertoire racine du projet pour lancer tous les conteneurs de l'application ainsi que la base de données PostgreSQL.
le `-d` permet de lancer les conteneurs en arriere plan, mais vous ne verrez pas les logs. 
Notez que pour lancer la base de données, vous devrez galement lancer le conteneur correspondant en exécutant la commande `docker run -p 5432:5432 postgres`

---

# README in English

## Running the application locally

 To run the application locally, simply run the command `mvn spring-boot:run` in the root directory of the project.

## Installing Docker and building the application

 To run the application in a Docker container, you need to install Docker on your computer. Here are the steps to follow:

  1. Install Docker on your computer by following the instructions on the Docker website: https://docs.docker.com/engine/installation/
  2. You can also use Docker Desktop (https://www.docker.com/products/docker-desktop) to install Docker on your computer.
  2. Once Docker is installed, run the command `docker build -t project-management-tool-back .` in the root directory of the project to build the Docker image of the application.
  3. To run the container, run the command `docker run -p 8080:8080 project-management-tool-back`

Alternatively, you can use the command `docker-compose up -d --build` in the root directory of the project to launch all the containers of the application as well as the PostgreSQL database.
The `-d` allows you to launch the containers in the background, but you will not see the logs.
Note that this will start the application container as well as the PostgreSQL database container.
Note that to run the database, you will also need to run the corresponding container by running the command `docker run -p 5432:5432 postgres`
