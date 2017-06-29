#include <stdio.h>
#include <curl/curl.h>
#include <fcntl.h>
#include <string.h>
#include <zconf.h>
#include <stdbool.h>
#include <termios.h>
#include <stdlib.h>

int USB;
char meteo[512];
char prevision[512];
char heure[512];
int nombre=0;

void initialisationPortSerie() {

    struct termios termios_p;
    /* Lecture des parametres courants */
    tcgetattr(USB, &termios_p);
    /* On ignore les BREAK et les caracteres avec erreurs de parite */
    termios_p.c_iflag = IGNBRK | IGNPAR;
    /* Pas de mode de sortie particulier */
    termios_p.c_oflag = 0;
    /* Liaison a 9600 bps avec 8 bits de donne */
    termios_p.c_cflag = B9600 | CS8;
    /* Pas de mode particulier */
    termios_p.c_lflag = 0;
    /* Caracteres immediatement disponibles */
    termios_p.c_cc[VMIN] = 1;
    termios_p.c_cc[VTIME] = 0;
    /* Sauvegarde des nouveaux parametres */
    tcsetattr(USB, TCSANOW, &termios_p);
//*****
}

void traitementArduino() {

    printf("\n\nDonnée envoyer par l'Arduino :\n");


    //*******************Lecture_port_serie***********************
    char received[1024] = "";
    char buf = '\0';
    bool started = false;
    bool ended = false;
    int n = 0;

    do {
        n = read(USB, &buf, 1);
        if (n > 0) {
            if (buf == '#') {
                started = true;
                ended = false;
                memset(received, 0, 1020);
            }
            if (buf == '!' && started) {
                ended = true;
            }
            printf("%c", buf);
            fflush(stdout);
            sprintf(received, "%s%c\0", received, buf);
        }
    } while (!started || !ended);
    printf("\nDonné envoyé au serveur :\n");
    /* Fermeture */
    close(USB);

    //******************SEPARATION_ELEMENT_POUR_JSON*********************
    meteo[512] = " ";
    prevision[512] = " ";
    heure[512] = " ";
    bool met = true;
    bool prev = false;
    bool her = false;
    int j = 0;


    for (int i = 1; i < strlen(received); i++) {
        if (received[i] != '!') {
            if (met == true) {
                if (received[i] == ',') {
                    met = false;
                    prev = true;
                    meteo[j]='\0';
                    i++;
                    j=0;
                } else {
                    meteo[j] = received[i];
                    j++;
                }
            }
            if (prev == true) {
                if (received[i] == ',') {
                    prev = false;
                    her = true;
                    prevision[j]='\0';
                    i++;
                    j=0;
                } else {
                    prevision[j] = received[i];
                    j++;
                }
            }
            if (her == true) {
                heure[j] = received[i];
                j++;
            }
        }
    }
    nombre = (int) strtol(heure, NULL, 10); //converti un char en int

}


void envoiServeur(){

    CURL *curl;
    CURLcode res;
    char jsonObj[1024];
    //Création de chaine en format Json :
    sprintf(jsonObj, "{ \"timestamp\" : %d , \"weather\" : [ { \"dfn\" : 0 , \"weather\" : \"%s\" }, { \"dfn\" : 1 , \"weather\" : \"%s\" } ]}", nombre, meteo, prevision);
    //init winsock stuffl
    curl_global_init(CURL_GLOBAL_ALL);
    struct curl_slist *list = NULL;

    // get a curl handle
    curl = curl_easy_init();
    if (curl) {
        /* First set the URL that is about to receive our POST. This URL can
           just as well be a https:// URL if that is what should receive the
           data. */// "http://bushakan-imerir.herokuapp.com/sales"
        //curl_easy_setopt(curl, CURLOPT_URL, "http://bushukan-imerir.herokuapp.com/metrology");
        curl_easy_setopt(curl, CURLOPT_URL, "http://127.0.0.1:5000/arduino");
        // Now specify the POST data
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonObj);

        list = curl_slist_append(list, "content-Type:application/json");
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, list);

        // Perform the request, res will get the return code *
        res = curl_easy_perform(curl);

        // Check for errors *
        if (res != CURLE_OK)
            fprintf(stderr, "curl_easy_perform() failed: %s\n", curl_easy_strerror(res));

        // always cleanup *
        curl_slist_free_all(list); /* free the list again */
        curl_easy_cleanup(curl);
    }
    curl_global_cleanup();
}


int main() {
    while(1) {
        USB = 0;
        USB = open("/dev/ttyACM0", O_RDWR);//| O_NONBLOCK | O_NDELAY
        if (USB != 0) {
            initialisationPortSerie();
            traitementArduino();
            envoiServeur();
        }
    }
}