1 - Check chbatey has the SPORTS permission

a) Check chbatey exists
b) Check he has the SPORTS permission

Call b depends on call a

2 -  Check chbatey can watch SkySportsOne

a) Check chbatey  exists
b) Check chbatey has the SPORTS permission
c) Check SkySportsOne exists

Call b depends on call a, call c is independent

3 - Speed up scenario two by making any independent calls concurrent

4 - Scalability requirement - Remove any blocked thread

5 - Remove dependency on Guava, it conflicts with another dependency - 40





