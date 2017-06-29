package modele;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Customer {

	Coordinates posCustomer;
	float sensibilitePub;
	float sensibilitePrix;
	float sensibiliteChaud;
	float sensibiliteFroid;
	float sensibiliteAlcool;
	float sensibiliteSansAlcool;
	MapGame donnéesJeu;
	
	
	public Customer(MapGame m,String meteo){	
		donnéesJeu = m;
		this.posCustomer =  new Coordinates((float)Math.random()*100,(float) Math.random()*100);
		
			switch(meteo){
			case "sunny":
				this.sensibiliteChaud = 0;
				this.sensibiliteFroid = 100;
				this.sensibilitePub = 100;//50;
			break;
			
			case "thunderS":
				this.sensibiliteFroid = 50;
				this.sensibiliteChaud = 50;
				this.sensibilitePub = 0;
			break;
			
			case "cloudy":
				this.sensibiliteChaud = 100;
				this.sensibiliteFroid = 0;
				this.sensibilitePub = 40;
			break;
			
			case "rainny":
				this.sensibiliteChaud = 100;
				this.sensibiliteFroid = 0;
				this.sensibilitePub = 10;
			break;
			
			case "heatwave":
				this.sensibiliteChaud = 0;
				this.sensibiliteFroid = 100;
				this.sensibilitePub = 20;
			break;
		}
			
		this.sensibiliteAlcool = 0;//(float)Math.random()*100;
		this.sensibiliteSansAlcool = 100;//1-sensibiliteAlcool;
		
	}
	
	

	//************************************Vendeur_Moins_Cher****************************************
	public String vendeurMoinsCher(String[][] vendeur2, String[] vendeur){
		
		Set<Entry<String, MapItem[]>> tmp = donnéesJeu.getItemsByPlayer().entrySet();
		String[][] vendeurMoinsCher=new String[tmp.size()][2];
		String[][] vendeurPotentiel=new String[vendeur2.length][2];
		int l = 0;
		int t = 0;
		int cpt = 0;
		vendeurMoinsCher[l][0] = "0";
		vendeurMoinsCher[l][1] = "0";
		for (int j=0; j<(vendeur.length); j++){
			for (int p=0; p<(vendeur2.length); p++){
				if (vendeur[j].equals(vendeur2[p][0])){
					vendeurPotentiel[t][0] = vendeur2[j][0];
					vendeurPotentiel[t][1] = vendeur2[j][1];
					if (vendeurMoinsCher[l][0] == "0" && vendeurMoinsCher[0][1] == "0"){
						vendeurMoinsCher[l][0] = vendeurPotentiel[t][0];
						vendeurMoinsCher[l][1] = vendeurPotentiel[t][1];
					}
					else{
						float prixMoinsCher = Float.parseFloat(vendeurMoinsCher[l][1]);
						float prixPotentiel = Float.parseFloat(vendeurPotentiel[l][1]);
						System.out.println(prixMoinsCher+" "+prixPotentiel);
						if(vendeurMoinsCher[l][0]!=vendeurPotentiel[0][0] && prixMoinsCher > prixPotentiel){
							vendeurMoinsCher[l][0] = vendeurPotentiel[t][0];
							vendeurMoinsCher[l][1] = vendeurPotentiel[t][1];
						}
						else if (vendeurMoinsCher[0][0]!=vendeurPotentiel[0][0] && prixMoinsCher == prixPotentiel){
							l++;
							vendeurMoinsCher[l][0] = vendeurPotentiel[t][0];
							vendeurMoinsCher[l][1] = vendeurPotentiel[t][1];
						}
					}
					t++;
				}
			}
		}
		/*float rnd = (float) Math.random();
		float q = (rnd*10);*/
		int rnd = 0;
		rnd = (int) Math.round(l);
		return vendeurMoinsCher[rnd][0];
	}
	
	//*********************************Vendeur_Plus_Loin****************************************
	public String[] vendeurPlusLoin(){
		Set<Entry<String, MapItem[]>> tmp = donnéesJeu.getItemsByPlayer().entrySet();
		String[] vendeur=new String[tmp.size()];
		Iterator<Entry<String, MapItem[]>> it = tmp.iterator();
		int i=0;
		while(it.hasNext()){
			Entry<String, MapItem[]> e = it.next();
			MapItem[] mapItems = e.getValue();
			for(MapItem mi : mapItems) {
				if(mi.getKind().equals("stand")){
					if(distanceTo(posCustomer,mi.getLocation())>mi.getInfluence()*20 && distanceTo(posCustomer,mi.getLocation())>=(mi.getInfluence()*20)/2){
						vendeur[i] = mi.getOwner();
						i++;
					}
				}
			}
		}
		return vendeur;
	}
	
	//*********************************Vendeur_Moins_Loin****************************************
		public String[] vendeurMoinsLoin(){
			Set<Entry<String, MapItem[]>> tmp = donnéesJeu.getItemsByPlayer().entrySet();
			String[] vendeur=new String[tmp.size()];
			Iterator<Entry<String, MapItem[]>> it = tmp.iterator();
			int i=0;
			while(it.hasNext()){
				Entry<String, MapItem[]> e = it.next();
				MapItem[] mapItems = e.getValue();
				for(MapItem mi : mapItems) {
					if(mi.getKind()=="stand"){
						if(distanceTo(posCustomer,mi.getLocation())>mi.getInfluence()*20 && distanceTo(posCustomer,mi.getLocation())<(mi.getInfluence()*20)/2){
							vendeur[i] = mi.getOwner();
							i++;
						}
					}
				}
			}
			return vendeur;
		}	
		
		
	public String[][] acheter(){		
		//*****************************************Choix_du_vendeur***************************************************
		float val = (float) Math.random();
		val = val*100;
		String alcF = "Mojito";
		String alcC = "Vin Chaud";
		String NalcF = "Limonade";
		String NalcC = "Chocolat Chaud";
		String[][] vendeurFinal = new String [1][2];
		vendeurFinal[0][0]="";
		vendeurFinal[0][1]="";	
		//**************Sensible_a_la_Pub_Deplacement***************
		if(sensibilitePub>val){
			//********************* sA > sSA ***********************
			if (sensibiliteAlcool > sensibiliteSansAlcool){
				if(sensibiliteFroid > sensibiliteChaud){
					//aller chez la personne la plus loin, avec des boissons Alcoolisées froides le moins cher
					
					//vendeurs loins :
					String[] vendeur = vendeurPlusLoin();
					
					//vendeurs boissons alcoolisées froides
					Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
					Iterator<Entry<String,Drink[]>> it = tmp.iterator();
					String[][] vendeur2=new String[tmp.size()][2];
					int i = 0;
					while(it.hasNext()){
						Entry<String,Drink[]> e = it.next();
						Drink[] drinks = e.getValue();
						e.getKey();
						for(Drink drink : drinks){
							if(drink.getPrice() != 0){
								if(drink.isHasAlcool() && drink.isCold()){
									vendeur2[i][0]=e.getKey();
								    vendeur2[i][1]=String.valueOf(drink.getPrice());
									i++;
								}
							}
						}
					}
					
					//vendeur moins cher
					String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
					if (moinsCher != null){
						vendeurFinal[0][0]=moinsCher;
						vendeurFinal[0][1]=alcF;
						return vendeurFinal;
					}
					else{
						return null;
					}
				}
	
				else if(sensibiliteFroid < sensibiliteChaud){
					//aller chez la personne la plus loin, avec des boissons Alcoolisé chaude le moins cher
					
					//vendeurs loins :
					String[] vendeur = vendeurPlusLoin();
					
					//vendeurs boissons alcoolisées chaudes
					Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
					Iterator<Entry<String,Drink[]>> it = tmp.iterator();
					String[][] vendeur2=new String[tmp.size()][2];
					//Map<String,> vendeurMap = new HashMap<String,Drink>();
					int i = 0;
					while(it.hasNext()){
						Entry<String,Drink[]> e = it.next();
						Drink[] drinks = e.getValue();
						e.getKey();
						for(Drink drink : drinks){
							if(drink.getPrice() != 0){
								if(drink.isHasAlcool() && !drink.isCold()){
									vendeur2[i][0]=e.getKey();
								    vendeur2[i][1]=String.valueOf(drink.getPrice());
									i++;
								}
							}
						}
					}
					
					//vendeur moins cher
					String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
					if (moinsCher != null){
						vendeurFinal[0][0]=moinsCher;
						vendeurFinal[0][1]=alcC;
						return vendeurFinal;
					}
					else{
						return null;
					}
				}
				else if(sensibiliteChaud == sensibiliteFroid){
					float rand = (float) Math.random();
					rand = rand*100;
					
					if(rand>=50){
						//aller chez la personne la plus loin, avec des boissons Alcoolisé froide le moins cher
						
						//vendeurs loins :
						String[] vendeur = vendeurPlusLoin();
						
						//vendeurs boissons alcoolisées froides
						Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
						Iterator<Entry<String,Drink[]>> it = tmp.iterator();
						String[][] vendeur2=new String[tmp.size()][2];
						int i = 0;
						while(it.hasNext()){
							Entry<String,Drink[]> e = it.next();
							Drink[] drinks = e.getValue();
							e.getKey();
							for(Drink drink : drinks){
								if(drink.getPrice() != 0){
									if(drink.isHasAlcool() && drink.isCold()){
										vendeur2[i][0]=e.getKey();
									    vendeur2[i][1]=String.valueOf(drink.getPrice());
										i++;
									}
								}
							}
						}
						//vendeur moins cher
						String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
						if (moinsCher != null){
							vendeurFinal[0][0]=moinsCher;
							vendeurFinal[0][1]=alcF;
							return vendeurFinal;
						}
						else{
							return null;
						}
						
					}
					else if(rand<50){
						//aller chez la personne la plus loin, avec des boissons Alcoolisé chaude le moins cher
						
						//vendeurs loins :
						String[] vendeur = vendeurPlusLoin();
						
						//vendeurs boissons alcoolisées chaude
						Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
						Iterator<Entry<String,Drink[]>> it = tmp.iterator();
						String[][] vendeur2=new String[tmp.size()][2];
						int i = 0;
						while(it.hasNext()){
							Entry<String,Drink[]> e = it.next();
							Drink[] drinks = e.getValue();
							e.getKey();
							for(Drink drink : drinks){
								if(drink.getPrice() != 0){
									if(drink.isHasAlcool() && !drink.isCold()){
										vendeur2[i][0]=e.getKey();
									    vendeur2[i][1]=String.valueOf(drink.getPrice());
										i++;
									}
								}
							}
						}
						//vendeur moins cher
						String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
						if (moinsCher != null){
							vendeurFinal[0][0]=moinsCher;
							vendeurFinal[0][1]=alcC;
							return vendeurFinal;
						}
						else{
							return null;
						}
					}
				}
			}
			//********************** sA < sSA **********************
			else if(sensibiliteAlcool < sensibiliteSansAlcool){
				if(sensibiliteFroid > sensibiliteChaud){
					//aller chez la personne la plus loin, avec des boissons non Alcoolisé froide  le moins cher
					
					//vendeurs loins :
					String[] vendeur = vendeurPlusLoin();
					//vendeurs boissons non alcoolisées froids
					Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
					Iterator<Entry<String,Drink[]>> it = tmp.iterator();
					String[][] vendeur2=new String[tmp.size()+1][2];
					int i = 0;				
					while(it.hasNext()){
						Entry<String,Drink[]> e = it.next();
						Drink[] drinks = e.getValue();
						
						for(Drink drink : drinks){
							if(drink.getPrice() != 0){
								if(!drink.isHasAlcool() && drink.isCold()){
									vendeur2[i][0]=e.getKey();
								    vendeur2[i][1]=String.valueOf(drink.getPrice());
									i++;
								}
							}
						}
					}
					
					//vendeur moins cher
					String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
					if (moinsCher != null){
						vendeurFinal[0][0]=moinsCher;
						vendeurFinal[0][1]=NalcF;
						return vendeurFinal;
					}
					else{
						return null;
					}
				}
				
				else if(sensibiliteFroid < sensibiliteChaud){
					//aller chez la personne la plus loin, avec des boissons non Alcoolisé chaude le moins cher
					
					//vendeurs loins :
					String[] vendeur = vendeurPlusLoin();
					
					//vendeurs boissons non alcoolisées chaudes
					Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
					Iterator<Entry<String,Drink[]>> it = tmp.iterator();
					String[][] vendeur2=new String[tmp.size()][2];
					int i = 0;
					while(it.hasNext()){
						Entry<String,Drink[]> e = it.next();
						Drink[] drinks = e.getValue();
						e.getKey();
						for(Drink drink : drinks){
							if(drink.getPrice() != 0){
								if(!drink.isHasAlcool() && !drink.isCold()){
									vendeur2[i][0]=e.getKey();
								    vendeur2[i][1]=String.valueOf(drink.getPrice());
									i++;
								}
							}
						}
					}
					//vendeur loin + boisson non alcoolisées + moins cher
					String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
					if (moinsCher != null){
						vendeurFinal[0][0]=moinsCher;
						vendeurFinal[0][1]=NalcC;
						return vendeurFinal;
					}
					else{
						return null;
					}
				}
				
				else if(sensibiliteChaud == sensibiliteFroid){
					float rand = (float) Math.random();
					rand = rand*100;
					
					if(rand>=50){
						//aller chez la personne la plus loin, avec des boissons non Alcoolisé froide le moins cher
						
						//vendeurs loins :
						String[] vendeur = vendeurPlusLoin();
						
						//vendeurs boissons non alcoolisées froides
						Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
						Iterator<Entry<String,Drink[]>> it = tmp.iterator();
						String[][] vendeur2=new String[tmp.size()][2];
						int i = 0;
						while(it.hasNext()){
							Entry<String,Drink[]> e = it.next();
							Drink[] drinks = e.getValue();
							e.getKey();
							for(Drink drink : drinks){
								if(drink.getPrice() != 0){
									if(!drink.isHasAlcool() && drink.isCold()){
										vendeur2[i][0]=e.getKey();
									    vendeur2[i][1]=String.valueOf(drink.getPrice());
										i++;
									}
								}
							}
						}
						//vendeur moins cher
						String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
						if (moinsCher != null){
							vendeurFinal[0][0]=moinsCher;
							vendeurFinal[0][1]=NalcF;
							return vendeurFinal;
						}
						else{
							return null;
						}
						
					}
					else if(rand<50){
						//aller chez la personne la plus loin, avec des boissons non Alcoolisé chaude le moins cher
						
						//vendeurs loins :
						String[] vendeur = vendeurPlusLoin();
						
						//vendeurs boissons non alcoolisées chaude
						Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
						Iterator<Entry<String,Drink[]>> it = tmp.iterator();
						String[][] vendeur2=new String[tmp.size()][2];
						int i = 0;
						while(it.hasNext()){
							Entry<String,Drink[]> e = it.next();
							Drink[] drinks = e.getValue();
							e.getKey();
							for(Drink drink : drinks){
								if(drink.getPrice() != 0){
									if(!drink.isHasAlcool() && !drink.isCold()){
										vendeur2[i][0]=e.getKey();
									    vendeur2[i][1]=String.valueOf(drink.getPrice());
										i++;
									}
								}
							}
						}
						//vendeur moins cher
						String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
						if (moinsCher != null){
							vendeurFinal[0][0]=moinsCher;
							vendeurFinal[0][1]=NalcC;
							return vendeurFinal;
						}
						else{
							return null;
						}
					}
				}
			}
		}
		//**********Pas_Sensible_a_la_Pub_non_Deplacement***********
		else if(sensibilitePub<val){
			//********************* sA > sSA ***********************
			if (sensibiliteAlcool > sensibiliteSansAlcool){
				if(sensibiliteFroid > sensibiliteChaud){
					//aller chez la personne la moins loin, avec des boissons Alcoolisées froides le moins cher
					
					//vendeurs !loins :
					String[] vendeur = vendeurMoinsLoin();
					
					//vendeurs boissons alcoolisées froides
					Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
					Iterator<Entry<String,Drink[]>> it = tmp.iterator();
					String[][] vendeur2=new String[tmp.size()][2];
					int i = 0;
					while(it.hasNext()){
						Entry<String,Drink[]> e = it.next();
						Drink[] drinks = e.getValue();
						e.getKey();
						for(Drink drink : drinks){
							if(drink.getPrice() != 0){
								if(drink.isHasAlcool() && drink.isCold()){
									vendeur2[i][0]=e.getKey();
								    vendeur2[i][1]=String.valueOf(drink.getPrice());
									i++;
								}
							}
						}
					}
					
					//vendeur moins cher
					String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
					if (moinsCher != null){
						vendeurFinal[0][0]=moinsCher;
						vendeurFinal[0][1]=alcF;
						return vendeurFinal;
					}
					else{
						return null;
					}
				}
	
				else if(sensibiliteFroid < sensibiliteChaud){
					//aller chez la personne la moins loin, avec des boissons Alcoolisé chaude le moins cher
					
					//vendeurs !loins :
					String[] vendeur = vendeurMoinsLoin();
					
					//vendeurs boissons alcoolisées chaudes
					Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
					Iterator<Entry<String,Drink[]>> it = tmp.iterator();
					String[][] vendeur2=new String[tmp.size()][2];
					int i = 0;
					while(it.hasNext()){
						Entry<String,Drink[]> e = it.next();
						Drink[] drinks = e.getValue();
						e.getKey();
						for(Drink drink : drinks){
							if(drink.isHasAlcool() && !drink.isCold()){
								if(drink.getPrice() != 0){
									vendeur2[i][0]=e.getKey();
								    vendeur2[i][1]=String.valueOf(drink.getPrice());
									i++;
								}
							}
						}
					}
					
					//vendeur moins cher
					String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
					if (moinsCher != null){
						vendeurFinal[0][0]=moinsCher;
						vendeurFinal[0][1]=alcC;
						return vendeurFinal;
					}
					else{
						return null;
					}
				}
				else if(sensibiliteChaud == sensibiliteFroid){
					float rand = (float) Math.random();
					rand = rand*100;
					
					if(rand>=50){
						//aller chez la personne la moins loin, avec des boissons Alcoolisé froide le moins cher
						
						//vendeurs !loins :
						String[] vendeur = vendeurMoinsLoin();
						
						//vendeurs boissons alcoolisées froides
						Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
						Iterator<Entry<String,Drink[]>> it = tmp.iterator();
						String[][] vendeur2=new String[tmp.size()][2];
						int i = 0;
						while(it.hasNext()){
							Entry<String,Drink[]> e = it.next();
							Drink[] drinks = e.getValue();
							e.getKey();
							for(Drink drink : drinks){
								if(drink.isHasAlcool() && drink.isCold()){
									if(drink.getPrice() != 0){
										vendeur2[i][0]=e.getKey();
									    vendeur2[i][1]=String.valueOf(drink.getPrice());
										i++;
									}
								}
							}
						}
						//vendeur moins cher
						String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
							if (moinsCher != null){
							vendeurFinal[0][0]=moinsCher;
							vendeurFinal[0][1]=alcF;
							return vendeurFinal;
						}
						else{
							return null;
						}
					}
					else if(rand<50){
						//aller chez la personne la moins loin, avec des boissons Alcoolisé chaude le moins cher
						
						//vendeurs !loins :
						String[] vendeur = vendeurMoinsLoin();
						
						//vendeurs boissons alcoolisées chaude
						Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
						Iterator<Entry<String,Drink[]>> it = tmp.iterator();
						String[][] vendeur2=new String[tmp.size()][2];
						int i = 0;
						while(it.hasNext()){
							Entry<String,Drink[]> e = it.next();
							Drink[] drinks = e.getValue();
							e.getKey();
							for(Drink drink : drinks){
								if(drink.getPrice() != 0){
									if(drink.isHasAlcool() && !drink.isCold()){
										vendeur2[i][0]=e.getKey();
									    vendeur2[i][1]=String.valueOf(drink.getPrice());
										i++;
									}
								}
							}
						}
						//vendeur moins cher
						String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
						if (moinsCher != null){
							vendeurFinal[0][0]=moinsCher;
							vendeurFinal[0][1]=alcC;
							return vendeurFinal;
						}
						else{
							return null;
						}
					}
				}
			}
			//********************** sA < sSA **********************
			else if(sensibiliteAlcool < sensibiliteSansAlcool){
				if(sensibiliteFroid > sensibiliteChaud){
					//aller chez la personne la moins loin, avec des boissons non Alcoolisé froide  le moins cher
					
					//vendeurs !loins :
					String[] vendeur = vendeurMoinsLoin();
					
					//vendeurs boissons non alcoolisées froids
					Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
					Iterator<Entry<String,Drink[]>> it = tmp.iterator();
					String[][] vendeur2=new String[tmp.size()][2];
					int i = 0;
					while(it.hasNext()){
						Entry<String,Drink[]> e = it.next();
						Drink[] drinks = e.getValue();
						e.getKey();
						for(Drink drink : drinks){
							if(drink.getPrice() != 0){
								if(!drink.isHasAlcool() && drink.isCold()){
									vendeur2[i][0]=e.getKey();
								    vendeur2[i][1]=String.valueOf(drink.getPrice());
									i++;
								}
							}
						}
					}
					
					//vendeur moins cher
					String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
						if (moinsCher != null){
						vendeurFinal[0][0]=moinsCher;
						vendeurFinal[0][1]=NalcF;
						return vendeurFinal;
					}
					else{
						return null;
					}
				}
				
				else if(sensibiliteFroid < sensibiliteChaud){
					//aller chez la personne la moins loin, avec des boissons non Alcoolisé chaude le moins cher
					
					//vendeurs pas loins 
					String[] vendeur = vendeurMoinsLoin();
					
					//vendeurs boissons non alcoolisées chaudes
					Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
					Iterator<Entry<String,Drink[]>> it = tmp.iterator();
					String[][] vendeur2=new String[tmp.size()][2];
					int i = 0;
					while(it.hasNext()){
						Entry<String,Drink[]> e = it.next();
						Drink[] drinks = e.getValue();
						e.getKey();
						for(Drink drink : drinks){
							if(drink.getPrice() != 0){
								if(!drink.isHasAlcool() && !drink.isCold()){
									vendeur2[i][0]=e.getKey();
								    vendeur2[i][1]=String.valueOf(drink.getPrice());
									i++;
								}
							}
						}
					}
					//vendeur moins cher
					String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
					if (moinsCher != null){
						vendeurFinal[0][0]=moinsCher;
						vendeurFinal[0][1]=NalcC;
						return vendeurFinal;
					}
					else return null;
				}
				
				else if(sensibiliteChaud == sensibiliteFroid){
					float rand = (float) Math.random();
					rand = rand*100;
					
					if(rand>=50){
						//aller chez la personne la moins loin, avec des boissons non Alcoolisé froide le moins cher
						
						//vendeurs !loins :
						String[] vendeur = vendeurMoinsLoin();
						
						//vendeurs boissons non alcoolisées froides
						Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
						Iterator<Entry<String,Drink[]>> it = tmp.iterator();
						String[][] vendeur2=new String[tmp.size()][2];
						int i = 0;
						while(it.hasNext()){
							Entry<String,Drink[]> e = it.next();
							Drink[] drinks = e.getValue();
							e.getKey();
							for(Drink drink : drinks){
								if(drink.getPrice() != 0){
									if(!drink.isHasAlcool() && drink.isCold()){
										vendeur2[i][0]=e.getKey();
									    vendeur2[i][1]=String.valueOf(drink.getPrice());
										i++;
									}
								}
							}
						}
						//vendeur moins cher
						String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
						if (moinsCher != null){
							vendeurFinal[0][0]=moinsCher;
							vendeurFinal[0][1]=NalcF;
							return vendeurFinal;
						}
						else return null;
						
					}
					else if(rand<50){
						//aller chez la personne la moins loin, avec des boissons non Alcoolisé chaude le moins cher
						
						//vendeurs !loins :
						String[] vendeur = vendeurMoinsLoin();
						
						//vendeurs boissons non alcoolisées chaude
						Set<Entry<String,Drink[]>> tmp = donnéesJeu.getDrinkByPlayer().entrySet();
						Iterator<Entry<String,Drink[]>> it = tmp.iterator();
						String[][] vendeur2=new String[tmp.size()][2];
						int i = 0;
						while(it.hasNext()){
							Entry<String,Drink[]> e = it.next();
							Drink[] drinks = e.getValue();
							e.getKey();
							for(Drink drink : drinks){
								if(drink.getPrice() != 0){
									if(!drink.isHasAlcool() && !drink.isCold()){
										vendeur2[i][0]=e.getKey();
									    vendeur2[i][1]=String.valueOf(drink.getPrice());
										i++;
									}
								}
							}
						}
						//vendeur moins cher
						String moinsCher = vendeurMoinsCher(vendeur2,vendeur);
						if (moinsCher != null){
							vendeurFinal[0][0]=moinsCher;
							vendeurFinal[0][1]=NalcC;
							return vendeurFinal;
						}
						else return null;
					}
				}
			}
		}
		return vendeurFinal;
	}
	
	public float distanceTo(Coordinates c1, Coordinates c2){
		float x1,x2,y1,y2;
		x1 = c1.getLatitude();
		y1 = c1.getLongitude();
		x2 = c2.getLatitude();
		y2 = c2.getLongitude();
		
		float distance = (float) Math.sqrt(((x2-x1)*(x2-x1))+
				((y2-y1)*(y2-y1)));
		return distance;
	}
}
