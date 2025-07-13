FROM mysql:8.0

ENV MYSQL_ROOT_PASSWORD=rootpass
ENV MYSQL_DATABASE=usersDB
ENV MYSQL_USER=jawen
ENV MYSQL_PASSWORD=pass

COPY init.sql /docker-entrypoint-initdb.d/

EXPOSE 3306