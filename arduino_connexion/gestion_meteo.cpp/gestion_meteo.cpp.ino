#include <LiquidCrystal.h>
#include <math.h>
#include <string.h>

//***************VAR_AFFICHAGE***************
LiquidCrystal lcd(8, 9, 4, 5, 6, 7);
int lcd_key     = 0;
int adc_key_in  = 0;
#define btnUP     1
#define btnDOWN   2
bool Click1 =0;
bool Click2 =0;
unsigned long tmp=millis();
unsigned long tim=millis();
//tmp = millis();

//**************VAR_GESTION_CLICK************
int tempo =5;
int p;
int Time;

//****************VAR_HEURE******************
int heure =1;
unsigned long periodStart;
bool tic;

//****************VAR_METEO******************
long randNumber;
int q;
String tabMeteo[]= {"rainny","cloudy","sunny","heatwave","thunderS"};
String meteo;
String prevision;
bool midDay;
int temporisation;

//*******************************************

//***************************AFFICHAGE*******************************
int read_LCD_buttons(){               // read the buttons
    adc_key_in = analogRead(0);       // read the value from the sensor 
    if (millis()-tmp > 100){
        if (adc_key_in < 250){ //plage bouton1 
            tempo ++;
            tmp = millis();
        return tempo;
        }
        else if (adc_key_in < 450) { //plage bouton 2
            tempo --; 
            tmp = millis();
        return tempo;
        }
    }   
}

void Init_Affichage(){
   lcd.begin(16, 2);               // start the library
             // set the LCD cursor   position 
}
void affichage(){
    lcd.clear();
    lcd.setCursor(0,0);  // init cursor on the first line
    lcd.print(meteo);
    lcd.setCursor(9,0);
    lcd.print("T:");
    lcd.print(tempo);
    lcd.print("s");
    lcd.setCursor(0,1);  // move to the begining of the second line  
    lcd.print(prevision);
    lcd.setCursor(9,1);
    lcd.print("H:");
    lcd.print(heure);
    
    lcd_key = read_LCD_buttons();   // read the buttons

}

//**************************METEO*****************************
typedef enum {
  IDLE_1,
  ENVOYER_1,
  FUTUR_1,
} METEO_STATE;

METEO_STATE CurrentState_1;

void Update_1(){
  METEO_STATE NextState_1 = CurrentState_1;
  switch (CurrentState_1)        
{
    case IDLE_1:
      if (0<=p<10){
        if(q != 4)meteo = tabMeteo[q+1];
        else meteo = tabMeteo[1];
        q = randNumber = random(5);
        NextState_1 = FUTUR_1;
      }
      else if (10<=p<12 && midDay == false){
        randNumber = random(5);
        meteo = tabMeteo[randNumber];
        q = randNumber = random(5);
        NextState_1 = FUTUR_1;
      }
      else if (p > 12){
        meteo = prevision;
        randNumber = random(5);
        q=randNumber;
        NextState_1 = FUTUR_1;
      }
      break;
      
    case FUTUR_1:
      if (tic == true) NextState_1 = ENVOYER_1;
      break;
      
    case ENVOYER_1:
      if (heure%12 == 0 && heure%24 != 0){
        midDay = true;
        NextState_1 = IDLE_1;
      }
      else if (tic == true) NextState_1 = ENVOYER_1;
      else if (heure%24 == 0) NextState_1 = IDLE_1;
      break;
     
    default:
      NextState_1 = IDLE_1;
    }
    CurrentState_1 = NextState_1;
}

void Output_1(){
  switch(CurrentState_1){
 
    case IDLE_1:
       randNumber = random(100);
       p = randNumber;
       break;

    case FUTUR_1:
       prevision = tabMeteo[q];
       midDay = false;
       break;

    case ENVOYER_1:
       Serial.write(45);
       int trans_1 = Serial.print(meteo);
       int sent_meteo = Serial.write(trans_1);
       int trans_2 = Serial.print(prevision);
       int sent_prev = Serial.write(trans_2);
       int sent_3 = Serial.write(heure);
       break;
  }
}

void Init_1(){
 
  int i = randNumber = random(5);
  q = randNumber = random(5);
  meteo = tabMeteo[i];
  prevision = tabMeteo[q];
  midDay = false;
  tic = false;

}

//**********************GESTION_CLICK****************************
typedef enum {
  IDLE_2,
  DECREASE_2,
  INCREASE_2//,
//  PLUS_2,
//  MOINS_2
} CLICK_STATE;

CLICK_STATE CurrentState_2;

void Update_2(){
  CLICK_STATE NextState_2 = CurrentState_2;
  switch (CurrentState_2)        
{
    case IDLE_2:
      if (Click2) NextState_2 = DECREASE_2;
      else if (Click1) NextState_2 = INCREASE_2;
      break;
      
    case DECREASE_2:
      if (Click1){
        NextState_2 = INCREASE_2;
      }
      break;
/*      
    case MOINS_2:
        if((millis()-Time)>2000)NextState_2 = DECREASE_2;
      break;
    
    case PLUS_2:
        if((millis()-Time)>2000)NextState_2 = INCREASE_2;
      break;
*/     
    case INCREASE_2:
      if (Click2){
        NextState_2 = DECREASE_2;
      }
      break;
    
      default:
        NextState_2 = IDLE_2;
      }
    CurrentState_2 = NextState_2;
}

void Output_2(){
  switch(CurrentState_2){
 
    case IDLE_2:
     if((millis()-tim)>100){
      read_LCD_buttons();
       affichage();
       tim=millis();
     }
       break;
      
    case DECREASE_2:
       if((millis()-tim)>100){
        read_LCD_buttons();
        affichage();
        tim=millis();
       }
       break;
       
    case INCREASE_2:
       if((millis()-tim)>100){
        read_LCD_buttons();
        affichage();
        tim=millis();
       }
       break;
 /*      
    case PLUS_2:
       tempo ++;
       Click1 = false;
       break;
       
    case MOINS_2:
       tempo --;
       Click2 = false;
       break;*/
  }
}

void Init_2(){
   
}

//*************************HEURE*******************************

typedef enum {
  IDLE_3,
  HEURE_3
} HEURE_STATE;
HEURE_STATE CurrentState_3;

void Update_3(){
  HEURE_STATE NextState_3 = CurrentState_3;
  switch (CurrentState_3)        
{
    case IDLE_3:
      if ((millis()-periodStart) > (tempo*1000)) NextState_3 = HEURE_3;
      break;
      
    case HEURE_3:
        periodStart = millis();
        NextState_3 = IDLE_3;
      break;
    
    default:
      NextState_3 = IDLE_3;
    }
    CurrentState_3 = NextState_3;
}

void Output_3(){
  switch(CurrentState_3){
 
    case IDLE_3:
       tic = false;
       break;
      
    case HEURE_3:
       tic = true;
       heure++;
       break;
  }
}

void Init_3(){
 periodStart=millis(); 
}

//****************************************************************
void setup() {
  // put your setup code here, to run once
Serial.begin(9600);
randomSeed(analogRead(0));
Init_3();
Init_1();
Init_2();
Init_Affichage();

}

void loop() {
  // put your main code here, to run repeatedly:

Output_3();
Output_1();
Output_2();

Update_3();
Update_1();
Update_2();
}



