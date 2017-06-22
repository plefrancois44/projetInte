DROP TABLE IF EXISTS Ingredient CASCADE;
DROP TABLE IF EXISTS Recette CASCADE;
DROP TABLE IF EXISTS Joueur CASCADE;
DROP TABLE IF EXISTS Meteo CASCADE;
DROP TABLE IF EXISTS Pub CASCADE;
DROP TABLE IF EXISTS composer CASCADE;
DROP TABLE IF EXISTS produire CASCADE;
DROP TABLE IF EXISTS vendre CASCADE;
DROP TABLE IF EXISTS Login CASCADE;

CREATE TABLE public.Ingredient(
        ing_nom           Varchar (50) NOT NULL ,
        ing_prix_unitaire Float ,
        ing_alcool        Bool NOT NULL,
        ing_froid         Bool NOT NULL,
        PRIMARY KEY (ing_nom )
);

CREATE TABLE public.Recette(
        rec_nom        Varchar (50) NOT NULL ,
        rec_cout_achat Float ,
        jou_nom        Varchar (25) ,
        PRIMARY KEY (rec_nom )
);

CREATE TABLE public.Joueur(
        jou_nom    Varchar (25) NOT NULL ,
        jou_budget Float ,
        jou_pos_x  Float ,
        jou_pos_y  Float ,
        jou_rayon  Float ,
		jou_actif Bool NOT NULL,
        PRIMARY KEY (jou_nom )
);

CREATE TABLE public.Meteo(
        met_jour            Date NOT NULL ,
        met_matin           Varchar (25) ,
        met_apres_midi      Varchar (25) ,
        met_prevu_lendemain Varchar (25) ,
        PRIMARY KEY (met_jour )
);

CREATE TABLE public.Pub(
        pub_jour  Date ,
        pub_pos_x Float NOT NULL ,
        pub_pos_y Float NOT NULL ,
        pub_rayon Float ,
        jou_nom   Varchar (25) ,
        PRIMARY KEY (pub_pos_x ,pub_pos_y )
);

CREATE TABLE public.composer(
        rec_nom Varchar (50) NOT NULL ,
        ing_nom Varchar (50) NOT NULL ,
        PRIMARY KEY (rec_nom ,ing_nom )
);

CREATE TABLE public.vendre(
        ven_jour     Date ,
        ven_quantite Int ,
        jou_nom      Varchar (25) NOT NULL ,
        rec_nom      Varchar (50) NOT NULL ,
        PRIMARY KEY (jou_nom ,rec_nom )
);

CREATE TABLE public.produire(
        pro_jour       Date ,
        pro_prix_vente Float ,
        pro_quantite   Int ,
        jou_nom        Varchar (25) NOT NULL ,
        rec_nom        Varchar (50) NOT NULL ,
        PRIMARY KEY (jou_nom ,rec_nom )
);

CREATE TABLE public.Compte (
  com_nom    VARCHAR,
  com_mot_de_passe VARCHAR,
  com_est_admin    BOOLEAN NOT NULL,
  PRIMARY KEY (com_nom)
);

ALTER TABLE Recette ADD CONSTRAINT FK_Recette_jou_nom FOREIGN KEY (jou_nom) REFERENCES Joueur(jou_nom);
ALTER TABLE Pub ADD CONSTRAINT FK_Pub_jou_nom FOREIGN KEY (jou_nom) REFERENCES Joueur(jou_nom);
ALTER TABLE composer ADD CONSTRAINT FK_composer_rec_nom FOREIGN KEY (rec_nom) REFERENCES Recette(rec_nom);
ALTER TABLE composer ADD CONSTRAINT FK_composer_ing_nom FOREIGN KEY (ing_nom) REFERENCES Ingredient(ing_nom);
ALTER TABLE vendre ADD CONSTRAINT FK_vendre_jou_nom FOREIGN KEY (jou_nom) REFERENCES Joueur(jou_nom);
ALTER TABLE vendre ADD CONSTRAINT FK_vendre_rec_nom FOREIGN KEY (rec_nom) REFERENCES Recette(rec_nom);
ALTER TABLE produire ADD CONSTRAINT FK_produire_jou_nom FOREIGN KEY (jou_nom) REFERENCES Joueur(jou_nom);
ALTER TABLE produire ADD CONSTRAINT FK_produire_rec_nom FOREIGN KEY (rec_nom) REFERENCES Recette(rec_nom);

INSERT INTO Compte (com_nom, com_mot_de_passe, com_est_admin) VALUES ('admin', 'admin', TRUE);