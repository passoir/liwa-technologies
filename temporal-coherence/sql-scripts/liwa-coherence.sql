DROP database liwa_coherence;
create database liwa_coherence;
\c liwa_coherence;

CREATE SEQUENCE links_seq
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

CREATE SEQUENCE pages_seq
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

CREATE SEQUENCE crawls_seq
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

CREATE SEQUENCE sites_seq
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

CREATE SEQUENCE urls_seq
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

CREATE SEQUENCE page_id_seq
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

CREATE SEQUENCE published_urls_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 65306398
  CACHE 1;
  
CREATE SEQUENCE robot_file_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 45
  CACHE 1;

CREATE TABLE t_crawls (
crawl_id integer,
title varchar,
recrawled_id integer
);


CREATE TABLE t_sites (
site_id integer,
site varchar uniquE
);

CREATE TABLE t_mimetypes (
mime_id integer,
mime_type varchar uniquE
);

CREATE TABLE t_urls (
url_id integer,
url varchar uniquE
);
CREATE TABLE t_pages (
page_id integer,
crawl_id integer,
url_id integer,
url varchar(4000),
site_id integer,
etag varchar(40),
page_size bigint,
page_type varchar(100),
parent_page_id integer,
visited_timestamp timestamp,
content bytea,
checksum varchar(40),
last_modified timestamp,
vs_page_id integer,
status_code integer,
download_time integer,
priority double precision,
frequency varchar(40),
expected_coherence double precision,
change_rate double precision,
sig0 bigint,
sig1 bigint,
sig2 bigint,
sig3 bigint,
sig4 bigint,
sig5 bigint,
sig6 bigint,
sig7 bigint,
sig8 bigint,
sig9 bigint,
mime_id integer,
filename varchar(200)
);

CREATE TABLE t_links (
from_page_id integer,
crawl_id integer,
from_url_id integer,
from_site_id integer,
to_url_id integer,
to_site_id integer,
link_type char(1),
checksum varchar(40),
visited_timestamp timestamp
); 

CREATE TABLE t_robot_files
(
  robot_file_id integer NOT NULL,
  robot_url character varying,
  visited_timestamp timestamp without time zone,
  CONSTRAINT robot_file_key PRIMARY KEY (robot_file_id)
)
WITH (OIDS=FALSE);

CREATE TABLE t_published_urls
(
  published_url_id integer NOT NULL,
  url character varying,
  robot_file_id integer,
  frequency character varying,
  priority double precision,
  last_modified timestamp without time zone,
  CONSTRAINT published_urls_key PRIMARY KEY (published_url_id),
  CONSTRAINT robot_file_key FOREIGN KEY (robot_file_id)
      REFERENCES t_robot_files (robot_file_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (OIDS=FALSE);

CREATE INDEX robots_file_index
  ON t_published_urls
  USING btree
  (robot_file_id, url, published_url_id, frequency, priority);
  
  