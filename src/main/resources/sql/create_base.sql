create schema "bank";

create table auth (
  id bigint not null auto_increment,
  login varchar (255),
  password varchar (255),
  primary key (id)
);

create table client (
  id bigint not null auto_increment,
  birthday date,
  middleName varchar (255),
  name varchar (255),
  surname varchar (255),
  contact_id bigint,
  gender_id bigint not null,
  passport_id bigint,
  status_id bigint not null,
  primary key (id)
);

create table company (
  id bigint not null auto_increment,
  BIK varchar (255),
  INN varchar (255),
  KPP varchar (255),
  OGRN varchar (255),
  OKATO varchar (255),
  OKPO varchar (255),
  actualAddress varchar (255),
  corScore varchar (255),
  dateRegistration date,
  legalAddress varchar (255),
  licence varchar (255),
  name varchar (255),
  contact_id bigint,
  primary key (id)
);

create table contact (
  id bigint not null auto_increment,
  email varchar (255),
  phone varchar (255),
  primary key (id)
);

create table currency (
  id bigint not null auto_increment,
  charCode varchar (255),
  currencyDate date,
  name varchar (255),
  nominal integer not null,
  numCode integer not null,
  value float not null,
  valueBuy float not null,
  valueSale float not null,
  primary key (id)
);

create table employee (
  id bigint not null auto_increment,
  birthday date,
  middleName varchar (255),
  name varchar (255),
  surname varchar (255),
  auth_id bigint,
  contact_id bigint,
  gender_id bigint not null,
  passport_id bigint,
  role_id bigint not null,
  status_id bigint not null,
  primary key (id)
);

create table gender (
  id bigint not null auto_increment,
  type varchar (255),
  primary key (id)
);

create table operation (
  id bigint not null auto_increment,
  code bigint,
  codeIn varchar (255),
  codeOut varchar (255),
  date date,
  rate float not null,
  sumIn varchar (255),
  sumOut varchar (255),
  time time,
  client_id bigint not null,
  employee_id bigint not null,
  typeOperation_id bigint not null,
  primary key (id)
);

create table passport (
  id bigint not null auto_increment,
  birthPlace varchar (255),
  dateReleased date,
  number bigint not null,
  registration varchar (255),
  releasedBy varchar (255),
  serial bigint not null,
  unitCode integer not null,
  primary key (id)
);

create table role (
  id bigint not null auto_increment,
  type varchar (255),
  primary key (id)
);

create table status (
  id bigint not null auto_increment,
  name varchar (255),
  primary key (id)
);

create table typeOperation (
  id bigint not null auto_increment,
  type varchar (255),
  primary key (id)
);

create table operation_currency (
  operation_id bigint not null,
  currency_id bigint not null,
  primary key (operation_id, currency_id)
);

alter table client add constraint fk_client_operation
foreign key (contact_id) references contact (id);

alter table client add constraint fk_client_gender
foreign key (gender_id) references gender (id);

alter table client add constraint fk_client_passport
foreign key (passport_id) references passport (id);

alter table client add constraint fk_client_status
foreign key (status_id) references status (id);

alter table company add constraint fk_company_contact
foreign key (contact_id) references contact (id);

alter table employee add constraint fk_employee_auth
foreign key (auth_id) references auth (id);

alter table employee add constraint fk_employee_contact
foreign key (contact_id) references contact (id);

alter table employee add constraint fk_employee_gender
foreign key (gender_id) references gender (id);

alter table employee add constraint fk_employee_passport
foreign key (passport_id) references passport (id);

alter table employee add constraint fk_employee_role
foreign key (role_id) references role (id);

alter table employee add constraint fk_employee_status
foreign key (status_id) references status (id);

alter table operation add constraint fk_operation_client
foreign key (client_id) references client (id);

alter table operation add constraint fk_operation_employee
foreign key (employee_id) references employee (id);

alter table operation add constraint fk_operation_typeOperation
foreign key (typeOperation_id) references typeOperation (id);

alter table operation_currency add constraint fk_operation_currency
foreign key (currency_id) references currency (id);

alter table operation_currency add constraint fk_currency_operation
foreign key (operation_id) references operation (id);