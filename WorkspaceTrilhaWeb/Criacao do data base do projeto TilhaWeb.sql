CREATE DATABASE IF NOT EXISTS bdcoldigo DEFAULT CHARACTER SET utf8 ;

CREATE TABLE IF NOT EXISTS marcas (
id INT UNSIGNED NOT NULL AUTO_INCREMENT,
nome VARCHAR(45) NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS produtos(
id INT(5) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT,
categoria TINYINT(1) UNSIGNED NOT NULL,
modelo VARCHAR(45) NOT NULL, 
capacidade INT(4) UNSIGNED NOT NULL,
valor DECIMAL(7,2) UNSIGNED NOT NULL,
marcas_id INT UNSIGNED NOT NULL,
PRIMARY KEY (id),
INDEX fk_produtos_marcas_idx (marcas_id ASC),
CONSTRAINT fk_produtos_marcas
	FOREIGN KEY (marcas_id)
    REFERENCES marcas (id)
);

insert into marcas (id, nome) VALUES ('1', 'Consul'), ('2','Brastemp'), ('3','Samsung');
