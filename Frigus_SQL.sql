

-- apagar o banco quando necessario
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

--Criação das tabelas
CREATE TABLE Usuario(
    cpf         varchar(11)  primary key,
    nome        varchar(20)  not null,
    Sobrenome   varchar(60)  not null,
    email       varchar(320) not null     unique,
    senha       varchar(16)  not null
);

CREATE TABLE Empresa(
    cnpj        varchar(14)  primary key,
    nome        varchar(80)  not null,
    email       varchar(320) not null,
    senha       varchar(16)  not null     unique
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
    descricao       text            not null
);


CREATE TABLE Receita(
    id            serial      primary key,
    idEmpresa     varchar(14)     null,    
    idUsuario     varchar(11)     null,
    nome          varchar(50) not null,
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

ALTER TABLE Endereco
ADD CONSTRAINT chk_cep_formato
CHECK (cep ~ '^\d{8}$');
    