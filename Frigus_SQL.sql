/*

--Criação das tabelas
CREATE TABLE Usuario(
    cpf         varchar(11)  primary key,
    nome        varchar(20)  not null,
    Sobrenome   varchar(60)  not null,
    email       varchar(320) not null,
    senha       varchar(16)  not null,
    telefone    varchar(14)      null
);

CREATE TABLE Empresa(
    cnpj        varchar(14)  primary key,
    nome        varchar(80)  not null,
    email       varchar(320) not null,
    senha       varchar(16)  not null,
    descricao   text             null
);

CREATE TABLE Funcionario(
    cpf         varchar(11)  primary key,
    idEmpresa   varchar(14)  not null,
    nome        varchar(20)  not null,
    Sobrenome   varchar(60)  not null,
    cargo       varchar(50)  not null,
    email       varchar(320) not null,
    senha       varchar(16)  not null,
    telefone    varchar(14)
);

CREATE TABLE MarcasParceiras(
    id              serial      primary key,
    idEmpresa       varchar(14) not null,
    idProdutos      int         not null,
    observacoes     text            null
);

CREATE TABLE Assinatura(
    id              serial          primary key,
    idUsuario       varchar(11)         null,
    idEmpresa       varchar(14)         null,
    tipoAssinatura  varchar(50)     not null,
    valor           numeric(10,2)   not null default 0,
    descricao       text                null
);


CREATE TABLE Receita(
    id            serial      primary key,
    idEmpresa     varchar(14)     null,    
    idUsuario     varchar(11)     null,
    nome          varchar(20) not null,
    descricao     text            null,
    modoPreparo   text        not null,
    dtReceita     date        not null default current_date,
    foiFeita      boolean     not null default false
);

CREATE TABLE Ingredientes(
    id            serial      primary key,
    idReceita     int         not null,
    idProdutos    int         not null,
    quantidade    int         not null default 1
);

CREATE TABLE ListaCompras(
    id          serial primary key,
    idUsuario   varchar(11)       null,
    idEmpresa   varchar(14)       null,
    dtLista     date          not null default current_date
);

CREATE TABLE ProdutoLista(
    id              serial primary key,
    idListaCompras  int    not null,
    idProdutos      int    not null,
    quantidade      int    not null
);

CREATE TABLE Produtos(
    id              serial         primary key,
    nome            varchar(20)    not null,
    descricao       text               null,
    preco           numeric(10, 2) not null default 0,
    categoria       varchar(50)    not null,
    marca           varchar(20)    not null,
    unidadeMedida   varchar(10)    not null
);

CREATE TABLE Frigorificos(
    id              serial      primary key,
    idEndereco      int         not null,
    idEmpresa       varchar(14) not null,
    quantidade      int         not null,
    status          varchar(20) not null default 'OCUPACAO_MODERADA',
    tipo            varchar(20) not null default 'Freezer'
);

CREATE TABLE ProdutosFrigorifico(
    id                serial      primary key,
    idNotificacao     int         not null,
    idFrigorificos    int         not null,
    idProdutos        int         not null,
    dtValidade        date        not null,
    status            varchar(20) not null default 'REGULAR',
    lote              int             null,
    quantidade        int         not null default 0
);

CREATE TABLE Compra(
    id              serial         primary key,
    idUsuario       varchar(11)        null,
    idEmpresa       varchar(14)        null,
    OndeComprou     varchar(250)   not null,
    dtCompra        date           not null,
    ValorCompra     numeric(10, 2) not null
);

CREATE TABLE Geladeira(
    id             serial      primary key,
    idUsuario      varchar(11) not null,
    status         varchar(20) not null default 'OCUPACAO_MODERADA',
    quantidade     int         not null default 0,
    tipo           varchar(20)     null default 'Geladeira'
);

CREATE TABLE ProdutosGeladeira(
    id                serial      primary key,
    idNotificacao     int         not null,
    idGeladeira       int         not null,
    idProdutos        int         not null,
    dtValidade        date        not null,
    status            varchar(20) not null default 'REGULAR',
    lote              int             null,
    quantidade        int         not null default 0

);

CREATE TABLE Notificacao(
    id              serial primary key,
    diasAviso       varchar(10) ,
    horarioAviso    time    default '08:00:00'
);

CREATE TABLE Endereco(
		id          serial           primary key,
		rua         varchar(150)     not null,
		bairro      varchar(150)     not null,
		cidade      varchar(150)     not null default 'São Paulo',
		uf          char(2)          not null default 'SP',
		pais        varchar(150)     not null default 'Brasil',
		cep         varchar(9)       not null,
		numero      varchar(20)      not null,
		nomeUnidade varchar(50)      not null
);







--Ligação entre tabelas e Checks
alter table Funcionario
    add constraint fk_funcionario_empresa
    foreign key (idEmpresa) references Empresa(cnpj)
    on delete cascade
    on update cascade;

alter table Geladeira
    add constraint fk_geladeira_usuario
    foreign key (idUsuario) references Usuario(cpf)
    on delete cascade
    on update cascade;

alter table Frigorificos
    add constraint fk_frigorificos_empresa
    foreign key (idEmpresa) references Empresa(cnpj)
    on delete cascade
    on update cascade;

ALTER TABLE frigorificos
ADD CONSTRAINT fk_frigorificos_endereco
FOREIGN KEY (idEndereco)
REFERENCES endereco(id)
ON UPDATE CASCADE;

alter table ProdutosGeladeira
    add constraint fk_prodgeladeira_geladeira
    foreign key (idGeladeira) references Geladeira(id)
    on delete cascade
    on update cascade,
    add constraint fk_prodgeladeira_produtos
    foreign key (idProdutos) references Produtos(id)
    on delete cascade
    on update cascade;

alter table ProdutosFrigorifico
    add constraint fk_prodfrigorifico_frigorificos
    foreign key (idFrigorificos) references Frigorificos(id)
    on delete cascade
    on update cascade,
    add constraint fk_prodfrigorifico_produtos
    foreign key (idProdutos) references Produtos(id)
    on delete cascade
    on update cascade;

alter table Receita
    add constraint fk_receita_empresa
    foreign key (idEmpresa) references Empresa(cnpj)
    on delete cascade
    on update cascade,
    add constraint fk_receita_usuario
    foreign key (idUsuario) references Usuario(cpf)
    on delete cascade
    on update cascade,
    add constraint chk_dono_receita
    check (
        (idUsuario is not null and idEmpresa is null) or
        (idUsuario is null and idEmpresa is not null)
    );

alter table Ingredientes
    add constraint fk_ingredientes_receita
    foreign key (idReceita) references Receita(id)
    on delete cascade
    on update cascade,
    add constraint fk_ingredientes_produtos
    foreign key (idProdutos) references Produtos(id)
    on delete cascade
    on update cascade;

alter table Assinatura
    add constraint fk_assinatura_usuario
    foreign key (idUsuario) references Usuario(cpf)
    on delete cascade
    on update cascade,
    add constraint fk_assinatura_empresa
    foreign key (idEmpresa) references Empresa(cnpj)
    on delete cascade
    on update cascade,
    add constraint chk_dono_assinatura
    check (
        (idUsuario is not null and idEmpresa is null) or
        (idUsuario is null and idEmpresa is not null)
    );
    CREATE UNIQUE INDEX uq_assinatura_usuario
    ON Assinatura(idUsuario)
    WHERE idUsuario IS NOT NULL;

    CREATE UNIQUE INDEX uq_assinatura_empresa
    ON Assinatura(idEmpresa)
    WHERE idEmpresa IS NOT NULL;

alter table Compra
    add constraint fk_compra_usuario
    foreign key (idUsuario) references Usuario(cpf)
    on delete cascade
    on update cascade,
    add constraint fk_compra_empresa
    foreign key (idEmpresa) references Empresa(cnpj)
    on delete cascade
    on update cascade,
    add constraint chk_dono_compra
    check (
        (idUsuario is not null and idEmpresa is null) or
        (idUsuario is null and idEmpresa is not null)
    );

alter table ListaCompras
    add constraint fk_listacompras_usuario
    foreign key (idUsuario) references Usuario(cpf)
    on delete cascade
    on update cascade,
    add constraint fk_listacompras_empresa
    foreign key (idEmpresa) references Empresa(cnpj)
    on delete cascade
    on update cascade,
    add constraint chk_dono_listacompras
    check (
        (idUsuario is not null and idEmpresa is null) or
        (idUsuario is null and idEmpresa is not null)
    );

alter table ProdutoLista
    add constraint fk_produtolista_listacompras
    foreign key (idListaCompras) references ListaCompras(id)
    on delete cascade
    on update cascade,
    add constraint fk_produtolista_produtos
    foreign key (idProdutos) references Produtos(id)
    on delete cascade
    on update cascade;

alter table MarcasParceiras
    add constraint fk_marcasparceiras_empresa
    foreign key (idEmpresa) references Empresa(cnpj)
    on delete cascade
    on update cascade,
    add constraint fk_marcasparceiras_produtos
    foreign key (idProdutos) references Produtos(id)
    on delete cascade
    on update cascade;

alter table Usuario
    add constraint chk_cpf_formato
    check (cpf ~ '^[0-9]{11}$');


alter table Empresa
    add constraint chk_cnpj_formato
    check (cnpj ~ '^[A-Z0-9]{12}[0-9]{2}$');


alter table Usuario
	add constraint chk_email_formato
    check (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');
    

alter table Empresa
	add constraint chk_email_formato
    check (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');


alter table Geladeira
    add constraint chk_quantidade_geladeira
    check (quantidade >= 0);


alter table Frigorificos
    add constraint chk_quantidade_frigorificos
    check (quantidade >= 0);


alter table Ingredientes
    add constraint chk_quantidade_ingredientes
    check (quantidade >= 0);


alter table ProdutoLista
    add constraint chk_quantidade_produtolista
    check (quantidade >= 0);
    
alter table ProdutosGeladeira
    add constraint chk_quantidade_produtolista
    check (quantidade >= 0);
    
alter table ProdutosFrigorifico
    add constraint chk_quantidade_produtolista
    check (quantidade >= 0);

alter table Usuario
	add constraint chk_telefone_formato
    check (telefone ~ '^\(?[1-9]{2}\)?\s?(?:9[0-9]{4}|[2-8][0-9]{3})\-?[0-9]{4}$');

alter table Endereco
	add constraint chk_cep_formato
    check (cep ~ '^\d{5}-?\d{3}$');






-- ============================================================================
--                              DATALOAD Frigus
-- ============================================================================

-- Usuario

INSERT INTO Usuario (cpf, nome, Sobrenome, email, senha, telefone) VALUES
('61651728658', 'Ana', 'Rodrigues', 'ana.rodrigues@gmail.com', 'Senha@100', '81907429263'),
('03186265940', 'Bruno', 'Gomes', 'bruno.gomes@gmail.com', 'Senha@101', '48905019909'),
('89632247248', 'Carla', 'Barbosa', 'carla.barbosa@gmail.com', 'Senha@102', '62908591449'),
('78537787671', 'Diego', 'Nascimento', 'diego.nascimento@gmail.com', 'Senha@103', '21983446125'),
('61988335000', 'Elaine', 'Cardoso', 'elaine.cardoso@gmail.com', 'Senha@104', '85909351989'),
('76869458200', 'Fabio', 'Cavalcanti', 'fabio.cavalcanti@gmail.com', 'Senha@105', '81901737613'),
('50370395093', 'Gabriela', 'Nunes', 'gabriela.nunes@gmail.com', 'Senha@106', '85961058133'),
('09242380385', 'Heitor', 'Freitas', 'heitor.freitas@gmail.com', 'Senha@107', '41916952821'),
('87511029191', 'Isabela', 'Vieira', 'isabela.vieira@gmail.com', 'Senha@108', '92928610665'),
('20718728351', 'Joao', 'Batista', 'joao.batista@gmail.com', 'Senha@109', '51992605249'),
('93816662293', 'Karina', 'Xavier', 'karina.xavier@gmail.com', 'Senha@110', '31977387743'),
('54062767864', 'Lucas', 'Guimaraes', 'lucas.guimaraes@gmail.com', 'Senha@111', '85983424050'),
('52263905480', 'Marina', 'Peixoto', 'marina.peixoto@gmail.com', 'Senha@112', '21975359089'),
('74299060601', 'Nicolas', 'Brito', 'nicolas.brito@gmail.com', 'Senha@113', '71906569876'),
('80340669900', 'Otavio', 'Braga', 'otavio.braga@gmail.com', 'Senha@114', '61996353667'),
('14786469424', 'Patricia', 'Costa', 'patricia.costa@gmail.com', 'Senha@115', '62910218629'),
('56858215735', 'Rafael', 'Pereira', 'rafael.pereira@gmail.com', 'Senha@116', '62978414217'),
('76315414427', 'Sofia', 'Almeida', 'sofia.almeida@gmail.com', 'Senha@117', '41975583187'),
('66139532884', 'Thiago', 'Ribeiro', 'thiago.ribeiro@gmail.com', 'Senha@118', '85920684168'),
('05313798332', 'Vanessa', 'Rocha', 'vanessa.rocha@gmail.com', 'Senha@119', '91923723359'),
('10769242987', 'Aline', 'Araujo', 'aline.araujo@gmail.com', 'Senha@120', '48992777568'),
('88293486945', 'Bernardo', 'Teixeira', 'bernardo.teixeira@gmail.com', 'Senha@121', '91968430779'),
('26951681520', 'Camila', 'Monteiro', 'camila.monteiro@gmail.com', 'Senha@122', '85947849086'),
('77726406500', 'Daniel', 'Marques', 'daniel.marques@gmail.com', 'Senha@123', '71976599511'),
('07658178535', 'Eduarda', 'Pinto', 'eduarda.pinto@gmail.com', 'Senha@124', '81929436483'),
('30035336579', 'Felipe', 'Tavares', 'felipe.tavares@gmail.com', 'Senha@125', '85917280569'),
('07464396790', 'Gustavo', 'Cunha', 'gustavo.cunha@gmail.com', 'Senha@126', '21958813998'),
('01187309672', 'Helena', 'Sales', 'helena.sales@gmail.com', 'Senha@127', '21971056262'),
('79643758656', 'Igor', 'Neves', 'igor.neves@gmail.com', 'Senha@128', '61920554203'),
('94982888604', 'Julia', 'Bezerra', 'julia.bezerra@gmail.com', 'Senha@129', '62977226882'),
('06923786185', 'Kevin', 'Coelho', 'kevin.coelho@gmail.com', 'Senha@130', '51965807766'),
('98005505426', 'Laura', 'Silva', 'laura.silva@gmail.com', 'Senha@131', '47925547793'),
('68625374310', 'Marcelo', 'Santos', 'marcelo.santos@gmail.com', 'Senha@132', '91909717322'),
('34711306676', 'Natalia', 'Ferreira', 'natalia.ferreira@gmail.com', 'Senha@133', '71921698365'),
('01116136392', 'Osvaldo', 'Lima', 'osvaldo.lima@gmail.com', 'Senha@134', '21955890814'),
('72421060877', 'Priscila', 'Carvalho', 'priscila.carvalho@gmail.com', 'Senha@135', '91942107499'),
('61101572019', 'Ricardo', 'Dias', 'ricardo.dias@gmail.com', 'Senha@136', '92909996028'),
('17710054919', 'Sabrina', 'Melo', 'sabrina.melo@gmail.com', 'Senha@137', '51980472061'),
('11421570750', 'Tiago', 'Correia', 'tiago.correia@gmail.com', 'Senha@138', '21971188903'),
('23103612249', 'Ursula', 'Moreira', 'ursula.moreira@gmail.com', 'Senha@139', '47988608449'),
('42935404904', 'Vitor', 'Machado', 'vitor.machado@gmail.com', 'Senha@140', '71985977685'),
('72760313140', 'Wagner', 'Ramos', 'wagner.ramos@gmail.com', 'Senha@141', '11979427318'),
('32505476462', 'Ximena', 'Fonseca', 'ximena.fonseca@gmail.com', 'Senha@142', '65959382416'),
('95573305468', 'Yara', 'Farias', 'yara.farias@gmail.com', 'Senha@143', '81993988627'),
('35779032289', 'Zeca', 'Duarte', 'zeca.duarte@gmail.com', 'Senha@144', '92915184780'),
('06771725758', 'Amanda', 'Pacheco', 'amanda.pacheco@gmail.com', 'Senha@145', '92926093146'),
('79715532004', 'Breno', 'Amaral', 'breno.amaral@gmail.com', 'Senha@146', '85965643172'),
('14647672549', 'Cintia', 'Assis', 'cintia.assis@gmail.com', 'Senha@147', '47937260670'),
('44230143605', 'Douglas', 'Souza', 'douglas.souza@gmail.com', 'Senha@148', '51978685350'),
('97037584959', 'Estela', 'Oliveira', 'estela.oliveira@gmail.com', 'Senha@149', '85941969690');


-- Empresa

INSERT INTO Empresa (cnpj, nome, email, senha, descricao) VALUES
('24144297000127', 'Laticinios Vale Verde LTDA', 'contato@valeverde.com.br', 'Emp@100', 'Empresa do ramo alimenticio especializada em laticinios, atuante desde 2000'),
('47253455000182', 'Frigorifico Boa Vista EIRELI', 'contato@boavista.com.br', 'Emp@101', 'Empresa do ramo alimenticio especializada em frigorifico, atuante desde 2001'),
('36006970000141', 'Distribuidora Sabor Real ME', 'contato@saborreal.com.br', 'Emp@102', 'Empresa do ramo alimenticio especializada em distribuidora, atuante desde 2002'),
('87349005000102', 'Empório Terra Nova S.A.', 'contato@terranova.com.br', 'Emp@103', 'Empresa do ramo alimenticio especializada em empório, atuante desde 2003'),
('77310109000150', 'Atacado Bom Preco LTDA', 'contato@bompreco.com.br', 'Emp@104', 'Empresa do ramo alimenticio especializada em atacado, atuante desde 2004'),
('06573130000108', 'Mercado Estrela do Sul EIRELI', 'contato@estreladosul.com.br', 'Emp@105', 'Empresa do ramo alimenticio especializada em mercado, atuante desde 2005'),
('81948539000179', 'Fazenda Rio Doce ME', 'contato@riodoce.com.br', 'Emp@106', 'Empresa do ramo alimenticio especializada em fazenda, atuante desde 2006'),
('56876315000151', 'Agro Santa Fe S.A.', 'contato@santafe.com.br', 'Emp@107', 'Empresa do ramo alimenticio especializada em agro, atuante desde 2007'),
('47026119000105', 'Nutri Horizonte LTDA', 'contato@horizonte.com.br', 'Emp@108', 'Empresa do ramo alimenticio especializada em nutri, atuante desde 2008'),
('79516470000162', 'Rede Campo Belo EIRELI', 'contato@campobelo.com.br', 'Emp@109', 'Empresa do ramo alimenticio especializada em rede, atuante desde 2009'),
('85589026000170', 'Comercial Serra Azul ME', 'contato@serraazul.com.br', 'Emp@110', 'Empresa do ramo alimenticio especializada em comercial, atuante desde 2010'),
('39829214000100', 'Granja Porto Seguro S.A.', 'contato@portoseguro.com.br', 'Emp@111', 'Empresa do ramo alimenticio especializada em granja, atuante desde 2011'),
('43734144000184', 'Casa do Alvorada LTDA', 'contato@alvorada.com.br', 'Emp@112', 'Empresa do ramo alimenticio especializada em casa do, atuante desde 2012'),
('70022229000102', 'Ponto do Pantanal EIRELI', 'contato@pantanal.com.br', 'Emp@113', 'Empresa do ramo alimenticio especializada em ponto do, atuante desde 2013'),
('18315670000162', 'Central de Cerrado ME', 'contato@cerrado.com.br', 'Emp@114', 'Empresa do ramo alimenticio especializada em central de, atuante desde 2014'),
('94007678000140', 'Laticinios Litoral S.A.', 'contato@litoral.com.br', 'Emp@115', 'Empresa do ramo alimenticio especializada em laticinios, atuante desde 2015'),
('40613964000120', 'Frigorifico Planalto LTDA', 'contato@planalto.com.br', 'Emp@116', 'Empresa do ramo alimenticio especializada em frigorifico, atuante desde 2016'),
('60898058000126', 'Distribuidora Aurora EIRELI', 'contato@aurora.com.br', 'Emp@117', 'Empresa do ramo alimenticio especializada em distribuidora, atuante desde 2017'),
('62402210000163', 'Empório Nova Era ME', 'contato@novaera.com.br', 'Emp@118', 'Empresa do ramo alimenticio especializada em empório, atuante desde 2018'),
('56281715000114', 'Atacado Vale do Sol S.A.', 'contato@valedosol.com.br', 'Emp@119', 'Empresa do ramo alimenticio especializada em atacado, atuante desde 2019'),
('67436371000191', 'Mercado Recanto LTDA', 'contato@recanto.com.br', 'Emp@120', 'Empresa do ramo alimenticio especializada em mercado, atuante desde 2020'),
('91004404000109', 'Fazenda Formosa EIRELI', 'contato@formosa.com.br', 'Emp@121', 'Empresa do ramo alimenticio especializada em fazenda, atuante desde 2021'),
('72264236000146', 'Agro Sao Francisco ME', 'contato@saofrancisco.com.br', 'Emp@122', 'Empresa do ramo alimenticio especializada em agro, atuante desde 2022'),
('96706343000145', 'Nutri Bela Vista S.A.', 'contato@belavista.com.br', 'Emp@123', 'Empresa do ramo alimenticio especializada em nutri, atuante desde 2023'),
('21473877000170', 'Rede Trevo LTDA', 'contato@trevo.com.br', 'Emp@124', 'Empresa do ramo alimenticio especializada em rede, atuante desde 2000'),
('08438764000175', 'Comercial Colina Verde EIRELI', 'contato@colinaverde.com.br', 'Emp@125', 'Empresa do ramo alimenticio especializada em comercial, atuante desde 2001'),
('82635120000120', 'Granja Rota Norte ME', 'contato@rotanorte.com.br', 'Emp@126', 'Empresa do ramo alimenticio especializada em granja, atuante desde 2002'),
('26012776000115', 'Casa do Costa Verde S.A.', 'contato@costaverde.com.br', 'Emp@127', 'Empresa do ramo alimenticio especializada em casa do, atuante desde 2003'),
('33665954000172', 'Ponto do Rio Claro LTDA', 'contato@rioclaro.com.br', 'Emp@128', 'Empresa do ramo alimenticio especializada em ponto do, atuante desde 2004'),
('82242871000186', 'Central de Lagoa Azul EIRELI', 'contato@lagoaazul.com.br', 'Emp@129', 'Empresa do ramo alimenticio especializada em central de, atuante desde 2005'),
('34910575000163', 'Laticinios Vitoria Regia ME', 'contato@vitoriaregia.com.br', 'Emp@130', 'Empresa do ramo alimenticio especializada em laticinios, atuante desde 2006'),
('66304631000102', 'Frigorifico Sul Bahia S.A.', 'contato@sulbahia.com.br', 'Emp@131', 'Empresa do ramo alimenticio especializada em frigorifico, atuante desde 2007'),
('22514034000137', 'Distribuidora Guanabara LTDA', 'contato@guanabara.com.br', 'Emp@132', 'Empresa do ramo alimenticio especializada em distribuidora, atuante desde 2008'),
('96116540000104', 'Empório Mantiqueira EIRELI', 'contato@mantiqueira.com.br', 'Emp@133', 'Empresa do ramo alimenticio especializada em empório, atuante desde 2009'),
('95405520000190', 'Atacado Pampa ME', 'contato@pampa.com.br', 'Emp@134', 'Empresa do ramo alimenticio especializada em atacado, atuante desde 2010'),
('85416602000187', 'Mercado Amazonia S.A.', 'contato@amazonia.com.br', 'Emp@135', 'Empresa do ramo alimenticio especializada em mercado, atuante desde 2011'),
('41182220000160', 'Fazenda Cordilheira LTDA', 'contato@cordilheira.com.br', 'Emp@136', 'Empresa do ramo alimenticio especializada em fazenda, atuante desde 2012'),
('61134330000164', 'Agro Vale Fertil EIRELI', 'contato@valefertil.com.br', 'Emp@137', 'Empresa do ramo alimenticio especializada em agro, atuante desde 2013'),
('21805824000109', 'Nutri Nascente ME', 'contato@nascente.com.br', 'Emp@138', 'Empresa do ramo alimenticio especializada em nutri, atuante desde 2014'),
('03853857000142', 'Rede Renascer S.A.', 'contato@renascer.com.br', 'Emp@139', 'Empresa do ramo alimenticio especializada em rede, atuante desde 2015'),
('93034110000155', 'Comercial Boa Safra LTDA', 'contato@boasafra.com.br', 'Emp@140', 'Empresa do ramo alimenticio especializada em comercial, atuante desde 2016'),
('00691117000169', 'Granja Terra Firme EIRELI', 'contato@terrafirme.com.br', 'Emp@141', 'Empresa do ramo alimenticio especializada em granja, atuante desde 2017'),
('73758788000173', 'Casa do Maré Alta ME', 'contato@marealta.com.br', 'Emp@142', 'Empresa do ramo alimenticio especializada em casa do, atuante desde 2018'),
('86598366000120', 'Ponto do Sabor da Terra S.A.', 'contato@sabordaterra.com.br', 'Emp@143', 'Empresa do ramo alimenticio especializada em ponto do, atuante desde 2019'),
('69366710000136', 'Central de Bom Sucesso LTDA', 'contato@bomsucesso.com.br', 'Emp@144', 'Empresa do ramo alimenticio especializada em central de, atuante desde 2020'),
('42839744000144', 'Laticinios Vale Dourado EIRELI', 'contato@valedourado.com.br', 'Emp@145', 'Empresa do ramo alimenticio especializada em laticinios, atuante desde 2021'),
('05122832000102', 'Frigorifico Estrela Guia ME', 'contato@estrelaguia.com.br', 'Emp@146', 'Empresa do ramo alimenticio especializada em frigorifico, atuante desde 2022'),
('59649891000154', 'Distribuidora Raiz Forte S.A.', 'contato@raizforte.com.br', 'Emp@147', 'Empresa do ramo alimenticio especializada em distribuidora, atuante desde 2023'),
('23628639000111', 'Empório Novo Rumo LTDA', 'contato@novorumo.com.br', 'Emp@148', 'Empresa do ramo alimenticio especializada em empório, atuante desde 2000'),
('78859395000170', 'Atacado Sol Nascente EIRELI', 'contato@solnascente.com.br', 'Emp@149', 'Empresa do ramo alimenticio especializada em atacado, atuante desde 2001');


-- Funcionario

INSERT INTO Funcionario (cpf, idEmpresa, nome, Sobrenome, cargo, email, senha, telefone) VALUES
('66738756151', '24144297000127', 'Heitor', 'Brito', 'Gerente de Estoque', 'heitor.brito@laticinios.com.br', 'Func@100', '11956718608'),
('62637779314', '47253455000182', 'Isabela', 'Braga', 'Atendente', 'isabela.braga@frigorifico.com.br', 'Func@101', '11969810293'),
('67103901449', '36006970000141', 'Joao', 'Costa', 'Motorista', 'joao.costa@distribuidora.com.br', 'Func@102', '11900967419'),
('33201894680', '87349005000102', 'Karina', 'Pereira', 'Comprador', 'karina.pereira@empório.com.br', 'Func@103', '11964869978'),
('75780607087', '77310109000150', 'Lucas', 'Almeida', 'Analista de Qualidade', 'lucas.almeida@atacado.com.br', 'Func@104', '11945992740'),
('39912409981', '06573130000108', 'Marina', 'Ribeiro', 'Supervisor', 'marina.ribeiro@mercado.com.br', 'Func@105', '11930377542'),
('83521906280', '81948539000179', 'Nicolas', 'Rocha', 'Auxiliar de Deposito', 'nicolas.rocha@fazenda.com.br', 'Func@106', '11921822589'),
('24226069382', '56876315000151', 'Otavio', 'Araujo', 'Nutricionista', 'otavio.araujo@agro.com.br', 'Func@107', '11959856183'),
('81451076797', '47026119000105', 'Patricia', 'Teixeira', 'Vendedor', 'patricia.teixeira@nutri.com.br', 'Func@108', '11992435096'),
('70041964292', '79516470000162', 'Rafael', 'Monteiro', 'Coordenador', 'rafael.monteiro@rede.com.br', 'Func@109', '11938853451'),
('24840662959', '85589026000170', 'Sofia', 'Marques', 'Gerente de Estoque', 'sofia.marques@comercial.com.br', 'Func@110', '11907393571'),
('06237315250', '39829214000100', 'Thiago', 'Pinto', 'Atendente', 'thiago.pinto@granja.com.br', 'Func@111', '11982811730'),
('29430110888', '43734144000184', 'Vanessa', 'Tavares', 'Motorista', 'vanessa.tavares@casa.com.br', 'Func@112', '11915899901'),
('05754758383', '70022229000102', 'Aline', 'Cunha', 'Comprador', 'aline.cunha@ponto.com.br', 'Func@113', '11987967857'),
('96999226059', '18315670000162', 'Bernardo', 'Sales', 'Analista de Qualidade', 'bernardo.sales@central.com.br', 'Func@114', '11936488379'),
('97402560236', '94007678000140', 'Camila', 'Neves', 'Supervisor', 'camila.neves@laticinios.com.br', 'Func@115', '11970610175'),
('35293610627', '40613964000120', 'Daniel', 'Bezerra', 'Auxiliar de Deposito', 'daniel.bezerra@frigorifico.com.br', 'Func@116', '11984560185'),
('55631414546', '60898058000126', 'Eduarda', 'Coelho', 'Nutricionista', 'eduarda.coelho@distribuidora.com.br', 'Func@117', '11931650204'),
('61868718867', '62402210000163', 'Felipe', 'Silva', 'Vendedor', 'felipe.silva@empório.com.br', 'Func@118', '11934802026'),
('47513786941', '56281715000114', 'Gustavo', 'Santos', 'Coordenador', 'gustavo.santos@atacado.com.br', 'Func@119', '11980769880'),
('33648781375', '67436371000191', 'Helena', 'Ferreira', 'Gerente de Estoque', 'helena.ferreira@mercado.com.br', 'Func@120', '11998949109'),
('05400110886', '91004404000109', 'Igor', 'Lima', 'Atendente', 'igor.lima@fazenda.com.br', 'Func@121', '11908343596'),
('51463473524', '72264236000146', 'Julia', 'Carvalho', 'Motorista', 'julia.carvalho@agro.com.br', 'Func@122', '11950408531'),
('77019159664', '96706343000145', 'Kevin', 'Dias', 'Comprador', 'kevin.dias@nutri.com.br', 'Func@123', '11958645784'),
('50641767919', '21473877000170', 'Laura', 'Melo', 'Analista de Qualidade', 'laura.melo@rede.com.br', 'Func@124', '11966104827'),
('24978734371', '08438764000175', 'Marcelo', 'Correia', 'Supervisor', 'marcelo.correia@comercial.com.br', 'Func@125', '11917305599'),
('06522153602', '82635120000120', 'Natalia', 'Moreira', 'Auxiliar de Deposito', 'natalia.moreira@granja.com.br', 'Func@126', '11988565816'),
('08296306352', '26012776000115', 'Osvaldo', 'Machado', 'Nutricionista', 'osvaldo.machado@casa.com.br', 'Func@127', '11954082670'),
('47679042500', '33665954000172', 'Priscila', 'Ramos', 'Vendedor', 'priscila.ramos@ponto.com.br', 'Func@128', '11953122886'),
('68537480002', '82242871000186', 'Ricardo', 'Fonseca', 'Coordenador', 'ricardo.fonseca@central.com.br', 'Func@129', '11977473294'),
('78123449089', '34910575000163', 'Sabrina', 'Farias', 'Gerente de Estoque', 'sabrina.farias@laticinios.com.br', 'Func@130', '11998361614'),
('87999827304', '66304631000102', 'Tiago', 'Duarte', 'Atendente', 'tiago.duarte@frigorifico.com.br', 'Func@131', '11905753199'),
('98263203406', '22514034000137', 'Ursula', 'Pacheco', 'Motorista', 'ursula.pacheco@distribuidora.com.br', 'Func@132', '11968599109'),
('95160465685', '96116540000104', 'Vitor', 'Amaral', 'Comprador', 'vitor.amaral@empório.com.br', 'Func@133', '11949982488'),
('50100940374', '95405520000190', 'Wagner', 'Assis', 'Analista de Qualidade', 'wagner.assis@atacado.com.br', 'Func@134', '11907441820'),
('42060103231', '85416602000187', 'Ximena', 'Souza', 'Supervisor', 'ximena.souza@mercado.com.br', 'Func@135', '11940635768'),
('09015542198', '41182220000160', 'Yara', 'Oliveira', 'Auxiliar de Deposito', 'yara.oliveira@fazenda.com.br', 'Func@136', '11921308492'),
('97790969064', '61134330000164', 'Zeca', 'Rodrigues', 'Nutricionista', 'zeca.rodrigues@agro.com.br', 'Func@137', '11963979677'),
('45232329707', '21805824000109', 'Amanda', 'Gomes', 'Vendedor', 'amanda.gomes@nutri.com.br', 'Func@138', '11976247399'),
('35642131125', '03853857000142', 'Breno', 'Barbosa', 'Coordenador', 'breno.barbosa@rede.com.br', 'Func@139', '11901542908'),
('15889956663', '93034110000155', 'Cintia', 'Nascimento', 'Gerente de Estoque', 'cintia.nascimento@comercial.com.br', 'Func@140', '11961583991'),
('88525364932', '00691117000169', 'Douglas', 'Cardoso', 'Atendente', 'douglas.cardoso@granja.com.br', 'Func@141', '11968943427'),
('90892209682', '73758788000173', 'Estela', 'Cavalcanti', 'Motorista', 'estela.cavalcanti@casa.com.br', 'Func@142', '11980217054'),
('68975161471', '86598366000120', 'Ana', 'Nunes', 'Comprador', 'ana.nunes@ponto.com.br', 'Func@143', '11979031604'),
('97553956953', '69366710000136', 'Bruno', 'Freitas', 'Analista de Qualidade', 'bruno.freitas@central.com.br', 'Func@144', '11965382184'),
('80406019088', '42839744000144', 'Carla', 'Vieira', 'Supervisor', 'carla.vieira@laticinios.com.br', 'Func@145', '11902717832'),
('98280909109', '05122832000102', 'Diego', 'Batista', 'Auxiliar de Deposito', 'diego.batista@frigorifico.com.br', 'Func@146', '11936368062'),
('78292465057', '59649891000154', 'Elaine', 'Xavier', 'Nutricionista', 'elaine.xavier@distribuidora.com.br', 'Func@147', '11995557182'),
('97989008475', '23628639000111', 'Fabio', 'Guimaraes', 'Vendedor', 'fabio.guimaraes@empório.com.br', 'Func@148', '11990947124'),
('77313275668', '78859395000170', 'Gabriela', 'Peixoto', 'Coordenador', 'gabriela.peixoto@atacado.com.br', 'Func@149', '11946321206');


-- Produtos

INSERT INTO Produtos (nome, descricao, preco, categoria, marca, unidadeMedida) VALUES
('Leite Integral', 'Leite de vaca integral pasteurizado, rico em calcio e proteinas', 6.49, 'LATICIONIOS', 'Itambe', 'litro'),
('Peito de Frango', 'Peito de frango resfriado, sem osso e sem pele', 22.9, 'CARNES', 'Sadia', 'kg'),
('Alface Crespa', 'Alface crespa fresca, colhida diariamente', 3.49, 'HORTALICAS', 'FazendaVerde', 'unidade'),
('Queijo Mussarela', 'Queijo mussarela fatiado, ideal para lanches e pizzas', 34.9, 'LATICIONIOS', 'Tirolez', 'kg'),
('Iogurte Natural', 'Iogurte natural integral sem adicao de acucar', 5.29, 'LATICIONIOS', 'Perdigao', 'unidade'),
('Carne Moida', 'Carne bovina moida na hora, patinho', 28.9, 'CARNES', 'Seara', 'kg'),
('Tomate Salada', 'Tomate salada maduro, ideal para saladas e molhos', 7.99, 'HORTALICAS', 'Vigor', 'kg'),
('Requeijao Cremoso', 'Requeijao cremoso tradicional em pote', 9.79, 'LATICIONIOS', 'Danone', 'unidade'),
('File de Tilapia', 'File de tilapia fresco, sem espinhas', 32.9, 'PESCADOS', 'Piracanjuba', 'kg'),
('Cenoura', 'Cenoura fresca lavada, rica em vitamina A', 4.59, 'HORTALICAS', 'Friboi', 'kg'),
('Manteiga com Sal', 'Manteiga com sal em tablete de 200g', 12.49, 'LATICIONIOS', 'Itambe', 'unidade'),
('Linguica Toscana', 'Linguica toscana resfriada para churrasco', 19.9, 'EMBUTIDOS', 'Sadia', 'kg'),
('Batata Inglesa', 'Batata inglesa lavada, tamanho medio', 5.99, 'HORTALICAS', 'FazendaVerde', 'kg'),
('Presunto Fatiado', 'Presunto cozido fatiado, embalagem a vacuo', 24.9, 'EMBUTIDOS', 'Tirolez', 'kg'),
('Brocolis', 'Brocolis ninja fresco, rico em fibras', 6.99, 'HORTALICAS', 'Perdigao', 'unidade'),
('Leite Desnatado', 'Leite desnatado UHT, baixo teor de gordura', 6.79, 'LATICIONIOS', 'Seara', 'litro'),
('Costela Bovina', 'Costela bovina resfriada, ideal para churrasco', 39.9, 'CARNES', 'Vigor', 'kg'),
('Cebola Roxa', 'Cebola roxa fresca, sabor suave e adocicado', 8.49, 'HORTALICAS', 'Danone', 'kg'),
('Cream Cheese', 'Cream cheese tradicional em pote de 150g', 14.9, 'LATICIONIOS', 'Piracanjuba', 'unidade'),
('Salmao Fresco', 'File de salmao fresco, corte porcionado', 69.9, 'PESCADOS', 'Friboi', 'kg'),
('Abobrinha', 'Abobrinha italiana fresca', 5.49, 'HORTALICAS', 'Itambe', 'kg'),
('Bacon em Cubos', 'Bacon defumado cortado em cubos', 27.9, 'EMBUTIDOS', 'Sadia', 'pacote'),
('Pepino Japones', 'Pepino japones fresco, casca fina', 4.99, 'HORTALICAS', 'FazendaVerde', 'unidade'),
('Ricota Fresca', 'Ricota fresca artesanal em pote', 11.9, 'LATICIONIOS', 'Tirolez', 'unidade'),
('Coxa de Frango', 'Coxa de frango resfriada com sobrecoxa', 15.9, 'CARNES', 'Perdigao', 'kg'),
('Couve Manteiga', 'Couve manteiga fresca, folhas selecionadas', 3.99, 'HORTALICAS', 'Seara', 'unidade'),
('Iogurte Grego', 'Iogurte grego cremoso, alto teor proteico', 8.49, 'LATICIONIOS', 'Vigor', 'unidade'),
('Pernil Suino', 'Pernil suino resfriado, ideal para assados', 21.9, 'CARNES', 'Danone', 'kg'),
('Pimentao Verde', 'Pimentao verde fresco, crocante', 9.99, 'HORTALICAS', 'Piracanjuba', 'kg'),
('Queijo Prato', 'Queijo prato fatiado, sabor suave', 36.9, 'LATICIONIOS', 'Friboi', 'kg'),
('Ovos Brancos', 'Ovos brancos tipo grande, caixa com 12 unidades', 14.9, 'OVOS', 'Itambe', 'caixa'),
('File Mignon', 'File mignon bovino, corte nobre', 79.9, 'CARNES', 'Sadia', 'kg'),
('Repolho Roxo', 'Repolho roxo fresco, ideal para saladas', 5.29, 'HORTALICAS', 'FazendaVerde', 'unidade'),
('Leite Semidesnatado', 'Leite semidesnatado UHT longa vida', 6.59, 'LATICIONIOS', 'Tirolez', 'litro'),
('Camarao Limpo', 'Camarao limpo e congelado, medio', 54.9, 'PESCADOS', 'Perdigao', 'kg'),
('Espinafre', 'Espinafre fresco em maco', 4.49, 'HORTALICAS', 'Seara', 'unidade'),
('Nata Fresca', 'Nata fresca pasteurizada em pote', 13.9, 'LATICIONIOS', 'Vigor', 'unidade'),
('Bisteca Suina', 'Bisteca suina resfriada com osso', 18.9, 'CARNES', 'Danone', 'kg'),
('Vagem', 'Vagem fresca selecionada', 7.49, 'HORTALICAS', 'Piracanjuba', 'kg'),
('Queijo Coalho', 'Queijo coalho para espetinho', 32.9, 'LATICIONIOS', 'Friboi', 'kg'),
('Whey Protein', 'Suplemento em po sabor baunilha, pote 900g', 149.9, 'BEBIDAS', 'Itambe', 'pacote'),
('Almondegas', 'Almondegas bovinas temperadas, congeladas', 24.9, 'ALIMENTOS_PREPARADOS', 'Sadia', 'pacote'),
('Berinjela', 'Berinjela fresca, casca brilhante', 6.29, 'HORTALICAS', 'FazendaVerde', 'kg'),
('Manteiga sem Sal', 'Manteiga sem sal em tablete de 200g', 12.9, 'LATICIONIOS', 'Tirolez', 'unidade'),
('Frango a Passarinho', 'Frango a passarinho temperado, congelado', 22.9, 'CONGELADOS', 'Perdigao', 'pacote'),
('Rucula', 'Rucula fresca em maco', 3.79, 'HORTALICAS', 'Seara', 'unidade'),
('Queijo Parmesao', 'Queijo parmesao ralado fino', 44.9, 'LATICIONIOS', 'Vigor', 'kg'),
('Salsicha', 'Salsicha tipo hot dog, pacote com 10 unidades', 13.9, 'EMBUTIDOS', 'Danone', 'pacote'),
('Chuchu', 'Chuchu fresco, textura macia', 4.79, 'HORTALICAS', 'Piracanjuba', 'kg'),
('Iogurte de Morango', 'Iogurte sabor morango, copo de 170g', 4.49, 'LATICIONIOS', 'Friboi', 'unidade');


-- Notificacao

INSERT INTO Notificacao (diasAviso, horarioAviso) VALUES
('1', '05:17:00'),
('2', '10:47:00'),
('3', '05:07:00'),
('4', '09:58:00'),
('5', '16:47:00'),
('6', '06:19:00'),
('7', '01:00:00'),
('1', '11:18:00'),
('2', '07:31:00'),
('3', '18:52:00'),
('4', '10:41:00'),
('5', '08:14:00'),
('6', '17:38:00'),
('7', '21:36:00'),
('1', '17:00:00'),
('2', '17:09:00'),
('3', '18:10:00'),
('4', '14:40:00'),
('5', '09:24:00'),
('6', '00:34:00'),
('7', '05:46:00'),
('1', '17:33:00'),
('2', '13:25:00'),
('3', '12:18:00'),
('4', '06:09:00'),
('5', '13:01:00'),
('6', '10:01:00'),
('7', '16:40:00'),
('1', '23:11:00'),
('2', '02:04:00'),
('3', '02:22:00'),
('4', '20:45:00'),
('5', '11:20:00'),
('6', '11:19:00'),
('7', '02:42:00'),
('1', '01:50:00'),
('2', '14:02:00'),
('3', '20:29:00'),
('4', '07:32:00'),
('5', '18:54:00'),
('6', '08:38:00'),
('7', '00:37:00'),
('1', '22:11:00'),
('2', '00:56:00'),
('3', '20:02:00'),
('4', '22:29:00'),
('5', '17:22:00'),
('6', '12:02:00'),
('7', '09:06:00'),
('1', '05:32:00');


-- Geladeira

INSERT INTO Geladeira (idUsuario, status, quantidade, tipo) VALUES
('61651728658', 'VAZIO', 0, 'Domestica'),
('03186265940', 'OCUPACAO_BAIXA', 8, 'Frost Free'),
('89632247248', 'OCUPACAO_MODERADA', 36, 'Duplex'),
('78537787671', 'OCUPACAO_ALTA', 8, 'Inox'),
('61988335000', 'CHEIA', 10, 'Compacta'),
('76869458200', 'ILIMITADO', 27, 'Domestica'),
('50370395093', 'VAZIO', 0, 'Frost Free'),
('09242380385', 'OCUPACAO_BAIXA', 39, 'Duplex'),
('87511029191', 'OCUPACAO_MODERADA', 18, 'Inox'),
('20718728351', 'OCUPACAO_ALTA', 21, 'Compacta'),
('93816662293', 'CHEIA', 7, 'Domestica'),
('54062767864', 'ILIMITADO', 23, 'Frost Free'),
('52263905480', 'VAZIO', 0, 'Duplex'),
('74299060601', 'OCUPACAO_BAIXA', 3, 'Inox'),
('80340669900', 'OCUPACAO_MODERADA', 4, 'Compacta'),
('14786469424', 'OCUPACAO_ALTA', 16, 'Domestica'),
('56858215735', 'CHEIA', 8, 'Frost Free'),
('76315414427', 'ILIMITADO', 31, 'Duplex'),
('66139532884', 'VAZIO', 0, 'Inox'),
('05313798332', 'OCUPACAO_BAIXA', 8, 'Compacta'),
('10769242987', 'OCUPACAO_MODERADA', 5, 'Domestica'),
('88293486945', 'OCUPACAO_ALTA', 29, 'Frost Free'),
('26951681520', 'CHEIA', 4, 'Duplex'),
('77726406500', 'ILIMITADO', 19, 'Inox'),
('07658178535', 'VAZIO', 0, 'Compacta'),
('30035336579', 'OCUPACAO_BAIXA', 40, 'Domestica'),
('07464396790', 'OCUPACAO_MODERADA', 22, 'Frost Free'),
('01187309672', 'OCUPACAO_ALTA', 26, 'Duplex'),
('79643758656', 'CHEIA', 28, 'Inox'),
('94982888604', 'ILIMITADO', 19, 'Compacta'),
('06923786185', 'VAZIO', 0, 'Domestica'),
('98005505426', 'OCUPACAO_BAIXA', 21, 'Frost Free'),
('68625374310', 'OCUPACAO_MODERADA', 17, 'Duplex'),
('34711306676', 'OCUPACAO_ALTA', 9, 'Inox'),
('01116136392', 'CHEIA', 39, 'Compacta'),
('72421060877', 'ILIMITADO', 39, 'Domestica'),
('61101572019', 'VAZIO', 0, 'Frost Free'),
('17710054919', 'OCUPACAO_BAIXA', 3, 'Duplex'),
('11421570750', 'OCUPACAO_MODERADA', 18, 'Inox'),
('23103612249', 'OCUPACAO_ALTA', 4, 'Compacta'),
('42935404904', 'CHEIA', 1, 'Domestica'),
('72760313140', 'ILIMITADO', 29, 'Frost Free'),
('32505476462', 'VAZIO', 0, 'Duplex'),
('95573305468', 'OCUPACAO_BAIXA', 7, 'Inox'),
('35779032289', 'OCUPACAO_MODERADA', 23, 'Compacta'),
('06771725758', 'OCUPACAO_ALTA', 5, 'Domestica'),
('79715532004', 'CHEIA', 19, 'Frost Free'),
('14647672549', 'ILIMITADO', 8, 'Duplex'),
('44230143605', 'VAZIO', 0, 'Inox'),
('97037584959', 'OCUPACAO_BAIXA', 10, 'Compacta');


-- Endereco

INSERT INTO Endereco (rua, bairro, cidade, uf, pais, cep, numero, nomeUnidade) VALUES
('Rua Oscar Freire', 'Jardins', 'Sao Paulo', 'SP', 'Brasil', '01310-000', '210', 'Unidade Sao Paulo 01'),
('Rua do Ouvidor', 'Centro', 'Rio de Janeiro', 'RJ', 'Brasil', '22041-002', '1238', 'Unidade Rio de Janeiro 02'),
('Av. do Contorno', 'Centro', 'Belo Horizonte', 'MG', 'Brasil', '30130-012', '1635', 'Unidade Belo Horizonte 03'),
('Rua XV de Novembro', 'Batel', 'Curitiba', 'PR', 'Brasil', '80010-003', '1670', 'Unidade Curitiba 04'),
('Rua dos Andradas', 'Cidade Baixa', 'Porto Alegre', 'RS', 'Brasil', '90010-154', '761', 'Unidade Porto Alegre 05'),
('Av. Sete de Setembro', 'Ondina', 'Salvador', 'BA', 'Brasil', '40010-005', '1541', 'Unidade Salvador 06'),
('Av. Conde da Boa Vista', 'Casa Forte', 'Recife', 'PE', 'Brasil', '50030-236', '223', 'Unidade Recife 07'),
('Rua Barao do Rio Branco', 'Meireles', 'Fortaleza', 'CE', 'Brasil', '60060-177', '628', 'Unidade Fortaleza 08'),
('SCS Quadra 6', 'Asa Norte', 'Brasilia', 'DF', 'Brasil', '70040-018', '2130', 'Unidade Brasilia 09'),
('Av. Norte-Sul', 'Centro', 'Campinas', 'SP', 'Brasil', '13010-169', '96', 'Unidade Campinas 10'),
('Rua Marechal Deodoro', 'Centro Civico', 'Curitiba', 'PR', 'Brasil', '80230-020', '536', 'Unidade Curitiba 11'),
('Rua 44', 'Setor Central', 'Goiania', 'GO', 'Brasil', '74015-021', '140', 'Unidade Goiania 12'),
('Av. Beira Mar Norte', 'Beira Mar', 'Florianopolis', 'SC', 'Brasil', '88010-412', '1130', 'Unidade Florianopolis 13'),
('Av. Eduardo Ribeiro', 'Aleixo', 'Manaus', 'AM', 'Brasil', '69010-023', '1506', 'Unidade Manaus 14'),
('Rua dos Mundurucus', 'Nazare', 'Belem', 'PA', 'Brasil', '66017-014', '603', 'Unidade Belem 15'),
('Av. Paulista', 'Consolacao', 'Sao Paulo', 'SP', 'Brasil', '01310-015', '2098', 'Unidade Sao Paulo 16'),
('Rua do Ouvidor', 'Copacabana', 'Rio de Janeiro', 'RJ', 'Brasil', '22041-017', '475', 'Unidade Rio de Janeiro 17'),
('Rua da Bahia', 'Funcionarios', 'Belo Horizonte', 'MG', 'Brasil', '30130-027', '62', 'Unidade Belo Horizonte 18'),
('Rua XV de Novembro', 'Batel', 'Curitiba', 'PR', 'Brasil', '80010-018', '892', 'Unidade Curitiba 19'),
('Rua dos Andradas', 'Moinhos de Vento', 'Porto Alegre', 'RS', 'Brasil', '90010-169', '430', 'Unidade Porto Alegre 20'),
('Av. Sete de Setembro', 'Pelourinho', 'Salvador', 'BA', 'Brasil', '40010-020', '521', 'Unidade Salvador 21'),
('Rua da Aurora', 'Recife Antigo', 'Recife', 'PE', 'Brasil', '50030-251', '2090', 'Unidade Recife 22'),
('Av. Beira Mar', 'Aldeota', 'Fortaleza', 'CE', 'Brasil', '60060-192', '809', 'Unidade Fortaleza 23'),
('Eixo Monumental', 'Asa Norte', 'Brasilia', 'DF', 'Brasil', '70040-033', '632', 'Unidade Brasilia 24'),
('Rua Barao de Jaguara', 'Cambui', 'Campinas', 'SP', 'Brasil', '13010-184', '1584', 'Unidade Campinas 25'),
('Rua Marechal Deodoro', 'Bigorrilho', 'Curitiba', 'PR', 'Brasil', '80230-035', '1835', 'Unidade Curitiba 26'),
('Rua 44', 'Setor Central', 'Goiania', 'GO', 'Brasil', '74015-036', '167', 'Unidade Goiania 27'),
('Av. Rio Branco', 'Beira Mar', 'Florianopolis', 'SC', 'Brasil', '88010-427', '1946', 'Unidade Florianopolis 28'),
('Av. Djalma Batista', 'Centro', 'Manaus', 'AM', 'Brasil', '69010-038', '1942', 'Unidade Manaus 29'),
('Av. Nazare', 'Umarizal', 'Belem', 'PA', 'Brasil', '66017-029', '237', 'Unidade Belem 30'),
('Rua Augusta', 'Bela Vista', 'Sao Paulo', 'SP', 'Brasil', '01310-030', '1982', 'Unidade Sao Paulo 31'),
('Rua do Ouvidor', 'Centro', 'Rio de Janeiro', 'RJ', 'Brasil', '22041-032', '1507', 'Unidade Rio de Janeiro 32'),
('Av. Afonso Pena', 'Centro', 'Belo Horizonte', 'MG', 'Brasil', '30130-042', '2148', 'Unidade Belo Horizonte 33'),
('Rua das Flores', 'Centro', 'Curitiba', 'PR', 'Brasil', '80010-033', '2124', 'Unidade Curitiba 34'),
('Av. Ipiranga', 'Moinhos de Vento', 'Porto Alegre', 'RS', 'Brasil', '90010-184', '1213', 'Unidade Porto Alegre 35'),
('Rua Chile', 'Ondina', 'Salvador', 'BA', 'Brasil', '40010-035', '298', 'Unidade Salvador 36'),
('Av. Conde da Boa Vista', 'Recife Antigo', 'Recife', 'PE', 'Brasil', '50030-266', '2320', 'Unidade Recife 37'),
('Rua Barao do Rio Branco', 'Centro', 'Fortaleza', 'CE', 'Brasil', '60060-207', '833', 'Unidade Fortaleza 38'),
('Eixo Monumental', 'Asa Sul', 'Brasilia', 'DF', 'Brasil', '70040-048', '10', 'Unidade Brasilia 39'),
('Av. Francisco Glicerio', 'Centro', 'Campinas', 'SP', 'Brasil', '13010-199', '934', 'Unidade Campinas 40'),
('Rua Comendador Araujo', 'Cabral', 'Curitiba', 'PR', 'Brasil', '80230-050', '1934', 'Unidade Curitiba 41'),
('Rua 44', 'Setor Central', 'Goiania', 'GO', 'Brasil', '74015-051', '481', 'Unidade Goiania 42'),
('Rua Felipe Schmidt', 'Trindade', 'Florianopolis', 'SC', 'Brasil', '88010-442', '932', 'Unidade Florianopolis 43'),
('Rua Barroso', 'Adrianopolis', 'Manaus', 'AM', 'Brasil', '69010-053', '2056', 'Unidade Manaus 44'),
('Rua dos Mundurucus', 'Umarizal', 'Belem', 'PA', 'Brasil', '66017-044', '2297', 'Unidade Belem 45'),
('Rua Oscar Freire', 'Consolacao', 'Sao Paulo', 'SP', 'Brasil', '01310-045', '182', 'Unidade Sao Paulo 46'),
('Av. Atlantica', 'Ipanema', 'Rio de Janeiro', 'RJ', 'Brasil', '22041-047', '1536', 'Unidade Rio de Janeiro 47'),
('Rua da Bahia', 'Savassi', 'Belo Horizonte', 'MG', 'Brasil', '30130-057', '2077', 'Unidade Belo Horizonte 48'),
('Rua XV de Novembro', 'Agua Verde', 'Curitiba', 'PR', 'Brasil', '80010-048', '1726', 'Unidade Curitiba 49'),
('Rua dos Andradas', 'Centro Historico', 'Porto Alegre', 'RS', 'Brasil', '90010-199', '1644', 'Unidade Porto Alegre 50');


-- Frigorificos

INSERT INTO Frigorificos (idEndereco, idEmpresa, quantidade, status, tipo) VALUES
(1, '24144297000127', 362, 'OCUPACAO_ALTA', 'Congelador'),
(2, '47253455000182', 47, 'CHEIA', 'Refrigerador'),
(3, '36006970000141', 292, 'ILIMITADO', 'Congelador'),
(4, '87349005000102', 0, 'VAZIO', 'Refrigerador'),
(5, '77310109000150', 476, 'OCUPACAO_BAIXA', 'Congelador'),
(6, '06573130000108', 368, 'OCUPACAO_MODERADA', 'Refrigerador'),
(7, '81948539000179', 82, 'OCUPACAO_ALTA', 'Congelador'),
(8, '56876315000151', 352, 'CHEIA', 'Refrigerador'),
(9, '47026119000105', 628, 'ILIMITADO', 'Congelador'),
(10, '79516470000162', 0, 'VAZIO', 'Refrigerador'),
(11, '85589026000170', 125, 'OCUPACAO_BAIXA', 'Congelador'),
(12, '39829214000100', 556, 'OCUPACAO_MODERADA', 'Refrigerador'),
(13, '43734144000184', 388, 'OCUPACAO_ALTA', 'Congelador'),
(14, '70022229000102', 862, 'CHEIA', 'Refrigerador'),
(15, '18315670000162', 338, 'ILIMITADO', 'Congelador'),
(16, '94007678000140', 0, 'VAZIO', 'Refrigerador'),
(17, '40613964000120', 457, 'OCUPACAO_BAIXA', 'Congelador'),
(18, '60898058000126', 544, 'OCUPACAO_MODERADA', 'Refrigerador'),
(19, '62402210000163', 281, 'OCUPACAO_ALTA', 'Congelador'),
(20, '56281715000114', 428, 'CHEIA', 'Refrigerador'),
(21, '67436371000191', 574, 'ILIMITADO', 'Congelador'),
(22, '91004404000109', 0, 'VAZIO', 'Refrigerador'),
(23, '72264236000146', 143, 'OCUPACAO_BAIXA', 'Congelador'),
(24, '96706343000145', 571, 'OCUPACAO_MODERADA', 'Refrigerador'),
(25, '21473877000170', 29, 'OCUPACAO_ALTA', 'Congelador'),
(26, '08438764000175', 219, 'CHEIA', 'Refrigerador'),
(27, '82635120000120', 278, 'ILIMITADO', 'Congelador'),
(28, '26012776000115', 0, 'VAZIO', 'Refrigerador'),
(29, '33665954000172', 412, 'OCUPACAO_BAIXA', 'Congelador'),
(30, '82242871000186', 445, 'OCUPACAO_MODERADA', 'Refrigerador'),
(31, '34910575000163', 316, 'OCUPACAO_ALTA', 'Congelador'),
(32, '66304631000102', 765, 'CHEIA', 'Refrigerador'),
(33, '22514034000137', 770, 'ILIMITADO', 'Congelador'),
(34, '96116540000104', 0, 'VAZIO', 'Refrigerador'),
(35, '95405520000190', 152, 'OCUPACAO_BAIXA', 'Congelador'),
(36, '85416602000187', 268, 'OCUPACAO_MODERADA', 'Refrigerador'),
(37, '41182220000160', 798, 'OCUPACAO_ALTA', 'Congelador'),
(38, '61134330000164', 591, 'CHEIA', 'Refrigerador'),
(39, '21805824000109', 34, 'ILIMITADO', 'Congelador'),
(40, '03853857000142', 0, 'VAZIO', 'Refrigerador'),
(41, '93034110000155', 390, 'OCUPACAO_BAIXA', 'Congelador'),
(42, '00691117000169', 787, 'OCUPACAO_MODERADA', 'Refrigerador'),
(43, '73758788000173', 268, 'OCUPACAO_ALTA', 'Congelador'),
(44, '86598366000120', 270, 'CHEIA', 'Refrigerador'),
(45, '69366710000136', 674, 'ILIMITADO', 'Congelador'),
(46, '42839744000144', 0, 'VAZIO', 'Refrigerador'),
(47, '05122832000102', 562, 'OCUPACAO_BAIXA', 'Congelador'),
(48, '59649891000154', 499, 'OCUPACAO_MODERADA', 'Refrigerador'),
(49, '23628639000111', 291, 'OCUPACAO_ALTA', 'Congelador'),
(50, '78859395000170', 662, 'CHEIA', 'Refrigerador');


-- ProdutosGeladeira

INSERT INTO ProdutosGeladeira (idNotificacao, idGeladeira, idProdutos, dtValidade, status, lote) VALUES
(1, 1, 1, '2026-08-13 00:00:00-03:00', 'VENCIDO', 0),
(2, 2, 2, '2026-08-28 00:00:00-03:00', 'VENCIDO', 1),
(3, 3, 3, '2026-09-12 00:00:00-03:00', 'VENCIDO', 2),
(4, 4, 4, '2026-09-19 00:00:00-03:00', 'VENCIDO', 3),
(5, 5, 5, '2026-09-23 00:00:00-03:00', 'VENCIDO', 4),
(6, 6, 6, '2026-09-25 00:00:00-03:00', 'VENCIDO', 5),
(7, 7, 7, '2026-09-26 00:00:00-03:00', 'VENCIDO', 6),
(8, 8, 8, '2026-09-27 00:00:00-03:00', 'ATENCAO', 7),
(9, 9, 9, '2026-09-28 00:00:00-03:00', 'ATENCAO', 8),
(10, 10, 10, '2026-09-29 00:00:00-03:00', 'ATENCAO', 9),
(11, 11, 11, '2026-09-30 00:00:00-03:00', 'ATENCAO', 10),
(12, 12, 12, '2026-10-01 00:00:00-03:00', 'PROXIMO_VENCIMENTO', 11),
(13, 13, 13, '2026-10-02 00:00:00-03:00', 'PROXIMO_VENCIMENTO', 12),
(14, 14, 14, '2026-10-03 00:00:00-03:00', 'PROXIMO_VENCIMENTO', 13),
(15, 15, 15, '2026-10-04 00:00:00-03:00', 'PROXIMO_VENCIMENTO', 14),
(16, 16, 16, '2026-10-05 00:00:00-03:00', 'REGULAR', 15),
(17, 17, 17, '2026-10-07 00:00:00-03:00', 'REGULAR', 16),
(18, 18, 18, '2026-10-09 00:00:00-03:00', 'REGULAR', 17),
(19, 19, 19, '2026-10-12 00:00:00-03:00', 'REGULAR', 18),
(20, 20, 20, '2026-10-15 00:00:00-03:00', 'REGULAR', 19),
(21, 21, 21, '2026-10-17 00:00:00-03:00', 'REGULAR', 20),
(22, 22, 22, '2026-10-19 00:00:00-03:00', 'VALIDADE_LONGA', 21),
(23, 23, 23, '2026-10-22 00:00:00-03:00', 'VALIDADE_LONGA', 22),
(24, 24, 24, '2026-10-27 00:00:00-03:00', 'VALIDADE_LONGA', 23),
(25, 25, 25, '2026-11-01 00:00:00-03:00', 'VALIDADE_LONGA', 24),
(26, 26, 26, '2026-11-06 00:00:00-03:00', 'VALIDADE_LONGA', 25),
(27, 27, 27, '2026-11-16 00:00:00-03:00', 'VALIDADE_LONGA', 26),
(28, 28, 28, '2026-11-26 00:00:00-03:00', 'VALIDADE_LONGA', 27),
(29, 29, 29, '2026-12-11 00:00:00-03:00', 'VALIDADE_LONGA', 28),
(30, 30, 30, '2026-12-26 00:00:00-03:00', 'VALIDADE_LONGA', 29),
(31, 31, 31, '2026-07-29 00:00:00-03:00', 'VENCIDO', 30),
(32, 32, 32, '2026-09-07 00:00:00-03:00', 'VENCIDO', 31),
(33, 33, 33, '2026-09-17 00:00:00-03:00', 'VENCIDO', 32),
(34, 34, 34, '2026-09-22 00:00:00-03:00', 'VENCIDO', 33),
(35, 35, 35, '2026-09-24 00:00:00-03:00', 'VENCIDO', 34),
(36, 36, 36, '2026-09-28 00:00:00-03:00', 'ATENCAO', 35),
(37, 37, 37, '2026-09-29 00:00:00-03:00', 'ATENCAO', 36),
(38, 38, 38, '2026-10-02 00:00:00-03:00', 'PROXIMO_VENCIMENTO', 37),
(39, 39, 39, '2026-10-03 00:00:00-03:00', 'PROXIMO_VENCIMENTO', 38),
(40, 40, 40, '2026-10-06 00:00:00-03:00', 'REGULAR', 39),
(41, 41, 41, '2026-10-11 00:00:00-03:00', 'REGULAR', 40),
(42, 42, 42, '2026-10-14 00:00:00-03:00', 'REGULAR', 41),
(43, 43, 43, '2026-10-16 00:00:00-03:00', 'REGULAR', 42),
(44, 44, 44, '2026-10-18 00:00:00-03:00', 'VALIDADE_LONGA', 43),
(45, 45, 45, '2026-10-25 00:00:00-03:00', 'VALIDADE_LONGA', 44),
(46, 46, 46, '2026-11-11 00:00:00-03:00', 'VALIDADE_LONGA', 45),
(47, 47, 47, '2026-12-01 00:00:00-03:00', 'VALIDADE_LONGA', 46),
(48, 48, 48, '2027-01-05 00:00:00-03:00', 'VALIDADE_LONGA', 47),
(49, 49, 49, '2027-01-25 00:00:00-03:00', 'VALIDADE_LONGA', 48),
(50, 50, 50, '2026-09-02 00:00:00-03:00', 'VENCIDO', 49);


-- ProdutosFrigorifico

INSERT INTO ProdutosFrigorifico (idNotificacao, idFrigorificos, idProdutos, dtValidade, status, lote) VALUES
(1, 1, 8, '2026-11-06 00:00:00-03:00', 'VALIDADE_LONGA', 0),
(2, 2, 9, '2026-11-16 00:00:00-03:00', 'VALIDADE_LONGA', 1),
(3, 3, 10, '2026-11-26 00:00:00-03:00', 'VALIDADE_LONGA', 2),
(4, 4, 11, '2026-12-11 00:00:00-03:00', 'VALIDADE_LONGA', 3),
(5, 5, 12, '2026-12-26 00:00:00-03:00', 'VALIDADE_LONGA', 4),
(6, 6, 13, '2026-07-29 00:00:00-03:00', 'VENCIDO', 5),
(7, 7, 14, '2026-09-07 00:00:00-03:00', 'VENCIDO', 6),
(8, 8, 15, '2026-09-17 00:00:00-03:00', 'VENCIDO', 7),
(9, 9, 16, '2026-09-22 00:00:00-03:00', 'VENCIDO', 8),
(10, 10, 17, '2026-09-24 00:00:00-03:00', 'VENCIDO', 9),
(11, 11, 18, '2026-09-28 00:00:00-03:00', 'ATENCAO', 10),
(12, 12, 19, '2026-09-29 00:00:00-03:00', 'ATENCAO', 11),
(13, 13, 20, '2026-10-02 00:00:00-03:00', 'PROXIMO_VENCIMENTO', 12),
(14, 14, 21, '2026-10-03 00:00:00-03:00', 'PROXIMO_VENCIMENTO', 13),
(15, 15, 22, '2026-10-06 00:00:00-03:00', 'REGULAR', 14),
(16, 16, 23, '2026-10-11 00:00:00-03:00', 'REGULAR', 15),
(17, 17, 24, '2026-10-14 00:00:00-03:00', 'REGULAR', 16),
(18, 18, 25, '2026-10-16 00:00:00-03:00', 'REGULAR', 17),
(19, 19, 26, '2026-10-18 00:00:00-03:00', 'VALIDADE_LONGA', 18),
(20, 20, 27, '2026-10-25 00:00:00-03:00', 'VALIDADE_LONGA', 19),
(21, 21, 28, '2026-11-11 00:00:00-03:00', 'VALIDADE_LONGA', 20),
(22, 22, 29, '2026-12-01 00:00:00-03:00', 'VALIDADE_LONGA', 21),
(23, 23, 30, '2027-01-05 00:00:00-03:00', 'VALIDADE_LONGA', 22),
(24, 24, 31, '2027-01-25 00:00:00-03:00', 'VALIDADE_LONGA', 23),
(25, 25, 32, '2026-09-02 00:00:00-03:00', 'VENCIDO', 24),
(26, 26, 33, '2026-08-13 00:00:00-03:00', 'VENCIDO', 25),
(27, 27, 34, '2026-08-28 00:00:00-03:00', 'VENCIDO', 26),
(28, 28, 35, '2026-09-12 00:00:00-03:00', 'VENCIDO', 27),
(29, 29, 36, '2026-09-19 00:00:00-03:00', 'VENCIDO', 28),
(30, 30, 37, '2026-09-23 00:00:00-03:00', 'VENCIDO', 29),
(31, 31, 38, '2026-09-25 00:00:00-03:00', 'VENCIDO', 30),
(32, 32, 39, '2026-09-26 00:00:00-03:00', 'VENCIDO', 31),
(33, 33, 40, '2026-09-27 00:00:00-03:00', 'ATENCAO', 32),
(34, 34, 41, '2026-09-28 00:00:00-03:00', 'ATENCAO', 33),
(35, 35, 42, '2026-09-29 00:00:00-03:00', 'ATENCAO', 34),
(36, 36, 43, '2026-09-30 00:00:00-03:00', 'ATENCAO', 35),
(37, 37, 44, '2026-10-01 00:00:00-03:00', 'PROXIMO_VENCIMENTO', 36),
(38, 38, 45, '2026-10-02 00:00:00-03:00', 'PROXIMO_VENCIMENTO', 37),
(39, 39, 46, '2026-10-03 00:00:00-03:00', 'PROXIMO_VENCIMENTO', 38),
(40, 40, 47, '2026-10-04 00:00:00-03:00', 'PROXIMO_VENCIMENTO', 39),
(41, 41, 48, '2026-10-05 00:00:00-03:00', 'REGULAR', 40),
(42, 42, 49, '2026-10-07 00:00:00-03:00', 'REGULAR', 41),
(43, 43, 50, '2026-10-09 00:00:00-03:00', 'REGULAR', 42),
(44, 44, 1, '2026-10-12 00:00:00-03:00', 'REGULAR', 43),
(45, 45, 2, '2026-10-15 00:00:00-03:00', 'REGULAR', 44),
(46, 46, 3, '2026-10-17 00:00:00-03:00', 'REGULAR', 45),
(47, 47, 4, '2026-10-19 00:00:00-03:00', 'VALIDADE_LONGA', 46),
(48, 48, 5, '2026-10-22 00:00:00-03:00', 'VALIDADE_LONGA', 47),
(49, 49, 6, '2026-10-27 00:00:00-03:00', 'VALIDADE_LONGA', 48),
(50, 50, 7, '2026-11-01 00:00:00-03:00', 'VALIDADE_LONGA', 49);


-- Receita

INSERT INTO Receita (idEmpresa, idUsuario, nome, descricao, modoPreparo, dtReceita, foiFeita) VALUES
(NULL, '61651728658', 'Salada de Frango com Abacaxi', 'Salada refrescante com frango desfiado e abacaxi', 'Cozinhe o frango, desfie e misture com abacaxi picado, alface e molho de iogurte.', '2026-09-27 00:00:00-03:00', false),
('47253455000182', NULL, 'Sanduiche Natural de Atum', 'Sanduiche leve com pasta de atum e vegetais', 'Misture atum, maionese e cenoura ralada; monte no pao integral com alface e tomate.', '2026-09-23 00:00:00-03:00', true),
(NULL, '89632247248', 'Sopa de Legumes', 'Sopa quente com legumes variados', 'Refogue cebola e alho, adicione os legumes picados e agua, cozinhe ate amaciar.', '2026-09-19 00:00:00-03:00', true),
('87349005000102', NULL, 'Omelete Simples', 'Omelete classico de ovos com temperos', 'Bata os ovos com sal e pimenta, despeje na frigideira quente e doure dos dois lados.', '2026-09-15 00:00:00-03:00', false),
(NULL, '61988335000', 'Bolo de Cenoura com Cobertura', 'Bolo tradicional com cobertura de chocolate', 'Bata cenoura, ovos e oleo no liquidificador, misture com farinha e asse por 40 min.', '2026-09-11 00:00:00-03:00', true),
('06573130000108', NULL, 'Frango Grelhado com Ervas', 'Peito de frango grelhado temperado com ervas finas', 'Tempere o frango e grelhe em fogo medio ate dourar dos dois lados.', '2026-09-07 00:00:00-03:00', true),
(NULL, '50370395093', 'Arroz Integral com Legumes', 'Arroz integral refogado com legumes picados', 'Refogue o arroz com alho, adicione agua e legumes, cozinhe em fogo baixo.', '2026-09-03 00:00:00-03:00', false),
('56876315000151', NULL, 'Feijoada Light', 'Versao mais leve da feijoada tradicional', 'Cozinhe o feijao com carnes magras e temperos ate ficar cremoso.', '2026-08-30 00:00:00-03:00', true),
(NULL, '87511029191', 'Panqueca Doce de Banana', 'Panqueca doce recheada com banana e canela', 'Prepare a massa, recheie com banana caramelizada e enrole.', '2026-08-26 00:00:00-03:00', true),
('79516470000162', NULL, 'Wrap de Frango', 'Wrap pratico com frango e vegetais', 'Recheie a tortilha com frango grelhado, alface e molho de iogurte.', '2026-08-22 00:00:00-03:00', false),
(NULL, '93816662293', 'Torta de Frango com Catupiry', 'Torta salgada cremosa de frango', 'Monte camadas de massa, recheio de frango e catupiry, asse ate dourar.', '2026-08-18 00:00:00-03:00', true),
('39829214000100', NULL, 'Lasanha Light de Berinjela', 'Lasanha com fatias de berinjela no lugar da massa', 'Grelhe as fatias de berinjela e monte camadas com molho e queijo.', '2026-08-14 00:00:00-03:00', true),
(NULL, '52263905480', 'Risoto de Camarao', 'Risoto cremoso com camarao salteado', 'Refogue o arroz arboreo, adicione caldo aos poucos e finalize com camarao.', '2026-08-10 00:00:00-03:00', false),
('70022229000102', NULL, 'Pure de Batata Cremoso', 'Pure macio de batata com manteiga', 'Cozinhe as batatas, amasse e misture com manteiga e leite quente.', '2026-08-06 00:00:00-03:00', true),
(NULL, '80340669900', 'Suco Detox Verde', 'Suco funcional com couve e frutas', 'Bata no liquidificador couve, maca, limao e agua, coe e sirva gelado.', '2026-08-02 00:00:00-03:00', true),
('94007678000140', NULL, 'Iscas de Frango Empanadas', 'Iscas crocantes de frango empanado', 'Empane as iscas de frango e frite ate dourar por igual.', '2026-07-29 00:00:00-03:00', false),
(NULL, '56858215735', 'Peixe Assado com Legumes', 'Filé de peixe assado com legumes no forno', 'Tempere o peixe, disponha com legumes na assadeira e leve ao forno.', '2026-07-25 00:00:00-03:00', true),
('60898058000126', NULL, 'Salada Caesar com Frango', 'Salada classica com frango grelhado e croutons', 'Monte a alface, frango grelhado, croutons e molho caesar.', '2026-07-21 00:00:00-03:00', true),
(NULL, '66139532884', 'Macarrao Integral ao Sugo', 'Macarrao integral com molho de tomate caseiro', 'Cozinhe o macarrao e sirva com molho de tomate refogado.', '2026-07-17 00:00:00-03:00', false),
('56281715000114', NULL, 'Creme de Abobora', 'Creme aveludado de abobora com gengibre', 'Cozinhe a abobora, bata no liquidificador e tempere com gengibre.', '2026-07-13 00:00:00-03:00', true),
(NULL, '10769242987', 'Salada de Frango com Abacaxi (2a versao)', 'Salada refrescante com frango desfiado e abacaxi', 'Cozinhe o frango, desfie e misture com abacaxi picado, alface e molho de iogurte.', '2026-07-09 00:00:00-03:00', true),
('91004404000109', NULL, 'Sanduiche Natural de Atum (2a versao)', 'Sanduiche leve com pasta de atum e vegetais', 'Misture atum, maionese e cenoura ralada; monte no pao integral com alface e tomate.', '2026-07-05 00:00:00-03:00', false),
(NULL, '26951681520', 'Sopa de Legumes (2a versao)', 'Sopa quente com legumes variados', 'Refogue cebola e alho, adicione os legumes picados e agua, cozinhe ate amaciar.', '2026-07-01 00:00:00-03:00', true),
('96706343000145', NULL, 'Omelete Simples (2a versao)', 'Omelete classico de ovos com temperos', 'Bata os ovos com sal e pimenta, despeje na frigideira quente e doure dos dois lados.', '2026-06-27 00:00:00-03:00', true),
(NULL, '07658178535', 'Bolo de Cenoura com Cobertura (2a versao)', 'Bolo tradicional com cobertura de chocolate', 'Bata cenoura, ovos e oleo no liquidificador, misture com farinha e asse por 40 min.', '2026-06-23 00:00:00-03:00', false),
('08438764000175', NULL, 'Frango Grelhado com Ervas (2a versao)', 'Peito de frango grelhado temperado com ervas finas', 'Tempere o frango e grelhe em fogo medio ate dourar dos dois lados.', '2026-06-19 00:00:00-03:00', true),
(NULL, '07464396790', 'Arroz Integral com Legumes (2a versao)', 'Arroz integral refogado com legumes picados', 'Refogue o arroz com alho, adicione agua e legumes, cozinhe em fogo baixo.', '2026-06-15 00:00:00-03:00', true),
('26012776000115', NULL, 'Feijoada Light (2a versao)', 'Versao mais leve da feijoada tradicional', 'Cozinhe o feijao com carnes magras e temperos ate ficar cremoso.', '2026-06-11 00:00:00-03:00', false),
(NULL, '79643758656', 'Panqueca Doce de Banana (2a versao)', 'Panqueca doce recheada com banana e canela', 'Prepare a massa, recheie com banana caramelizada e enrole.', '2026-06-07 00:00:00-03:00', true),
('82242871000186', NULL, 'Wrap de Frango (2a versao)', 'Wrap pratico com frango e vegetais', 'Recheie a tortilha com frango grelhado, alface e molho de iogurte.', '2026-06-03 00:00:00-03:00', true),
(NULL, '06923786185', 'Torta de Frango com Catupiry (2a versao)', 'Torta salgada cremosa de frango', 'Monte camadas de massa, recheio de frango e catupiry, asse ate dourar.', '2026-05-30 00:00:00-03:00', false),
('66304631000102', NULL, 'Lasanha Light de Berinjela (2a versao)', 'Lasanha com fatias de berinjela no lugar da massa', 'Grelhe as fatias de berinjela e monte camadas com molho e queijo.', '2026-05-26 00:00:00-03:00', true),
(NULL, '68625374310', 'Risoto de Camarao (2a versao)', 'Risoto cremoso com camarao salteado', 'Refogue o arroz arboreo, adicione caldo aos poucos e finalize com camarao.', '2026-05-22 00:00:00-03:00', true),
('96116540000104', NULL, 'Pure de Batata Cremoso (2a versao)', 'Pure macio de batata com manteiga', 'Cozinhe as batatas, amasse e misture com manteiga e leite quente.', '2026-05-18 00:00:00-03:00', false),
(NULL, '01116136392', 'Suco Detox Verde (2a versao)', 'Suco funcional com couve e frutas', 'Bata no liquidificador couve, maca, limao e agua, coe e sirva gelado.', '2026-05-14 00:00:00-03:00', true),
('85416602000187', NULL, 'Iscas de Frango Empanadas (2a versao)', 'Iscas crocantes de frango empanado', 'Empane as iscas de frango e frite ate dourar por igual.', '2026-05-10 00:00:00-03:00', true),
(NULL, '61101572019', 'Peixe Assado com Legumes (2a versao)', 'Filé de peixe assado com legumes no forno', 'Tempere o peixe, disponha com legumes na assadeira e leve ao forno.', '2026-05-06 00:00:00-03:00', false),
('61134330000164', NULL, 'Salada Caesar com Frango (2a versao)', 'Salada classica com frango grelhado e croutons', 'Monte a alface, frango grelhado, croutons e molho caesar.', '2026-05-02 00:00:00-03:00', true),
(NULL, '11421570750', 'Macarrao Integral ao Sugo (2a versao)', 'Macarrao integral com molho de tomate caseiro', 'Cozinhe o macarrao e sirva com molho de tomate refogado.', '2026-04-28 00:00:00-03:00', true),
('03853857000142', NULL, 'Creme de Abobora (2a versao)', 'Creme aveludado de abobora com gengibre', 'Cozinhe a abobora, bata no liquidificador e tempere com gengibre.', '2026-04-24 00:00:00-03:00', false),
(NULL, '42935404904', 'Salada de Frango com Abacaxi (3a versao)', 'Salada refrescante com frango desfiado e abacaxi', 'Cozinhe o frango, desfie e misture com abacaxi picado, alface e molho de iogurte.', '2026-04-20 00:00:00-03:00', true),
('00691117000169', NULL, 'Sanduiche Natural de Atum (3a versao)', 'Sanduiche leve com pasta de atum e vegetais', 'Misture atum, maionese e cenoura ralada; monte no pao integral com alface e tomate.', '2026-04-16 00:00:00-03:00', true),
(NULL, '32505476462', 'Sopa de Legumes (3a versao)', 'Sopa quente com legumes variados', 'Refogue cebola e alho, adicione os legumes picados e agua, cozinhe ate amaciar.', '2026-04-12 00:00:00-03:00', false),
('86598366000120', NULL, 'Omelete Simples (3a versao)', 'Omelete classico de ovos com temperos', 'Bata os ovos com sal e pimenta, despeje na frigideira quente e doure dos dois lados.', '2026-04-08 00:00:00-03:00', true),
(NULL, '35779032289', 'Bolo de Cenoura com Cobertura (3a versao)', 'Bolo tradicional com cobertura de chocolate', 'Bata cenoura, ovos e oleo no liquidificador, misture com farinha e asse por 40 min.', '2026-04-04 00:00:00-03:00', true),
('42839744000144', NULL, 'Frango Grelhado com Ervas (3a versao)', 'Peito de frango grelhado temperado com ervas finas', 'Tempere o frango e grelhe em fogo medio ate dourar dos dois lados.', '2026-03-31 00:00:00-03:00', false),
(NULL, '79715532004', 'Arroz Integral com Legumes (3a versao)', 'Arroz integral refogado com legumes picados', 'Refogue o arroz com alho, adicione agua e legumes, cozinhe em fogo baixo.', '2026-03-27 00:00:00-03:00', true),
('59649891000154', NULL, 'Feijoada Light (3a versao)', 'Versao mais leve da feijoada tradicional', 'Cozinhe o feijao com carnes magras e temperos ate ficar cremoso.', '2026-03-23 00:00:00-03:00', true),
(NULL, '44230143605', 'Panqueca Doce de Banana (3a versao)', 'Panqueca doce recheada com banana e canela', 'Prepare a massa, recheie com banana caramelizada e enrole.', '2026-03-19 00:00:00-03:00', false),
('78859395000170', NULL, 'Wrap de Frango (3a versao)', 'Wrap pratico com frango e vegetais', 'Recheie a tortilha com frango grelhado, alface e molho de iogurte.', '2026-03-15 00:00:00-03:00', true);


-- Ingredientes

INSERT INTO Ingredientes (idReceita, idProdutos, quantidade) VALUES
(1, 4, 10),
(2, 5, 6),
(3, 6, 7),
(4, 7, 4),
(5, 8, 10),
(6, 9, 9),
(7, 10, 4),
(8, 11, 5),
(9, 12, 3),
(10, 13, 6),
(11, 14, 9),
(12, 15, 3),
(13, 16, 8),
(14, 17, 6),
(15, 18, 7),
(16, 19, 3),
(17, 20, 8),
(18, 21, 10),
(19, 22, 9),
(20, 23, 8),
(21, 24, 2),
(22, 25, 10),
(23, 26, 8),
(24, 27, 3),
(25, 28, 3),
(26, 29, 8),
(27, 30, 10),
(28, 31, 4),
(29, 32, 6),
(30, 33, 3),
(31, 34, 10),
(32, 35, 6),
(33, 36, 1),
(34, 37, 9),
(35, 38, 9),
(36, 39, 9),
(37, 40, 6),
(38, 41, 3),
(39, 42, 2),
(40, 43, 4),
(41, 44, 2),
(42, 45, 7),
(43, 46, 6),
(44, 47, 2),
(45, 48, 4),
(46, 49, 5),
(47, 50, 6),
(48, 1, 8),
(49, 2, 1),
(50, 3, 1);


-- Assinatura

INSERT INTO Assinatura (idUsuario, idEmpresa, tipoAssinatura, valor, descricao) VALUES
('61651728658', NULL, 'FREE', 0.0, 'Plano gratuito com funcionalidades basicas'),
(NULL, '47253455000182', 'PLUS', 29.9, 'Plano individual com notificacoes e relatorios'),
('89632247248', NULL, 'FAMILIA', 49.9, 'Plano para toda a familia com multiplos usuarios'),
(NULL, '87349005000102', 'COMERCIAL', 99.9, 'Plano para empresas com uso ilimitado'),
('61988335000', NULL, 'FREE', 0.0, 'Plano gratuito com funcionalidades basicas'),
(NULL, '06573130000108', 'PLUS', 29.9, 'Plano individual com notificacoes e relatorios'),
('50370395093', NULL, 'FAMILIA', 49.9, 'Plano para toda a familia com multiplos usuarios'),
(NULL, '56876315000151', 'COMERCIAL', 99.9, 'Plano para empresas com uso ilimitado'),
('87511029191', NULL, 'FREE', 0.0, 'Plano gratuito com funcionalidades basicas'),
(NULL, '79516470000162', 'PLUS', 29.9, 'Plano individual com notificacoes e relatorios'),
('93816662293', NULL, 'FAMILIA', 49.9, 'Plano para toda a familia com multiplos usuarios'),
(NULL, '39829214000100', 'COMERCIAL', 99.9, 'Plano para empresas com uso ilimitado'),
('52263905480', NULL, 'FREE', 0.0, 'Plano gratuito com funcionalidades basicas'),
(NULL, '70022229000102', 'PLUS', 29.9, 'Plano individual com notificacoes e relatorios'),
('80340669900', NULL, 'FAMILIA', 49.9, 'Plano para toda a familia com multiplos usuarios'),
(NULL, '94007678000140', 'COMERCIAL', 99.9, 'Plano para empresas com uso ilimitado'),
('56858215735', NULL, 'FREE', 0.0, 'Plano gratuito com funcionalidades basicas'),
(NULL, '60898058000126', 'PLUS', 29.9, 'Plano individual com notificacoes e relatorios'),
('66139532884', NULL, 'FAMILIA', 49.9, 'Plano para toda a familia com multiplos usuarios'),
(NULL, '56281715000114', 'COMERCIAL', 99.9, 'Plano para empresas com uso ilimitado'),
('10769242987', NULL, 'FREE', 0.0, 'Plano gratuito com funcionalidades basicas'),
(NULL, '91004404000109', 'PLUS', 29.9, 'Plano individual com notificacoes e relatorios'),
('26951681520', NULL, 'FAMILIA', 49.9, 'Plano para toda a familia com multiplos usuarios'),
(NULL, '96706343000145', 'COMERCIAL', 99.9, 'Plano para empresas com uso ilimitado'),
('07658178535', NULL, 'FREE', 0.0, 'Plano gratuito com funcionalidades basicas'),
(NULL, '08438764000175', 'PLUS', 29.9, 'Plano individual com notificacoes e relatorios'),
('07464396790', NULL, 'FAMILIA', 49.9, 'Plano para toda a familia com multiplos usuarios'),
(NULL, '26012776000115', 'COMERCIAL', 99.9, 'Plano para empresas com uso ilimitado'),
('79643758656', NULL, 'FREE', 0.0, 'Plano gratuito com funcionalidades basicas'),
(NULL, '82242871000186', 'PLUS', 29.9, 'Plano individual com notificacoes e relatorios'),
('06923786185', NULL, 'FAMILIA', 49.9, 'Plano para toda a familia com multiplos usuarios'),
(NULL, '66304631000102', 'COMERCIAL', 99.9, 'Plano para empresas com uso ilimitado'),
('68625374310', NULL, 'FREE', 0.0, 'Plano gratuito com funcionalidades basicas'),
(NULL, '96116540000104', 'PLUS', 29.9, 'Plano individual com notificacoes e relatorios'),
('01116136392', NULL, 'FAMILIA', 49.9, 'Plano para toda a familia com multiplos usuarios'),
(NULL, '85416602000187', 'COMERCIAL', 99.9, 'Plano para empresas com uso ilimitado'),
('61101572019', NULL, 'FREE', 0.0, 'Plano gratuito com funcionalidades basicas'),
(NULL, '61134330000164', 'PLUS', 29.9, 'Plano individual com notificacoes e relatorios'),
('11421570750', NULL, 'FAMILIA', 49.9, 'Plano para toda a familia com multiplos usuarios'),
(NULL, '03853857000142', 'COMERCIAL', 99.9, 'Plano para empresas com uso ilimitado'),
('42935404904', NULL, 'FREE', 0.0, 'Plano gratuito com funcionalidades basicas'),
(NULL, '00691117000169', 'PLUS', 29.9, 'Plano individual com notificacoes e relatorios'),
('32505476462', NULL, 'FAMILIA', 49.9, 'Plano para toda a familia com multiplos usuarios'),
(NULL, '86598366000120', 'COMERCIAL', 99.9, 'Plano para empresas com uso ilimitado'),
('35779032289', NULL, 'FREE', 0.0, 'Plano gratuito com funcionalidades basicas'),
(NULL, '42839744000144', 'PLUS', 29.9, 'Plano individual com notificacoes e relatorios'),
('79715532004', NULL, 'FAMILIA', 49.9, 'Plano para toda a familia com multiplos usuarios'),
(NULL, '59649891000154', 'COMERCIAL', 99.9, 'Plano para empresas com uso ilimitado'),
('44230143605', NULL, 'FREE', 0.0, 'Plano gratuito com funcionalidades basicas'),
(NULL, '78859395000170', 'PLUS', 29.9, 'Plano individual com notificacoes e relatorios');


-- Compra

INSERT INTO Compra (idUsuario, idEmpresa, OndeComprou, dtCompra, ValorCompra) VALUES
('61651728658', NULL, 'Mercado Bom Preco', '2026-09-07 00:00:00-03:00', 938.72),
(NULL, '47253455000182', 'Fazenda Verde Direto', '2026-05-31 00:00:00-03:00', 1179.54),
('89632247248', NULL, 'Atacadao Distribuidor', '2026-04-06 00:00:00-03:00', 1200.94),
(NULL, '87349005000102', 'Empório Sabor & Cia', '2026-07-28 00:00:00-03:00', 647.06),
('61988335000', NULL, 'Feira Livre do Bairro', '2026-09-05 00:00:00-03:00', 439.27),
(NULL, '06573130000108', 'Supermercado Extra', '2025-12-19 00:00:00-03:00', 366.32),
('50370395093', NULL, 'Atacado Rio Distribuicao', '2026-05-21 00:00:00-03:00', 588.63),
(NULL, '56876315000151', 'Central de Compras', '2026-04-17 00:00:00-03:00', 1160.12),
('87511029191', NULL, 'Hortifruti Express', '2026-02-24 00:00:00-03:00', 219.43),
(NULL, '79516470000162', 'Casa de Carnes Premium', '2026-07-28 00:00:00-03:00', 464.59),
('93816662293', NULL, 'Mercado Bom Preco', '2026-02-23 00:00:00-03:00', 1751.7),
(NULL, '39829214000100', 'Fazenda Verde Direto', '2026-04-26 00:00:00-03:00', 1308.34),
('52263905480', NULL, 'Atacadao Distribuidor', '2026-06-04 00:00:00-03:00', 1338.99),
(NULL, '70022229000102', 'Empório Sabor & Cia', '2026-07-08 00:00:00-03:00', 1143.03),
('80340669900', NULL, 'Feira Livre do Bairro', '2026-06-16 00:00:00-03:00', 1709.83),
(NULL, '94007678000140', 'Supermercado Extra', '2026-04-05 00:00:00-03:00', 1139.34),
('56858215735', NULL, 'Atacado Rio Distribuicao', '2026-07-08 00:00:00-03:00', 879.62),
(NULL, '60898058000126', 'Central de Compras', '2026-03-18 00:00:00-03:00', 1494.1),
('66139532884', NULL, 'Hortifruti Express', '2026-02-18 00:00:00-03:00', 1739.03),
(NULL, '56281715000114', 'Casa de Carnes Premium', '2026-06-30 00:00:00-03:00', 68.64),
('10769242987', NULL, 'Mercado Bom Preco', '2026-03-13 00:00:00-03:00', 1653.86),
(NULL, '91004404000109', 'Fazenda Verde Direto', '2026-03-18 00:00:00-03:00', 1181.65),
('26951681520', NULL, 'Atacadao Distribuidor', '2026-06-18 00:00:00-03:00', 920.49),
(NULL, '96706343000145', 'Empório Sabor & Cia', '2026-05-27 00:00:00-03:00', 758.1),
('07658178535', NULL, 'Feira Livre do Bairro', '2026-07-06 00:00:00-03:00', 1771.16),
(NULL, '08438764000175', 'Supermercado Extra', '2026-05-13 00:00:00-03:00', 703.57),
('07464396790', NULL, 'Atacado Rio Distribuicao', '2026-08-03 00:00:00-03:00', 912.9),
(NULL, '26012776000115', 'Central de Compras', '2026-03-14 00:00:00-03:00', 1716.9),
('79643758656', NULL, 'Hortifruti Express', '2026-08-26 00:00:00-03:00', 146.52),
(NULL, '82242871000186', 'Casa de Carnes Premium', '2026-06-29 00:00:00-03:00', 570.38),
('06923786185', NULL, 'Mercado Bom Preco', '2026-07-08 00:00:00-03:00', 1014.66),
(NULL, '66304631000102', 'Fazenda Verde Direto', '2026-03-08 00:00:00-03:00', 819.09),
('68625374310', NULL, 'Atacadao Distribuidor', '2026-02-10 00:00:00-03:00', 451.36),
(NULL, '96116540000104', 'Empório Sabor & Cia', '2026-07-30 00:00:00-03:00', 1513.18),
('01116136392', NULL, 'Feira Livre do Bairro', '2026-07-02 00:00:00-03:00', 210.29),
(NULL, '85416602000187', 'Supermercado Extra', '2026-05-30 00:00:00-03:00', 628.33),
('61101572019', NULL, 'Atacado Rio Distribuicao', '2026-06-25 00:00:00-03:00', 749.83),
(NULL, '61134330000164', 'Central de Compras', '2026-02-04 00:00:00-03:00', 1299.22),
('11421570750', NULL, 'Hortifruti Express', '2026-08-20 00:00:00-03:00', 938.94),
(NULL, '03853857000142', 'Casa de Carnes Premium', '2026-02-14 00:00:00-03:00', 546.64),
('42935404904', NULL, 'Mercado Bom Preco', '2025-12-24 00:00:00-03:00', 1225.03),
(NULL, '00691117000169', 'Fazenda Verde Direto', '2026-04-08 00:00:00-03:00', 1582.75),
('32505476462', NULL, 'Atacadao Distribuidor', '2026-01-29 00:00:00-03:00', 1408.52),
(NULL, '86598366000120', 'Empório Sabor & Cia', '2026-08-12 00:00:00-03:00', 246.6),
('35779032289', NULL, 'Feira Livre do Bairro', '2026-03-20 00:00:00-03:00', 1433.82),
(NULL, '42839744000144', 'Supermercado Extra', '2026-02-26 00:00:00-03:00', 166.98),
('79715532004', NULL, 'Atacado Rio Distribuicao', '2026-01-01 00:00:00-03:00', 1529.77),
(NULL, '59649891000154', 'Central de Compras', '2026-04-17 00:00:00-03:00', 966.82),
('44230143605', NULL, 'Hortifruti Express', '2026-03-08 00:00:00-03:00', 1235.17),
(NULL, '78859395000170', 'Casa de Carnes Premium', '2026-05-02 00:00:00-03:00', 876.91);


-- ListaCompras

INSERT INTO ListaCompras (idUsuario, idEmpresa, dtLista) VALUES
('61651728658', NULL, '2026-09-25 00:00:00-03:00'),
(NULL, '47253455000182', '2026-08-07 00:00:00-03:00'),
('89632247248', NULL, '2026-09-08 00:00:00-03:00'),
(NULL, '87349005000102', '2026-08-29 00:00:00-03:00'),
('61988335000', NULL, '2026-09-04 00:00:00-03:00'),
(NULL, '06573130000108', '2026-09-25 00:00:00-03:00'),
('50370395093', NULL, '2026-08-18 00:00:00-03:00'),
(NULL, '56876315000151', '2026-09-04 00:00:00-03:00'),
('87511029191', NULL, '2026-09-20 00:00:00-03:00'),
(NULL, '79516470000162', '2026-08-09 00:00:00-03:00'),
('93816662293', NULL, '2026-09-05 00:00:00-03:00'),
(NULL, '39829214000100', '2026-08-08 00:00:00-03:00'),
('52263905480', NULL, '2026-08-01 00:00:00-03:00'),
(NULL, '70022229000102', '2026-09-03 00:00:00-03:00'),
('80340669900', NULL, '2026-08-16 00:00:00-03:00'),
(NULL, '94007678000140', '2026-09-04 00:00:00-03:00'),
('56858215735', NULL, '2026-08-10 00:00:00-03:00'),
(NULL, '60898058000126', '2026-09-08 00:00:00-03:00'),
('66139532884', NULL, '2026-08-19 00:00:00-03:00'),
(NULL, '56281715000114', '2026-09-01 00:00:00-03:00'),
('10769242987', NULL, '2026-08-07 00:00:00-03:00'),
(NULL, '91004404000109', '2026-09-13 00:00:00-03:00'),
('26951681520', NULL, '2026-08-06 00:00:00-03:00'),
(NULL, '96706343000145', '2026-08-17 00:00:00-03:00'),
('07658178535', NULL, '2026-09-04 00:00:00-03:00'),
(NULL, '08438764000175', '2026-08-07 00:00:00-03:00'),
('07464396790', NULL, '2026-08-19 00:00:00-03:00'),
(NULL, '26012776000115', '2026-09-09 00:00:00-03:00'),
('79643758656', NULL, '2026-08-15 00:00:00-03:00'),
(NULL, '82242871000186', '2026-09-26 00:00:00-03:00'),
('06923786185', NULL, '2026-08-06 00:00:00-03:00'),
(NULL, '66304631000102', '2026-08-28 00:00:00-03:00'),
('68625374310', NULL, '2026-08-25 00:00:00-03:00'),
(NULL, '96116540000104', '2026-07-30 00:00:00-03:00'),
('01116136392', NULL, '2026-09-19 00:00:00-03:00'),
(NULL, '85416602000187', '2026-08-06 00:00:00-03:00'),
('61101572019', NULL, '2026-09-18 00:00:00-03:00'),
(NULL, '61134330000164', '2026-09-03 00:00:00-03:00'),
('11421570750', NULL, '2026-09-07 00:00:00-03:00'),
(NULL, '03853857000142', '2026-08-17 00:00:00-03:00'),
('42935404904', NULL, '2026-09-03 00:00:00-03:00'),
(NULL, '00691117000169', '2026-08-18 00:00:00-03:00'),
('32505476462', NULL, '2026-09-17 00:00:00-03:00'),
(NULL, '86598366000120', '2026-08-13 00:00:00-03:00'),
('35779032289', NULL, '2026-08-24 00:00:00-03:00'),
(NULL, '42839744000144', '2026-09-08 00:00:00-03:00'),
('79715532004', NULL, '2026-08-02 00:00:00-03:00'),
(NULL, '59649891000154', '2026-09-08 00:00:00-03:00'),
('44230143605', NULL, '2026-08-25 00:00:00-03:00'),
(NULL, '78859395000170', '2026-08-12 00:00:00-03:00');


-- ProdutoLista

INSERT INTO ProdutoLista (idListaCompras, idProdutos, quantidade) VALUES
(1, 6, 4),
(2, 7, 18),
(3, 8, 15),
(4, 9, 9),
(5, 10, 12),
(6, 11, 8),
(7, 12, 11),
(8, 13, 7),
(9, 14, 20),
(10, 15, 7),
(11, 16, 17),
(12, 17, 4),
(13, 18, 7),
(14, 19, 3),
(15, 20, 15),
(16, 21, 8),
(17, 22, 14),
(18, 23, 14),
(19, 24, 15),
(20, 25, 5),
(21, 26, 15),
(22, 27, 11),
(23, 28, 15),
(24, 29, 4),
(25, 30, 9),
(26, 31, 12),
(27, 32, 6),
(28, 33, 9),
(29, 34, 9),
(30, 35, 9),
(31, 36, 15),
(32, 37, 16),
(33, 38, 10),
(34, 39, 20),
(35, 40, 15),
(36, 41, 9),
(37, 42, 5),
(38, 43, 15),
(39, 44, 17),
(40, 45, 3),
(41, 46, 12),
(42, 47, 14),
(43, 48, 11),
(44, 49, 8),
(45, 50, 7),
(46, 1, 5),
(47, 2, 11),
(48, 3, 1),
(49, 4, 8),
(50, 5, 16);


-- MarcasParceiras

INSERT INTO MarcasParceiras (idEmpresa, idProdutos, observacoes) VALUES
('24144297000127', 3, 'Parceria comercial com fornecimento preferencial de alface crespa e condicoes especiais de prazo'),
('47253455000182', 4, 'Parceria comercial com fornecimento preferencial de queijo mussarela e condicoes especiais de prazo'),
('36006970000141', 5, 'Parceria comercial com fornecimento preferencial de iogurte natural e condicoes especiais de prazo'),
('87349005000102', 6, 'Parceria comercial com fornecimento preferencial de carne moida e condicoes especiais de prazo'),
('77310109000150', 7, 'Parceria comercial com fornecimento preferencial de tomate salada e condicoes especiais de prazo'),
('06573130000108', 8, 'Parceria comercial com fornecimento preferencial de requeijao cremoso e condicoes especiais de prazo'),
('81948539000179', 9, 'Parceria comercial com fornecimento preferencial de file de tilapia e condicoes especiais de prazo'),
('56876315000151', 10, 'Parceria comercial com fornecimento preferencial de cenoura e condicoes especiais de prazo'),
('47026119000105', 11, 'Parceria comercial com fornecimento preferencial de manteiga com sal e condicoes especiais de prazo'),
('79516470000162', 12, 'Parceria comercial com fornecimento preferencial de linguica toscana e condicoes especiais de prazo'),
('85589026000170', 13, 'Parceria comercial com fornecimento preferencial de batata inglesa e condicoes especiais de prazo'),
('39829214000100', 14, 'Parceria comercial com fornecimento preferencial de presunto fatiado e condicoes especiais de prazo'),
('43734144000184', 15, 'Parceria comercial com fornecimento preferencial de brocolis e condicoes especiais de prazo'),
('70022229000102', 16, 'Parceria comercial com fornecimento preferencial de leite desnatado e condicoes especiais de prazo'),
('18315670000162', 17, 'Parceria comercial com fornecimento preferencial de costela bovina e condicoes especiais de prazo'),
('94007678000140', 18, 'Parceria comercial com fornecimento preferencial de cebola roxa e condicoes especiais de prazo'),
('40613964000120', 19, 'Parceria comercial com fornecimento preferencial de cream cheese e condicoes especiais de prazo'),
('60898058000126', 20, 'Parceria comercial com fornecimento preferencial de salmao fresco e condicoes especiais de prazo'),
('62402210000163', 21, 'Parceria comercial com fornecimento preferencial de abobrinha e condicoes especiais de prazo'),
('56281715000114', 22, 'Parceria comercial com fornecimento preferencial de bacon em cubos e condicoes especiais de prazo'),
('67436371000191', 23, 'Parceria comercial com fornecimento preferencial de pepino japones e condicoes especiais de prazo'),
('91004404000109', 24, 'Parceria comercial com fornecimento preferencial de ricota fresca e condicoes especiais de prazo'),
('72264236000146', 25, 'Parceria comercial com fornecimento preferencial de coxa de frango e condicoes especiais de prazo'),
('96706343000145', 26, 'Parceria comercial com fornecimento preferencial de couve manteiga e condicoes especiais de prazo'),
('21473877000170', 27, 'Parceria comercial com fornecimento preferencial de iogurte grego e condicoes especiais de prazo'),
('08438764000175', 28, 'Parceria comercial com fornecimento preferencial de pernil suino e condicoes especiais de prazo'),
('82635120000120', 29, 'Parceria comercial com fornecimento preferencial de pimentao verde e condicoes especiais de prazo'),
('26012776000115', 30, 'Parceria comercial com fornecimento preferencial de queijo prato e condicoes especiais de prazo'),
('33665954000172', 31, 'Parceria comercial com fornecimento preferencial de ovos brancos e condicoes especiais de prazo'),
('82242871000186', 32, 'Parceria comercial com fornecimento preferencial de file mignon e condicoes especiais de prazo'),
('34910575000163', 33, 'Parceria comercial com fornecimento preferencial de repolho roxo e condicoes especiais de prazo'),
('66304631000102', 34, 'Parceria comercial com fornecimento preferencial de leite semidesnatado e condicoes especiais de prazo'),
('22514034000137', 35, 'Parceria comercial com fornecimento preferencial de camarao limpo e condicoes especiais de prazo'),
('96116540000104', 36, 'Parceria comercial com fornecimento preferencial de espinafre e condicoes especiais de prazo'),
('95405520000190', 37, 'Parceria comercial com fornecimento preferencial de nata fresca e condicoes especiais de prazo'),
('85416602000187', 38, 'Parceria comercial com fornecimento preferencial de bisteca suina e condicoes especiais de prazo'),
('41182220000160', 39, 'Parceria comercial com fornecimento preferencial de vagem e condicoes especiais de prazo'),
('61134330000164', 40, 'Parceria comercial com fornecimento preferencial de queijo coalho e condicoes especiais de prazo'),
('21805824000109', 41, 'Parceria comercial com fornecimento preferencial de whey protein e condicoes especiais de prazo'),
('03853857000142', 42, 'Parceria comercial com fornecimento preferencial de almondegas e condicoes especiais de prazo'),
('93034110000155', 43, 'Parceria comercial com fornecimento preferencial de berinjela e condicoes especiais de prazo'),
('00691117000169', 44, 'Parceria comercial com fornecimento preferencial de manteiga sem sal e condicoes especiais de prazo'),
('73758788000173', 45, 'Parceria comercial com fornecimento preferencial de frango a passarinho e condicoes especiais de prazo'),
('86598366000120', 46, 'Parceria comercial com fornecimento preferencial de rucula e condicoes especiais de prazo'),
('69366710000136', 47, 'Parceria comercial com fornecimento preferencial de queijo parmesao e condicoes especiais de prazo'),
('42839744000144', 48, 'Parceria comercial com fornecimento preferencial de salsicha e condicoes especiais de prazo'),
('05122832000102', 49, 'Parceria comercial com fornecimento preferencial de chuchu e condicoes especiais de prazo'),
('59649891000154', 50, 'Parceria comercial com fornecimento preferencial de iogurte de morango e condicoes especiais de prazo'),
('23628639000111', 1, 'Parceria comercial com fornecimento preferencial de leite integral e condicoes especiais de prazo'),
('78859395000170', 2, 'Parceria comercial com fornecimento preferencial de peito de frango e condicoes especiais de prazo');







set timezone = 'America/Sao_Paulo';
show timezone;
SET datestyle = 'ISO, DMY';
show datastyle;
*/
