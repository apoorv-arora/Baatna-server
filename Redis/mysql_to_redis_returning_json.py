import mysql.connector
import json
import collections
import redis

cnx = mysql.connector.connect(user='root', password='aditya',
                              host='127.0.0.1',
                              database='aditya')
cursor = cnx.cursor()
 
cursor.execute("""
            SELECT ID, FirstName, LastName
            FROM students
            """)
 
rows = cursor.fetchall()
cnx.close()
 
# Convert query to row arrays
 
rowarray_list = []
for row in rows:
    t = (row[0], row[1], row[2])
    rowarray_list.append(t)
 
j = json.dumps(rowarray_list)
#rowarrays_file = 'student_rowarrays.js'
#f = open(rowarrays_file,'w')
print (j)

parsed_json = json.loads(j)

print (parsed_json)

r= redis.StrictRedis(host='localhost',port='6379',db=0)


leng=len(parsed_json)

for i in range(0,leng):
    r.lpush('users',parsed_json[i][0])

for i in range(0,leng):
    r.hset(parsed_json[i][0],'ID',parsed_json[i][0])
    r.hset(parsed_json[i][0],'Firstname',parsed_json[i][1])
    r.hset(parsed_json[i][0],'Secondname',parsed_json[i][2])

to1=r.hgetall(parsed_json[0][0])   
to2=r.hgetall(parsed_json[1][0])   
to3=r.hgetall(parsed_json[2][0])   
temp=(to1,to2,to3)
temp=str(temp)

#print (to)

js = json.dumps(temp)
print ('aloooo')
print (js)
