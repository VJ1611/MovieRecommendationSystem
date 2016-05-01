
create table moviesinfo ( 
   movieid int primary key, 
   title nvarchar(100) not null, 
   date1 text, 
   videodate text, 
   imdb text, 
   unknown1 boolean, 
   action boolean, 
   adventure boolean, 
   animation boolean, 
   childrens boolean, 
   comedy boolean, 
   crime boolean, 
   documentary boolean, 
   drama boolean, 
   fantasy boolean, 
   noir boolean, 
   horror boolean, 
   musical boolean, 
   mystery boolean, 
   romance boolean, 
   scifi boolean, 
   thriller boolean, 
   war boolean, 
   western boolean); 
 
create table users ( 
   userid int primary key, 
   age int, 
   gender char, 
   occupation text, 
   zip text); 
 create table ratinginfo ( 
   userid int , 
   movieid int , 
   rating int, 
   timestamp1 int, 
   foreign key(userid) references users(userid),
   foreign key(movieid) references moviesinfo(movieid)
   );