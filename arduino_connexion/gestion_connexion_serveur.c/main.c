#include <stdio.h>
#include <curl/curl.h>
#include <fcntl.h>
#include <string.h>
#include <zconf.h>
#include <stdbool.h>
#include <termios.h>
#include <stdlib.h>

int USB;

void envoiServeur(){
    struct termios termios_p;
    /* Lecture des parametres courants */
    tcgetattr(USB, &termios_p);
    /* On ignore les BREAK et les caracteres avec erreurs de parite */
    termios_p.c_iflag = IGNBRK | IGNPAR;
    /* Pas de mode de sortie particulier */
    termios_p.c_oflag = 0;
    /* Liaison a 9600 bps avec 8 bits de donne */
    termios_p.c_cflag = B9600 | CS8;
    /* Mode non-canonique avec echo */
    termios_p.c_lflag = 0;
    /* Caracteres immediatement disponibles */
    termios_p.c_cc[VMIN] = 1;
    termios_p.c_cc[VTIME] = 0;
    /* Sauvegarde des nouveaux parametres */
    tcsetattr(USB, TCSANOW, &termios_p);
//*****

    printf("\n\nDonnée envoyer par l'Arduino :\n");

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

    //****************************************
    char meteo[1024] = "";
    char prevision[1024] = "";
    char heure[1024] = "";
    bool met = true;
    bool prev = false;
    bool her = false;
    int cpt = 0;

    for (int i = 1; i < 200; i++) {
        if (received[i] != '!') {
            if (met == true) {
                if (received[i] == ',') {
                    met = false;
                    prev = true;
                    i++;
                    cpt = i;
                } else meteo[i - 1] = received[i];
            }
            if (prev == true) {
                if (received[i] == ',') {
                    prev = false;
                    her = true;
                    i++;
                    cpt = i;
                } else prevision[(i - cpt)] = received[i];
            }
            if (her == true) {
                heure[(i - cpt)] = received[i];
            }
        }
    }
    int nombre = (int) strtol(heure, NULL, 10); //converti un char en int

//****************************************************************


    CURL *curl;
    CURLcode res;
    char jsonObj[1024];
    sprintf(jsonObj, "{ \"timeStamp\" : \"%d\" , \"Weather\" : [ { \"dfn\" : 0 , \"Weather\" : \"%s\" }, { \"dfn\" : 1 , \"Weather\" : \"%s\" } ]}", nombre, meteo, prevision);
    //In windows, this will init the winsock stuffl
    curl_global_init(CURL_GLOBAL_ALL);
    struct curl_slist *list = NULL;

    // get a curl handle
    curl = curl_easy_init();
    if (curl) {
        /* First set the URL that is about to receive our POST. This URL can
           just as well be a https:// URL if that is what should receive the
           data. */
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
        USB = open("/dev/ttyACM0", O_RDWR | O_NONBLOCK | O_NDELAY);
        if (USB != 0) {
            envoiServeur();
        }
    }
}