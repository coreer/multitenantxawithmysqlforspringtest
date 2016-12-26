# Test project to show the bug with Spring test @Sql and multitenant mysql XA env

* [Bugtracker](https://github.com/spring-projects/spring-boot/issues/7729)
* [Question on stackoverflow](http://stackoverflow.com/questions/41288483/spring-boot-testing-preparing-database-with-sql-in-multitenant-env-with-xa)

Steps to launch application:

## 1. Provide multitenant mysql xa cluster
Developer environment example:
```shell
docker-compose up -d
```

## 2. Run tests to show the problem
