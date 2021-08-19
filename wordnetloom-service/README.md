**old_database:** 
```shell script
wordnet_work
```

**new_database:**
```shell script
wordnet
```
<br />

**Database connection in pom.xml:**
```shell script
<configuration>

</configuration>
```

**To compile migrations:**
```shell script
mvn compile
```

**After compile to make migrations:**
```shell script
mvn flyway:migrate
```
<br />

**To check or repair use:**
```shell script
mvn flyway:info

mvn flyway:repair
```



