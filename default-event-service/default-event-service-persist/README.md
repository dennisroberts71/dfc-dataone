## Database setup notes

If you don't have an IRODS-EXT extensions db, add one and a user

```
$ (sudo) su - postgres
postgres$ psql
psql> CREATE USER irodsext WITH PASSWORD 'password';
psql> CREATE DATABASE "IRODS-EXT";
psql> GRANT ALL PRIVILEGES ON DATABASE "IRODS-EXT" TO irodsext;

```


Create the table for the event log

```

CREATE TABLE IF NOT EXISTS access_log  ( id BIGSERIAL PRIMARY KEY, file_key VARCHAR(1024) NOT NULL, event_id VARCHAR(30) NOT NULL, event_detail VARCHAR(200) NULL, access_principal VARCHAR(50) NULL, date_added TIMESTAMP DEFAULT NULL );


```

(no other indexes yet...may add)

yields

```
       ^
IRODS-EXT=# \d access_log
                                        Table "public.access_log"
      Column      |            Type             |                        Modifiers                        
------------------+-----------------------------+---------------------------------------------------------
 id               | bigint                      | not null default nextval('access_log_id_seq'::regclass)
 file_key         | character varying(1024)     | not null
 event_id         | character varying(30)       | not null
 event_detail     | character varying(200)      | 
 access_principal | character varying(50)       | 
 date_added       | timestamp without time zone | 
Indexes:
    "access_log_pkey" PRIMARY KEY, btree (id)


```